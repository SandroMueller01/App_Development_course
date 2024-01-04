package com.example.griptrainerapp.BluetoothLowEnergy;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.griptrainerapp.R;

import java.util.ArrayList;


public class BluetoothInterfaceActivity extends AppCompatActivity implements BluetoothLEService.BluetoothServiceListener {
    private Button buttonConnect, buttonSend, buttonDiscoverDevices;
    private EditText editTextSend;
    private TextView textViewReceive;

    private ArrayAdapter<String> deviceArrayAdapter;
    private final ArrayList<String> deviceList = new ArrayList<>();
    private BluetoothLEService bluetoothService;
    private boolean isBound = false;

    private final BroadcastReceiver deviceDiscoveryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("ACTION_DEVICE_FOUND".equals(intent.getAction())) {
                String deviceName = intent.getStringExtra("DEVICE_NAME");
                String deviceAddress = intent.getStringExtra("DEVICE_ADDRESS");
                String deviceInfo = deviceName + " - " + deviceAddress;
                deviceList.add(deviceInfo);
                deviceArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_le_test);

        // Initialize UI elements
        buttonConnect = findViewById(R.id.buttonConnect);
        buttonSend = findViewById(R.id.buttonSend);
        buttonDiscoverDevices = findViewById(R.id.buttonDiscoverDevices);
        editTextSend = findViewById(R.id.editTextSend);
        textViewReceive = findViewById(R.id.textViewReceive);
        ListView deviceListView = findViewById(R.id.deviceListView);
        deviceArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, deviceList);
        deviceListView.setAdapter(deviceArrayAdapter);

        // Check and request Bluetooth permissions
        if (!PermissionsUtil.hasBluetoothPermissions(this)) {
            PermissionsUtil.requestBluetoothPermissions(this);
        } else {
            Log.d("BluetoothInterfaceActivity", "Bluetooth permission was granted");
        }

        // Set up button listeners
        setupButtonListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("ACTION_DEVICE_FOUND");
        registerReceiver(deviceDiscoveryReceiver, filter);
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(deviceDiscoveryReceiver);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionsUtil.REQUEST_BLUETOOTH_PERMISSIONS) {
            if (PermissionsUtil.hasBluetoothPermissions(this)) {
                Log.d("BluetoothInterfaceActivity", "Permissions are all granted");
            } else {
                Log.e("BluetoothInterfaceActivity", "Bluetooth permission was denied");
                Toast.makeText(this, "Bluetooth permissions are required for this feature", Toast.LENGTH_LONG).show();
            }
        }
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            BluetoothLEService.LocalBinder binder = (BluetoothLEService.LocalBinder) service;
            bluetoothService = binder.getService();
            bluetoothService.registerListener(BluetoothInterfaceActivity.this);
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
            bluetoothService = null;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, BluetoothLEService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isBound) {
            bluetoothService.unregisterListener(this);
            unbindService(serviceConnection);
            isBound = false;
        }
    }

    private void setupButtonListeners() {
        buttonConnect.setOnClickListener(v -> {
            if (isBound) {
                String deviceAddress = getDeviceAddress();
                if (bluetoothService != null) {
                    bluetoothService.connect(deviceAddress);
                    Log.d("BluetoothInterfaceActivity","Trying to connect to device");
                }
            } else {
                Toast.makeText(BluetoothInterfaceActivity.this, "Service not bound", Toast.LENGTH_SHORT).show();
            }
        });

        buttonSend.setOnClickListener(v -> {
            String message = editTextSend.getText().toString();
            if (isBound && bluetoothService != null) {
                bluetoothService.sendData(message);
            }
        });

        buttonDiscoverDevices.setOnClickListener(v -> {
            if (isBound && bluetoothService != null) {
                bluetoothService.discoverDevices();
            }
            else{
                Toast.makeText(BluetoothInterfaceActivity.this, "Bluetooth service not bound", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDataReceived(String data) {
        runOnUiThread(() -> textViewReceive.setText(data));
    }

    private String getDeviceAddress() {
        // Return the address of the device you want to connect to
        return "08:3A:8D:90:3B:DA";
    }
}
