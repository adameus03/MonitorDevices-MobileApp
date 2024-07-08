package com.example.myapplication.monitordevices;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothStatusCodes;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.UUID;


/**
 * TODO [priority: 1/5, difficulty: 1/5] - switch from System.out.println calls to android.util.Log
 * TODO [priority 2/5, difficulty 2/5] - separate user alerts (bluetooth turned off, bluetooth not supported, device not found, device connected to Wi-Fi, device not connected to Wi-Fi) from the communications logic - use events/callbacks?
 * TODO [priority 2/5, difficulty 1/5] - create separate public methods instead of doing everything when constructor is called
 * DONE [priority 5/5, difficulty 1/5] - pass characteristics to send during registration either into constructor or to a setter method (the second option is rather preffered, as then, this class (and the GATT connection) can be reused for multiple tries)
 */
public class BLEController {
    static String TAG = "BLEController";
    static boolean USE_TEST_HARDCODED_CHARACTERISTICS = false;


    public static int BLE_CONTROLLER_RESULT_BT_OFF = -1;
    public static int BLE_CONTROLLER_RESULT_BT_NOT_SUPPORTED = -2;
    public static int BLE_CONTROLLER_RESULT_DEV_NOT_FOUND = -3;
    public static int BLE_CONTROLLER_RESULT_ERROR_NULL_SERVICE = -4;

    public static int BLE_CONTROLLER_RESULT_DEV_WIFI_CONN_FAIL = 1;
    public static int BLE_CONTROLLER_RESULT_DEV_WIFI_CONN_SUCCESS = 2;

    private BluetoothAdapter bluetoothAdapter;
    //private BLEDeviceListAdapter bleDeviceListAdapter = new BLEDeviceListAdapter();
    private boolean scanning = false;
    private long SCAN_PERIOD = 10000;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Context context;
    private Activity activity;

    private BluetoothLeScanner bluetoothLeScanner;

    private static final String CLIENT_CHARACTERISTIC_CONFIGURATION_DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805f9b34fb"; // From Bluetooth core spec, known as "Client Characteristic Configuration". Also listed in this handy github gist: https://gist.github.com/sam016/4abe921b5a9ee27f67b3686910293026

    private final String network_ssid;
    private final String network_psk;
    private final Buffer user_id;

    public BLEController(Context context, Activity activity, String network_ssid, String network_psk, Buffer user_id) {
        this.context = context;
        this.activity = activity;

        this.network_ssid = network_ssid;
        this.network_psk = network_psk;
        this.user_id = user_id;

        BluetoothManager bluetoothManager = (BluetoothManager) this.context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            //throw new RuntimeException("Device doesn't support Bluetooth");
//            new AlertDialog.Builder(context)
//                    .setTitle("Bluetooth not supported")
//                    .setMessage("Your device doesn't support Bluetooth, sorry!")
//                    .show();
            activity.setResult(BLE_CONTROLLER_RESULT_BT_NOT_SUPPORTED);
            activity.finish();
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            //throw new RuntimeException("Bluetooth is turned off");
//            new AlertDialog.Builder(context)
//                    .setTitle("Bluetooth turned off")
//                    .setMessage("Please turn Bluetooth on!")
//                    .show();
            activity.setResult(BLE_CONTROLLER_RESULT_BT_OFF);
            activity.finish();
            return;
        }

