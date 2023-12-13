package com.example.griptrainerapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class BluetoothTestActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_test);

        // Initialize your views here
        EditText editTextSend = findViewById(R.id.editTextSend);
        Button buttonSend = findViewById(R.id.buttonSend);
        TextView textViewReceive = findViewById(R.id.textViewReceive);

        // Add action listeners and Bluetooth handling code
    }

}
