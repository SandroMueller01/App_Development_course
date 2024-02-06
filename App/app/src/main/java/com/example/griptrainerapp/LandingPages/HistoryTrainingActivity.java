package com.example.griptrainerapp.LandingPages;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.griptrainerapp.R;
import com.example.griptrainerapp.TrainingActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HistoryTrainingActivity extends AppCompatActivity {

    private ListView listViewTrainingSessions;
    private List<String> trainingFiles;
    private int selectedIndex = -1;  // Add this line to track the selected item index

    private Button backButton, startTrainingButton;

    private static final int REQUEST_EXTERNAL_STORAGE_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainings_history);

        listViewTrainingSessions = findViewById(R.id.listViewTrainingsHistory);
        backButton = findViewById(R.id.buttonBack);  // Assuming you have a backButton in your XML
        startTrainingButton = findViewById(R.id.buttonStartTraining);  // Assuming you have a startTrainingButton in your XML
        trainingFiles = new ArrayList<>();

        backButton.setOnClickListener(view -> finish());  // Finish the activity, acting as a "Back" functionality

        startTrainingButton.setOnClickListener(view -> {
            // Implement the action to start training
            // For example, if you want to start a new Activity, you can do so here
            if (selectedIndex != -1) {
                String selectedTraining = trainingFiles.get(selectedIndex);
                startTraining(selectedTraining);  // A method to handle starting the training
            } else {
                Toast.makeText(HistoryTrainingActivity.this, "Please select a training session", Toast.LENGTH_SHORT).show();
            }
        });

        checkPermissionsAndListFiles();
    }
    private void startTraining(String trainingSession) {
        Intent intent = new Intent(HistoryTrainingActivity.this, TrainingActivity.class);
        intent.putExtra("trainingSessionFileName", trainingSession);  // Pass the filename with the intent
        startActivity(intent);  // Start the new activity
    }


    private void checkPermissionsAndListFiles() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE_PERMISSION);
        } else {
            listTrainingFiles();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                listTrainingFiles();
            } else {
                Toast.makeText(this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void listTrainingFiles() {
        File directory = getExternalFilesDir(null);
        assert directory != null;
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().startsWith("Training_")) {
                    trainingFiles.add(file.getName());
                }
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, trainingFiles) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setTextColor(getResources().getColor(android.R.color.white));  // Set text color to white

                // Set background color for the selected item
                if (selectedIndex == position) {
                    view.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                } else {
                    view.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                }

                return view;
            }
        };

        listViewTrainingSessions.setAdapter(adapter);

        listViewTrainingSessions.setOnItemClickListener((parent, view, position, id) -> {
            selectedIndex = position;  // Update the selected index
            adapter.notifyDataSetChanged();  // Notify the adapter to refresh and apply the selection color change

            String fileName = trainingFiles.get(position);
            displayTrainingData(fileName);
            // Additional actions for the selected training session can be added here
        });
    }

    private void displayTrainingData(String fileName) {
        File file = new File(getExternalFilesDir(null), fileName);
        StringBuilder text = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line).append('\n');
            }
            Toast.makeText(this, text.toString(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error reading file", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
