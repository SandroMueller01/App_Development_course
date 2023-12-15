package com.example.griptrainerapp;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;
import android.os.Build;

public class BluetoothManager {
    private final BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private DataListener dataListener;

    private static final int REQUEST_BLUETOOTH_CONNECT = 1;
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public interface DataListener {
        void onDataReceived(String data);
    }

    public BluetoothManager() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void setDataListener(DataListener listener) {
        this.dataListener = listener;
    }

    public void startDataListening() {
        new Thread(() -> {
            if (bluetoothSocket != null && bluetoothSocket.isConnected()) {
                try {
                    InputStream inputStream = bluetoothSocket.getInputStream();
                    byte[] buffer = new byte[1024];
                    int bytes;

                    while (true) {
                        bytes = inputStream.read(buffer);
                        if (bytes > 0) {
                            final String receivedData = new String(buffer, 0, bytes);
                            if (dataListener != null) {
                                dataListener.onDataReceived(receivedData);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public boolean connectToDevice(String deviceName, Activity activity) {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT);
                return false;
            }
        }

        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
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
        if (bluetoothSocket != null && bluetoothSocket.isConnected()) {
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