        scanForBLE();
    }

    public void scanForBLE() {
        this.bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        //if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
        //    System.out.println("ASKING FOR PERMISSION...");
        //    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_SCAN}, 1);
        //}
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("ASKING FOR ACCESS_FINE_LOCATION PERMISSION...");
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        boolean hasPermissions = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (hasPermissions) {
            if (!scanning) {
                // Stops scanning after a predefined scan period.
                handler.postDelayed(() -> {
                    scanning = false;
                    if (!hasPermissions) {
                        // Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        System.out.println("In postDelayed callback, but no permissions");
                        return;
                    }

                    if (!sauCameraDeviceFound) {
                        System.out.println("In postDelayed callback, stopping scan");
                        bluetoothLeScanner.stopScan(bleScanCallback);
                        System.out.println("SCANNING STOPPED by postDelayed callback");

                        System.out.println("No SAU camera device found");
                        // Notify the user that the camera device was not found
//                        new AlertDialog.Builder(context)
//                                .setTitle("Camera device not found")
//                                .setMessage("Is the camera turned on? Is it close enough? Are you sure you aren't trying to register an already registered camera? (it has bluetooth LE turned off then, need to unregister it first to turn it into registration mode).")
//                                .show();
                        activity.setResult(BLE_CONTROLLER_RESULT_DEV_NOT_FOUND);
                        activity.finish();

                    } else {
                        System.out.println("In postDelayed callback, but the camera device was found");
                    }

                }, SCAN_PERIOD);

                scanning = true;
                System.out.println("STARTING SCAN...");
                bluetoothLeScanner.startScan(bleScanCallback);
                System.out.println("SCANNING STARTED...");
            } else {
                scanning = false;
                bluetoothLeScanner.stopScan(bleScanCallback);
                System.out.println("SCANNING STOPPED...");
            }
        } else {
            System.out.println("No Permission - nothing to do");
        }

    }

    private boolean sauCameraDeviceFound = false;

    private ScanCallback bleScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            BluetoothDevice device = result.getDevice();
            try {
                String deviceName = device.getName();
                //String deviceAlias = device.getAlias();
                System.out.println("DISCOVERED NEW DEVICE: " + result.getDevice().toString() + ", name: " + deviceName);

                if (deviceName != null && deviceName.equals("SAU Camera Device")) {
                    sauCameraDeviceFound = true;
                    System.out.println("Found SAU camera device: " + device.toString());
                    bluetoothLeScanner.stopScan(bleScanCallback);
                    System.out.println("SCANNING STOPPED AS THE TARGET DEVICE WAS FOUND.");

                    setupDevice(device);
                }
            } catch (SecurityException e) {
                throw new RuntimeException(e);
            }

            //bleDeviceListAdapter.addDevice(result.getDevice());
        }
    };

    private static final String SAU_REGISTRATION_SERVICE_UUID = "9c65cb67-faa6-098a-c14d-1211a5051082";

    private static final String SAU_REGISTRATION_SERVICE_CHARACTERISTIC_UUID_WIFI_SSID = "c4cdddf0-bcf3-1a85-2348-bea2aa91c9d8";
    private static final String SAU_REGISTRATION_SERVICE_CHARACTERISTIC_UUID_WIFI_PSK = "4e44eb46-7080-9d83-ac4a-e9f1b82b13d8";
    private static final String SAU_REGISTRATION_SERVICE_CHARACTERISTIC_UUID_USER_ID = "b8b40435-be4f-55a5-a447-9c14ddcd20fc";


    private static final String SAU_REGISTRATION_SERVICE_CHARACTERISTIC_NOTIFIER_UUID_NETWORK_STATE = "dd0a6b3f-86d8-68a9-4141-3ed99c4c2ac7";


    private BluetoothGattService sauRegistrationService;

    private BluetoothGattCharacteristic userIdCharacteristic;
    private BluetoothGattCharacteristic wifiSsidCharacteristic;
    private BluetoothGattCharacteristic wifiPskCharacteristic;
    private BluetoothGattCharacteristic networkStateNotificationCharacteristic;

    /*
        CHARACTERISTICS WRITE FULFILLMENT STATES
        0 - no characteristics written
        1 - user id written
        2 - wifi ssid written
        3 - wifi psk written
     */
    private int state = 0;

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {


        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            System.out.println("onDescriptorWrite: descriptor uuid: " + descriptor.getUuid() + ", status: " + status);

            if (status == BluetoothGatt.GATT_SUCCESS && descriptor.getUuid().equals(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIGURATION_DESCRIPTOR_UUID))) {
                System.out.println("Writing userIdCharacteristic...");

                //if (this.writeCharacteristicCompat(gatt, userIdCharacteristic, "aaaaaaaabbbbbbbb".getBytes())) {
                if (this.writeCharacteristicCompat(gatt, userIdCharacteristic, user_id.getData())) {
                    System.out.println("Wrote userIdCharacteristic, waiting for onCharacteristicWrite to be called");

                    //System.out.println("The wifi ssid and psk are: " + BuildConfig.TEST_HARDCODED_WIFI_SSID + ", " + BuildConfig.TEST_HARDCODED_WIFI_PSK);

//                    System.out.println("Writing wifiSsidCharacteristic...");
//                    if (this.writeCharacteristicCompat(gatt, wifiSsidCharacteristic, BuildConfig.TEST_HARDCODED_WIFI_SSID.getBytes())) {
//                        System.out.println("Wrote wifiSsidCharacteristic");
//                        System.out.println("Writing wifiPskCharacteristic...");
//                        if (this.writeCharacteristicCompat(gatt, wifiPskCharacteristic, BuildConfig.TEST_HARDCODED_WIFI_PSK.getBytes())) {
//                            System.out.println("Wrote wifiPskCharacteristic");
//                        }
//                    } else {
//                        System.out.println("ERROR - failed to write wifiSsidCharacteristic");
//                    }
                } else {
                    System.out.println("ERROR - failed to write userIdCharacteristic"); // TODO handle this
                }
            } else {
                Log.e(TAG, "onDescriptorWrite - status: " + status);
            }
        }


        @Override
        public void onDescriptorRead(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattDescriptor descriptor, int status, @NonNull byte[] value) {
            System.out.println("ONDESCRIPTORREAD");
            super.onDescriptorRead(gatt, descriptor, status, value);

        }

        @Override
        public void onCharacteristicChanged(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] value) {
            System.out.println("ONCHARACTERISTICCHANGED (API level >= 33)");
            super.onCharacteristicChanged(gatt, characteristic, value);

            System.out.println("RECEIVED NOTIFICATION (api ver >= 33)");

            handleDeviceFeedbackNotification(value, gatt);
        }

        @Override
        public void onCharacteristicChanged (BluetoothGatt gatt,
                                             BluetoothGattCharacteristic characteristic) {
            System.out.println("ONCHARACTERISTICCHANGED (API level < 33)");
            super.onCharacteristicChanged(gatt, characteristic);

            System.out.println("RECEIVED NOTIFICATION (api ver < 33)");

            handleDeviceFeedbackNotification(characteristic.getValue(), gatt);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            System.out.println("onCharacteristicWrite: characteristic uuid: " + characteristic.getUuid() + ", status: " + status);

            switch (state) {
                case 0:
                    if (status == BluetoothGatt.GATT_SUCCESS && characteristic.getUuid().equals(UUID.fromString(SAU_REGISTRATION_SERVICE_CHARACTERISTIC_UUID_USER_ID))) {
                        state = 1;
                        System.out.println("Writing wifiSsidCharacteristic...");
                        //if (writeCharacteristicCompat(gatt, wifiSsidCharacteristic, BuildConfig.TEST_HARDCODED_WIFI_SSID.getBytes())) {
                        if (writeCharacteristicCompat(gatt, wifiSsidCharacteristic, network_ssid.getBytes())) {
                            System.out.println("Wrote wifiSsidCharacteristic, waiting for onCharacteristicWrite to be called");
                        } else {
                            System.out.println("ERROR - failed to write wifiSsidCharacteristic"); // TODO handle this
                        }
                    } else {
                        Log.e(TAG, "onCharacteristicWrite for user id - status: " + status);
                    }
                    break;
                case 1:
                    if (status == BluetoothGatt.GATT_SUCCESS && characteristic.getUuid().equals(UUID.fromString(SAU_REGISTRATION_SERVICE_CHARACTERISTIC_UUID_WIFI_SSID))) {
                        state = 2;
                        System.out.println("Writing wifiPskCharacteristic...");
                        //if (writeCharacteristicCompat(gatt, wifiPskCharacteristic, BuildConfig.TEST_HARDCODED_WIFI_PSK.getBytes())) {
                        if (writeCharacteristicCompat(gatt, wifiPskCharacteristic, network_psk.getBytes())) {
                            System.out.println("Wrote wifiPskCharacteristic, waiting for onCharacteristicWrite to be called");
                        } else {
                            System.out.println("ERROR - failed to write wifiPskCharacteristic"); // TODO handle this
                        }
                    } else {
                        Log.e(TAG, "onCharacteristicWrite for wifi ssid - status: " + status);
                    }
                    break;
                case 2:
                    if (status == BluetoothGatt.GATT_SUCCESS && characteristic.getUuid().equals(UUID.fromString(SAU_REGISTRATION_SERVICE_CHARACTERISTIC_UUID_WIFI_PSK))) {
                        state = 3;
                        System.out.println("All required characteristics were written and their onCharacteristicWrite were called with status 0");

//                        System.out.println("Sleeping for 60 seconds...");
//                        // Sleep for 60 seconds
//                        try {
//                            Thread.sleep(60000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        System.out.println("Woke up after 60 seconds sleep");
                    } else {
                        Log.e(TAG, "onCharacteristicWrite for wifi psk - status: " + status);
                    }
                    break;
                case 3:
                    throw new RuntimeException("Should not enter onCharacteristicWrite in state 3 (after wifi psk characteristic onCharacteristicWrite was already called with status 0)");
                default:
                    throw new RuntimeException("Invalid state: " + state);

            }
        }

        private boolean writeCharacteristicCompat(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, byte[] value) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    return BluetoothStatusCodes.SUCCESS == gatt.writeCharacteristic(characteristic, value, BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                } else {
                    // Backport using deprecated version of writeCharacteristic
                    if (USE_TEST_HARDCODED_CHARACTERISTICS) {
                        if (characteristic.equals(userIdCharacteristic)) {
                            value = "aaaaaaaabbbbbbbb".getBytes();
                        } else if (characteristic.equals(wifiSsidCharacteristic)) {
                            value = BuildConfig.TEST_HARDCODED_WIFI_SSID.getBytes();
                        } else if (characteristic.equals(wifiPskCharacteristic)) {
                            value = BuildConfig.TEST_HARDCODED_WIFI_PSK.getBytes();
                        } else {
                            throw new RuntimeException("Invalid characteristic: " + characteristic);
                        }
                    } else {
                        characteristic.setValue(value);
                    }
                    return gatt.writeCharacteristic(characteristic);
                }
            } catch (SecurityException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                System.out.println("GATT services discovered.");

                sauRegistrationService = gatt.getService(UUID.fromString(BLEController.SAU_REGISTRATION_SERVICE_UUID));

                if (sauRegistrationService == null) { // TODO I don't know why could this happen
                    handler.post(() -> {
                        activity.setResult(BLE_CONTROLLER_RESULT_ERROR_NULL_SERVICE);
                        activity.finish();
                    });
                    return;
                }

                userIdCharacteristic = sauRegistrationService.getCharacteristic(UUID.fromString(BLEController.SAU_REGISTRATION_SERVICE_CHARACTERISTIC_UUID_USER_ID));
                wifiSsidCharacteristic = sauRegistrationService.getCharacteristic(UUID.fromString(BLEController.SAU_REGISTRATION_SERVICE_CHARACTERISTIC_UUID_WIFI_SSID));
                wifiPskCharacteristic = sauRegistrationService.getCharacteristic(UUID.fromString(BLEController.SAU_REGISTRATION_SERVICE_CHARACTERISTIC_UUID_WIFI_PSK));

                networkStateNotificationCharacteristic = sauRegistrationService.getCharacteristic(UUID.fromString(BLEController.SAU_REGISTRATION_SERVICE_CHARACTERISTIC_NOTIFIER_UUID_NETWORK_STATE));
                //networkStateNotificationCharacteristic.describeContents()

                userIdCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE); // May be better defensively set as WRITE_TYPE_DEFAULT if needed, see below
                wifiSsidCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE); // May be better defensively set as WRITE_TYPE_DEFAULT if needed, see below
                wifiPskCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT); // WRITE_TYPE_NO_RESPONSE for some reason truncates
                                                                                        // characteristic values longer than 20 characters - not sure why
                                                                                      // (in Wireshark the ATT protocol MTU enforced splitting characteristic data into chunks of 18 bytes,
                                                                                      // however maybe the other times the MTU was greater and allowed a chunk of 20 bytes -
                                                                                      // if you want to know, we need to re-check with Wireshark and seek in the documentation of ATT protocol for more information)


                int props = networkStateNotificationCharacteristic.getProperties();
                System.out.println("networkStateNotificationCharacteristic properties: " + props);

                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        System.out.println("SDK >= 33");

                    } else {
                        System.out.println("SDK < 33");
                        //throw new RuntimeException("Unsupported SDK version (< 33) - possible backport using deprecated version of writeCharacteristic");
                    }

                    System.out.println("Enabling notifications for networkStateNotificationCharacteristic...");
                    if (gatt.setCharacteristicNotification(networkStateNotificationCharacteristic, true)) {
                        System.out.println("Enabled GATT notifications for networkStateNotificationCharacteristic");

                        // Set descriptor value to allow notification
                        //BluetoothGattDescriptor descriptor = networkStateNotificationCharacteristic.getDescriptor(networkStateNotificationCharacteristic.getUuid()); // Is this correct? - no, need to use CLIENT_CHARACTERISTIC_CONFIGURATION_DESCRIPTOR_UUID

                        System.out.println("Listing descriptors for networkStateNotificationCharacteristic...");
                        networkStateNotificationCharacteristic.getDescriptors().forEach((descriptor) -> {
                            System.out.println("Descriptor uuid: " + descriptor.getUuid());
                            System.out.println("Descriptor value: " + descriptor.getValue());
                            System.out.println("Permissions: " + descriptor.getPermissions());
                        });
                        System.out.println("Done listing descriptors for networkStateNotificationCharacteristic");

                        BluetoothGattDescriptor descriptor = networkStateNotificationCharacteristic.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIGURATION_DESCRIPTOR_UUID));
                        if (descriptor == null) {
                            throw new RuntimeException("Descriptor not found");
                        }
                        System.out.println("Setting descriptor value to ENABLE_NOTIFICATION_VALUE...");
                        if (descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)) { // TODO (would require changes in cam firmware - maybe it is not that important?): replace notification with indication? (I heard indication is akin to TCP while notification is akin to UDP. We wouldn't like to loose the notification - confirm this)
                            System.out.println("Done setting descriptor value to ENABLE_NOTIFICATION_VALUE");
                            System.out.println("Writing to the descriptor...");
                            if (gatt.writeDescriptor(descriptor)) {
                                System.out.println("Wrote to the descriptor, waiting for onDescriptorWrite to be called");
                            } else {
                                System.out.println("ERROR - failed to write to the descriptor"); // TODO handle this
                            }
                        } else {
                            System.out.println("ERROR - failed to set descriptor value to ENABLE_NOTIFICATION_VALUE"); // TODO handle this
                        }
                        // Writing to the first characteristic was moved to onDescriptorWrite

                    } else {
                        System.out.println("ERROR - failed to enable GATT notifications for networkStateNotificationCharacteristic");
                    }

                } catch (SecurityException e) {
                    throw new RuntimeException(e);
                }

            } else {
                Log.w(TAG, "onServicesDiscovered - status: " + status);
            }
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothGatt.STATE_CONNECTED) {
                System.out.println("CONNECTED TO GATT SERVER");
                try {
                    System.out.println("Starting remote GATT service discovery...");
                    if(gatt.discoverServices()) {
                        System.out.println("Started remote GATT service discovery");
                    } else {
                        Log.e(TAG, "ERROR - failed to start remote GATT service discovery");
                    }
                } catch (SecurityException e) {
                    throw new RuntimeException(e);
                }
            } else {
                System.out.println("DISCONNECTED FROM GATT SERVER");
            }
        }
    };

    /**
     * Configures the device to operate after registration.
     * This is done by connecting to the GATT server running on the device.
     * @param device The BLE device to configure
     */
    private void setupDevice(BluetoothDevice device) {
        System.out.println("In setupDevice");
        // Communicate with GATT server on the device
        try {
            System.out.println("Connecting to the GATT server...");
            BluetoothGatt gattConnection = device.connectGatt(this.context, false, this.gattCallback, BluetoothDevice.TRANSPORT_LE);
            //gattConnection.setPreferredPhy();
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleDeviceFeedbackNotification(byte[] data, BluetoothGatt gatt) {
        System.out.println("In handleDeviceFeedbackNotification");

        System.out.println("Data length: " + data.length + " byte"); // Should be 1 byte - 0x01 if WiFi network was connected successfully, 0x00 otherwise

        if (data.length != 1) {
            throw new RuntimeException("Invalid GATT notification - feedback data length: " + data.length + " bytes, while should be 1 byte - 0x01 if WiFi network was connected successfully, 0x00 otherwise");
        }

        switch (data[0]) {
            case 0x00:
                //Device failed to connect to the WiFi network
                Log.d(TAG, "Remote device failed to connect to the WiFi network");

                Log.d(TAG, "Disconnecting from GATT server");

                //ask user to try again
                handler.post(() -> {
//                    new AlertDialog.Builder(context)
//                        .setTitle("Camera device failed to connect Wi-Fi network")
//                        .setMessage("Try again? Possibly fix Wi-Fi SSID and/or password.")
//                        .show();
                    activity.setResult(BLE_CONTROLLER_RESULT_DEV_WIFI_CONN_FAIL);
                    activity.finish();
                });

                //Should we close the GATT connection here? - probably not, unless there's a good reason for that. It can be reused when the user retries to register the camera.
                //Although we will - it simplifies the implementation and prevents the camera device from being stuck in GATT connection state
                try {
                    gatt.disconnect();
                } catch (SecurityException e) {
                    throw new RuntimeException(e);
                }

                break;
            case 0x01:
                //Device connected to the WiFi network
                Log.d(TAG, "Remote device successfully connected to the WiFi network");

                // GATT connection should be closed by the peripheral after the WiFi network connection is successful

                //notify user that the connection was successful and registration will be soon completed with the server
                handler.post(() -> {
//                    new AlertDialog.Builder(context)
//                            .setTitle("Camera device connected to Wi-Fi network")
//                            .setMessage("Registration will be completed soon with the server. You can close the app and after you reopen it, the added camera should be visible.") // TODO save the user from needing to close and reopen the app
//                            .show();
                    activity.setResult(BLE_CONTROLLER_RESULT_DEV_WIFI_CONN_SUCCESS);
                    activity.finish();
                });
                break;
            default:
                throw new RuntimeException("Invalid GATT notification detected - feedback data contains an unsupported byte value other than 0x00 or 0x01: " + data[0]);
        }
    }
}
