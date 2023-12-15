package com.example.griptrainerapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import androidx.core.content.ContextCompat;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import android.Manifest;

public class BluetoothLEManager {
    private final BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;
    private final BluetoothLeScanner bluetoothLeScanner;
    private final Context context;

    private static final UUID UART_SERVICE_UUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
    private static final UUID RX_CHAR_UUID = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");
    private static final UUID TX_CHAR_UUID = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");

    public interface BluetoothDataListener {
        void onDataReceived(String data);
    }

    private BluetoothDataListener dataListener;

    public BluetoothLEManager(Context context) {
        this.context = context;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
    }

    public void setDataListener(BluetoothDataListener listener) {
        this.dataListener = listener;
    }

    public void startScanning() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
                || (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            bluetoothLeScanner.startScan(scanCallback);
        } else {
            Log.e("BluetoothLEManager", "Missing permission for scanning");
        }
    }


    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();

            // Check permissions before accessing device information
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
                    || (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {

                if (device.getName() != null && device.getName().equals("GripStar")) {
                    connectToDevice(device);
                    bluetoothLeScanner.stopScan(this);
                }
                else{
                    Log.e("Device", "Device can not be found");
                }

            } else {
                Log.e("BluetoothLEManager", "Missing permission to access Bluetooth device information");
            }
        }
    };

    private void connectToDevice(BluetoothDevice device) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            bluetoothGatt = device.connectGatt(context, false, gattCallback);
        } else {
            Log.e("BluetoothLEManager", "Missing permission to connect to BLE device");
        }
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothGatt.STATE_CONNECTED) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                    gatt.discoverServices();
                } else {
                    Log.e("BluetoothLEManager", "Missing permission to perform service discovery");
                }
            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                Log.e("BluetoothLEManager", "Device disconnected");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            // Discover services and characteristics
            // Subscribe to notifications if needed
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (characteristic.getUuid().equals(TX_CHAR_UUID)) {
                byte[] data = characteristic.getValue();
                String dataStr = new String(data, StandardCharsets.UTF_8);
                if (dataListener != null) {
                    dataListener.onDataReceived(dataStr);
                }
            }
        }
    };

    public void sendData(byte[] data) {
        if (bluetoothGatt != null) {
            // Check permission before attempting to write to a characteristic
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                BluetoothGattService service = bluetoothGatt.getService(UART_SERVICE_UUID);
                if (service != null) {
                    BluetoothGattCharacteristic characteristic = service.getCharacteristic(RX_CHAR_UUID);
                    if (characteristic != null) {
                        characteristic.setValue(data);
                        bluetoothGatt.writeCharacteristic(characteristic);
                    }
                }
            } else {
                Log.e("BluetoothLEManager", "Missing permission to send data to BLE device");
                // Optionally, handle the lack of permission more gracefully,
                // e.g., notify the user or the rest of the application.
            }
        }
    }

    public void disconnect() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            if (bluetoothGatt != null) {
                bluetoothGatt.close();
                bluetoothGatt = null;
            }
        } else {
            Log.e("BluetoothLEManager", "Missing permission to disconnect");
            // Optionally, you can request the missing permissions from the user here.
        }
    }
}
