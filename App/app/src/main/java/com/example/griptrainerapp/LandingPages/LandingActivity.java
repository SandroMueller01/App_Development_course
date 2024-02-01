package com.example.griptrainerapp.LandingPages;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.griptrainerapp.R;

public class LandingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_options); // Make sure to use the correct layout file name here

        // Initialize buttons
        Button startTrainingButton = findViewById(R.id.startTrainingConfigButton);
        Button resumeTrainingButton = findViewById(R.id.manuelTrainingButton);
        Button trainingHistoryButton = findViewById(R.id.trainingHistoryButton);
        Button backButton = findViewById(R.id.backButton);
        Button closeButton = findViewById(R.id.closeButton);

        // Set onClickListeners
        startTrainingButton.setOnClickListener(v -> {
            // Handle "Start Training" action
            Intent intent = new Intent(LandingActivity.this, TrainingsConfigActivity.class);
            startActivity(intent);
        });

        resumeTrainingButton.setOnClickListener(v -> {
            // Handle "Resume Training" action
            Intent intent = new Intent(LandingActivity.this, ResumeActivity.class);
            startActivity(intent);
        });

        trainingHistoryButton.setOnClickListener(v -> {
            // Handle "Training History" action
             Intent intent = new Intent(LandingActivity.this, HistoryTrainingActivity.class);
             startActivity(intent);
        });

        backButton.setOnClickListener(v -> {
            // Handle "Back" action
            // This could simply finish the current activity
            finish();
        });

        closeButton.setOnClickListener(v -> {
            // Handle "Close" action
            // This should close the app
            finishAffinity(); // This method finishes this activity as well as all activities immediately below it in the current task that have the same affinity.
        });
    }
}
