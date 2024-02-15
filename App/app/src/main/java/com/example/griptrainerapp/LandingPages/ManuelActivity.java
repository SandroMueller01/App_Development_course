package com.example.griptrainerapp.LandingPages;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.griptrainerapp.BluetoothLowEnergy.BluetoothLEService;
import com.example.griptrainerapp.R;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ManuelActivity extends Activity implements BluetoothLEService.BluetoothServiceListener {

    private ProgressBar progressBar1, progressBar2, progressBar3, progressBar4;
    private TextView textView;
    private ListView sendMessageListView;
    private ArrayList<Message> sentMessagesList;
    private ArrayAdapter<Message> sendMessagesAdapter;
    private Button backButton;
    private BluetoothLEService bluetoothService;
    private boolean isServiceBound = false;
    private boolean isTrainingStopped = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            BluetoothLEService.LocalBinder binder = (BluetoothLEService.LocalBinder) service;
            bluetoothService = binder.getService();
            bluetoothService.registerListener(ManuelActivity.this);
            isServiceBound = true;
            // Update connection state TextView
            textView.setText(R.string.connection_state_tu);
            sendAndDisplayMessage("Manuel Steering");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bluetoothService = null;
            isServiceBound = false;
            // Update connection state TextView
            textView.setText(R.string.connection_state_fl);
            if (!isFinishing()) {
                navigateToLandingActivity();
            }
        }
    };
    @Override
    public void onConnectionStatusChanged(boolean connected) {
        runOnUiThread(() -> {
            if (!connected) {
                // Connection lost, return to LandingActivity
                Toast.makeText(ManuelActivity.this, "Bluetooth connection lost, returning to main menu", Toast.LENGTH_SHORT).show();
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
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, BluetoothLEService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        if (isServiceBound) {
            bluetoothService.unregisterListener(this);
            unbindService(serviceConnection);
            isServiceBound = false;
        }
        super.onStop();
    }

    public void onDataReceived(String data) {
        if (isFinishing()) return;
        runOnUiThread(() -> {
            if (isTrainingStopped) return;

            String timestamp = getCurrentTimestamp();
            Message message = new Message(data, timestamp);
            sentMessagesList.add(message);
            sendMessagesAdapter.notifyDataSetChanged();

            // Directly parse the incoming data without splitting on comma
            String[] parts = data.trim().split(": ");
            if (parts.length == 2) {
                try {
                    int value = Integer.parseInt(parts[1].trim());
                    switch (parts[0]) {
                        case "Force 1":
                            progressBar1.setProgress(value);
                            break;
                        case "Force 2":
                            progressBar2.setProgress(value);
                            break;
                        case "Force 3":
                            progressBar3.setProgress(value);
                            break;
                        case "Force 4":
                            progressBar4.setProgress(value);
                            break;
                        default:
                            Toast.makeText(this, "Unknown force label", Toast.LENGTH_SHORT).show();
                            break;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid force value", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manuel_training);

        initializeViews();
        setupListViewAdapters();
        setupBackButton();
        setupArrowButtons();
        setupStopButton();
    }

    private void initializeViews() {
        progressBar1 = findViewById(R.id.progressBar1);
        progressBar2 = findViewById(R.id.progressBar2);
        progressBar3 = findViewById(R.id.progressBar3);
        progressBar4 = findViewById(R.id.progressBar4);

        // Set the maximum value for each progress bar
        progressBar1.setMax(1024);
        progressBar2.setMax(1024);
        progressBar3.setMax(1024);
        progressBar4.setMax(1024);

        textView = findViewById(R.id.textView2);
        // Set an initial state or consider updating dynamically

        sendMessageListView = findViewById(R.id.SendMessages_lv);
        sentMessagesList = new ArrayList<>();

        backButton = findViewById(R.id.backButton);
    }

    private void setupBackButton() {
        backButton.setOnClickListener(v -> {
            // Send "Stop Training" message
            sendAndDisplayMessage("Stop Training");
            // Finish the activity
            finish();
        });
    }

    private void setupArrowButtons() {
        ImageButton buttonArrowUp = findViewById(R.id.buttonArrowUp);
        buttonArrowUp.setOnTouchListener((v, event) -> {
            if (!isTrainingStopped) {
                sendAndDisplayMessage("Steps: 30");
            }
            return true;  // Consume the touch event
        });

        ImageButton buttonArrowDown = findViewById(R.id.buttonArrowDown);
        buttonArrowDown.setOnTouchListener((v, event) -> {
            if (!isTrainingStopped) {
                sendAndDisplayMessage("Steps: -30");
            }
            return true;  // Consume the touch event
        });
    }

    private void setupStopButton() {
        Button stopButton = findViewById(R.id.stopButton);
        stopButton.setOnClickListener(v -> {
            if (!isTrainingStopped) {
                stopTraining();
            }
        });
    }

    private void stopTraining() {
        isTrainingStopped = true;
        // Send "Stop Training" message
        sendAndDisplayMessage("Stop Training");
        // Delay finishing the activity to allow time for messages to be sent
        exportAndSaveTrainingData();
        new Handler().postDelayed(this::finish, 2000); // Delay for 2 seconds
    }

    private void sendAndDisplayMessage(String message) {
        sendDataOverBLE(message);

        String timestamp = getCurrentTimestamp();
        sentMessagesList.add(new Message(message, timestamp));

        // Notify the adapter to refresh the ListView
        sendMessagesAdapter.notifyDataSetChanged();
    }

    private void sendDataOverBLE(String message) {
        if (isServiceBound && bluetoothService != null) {
            bluetoothService.sendData(message);
        } else {
            Toast.makeText(this, "BLE Service not bound or not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupListViewAdapters() {
        sendMessagesAdapter = new ArrayAdapter<Message>(this, android.R.layout.simple_list_item_1, sentMessagesList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);

                // Change the item text color to white
                textView.setTextColor(getResources().getColor(android.R.color.white));

                // Set text to include message content and timestamp
                Message message = getItem(position);
                if (message != null) {
                    textView.setText(message.timestamp + "\n" + message.content);
                }

                return view;
            }
        };

        sendMessageListView.setAdapter(sendMessagesAdapter);
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentTime = new Date();
        return dateFormat.format(currentTime);
    }

    private void exportAndSaveTrainingData() {
        if (sentMessagesList.isEmpty()) {
            Toast.makeText(this, "No training data to export", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder exportData = new StringBuilder();
        for (Message message : sentMessagesList) {
            exportData.append(message.timestamp).append("\n").append(message.content).append("\n");
        }

        // Use current timestamp as part of the file name to ensure uniqueness
        String fileName = "Training_M_" + getCurrentTimestamp().replace(" ", "_").replace(":", "-") + ".txt";
        boolean success = saveDataToFile(exportData.toString(), fileName);

        if (success) {
            Toast.makeText(this, "Training data exported and saved successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to save training data", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean saveDataToFile(String data, String fileName) {
        try {
            File directory = getExternalFilesDir(null);
            File file = new File(directory, fileName);

            FileWriter writer = new FileWriter(file);
            writer.write(data);
            writer.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    // Create a class to represent each message with both content and timestamp
    private static class Message {
        String content;
        String timestamp;

        Message(String content, String timestamp) {
            this.content = content;
            this.timestamp = timestamp;
        }
    }
}


