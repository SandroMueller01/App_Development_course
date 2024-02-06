package com.example.griptrainerapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.griptrainerapp.BluetoothLowEnergy.BluetoothLEService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TrainingActivity extends AppCompatActivity implements BluetoothLEService.BluetoothServiceListener {

    private ProgressBar progressBar1, progressBar2, progressBar3, progressBar4;
    private int currentInstructionIndex = 0;
    private EditText editTextTime;
    private Button buttonFinishTrain, buttonStart;
    private ListView messagesListView, instructionsListView;
    private Handler timerHandler;
    private Runnable timerRunnable;
    private long startTime;
    private BluetoothLEService bluetoothService;
    private boolean isServiceBound = false;

    private ArrayList<String> messagesList = new ArrayList<>();
    private ArrayList<String> instructionsList = new ArrayList<>();

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            BluetoothLEService.LocalBinder binder = (BluetoothLEService.LocalBinder) service;
            bluetoothService = binder.getService();
            bluetoothService.registerListener(TrainingActivity.this);
            isServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bluetoothService = null;
            isServiceBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training); // Ensure this matches your layout file name

        // Initialize UI elements
        editTextTime = findViewById(R.id.editTextTime);
        buttonFinishTrain = findViewById(R.id.buttonFinishTrain);
        buttonStart = findViewById(R.id.buttonStartTrain);
        messagesListView = findViewById(R.id.ReceiveMessage_lv_1);
        instructionsListView = findViewById(R.id.Instructions_lv);

        ArrayList<String> passedInstructions = getIntent().getStringArrayListExtra("trainingInstructions");
        if (passedInstructions != null) {
            instructionsList.addAll(passedInstructions); // Add all received instructions to the list
        }

        // Setup ListViews with custom ArrayAdapter
        setupListView(messagesListView, messagesList);
        setupListView(instructionsListView, instructionsList);

        // Setup ProgressBars
        progressBar1 = findViewById(R.id.progressBar1);
        progressBar2 = findViewById(R.id.progressBar2);
        progressBar3 = findViewById(R.id.progressBar3);
        progressBar4 = findViewById(R.id.progressBar4);

        progressBar1.setIndeterminate(false);
        progressBar2.setIndeterminate(false);
        progressBar3.setIndeterminate(false);
        progressBar4.setIndeterminate(false);

        // Setup Button click listeners
        buttonFinishTrain.setOnClickListener(v -> finishActivity());
        buttonStart.setOnClickListener(v -> startButtonClicked());

        String fileName = getIntent().getStringExtra("trainingSessionFileName");
        if (fileName != null) {
            displayTrainingData(fileName); // Display the training data from the file
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, BluetoothLEService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isServiceBound) {
            bluetoothService.unregisterListener(this);
            unbindService(serviceConnection);
            isServiceBound = false;
        }
    }

    private void setupListView(ListView listView, ArrayList<String> dataList) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setTextColor(getResources().getColor(android.R.color.white));
                return view;
            }
        };
        listView.setAdapter(adapter);
    }

    private void finishActivity() {
        saveTrainingData(); // Save data before finishing

        if (isServiceBound) {
            sendDataOverBLE("Stop Training");
            bluetoothService.unregisterListener(this);
            unbindService(serviceConnection);
            isServiceBound = false;
        }
        stopTimer();
        finish();
    }

    private void saveTrainingData() {
        StringBuilder dataToSave = new StringBuilder();

        // Add instructions data
        for (String instruction : instructionsList) {
            dataToSave.append(instruction).append("\n");
        }

        // Add a separator between instructions and messages, if needed
        dataToSave.append("-----\n");

        // Add received messages data
        for (String message : messagesList) {
            dataToSave.append(message).append("\n");
        }

        boolean isConfiguredTraining = getIntent().getBooleanExtra("isConfiguredTraining", false);
        String prefix = isConfiguredTraining ? "Training_C_" : "Training_H_";

        // Use the prefix in the filename
        String fileName = prefix + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()) + ".txt";


        // Save data to file
        boolean success = saveToFile(dataToSave.toString(), fileName);

        if (success) {
            Toast.makeText(this, "Data saved successfully.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to save data.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean saveToFile(String data, String fileName) {
        try {
            File file = new File(getExternalFilesDir(null), fileName);
            FileWriter writer = new FileWriter(file);
            writer.write(data);
            writer.close();
            return true;
        } catch (IOException e) {
            Log.e("TrainingActivity", "Error saving training data", e);
            return false;
        }
    }

    private void startButtonClicked() {
        // Setup Timer
        setupTimer();

        // Setup start of sending
        sendDataOverBLE("Start Training");

        // Ensure BLE connection is established
        if (isServiceBound && bluetoothService != null) {
            // Start sending instructions
            startSendingInstructions();
        } else {
            Toast.makeText(this, "BLE Service not connected. Please connect first.", Toast.LENGTH_SHORT).show();
        }
    }

    private void startSendingInstructions() {
        currentInstructionIndex = 0; // Start from the first instruction
        sendNextInstruction();
    }

    private void sendNextInstruction() {
        if (currentInstructionIndex < instructionsList.size()) {
            String instruction = instructionsList.get(currentInstructionIndex);
            // Extracting the delay from the instruction text
            int delay = extractDelay(instruction);

            // Sending the actual instruction part over BLE, assuming instruction details are before ' - Delay:'
            String[] parts = instruction.split(" - Delay:");
            if (parts.length > 0) {
                sendDataOverBLE(parts[0]); // Send only the instruction part, without the delay information
            }

            // Highlight the current instruction in the ListView (if needed)
            ((ArrayAdapter<String>) instructionsListView.getAdapter()).notifyDataSetChanged();

            // Use extracted delay for the next instruction
            new Handler().postDelayed(() -> {
                // Remove the sent instruction from the list
                instructionsList.remove(currentInstructionIndex);
                // No need to increment index as the list size has decreased
                ((ArrayAdapter<String>) instructionsListView.getAdapter()).notifyDataSetChanged();

                // Send the next instruction
                sendNextInstruction();
            }, (long) delay * 1000); // Convert seconds to milliseconds
        }
    }

    // Method to extract delay from the instruction string
    private int extractDelay(String instruction) {
        try {
            String delayString = instruction.substring(instruction.lastIndexOf(":") + 1).trim();
            return Integer.parseInt(delayString); // Assuming the delay is in seconds
        } catch (Exception e) {
            Log.e("TrainingActivity", "Error parsing delay", e);
            return 1; // Default delay of 1 second if parsing fails
        }
    }


    private void sendDataOverBLE(String message) {
        if (isServiceBound && bluetoothService != null) {
            bluetoothService.sendData(message);
        } else {
            Toast.makeText(this, "BLE Service not bound or not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupTimer() {
        timerHandler = new Handler();
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                updateTimer(); // Update the EditText with the current time
                timerHandler.postDelayed(this, 500); // Schedule the next update
            }
        };
        startTimer(); // Start the timer when the activity is created
    }
    private void displayTrainingData(String fileName) {
        File file = new File(getExternalFilesDir(null), fileName);
        instructionsList.clear();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date lastStepTimestamp = null; // Timestamp of the last "Steps:" entry processed
        Date currentTimestamp = null; // Most recent timestamp read from the file

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                // Check if the line looks like a timestamp line
                if (line.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
                    // It's a timestamp line, parse it
                    try {
                        currentTimestamp = dateFormat.parse(line);
                    } catch (ParseException e) {
                        Log.e("TrainingData", "Failed to parse timestamp: \"" + line + "\"", e);
                    }
                } else if (line.contains("Steps:") && currentTimestamp != null) {
                    // It's a "Steps:" line and we have a valid current timestamp
                    if (lastStepTimestamp != null) {
                        // Calculate the delay from the last "Steps:" entry
                        long delayInMillis = currentTimestamp.getTime() - lastStepTimestamp.getTime();
                        double delayInSeconds = 0;
                        if (delayInMillis < 0) {
                            delayInSeconds = Math.max(0.1, delayInMillis / 1000.0);
                        }
                        instructionsList.add("Delay: " + delayInSeconds + " seconds");
                    }

                    // Add the "Steps:" information from the line to the list
                    instructionsList.add(line.trim());
                    lastStepTimestamp = currentTimestamp; // Update the timestamp for the last "Steps:" entry
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Consider adding user-friendly error handling here
        }

        // Notify the adapter that the data has changed so the list can be updated
        ((ArrayAdapter) instructionsListView.getAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onDataReceived(String data) {
        runOnUiThread(() -> {
            // Update your messages list
            messagesList.add(data);
            ((ArrayAdapter<String>) messagesListView.getAdapter()).notifyDataSetChanged();

            // Parse the incoming data and update progress bars
            String[] parts = data.split(","); // Assuming data is comma-separated
            for (String part : parts) {
                String[] keyValue = part.split(":"); // Assuming each part is "key:value"
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim();
                    try {
                        int value = Integer.parseInt(keyValue[1].trim());
                        switch (key) {
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
                                Log.w("TrainingActivity", "Unknown force key: " + key);
                                break;
                        }
                    } catch (NumberFormatException e) {
                        Log.e("TrainingActivity", "Error parsing force value", e);
                        Toast.makeText(TrainingActivity.this, "Error parsing force value", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    private void startTimer() {
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0); // Start the timer immediately
    }

    private void stopTimer() {
        timerHandler.removeCallbacks(timerRunnable); // Stop the timer
    }

    private void updateTimer() {
        long millis = System.currentTimeMillis() - startTime;
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;

        editTextTime.setText(String.format("%d:%02d", minutes, seconds)); // Update the EditText
    }

    // Custom ArrayAdapter to set the text color of each item to white
    public class WhiteTextArrayAdapter extends ArrayAdapter<String> {

        public WhiteTextArrayAdapter(@NonNull Context context, int resource) {
            super(context, resource);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            TextView textView = view.findViewById(android.R.id.text1);
            textView.setTextColor(getContext().getResources().getColor(android.R.color.white)); // Set text color to white
            return view;
        }
    }
}
