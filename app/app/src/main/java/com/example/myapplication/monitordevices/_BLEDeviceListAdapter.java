//package com.example.myapplication.monitordevices;
//
//import java.util.ArrayList;
//
//import android.bluetooth.BluetoothDevice;
//
//public class BLEDeviceListAdapter {
//
//    // Adapter for holding devices found through scanning.
//
//    private ArrayList<BluetoothDevice> mBleDevices;
//
//    public BLEDeviceListAdapter() {
//        super();
//        mBleDevices = new ArrayList<BluetoothDevice>();
//    }
//
//    public void addDevice(BluetoothDevice device) {
//        if (!mBleDevices.contains(device)) {
//            mBleDevices.add(device);
//        }
//    }
//
//    public BluetoothDevice getDevice(int position) {
//        return mBleDevices.get(position);
//    }
//
//    public void clear() {
//        mBleDevices.clear();
//    }
//
//    public int getCount() {
//        return mBleDevices.size();
//    }
//
//    public Object getItem(int i) {
//        return mBleDevices.get(i);
//    }
//
//    public long getItemId(int i) {
//        return i;
//    }
//}
