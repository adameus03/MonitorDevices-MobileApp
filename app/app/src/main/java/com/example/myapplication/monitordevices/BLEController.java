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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.UUID;


public class BLEController {
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

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onCharacteristicChanged(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] value) {
            super.onCharacteristicChanged(gatt, characteristic, value);

            System.out.println("RECEIVED NOTIFICATION: " + new String(value)); // TODO verify if this is correct
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothGatt.STATE_CONNECTED) {
                System.out.println("CONNECTED TO GATT SERVER");

                BluetoothGattService sauRegistrationService = gatt.getService(UUID.fromString(BLEController.SAU_REGISTRATION_SERVICE_UUID));

                BluetoothGattCharacteristic wifiSsidCharacteristic = sauRegistrationService.getCharacteristic(UUID.fromString(BLEController.SAU_REGISTRATION_SERVICE_CHARACTERISTIC_UUID_WIFI_SSID));
                BluetoothGattCharacteristic wifiPskCharacteristic = sauRegistrationService.getCharacteristic(UUID.fromString(BLEController.SAU_REGISTRATION_SERVICE_CHARACTERISTIC_UUID_WIFI_PSK));
                BluetoothGattCharacteristic userIdCharacteristic = sauRegistrationService.getCharacteristic(UUID.fromString(BLEController.SAU_REGISTRATION_SERVICE_CHARACTERISTIC_UUID_USER_ID));

                BluetoothGattCharacteristic networkStateNotificationCharacteristic = sauRegistrationService.getCharacteristic(UUID.fromString(BLEController.SAU_REGISTRATION_SERVICE_CHARACTERISTIC_NOTIFIER_UUID_NETWORK_STATE));

                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        System.out.println("SDK >= 33");

                        System.out.println("Enabling notifications for networkStateNotificationCharacteristic...");
                        if (gatt.setCharacteristicNotification(networkStateNotificationCharacteristic, true)) {
                            System.out.println("Enabled GATT notifications for networkStateNotificationCharacteristic");

                            System.out.println("Writing wifiSsidCharacteristic...");
                            if (BluetoothStatusCodes.SUCCESS == gatt.writeCharacteristic(userIdCharacteristic, "bbbbbbbbaaaaaaaa".getBytes(), BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)) {
                                System.out.println("Wrote userIdCharacteristic");
                                System.out.println("Writing wifiSsidCharacteristic...");
                                if (BluetoothStatusCodes.SUCCESS == gatt.writeCharacteristic(wifiSsidCharacteristic, BuildConfig.TEST_HARDCODED_WIFI_SSID.getBytes(), BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)) {
                                    System.out.println("Wrote wifiSsidCharacteristic");
                                    System.out.println("Writing wifiPskCharacteristic...");
                                    if (BluetoothStatusCodes.SUCCESS == gatt.writeCharacteristic(wifiPskCharacteristic, BuildConfig.TEST_HARDCODED_WIFI_PSK.getBytes(), BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)) {
                                        System.out.println("Wrote wifiPskCharacteristic");
                                    }
                                } else {
                                    System.out.println("ERROR - failed to write wifiSsidCharacteristic");
                                }
                            } else {
                                System.out.println("ERROR - failed to write userIdCharacteristic");
                            }

                        } else {
                            System.out.println("ERROR - failed to enable GATT notifications for networkStateNotificationCharacteristic");
                        }

                    } else {
                        System.out.println("SDK < 33");
                        throw new RuntimeException("Unsupported SDK version (< 33) - possible backport using deprecated version of writeCharacteristic");
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
