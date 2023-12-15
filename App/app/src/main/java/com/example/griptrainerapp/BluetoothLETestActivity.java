package com.example.griptrainerapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;
import androidx.annotation.NonNull;

public class BluetoothLETestActivity extends AppCompatActivity implements BluetoothLEManager.BluetoothDataListener {
    private static final int REQUEST_CODE_BLUETOOTH_PERMISSIONS = 1;
    private BluetoothLEManager bluetoothLEManager;
    private TextView textViewReceive;
    private EditText editTextSend;

    private void showToast(String message) {
        Toast.makeText(BluetoothLETestActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_le_test);

        editTextSend = findViewById(R.id.editTextSend);
        Button buttonSend = findViewById(R.id.buttonSend);
        textViewReceive = findViewById(R.id.textViewReceive);
        Button connectButton = findViewById(R.id.buttonConnect);

        bluetoothLEManager = new BluetoothLEManager(this);
        bluetoothLEManager.setDataListener(this); // Set the data listener to this activity
        checkBluetoothPermissions();

        connectButton.setOnClickListener(view -> {
            bluetoothLEManager.startScanning();
            showToast("Scanning for BLE devices...");
        });

        buttonSend.setOnClickListener(view -> {
            String dataToSend = editTextSend.getText().toString();
            if (!dataToSend.isEmpty()) {
                bluetoothLEManager.sendData(dataToSend.getBytes());
                showToast("Data sent");
            } else {
                showToast("Please enter some data to send");
            }
        });
    }

    private void checkBluetoothPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_BLUETOOTH_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_BLUETOOTH_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted, start Bluetooth operations
                bluetoothLEManager.startScanning();
                showToast("Permissions granted. Starting Bluetooth operations.");
            } else {
                // Permissions denied, inform the user and possibly close the activity or disable certain features
                showToast("Bluetooth permissions are required for this app to function properly.");
            }
        }
    }

    @Override
    public void onDataReceived(String data) {
        runOnUiThread(() -> updateReceivedData(data));
    }

    public void updateReceivedData(String data) {
        textViewReceive.setText(data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothLEManager != null) {
            bluetoothLEManager.disconnect();
        }
    }
}

