package com.example.griptrainerapp.LandingPages;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.griptrainerapp.BluetoothLowEnergy.BluetoothLEService;
import com.example.griptrainerapp.R;
import com.example.griptrainerapp.TrainingActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TrainingsConfigActivity extends AppCompatActivity implements BluetoothLEService.BluetoothServiceListener{
        private EditText lengthInput, stepsInput;
        private ArrayAdapter<String> adapter;
        private List<String> itemList;

        private BluetoothLEService bluetoothService;
        private boolean isServiceBound = false;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_start_training_config);

                Button addBlockButton = findViewById(R.id.addBlockButton);
                Button backButton = findViewById(R.id.backButton);
                backButton.setOnClickListener(v -> {
                        // This will close the current activity and take you back to the previous activity
                        finish();
                });
                Button sendButton = findViewById(R.id.sendButton);
                sendButton.setOnClickListener(v -> {
                        if (isServiceBound && bluetoothService != null) {
                                bluetoothService.sendData("Config Steering");
                                Toast.makeText(TrainingsConfigActivity.this, "Config Steering command sent", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(TrainingsConfigActivity.this, TrainingActivity.class);
                                intent.putStringArrayListExtra("trainingInstructions", new ArrayList<>(itemList));
                                intent.putExtra("isConfiguredTraining", true);
                                intent.putExtra("configSource", "TrainingsConfigActivity"); // Set the source flag
                                startActivity(intent);
                        } else {
                                Toast.makeText(TrainingsConfigActivity.this, "Bluetooth not connected", Toast.LENGTH_SHORT).show();
                        }
                });


                // Added minusBlockButton
                Button minusBlockButton = findViewById(R.id.minusBlockButton); // Initialize minusBlockButton
                lengthInput = findViewById(R.id.lengthInput);
                stepsInput = findViewById(R.id.stepsInput);
                ListView listView = findViewById(R.id.Listoftraining);

                itemList = new ArrayList<>();
                adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, itemList) {
                        @NonNull
                        @Override
                        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                TextView textView = view.findViewById(android.R.id.text1);

                                // Change the item text color to white
                                textView.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));

                                return view;
                        }
                };



                listView.setAdapter(adapter);

                addBlockButton.setOnClickListener(v -> addItemToList());

                minusBlockButton.setOnClickListener(v -> removeLastItem());

                listView.setOnItemClickListener((parent, view, position, id) -> editItem(position));
        }

        private final ServiceConnection serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                        BluetoothLEService.LocalBinder binder = (BluetoothLEService.LocalBinder) service;
                        bluetoothService = binder.getService();
                        bluetoothService.registerListener(TrainingsConfigActivity.this);
                        isServiceBound = true;
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                        if (isServiceBound) {
                                bluetoothService.unregisterListener(TrainingsConfigActivity.this);
                                bluetoothService = null;
                                isServiceBound = false;
                        }
                }
        };

        @Override
        protected void onStart() {
                super.onStart();
                Intent intent = new Intent(this, BluetoothLEService.class);
                bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        }

        @Override
        protected void onStop() {
                if (isServiceBound && bluetoothService != null) {
                        bluetoothService.unregisterListener(this);
                        unbindService(serviceConnection);
                        isServiceBound = false;
                }
                super.onStop();
        }


        private void addItemToList() {
                String length = lengthInput.getText().toString();
                String steps = stepsInput.getText().toString();

                if (steps.isEmpty()) {
                        steps = "0";
                }

                if (!length.isEmpty()) {
                        // Add "Steps" and "Delay" as separate items
                        itemList.add("Steps: " + steps);
                        itemList.add("Delay: " + length);
                        adapter.notifyDataSetChanged(); // Notify the adapter to refresh the list view

                        lengthInput.setText(""); // Clear the length input field
                        stepsInput.setText(""); // Clear the steps input field
                } else {
                        Toast.makeText(this, "Please enter length", Toast.LENGTH_SHORT).show();
                }
        }


        private void editItem(int position) {
                String item = itemList.get(position);
                Pattern pattern = Pattern.compile("-?\\d+");
                Matcher matcher = pattern.matcher(item);

                List<String> numbers = new ArrayList<>();
                while (matcher.find()) {
                        numbers.add(matcher.group());
                }

                if (numbers.size() >= 2) {
                        lengthInput.setText(numbers.get(0));
                        stepsInput.setText(numbers.get(1));

                        itemList.remove(position);
                        adapter.notifyDataSetChanged();
                } else {
                        Toast.makeText(this, "Invalid item format", Toast.LENGTH_SHORT).show();
                }
        }
        public void onConnectionStatusChanged(boolean connected) {
                runOnUiThread(() -> {
                        if (!connected) {
                                // Connection lost, return to LandingActivity
                                Toast.makeText(TrainingsConfigActivity.this, "Bluetooth connection lost, returning to main menu", Toast.LENGTH_SHORT).show();
                                navigateToLandingActivity();
                        }
                });
        }

        private void navigateToLandingActivity() {
                Intent intent = new Intent(this, LandingActivity.class);
                startActivity(intent);
                finish(); // Close the current activity
        }

        @Override
        public void onDataReceived(String data) {
                // Handle data if needed
        }
        private void removeLastItem() {
                if (!itemList.isEmpty()) {
                        itemList.remove(itemList.size() - 1);
                        adapter.notifyDataSetChanged();
                }
        }
}
