package com.example.myapplication.monitordevices;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
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
 */
public class BLEController {
    static String TAG = "BLEController";
    private BluetoothAdapter bluetoothAdapter;
    //private BLEDeviceListAdapter bleDeviceListAdapter = new BLEDeviceListAdapter();
    private boolean scanning = false;
    private long SCAN_PERIOD = 10000;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Context context;
    private Activity activity;

    private BluetoothLeScanner bluetoothLeScanner;

    public BLEController(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        BluetoothManager bluetoothManager = (BluetoothManager) this.context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            //throw new RuntimeException("Device doesn't support Bluetooth");
            new AlertDialog.Builder(context)
                    .setTitle("Bluetooth not supported")
                    .setMessage("Your device doesn't support Bluetooth, sorry!")
                    .show();
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            //throw new RuntimeException("Bluetooth is turned off");
            new AlertDialog.Builder(context)
                    .setTitle("Bluetooth turned off")
                    .setMessage("Please turn Bluetooth on!")
                    .show();
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
                    System.out.println("In postDelayed callback, stopping scan");
                    bluetoothLeScanner.stopScan(bleScanCallback);
                    System.out.println("SCANNING STOPPED by postDelayed callback");
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
        public void onCharacteristicChanged(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] value) {
            super.onCharacteristicChanged(gatt, characteristic, value);

            System.out.println("RECEIVED NOTIFICATION: " + new String(value)); // TODO verify if this is correct
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
                        if (writeCharacteristicCompat(gatt, wifiSsidCharacteristic, BuildConfig.TEST_HARDCODED_WIFI_SSID.getBytes())) {
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
                        if (writeCharacteristicCompat(gatt, wifiPskCharacteristic, BuildConfig.TEST_HARDCODED_WIFI_PSK.getBytes())) {
                            System.out.println("Wrote wifiPskCharacteristic, waiting for onCharacteristicWrite to be called");
                        } else {
                            System.out.println("ERROR - failed to write wifiPskCharacteristic"); // TODO handle this
                        }
                    }
                    break;
                case 2:
                    if (status == BluetoothGatt.GATT_SUCCESS && characteristic.getUuid().equals(UUID.fromString(SAU_REGISTRATION_SERVICE_CHARACTERISTIC_UUID_WIFI_PSK))) {
                        state = 3;
                        System.out.println("All required characteristics were written and their onCharacteristicWrite were called with status 0");
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
                    characteristic.setValue(value);
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

                wifiSsidCharacteristic = sauRegistrationService.getCharacteristic(UUID.fromString(BLEController.SAU_REGISTRATION_SERVICE_CHARACTERISTIC_UUID_WIFI_SSID));
                wifiPskCharacteristic = sauRegistrationService.getCharacteristic(UUID.fromString(BLEController.SAU_REGISTRATION_SERVICE_CHARACTERISTIC_UUID_WIFI_PSK));
                userIdCharacteristic = sauRegistrationService.getCharacteristic(UUID.fromString(BLEController.SAU_REGISTRATION_SERVICE_CHARACTERISTIC_UUID_USER_ID));

                networkStateNotificationCharacteristic = sauRegistrationService.getCharacteristic(UUID.fromString(BLEController.SAU_REGISTRATION_SERVICE_CHARACTERISTIC_NOTIFIER_UUID_NETWORK_STATE));

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



                        System.out.println("Writing userIdCharacteristic...");
                        if (this.writeCharacteristicCompat(gatt, userIdCharacteristic, "bbbbbbbbaaaaaaaa".getBytes())) {
                            System.out.println("Wrote userIdCharacteristic, waiting for onCharacteristicWrite to be called");

//                            //System.out.println("The wifi ssid and psk are: " + BuildConfig.TEST_HARDCODED_WIFI_SSID + ", " + BuildConfig.TEST_HARDCODED_WIFI_PSK);
//                            System.out.println("Writing wifiSsidCharacteristic...");
//                            if (this.writeCharacteristicCompat(gatt, wifiSsidCharacteristic, BuildConfig.TEST_HARDCODED_WIFI_SSID.getBytes())) {
//                                System.out.println("Wrote wifiSsidCharacteristic");
//                                System.out.println("Writing wifiPskCharacteristic...");
//                                if (this.writeCharacteristicCompat(gatt, wifiPskCharacteristic, BuildConfig.TEST_HARDCODED_WIFI_PSK.getBytes())) {
//                                    System.out.println("Wrote wifiPskCharacteristic");
//                                }
//                            } else {
//                                System.out.println("ERROR - failed to write wifiSsidCharacteristic");
//                            }
                        } else {
                            System.out.println("ERROR - failed to write userIdCharacteristic"); // TODO handle this
                        }

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
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        }
    }
}
