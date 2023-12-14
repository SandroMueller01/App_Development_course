package com.example.griptrainerapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View; // Import View
import androidx.appcompat.app.AppCompatActivity;
import android.bluetooth.BluetoothAdapter;






public class BluetoothTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_test);

        // Initialize your views here
        EditText editTextSend = findViewById(R.id.editTextSend);
        Button buttonSend = findViewById(R.id.buttonSend);
        TextView textViewReceive = findViewById(R.id.textViewReceive);
        Button connectButton = findViewById(R.id.buttonConnect);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Implement your Bluetooth connection logic here
                // This is where you'll establish a connection to the ESP32 device
                // You can show a dialog or perform other actions as needed
            }
        });
    }
}
