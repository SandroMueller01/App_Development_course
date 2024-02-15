package com.example.griptrainerapp.BluetoothLowEnergy;

import android.app.Service;
import android.bluetooth.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class BluetoothLEService extends Service {
    private final IBinder binder = new LocalBinder();
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;
    private final Handler handler = new Handler();
    private static final long SCAN_PERIOD = 10000; // 10 seconds
    private static final UUID ESP32_SERVICE_UUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
    private static final UUID ESP32_TX_CHARACTERISTIC_UUID = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");
    private static final UUID ESP32_RX_CHARACTERISTIC_UUID = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");


    public class LocalBinder extends Binder {
        public BluetoothLEService getService() {
            return BluetoothLEService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initialize();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    // Initialize Bluetooth adapter
    public boolean initialize() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager == null) {
            return false;
        }
        bluetoothAdapter = bluetoothManager.getAdapter();
        return bluetoothAdapter != null;
    }

    // Method to connect to a BLE device
    public boolean connect(final String address) {
        Log.d("BluetoothLEService", "Trying to connect to device");
        if (bluetoothAdapter == null || address == null) {
            Log.e("BluetoothLEService", "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Reconnect if already connected
        if (bluetoothGatt != null) {
            if (bluetoothGatt.getDevice().getAddress().equals(address)) {
                Log.d("BluetoothLEService", "Trying to use an existing BluetoothGatt for connection.");
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("BluetoothLEService", "Trying to create a new connection.");
                }
                return bluetoothGatt.connect();
            } else {
                bluetoothGatt.close();
                bluetoothGatt = null;
            }
        }

        final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w("BluetoothLEService", "Device not found. Unable to connect.");
            return false;
        }

        // Connect to the GATT server on the device
        bluetoothGatt = device.connectGatt(this, false, gattCallback);
        Log.d("BluetoothLEService", "Trying to create a new connection.");
        return true;
    }

    // Method to disconnect from a BLE device

    // Method to read a characteristic from the connected device

    // Method to close the GATT client
    public void close() {
        if (bluetoothGatt == null) {
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.d("BluetoothLEService", "Trying to create a new connection.");
        }
        bluetoothGatt.close();
        bluetoothGatt = null;
    }

    // Discover devices using BluetoothAdapter
    public void discoverDevices() {
        if (bluetoothAdapter == null) {
            Log.w("BluetoothLEService", "BluetoothAdapter not initialized");
            return;
        }

        // Check for permissions if needed
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // Handle the lack of Bluetooth scan permission
            return;
        }

        // Start Scanning
        boolean isStarted = bluetoothAdapter.startDiscovery();
        Log.d("BluetoothLEService", "Discovering devices: " + isStarted);
        // Optionally, set a timeout for the scan
        handler.postDelayed(this::stopDiscovery, SCAN_PERIOD);
    }


    public void stopDiscovery() {
        // Unregister broadcast receiver
        unregisterReceiver(receiver);
        Log.d("BluetoothLEService", "Stop discovering");
        // Cancel discovery
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            Log.e("BluetoothLEService", "Bluetooth_Connect no permission granted");
        }
        bluetoothAdapter.cancelDiscovery();
    }

    // BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (ActivityCompat.checkSelfPermission(BluetoothLEService.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    Log.e("BluetoothLEService", "Bluetooth_Connect no permission granted");
                }
                assert device != null;
                String deviceName = device.getName();
                String deviceAddress = device.getAddress();

                Log.d("BluetoothLEService", "Discovered device: Name=" + deviceName + ", Address=" + deviceAddress);

                // Broadcasting the device information
                Intent deviceIntent = new Intent("ACTION_DEVICE_FOUND");
                deviceIntent.putExtra("DEVICE_NAME", deviceName);
                deviceIntent.putExtra("DEVICE_ADDRESS", deviceAddress);
                sendBroadcast(deviceIntent);
            }
        }
    };
    private final List<BluetoothServiceListener> listeners = new ArrayList<>();

    public void registerListener(BluetoothServiceListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void unregisterListener(BluetoothServiceListener listener) {
        listeners.remove(listener);
    }

    public interface BluetoothServiceListener {
        void onDataReceived(String data);
        void onConnectionStatusChanged(boolean connected);

    }

    public void sendData(String data) {
        if (bluetoothGatt != null) {
            BluetoothGattService service = bluetoothGatt.getService(ESP32_SERVICE_UUID);
            if (service != null) {
                BluetoothGattCharacteristic characteristic = service.getCharacteristic(ESP32_RX_CHARACTERISTIC_UUID);
                if (characteristic != null) {
                    characteristic.setValue(data.getBytes());
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        Log.e("BluetoothLEService", "Error in permission regarding sending data");
                    }
                    bluetoothGatt.writeCharacteristic(characteristic);
                }
            }
        }
    }


    // Implement a BluetoothGattCallback to handle GATT events
    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i("BluetoothLEService", "Connected to GATT server.");
                notifyConnectionStatusChanged(true);
                // Discover services after successful connection
                if (ActivityCompat.checkSelfPermission(BluetoothLEService.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("BluetoothLEService", "Trying to create a new connection.");
                }
                bluetoothGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                notifyConnectionStatusChanged(false);
                Log.i("BluetoothLEService", "Disconnected from GATT server.");
                close();
            }
        }

        private void notifyConnectionStatusChanged(boolean connected) {
            for (BluetoothServiceListener listener : listeners) {
                listener.onConnectionStatusChanged(connected);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                for (BluetoothGattService service : gatt.getServices()) {
                    if (ESP32_SERVICE_UUID.equals(service.getUuid())) {
                        for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                            if (ESP32_TX_CHARACTERISTIC_UUID.equals(characteristic.getUuid())) {
                                Log.d("BluetoothLEService", "service was discovered");
                            }
                        }
                    }
                }
                setCharacteristicNotification();
            } else {
                Log.w("BluetoothLEService", "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                byte[] data = characteristic.getValue();
                Log.d("BluetoothLEService","Characteristic read" + Arrays.toString(data));
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i("BluetoothLEService", "Characteristic write successful");
            }
        }

        public void setCharacteristicNotification() {
            if (bluetoothGatt != null) {
                BluetoothGattService service = bluetoothGatt.getService(ESP32_SERVICE_UUID);
                if (service != null) {
                    BluetoothGattCharacteristic characteristic = service.getCharacteristic(ESP32_TX_CHARACTERISTIC_UUID);
                    if (characteristic != null) {
                        if (ActivityCompat.checkSelfPermission(BluetoothLEService.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            Log.e("BluetoothLEService", "Permission not granted for Bluetooth_Connect");
                        }
                        bluetoothGatt.setCharacteristicNotification(characteristic, true);
                        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                                UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
                        if (descriptor != null) {
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            bluetoothGatt.writeDescriptor(descriptor);
                        }
                    }
                }
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            // Check if this is the correct characteristic
            if (ESP32_TX_CHARACTERISTIC_UUID.equals(characteristic.getUuid())) {
                byte[] data = characteristic.getValue();
                String dataStr = new String(data);
                Log.d("BluetoothLEService", "Data received from ESP32: " + dataStr);

                // Notify listeners or handle data as needed
                notifyDataReceived(dataStr);
            }
        }
        private void notifyDataReceived(String data) {
            for (BluetoothServiceListener listener : listeners) {
                listener.onDataReceived(data);
            }
        }

    };
}
