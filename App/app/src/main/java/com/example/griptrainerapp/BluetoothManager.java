package com.example.griptrainerapp;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;


public class BluetoothManager {
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private Context context;

    private static final int REQUEST_BLUETOOTH_CONNECT = 1;


    // UUID for the Serial Port Profile (SPP)
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public BluetoothManager(Context context) {
        this.context = context;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public boolean isBluetoothAvailable() {
        return bluetoothAdapter != null;
    }

    public boolean isBluetoothEnabled() {
        return bluetoothAdapter.isEnabled();
    }

    public boolean connectToDevice(String deviceName, Activity activity) {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            return false;
        }

        // Check if BLUETOOTH_CONNECT permission is granted
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted, proceed with the operation that requires this permission
            // ...
        } else {
            // Permission is not granted, request it from the user
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT);
            return false;
        }

        // Get a list of bonded devices
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();

        // Iterate through the bonded devices and look for the one with the matching name
        for (BluetoothDevice device : bondedDevices) {
            if (device.getName().equals(deviceName)) {
                try {
                    bluetoothSocket = device.createRfcommSocketToServiceRecord(SPP_UUID);
                    bluetoothSocket.connect();
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }

        // Device with the specified name not found
        return false;
    }


    public void disconnect() {
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean sendData(byte[] data) {
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.getOutputStream().write(data);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
}
