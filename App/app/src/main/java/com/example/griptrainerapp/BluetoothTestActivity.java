package com.example.griptrainerapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

public class BluetoothTestActivity extends AppCompatActivity implements BluetoothManager.DataListener {
    private BluetoothManager bluetoothManager;
    private TextView textViewReceive;

    private void showToast(String message) {
        Toast.makeText(BluetoothTestActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_le_test);

        EditText editTextSend = findViewById(R.id.editTextSend);
        Button buttonSend = findViewById(R.id.buttonSend);
        textViewReceive = findViewById(R.id.textViewReceive);
        Button connectButton = findViewById(R.id.buttonConnect);

        bluetoothManager = new BluetoothManager();
        bluetoothManager.setDataListener(this);
        bluetoothManager.startDataListening();

        connectButton.setOnClickListener(view -> {
            String deviceName = "GripStar";
            boolean isConnected = bluetoothManager.connectToDevice(deviceName, BluetoothTestActivity.this);

            if (isConnected) {
                showToast("Connection successful");
            } else {
                showToast("Connection failed");
            }
        });

        buttonSend.setOnClickListener(view -> {
            if (bluetoothManager != null) {
                String dataToSend = editTextSend.getText().toString();

                if (!dataToSend.isEmpty()) {
                    if (bluetoothManager.sendData(dataToSend.getBytes())) {
                        showToast("Data send successful");
                    } else {
                        showToast("Data wasn't send successful");
                    }
                } else {
                    showToast("Please enter some data to send");
                }
            }
        });
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
        if (bluetoothManager != null) {
            bluetoothManager.disconnect();
        }
    }
}
