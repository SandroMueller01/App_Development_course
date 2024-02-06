package com.example.griptrainerapp.LandingPages;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.griptrainerapp.BluetoothLowEnergy.BluetoothLEService;
import com.example.griptrainerapp.BluetoothLowEnergy.PermissionsUtil;
import com.example.griptrainerapp.R;

public class LandingActivity extends AppCompatActivity implements BluetoothLEService.BluetoothServiceListener {
    private ImageView bluetoothIcon;
    private boolean isBluetoothConnected = false;
    private BluetoothLEService bluetoothService;
    private boolean isBound = false;
    private boolean deviceReady = true;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BluetoothLEService.LocalBinder binder = (BluetoothLEService.LocalBinder) service;
            bluetoothService = binder.getService();
            bluetoothService.registerListener(LandingActivity.this);
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bluetoothService = null;
            isBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_options);

        initializeUIComponents();

        if (!PermissionsUtil.hasBluetoothPermissions(this)) {
            PermissionsUtil.requestBluetoothPermissions(this);
        } else {
            Log.d("BluetoothInterfaceActivity", "Bluetooth permission was granted");
        }

        Intent intent = new Intent(this, BluetoothLEService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    private void initializeUIComponents() {
        bluetoothIcon = findViewById(R.id.bluetoothIcon);
        Button startTrainingButton = findViewById(R.id.startTrainingConfigButton);
        Button manualTrainingButton = findViewById(R.id.manuelTrainingButton);

        startTrainingButton.setEnabled(false);
        manualTrainingButton.setEnabled(false);

        View.OnClickListener buttonClickListener = v -> {
            if (!deviceReady) {
                Toast.makeText(LandingActivity.this, "Please connect with BLE, press on the bluetooth icon", Toast.LENGTH_SHORT).show();
            } else {
                // Proceed with the action
                if (v.getId() == R.id.startTrainingConfigButton) {
                    navigateTo(TrainingsConfigActivity.class);
                } else if (v.getId() == R.id.manuelTrainingButton) {
                    navigateTo(ManuelActivity.class);
                }
            }
        };

        startTrainingButton.setOnClickListener(buttonClickListener);
        manualTrainingButton.setOnClickListener(buttonClickListener);

        Button trainingHistoryButton = findViewById(R.id.trainingHistoryButton);
        Button backButton = findViewById(R.id.backButton);
        Button closeButton = findViewById(R.id.closeButton);

        trainingHistoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(LandingActivity.this, HistoryTrainingActivity.class);
            intent.putExtra("DeviceReady", deviceReady); // Pass the deviceReady state
            startActivity(intent);
        });

        backButton.setOnClickListener(v -> finish());
        closeButton.setOnClickListener(v -> finishAffinity());

        bluetoothIcon.setOnClickListener(v -> {
            if (!isBluetoothConnected) {
                connectToDevice();
            } else {
                Toast.makeText(this,"BluetoothConnection lost", Toast.LENGTH_SHORT).show();
            }
        });
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
    private void navigateTo(Class<?> targetActivity) {
        Intent intent = new Intent(LandingActivity.this, targetActivity);
        startActivity(intent);
    }


    private void connectToDevice() {
        if (isBound) {
            String deviceAddress = getDeviceAddress();
            boolean attemptingToConnect = bluetoothService.connect(deviceAddress);

            if (!attemptingToConnect) {
                Toast.makeText(this, "Failed to start connection attempt", Toast.LENGTH_SHORT).show();
            }else{
                bluetoothIcon.setColorFilter(ContextCompat.getColor(LandingActivity.this, R.color.lime));

                deviceReady = true;

                Button startTrainingButton = findViewById(R.id.startTrainingConfigButton);
                Button manualTrainingButton = findViewById(R.id.manuelTrainingButton);

                // Enable the buttons
                startTrainingButton.setEnabled(true);
                manualTrainingButton.setEnabled(true);
            }

        } else {
            Toast.makeText(this, "Bluetooth service not bound", Toast.LENGTH_SHORT).show();
        }
    }

    private String getDeviceAddress() {
        // Return the address of the device you want to connect to
        return "7C:9E:BD:66:4C:26";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            bluetoothService.unregisterListener(this);
            unbindService(serviceConnection);
            isBound = false;
            bluetoothIcon.setColorFilter(ContextCompat.getColor(LandingActivity.this, R.color.white));
        }
    }
    @Override
    public void onConnectionStatusChanged(boolean connected) {
        runOnUiThread(() -> {
            if (connected) {
                // Connection established - enable buttons
                findViewById(R.id.startTrainingConfigButton).setEnabled(true);
                findViewById(R.id.manuelTrainingButton).setEnabled(true);
                bluetoothIcon.setColorFilter(ContextCompat.getColor(LandingActivity.this, R.color.lime));
            } else {
                // Connection lost - disable buttons
                findViewById(R.id.startTrainingConfigButton).setEnabled(false);
                findViewById(R.id.manuelTrainingButton).setEnabled(false);
                bluetoothIcon.setColorFilter(ContextCompat.getColor(LandingActivity.this, R.color.white));
                Toast.makeText(LandingActivity.this, "Bluetooth connection lost", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onDataReceived(String data) {
        //Handle start message from ESP32

    }

}
