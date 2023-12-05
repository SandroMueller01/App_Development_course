package com.example.griptrainerapp;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

public class LoginRegistrationActivity extends AppCompatActivity {
    private ImageView imageViewFemale, imageViewMale, imageViewDivers;
    private TextView textViewFemale, textViewMale, textViewDivers;


    private int SELECTED_TINT_COLOR;
    private int DEFAULT_TINT_COLOR;
    private int DEFAULT_TEXT_COLOR;
    private int SELECTED_TEXT_COLOR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_registration);

        // Get switching colors for genders
        SELECTED_TINT_COLOR = ContextCompat.getColor(this, R.color.lime);
        DEFAULT_TINT_COLOR = ContextCompat.getColor(this,R.color.white);

        DEFAULT_TEXT_COLOR = ContextCompat.getColor(this, R.color.white);
        SELECTED_TEXT_COLOR = ContextCompat.getColor(this, R.color.lime);

        // Initialize components
        // Login or registration change
        SwitchCompat switchLoginRegister = findViewById(R.id.switchLoginRegister);

        // Username and password
        EditText editTextUsername = findViewById(R.id.editTextUsername);
        EditText editTextPassword = findViewById(R.id.editTextPassword);

        // Registration Specific Information
        // Confirm Password and E-Mail
        EditText editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        EditText editTextEmail = findViewById(R.id.editTextEmail);

        // Gender
        imageViewFemale = findViewById(R.id.imageFemale);
        textViewFemale = findViewById(R.id.textViewFemale);
        imageViewFemale.setOnClickListener(v -> selectGender("female"));

        imageViewMale = findViewById(R.id.imageMale);
        textViewMale = findViewById(R.id.textViewMale);
        imageViewMale.setOnClickListener(v -> selectGender("male"));

        imageViewDivers = findViewById(R.id.imageDivers);
        textViewDivers = findViewById(R.id.textViewDivers);
        imageViewDivers.setOnClickListener(v -> selectGender("divers"));

        // Date of birth
        TextView textViewDateOfBirth = findViewById(R.id.date_of_birthText);
        EditText editTextDay = findViewById(R.id.editTextDay);
        EditText editTextMonth = findViewById(R.id.editTextMonth);
        EditText editTextYear = findViewById(R.id.editTextYear);

        // Button Login/Register
        Button buttonLoginRegister = findViewById(R.id.buttonLoginRegister);
        buttonLoginRegister.setOnClickListener(v -> finish());
        // Add listener method as well

        //Close the application
        Button buttonClose = findViewById(R.id.buttonClose);
        buttonClose.setOnClickListener(v -> finish(
                // Navigate to the landing page
                // Save information and then later usage for the login

        ));

        // Initially gone
        // Show confirm password
        editTextConfirmPassword.setVisibility(View.GONE);
        editTextEmail.setVisibility(View.GONE);

        // Show gender pick
        imageViewFemale.setVisibility(View.GONE);
        textViewFemale.setVisibility(View.GONE);

        imageViewMale.setVisibility(View.GONE);
        textViewMale.setVisibility(View.GONE);

        imageViewDivers.setVisibility(View.GONE);
        textViewDivers.setVisibility(View.GONE);

        // Date of birth
        textViewDateOfBirth.setVisibility(View.GONE);
        editTextDay.setVisibility(View.GONE);
        editTextMonth.setVisibility(View.GONE);
        editTextYear.setVisibility(View.GONE);

        // Setup Switch Listener
        switchLoginRegister.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Handle switch change
            if (isChecked) {
                // Change name of slider
                switchLoginRegister.setText(R.string.register_button);
                //Change the login button
                buttonLoginRegister.setText(R.string.register_button);

                // Show confirm password
                editTextConfirmPassword.setVisibility(View.VISIBLE);
                editTextEmail.setVisibility(View.VISIBLE);

                // Show gender pick
                imageViewFemale.setVisibility(View.VISIBLE);
                textViewFemale.setVisibility(View.VISIBLE);

                imageViewMale.setVisibility(View.VISIBLE);
                textViewMale.setVisibility(View.VISIBLE);

                imageViewDivers.setVisibility(View.VISIBLE);
                textViewDivers.setVisibility(View.VISIBLE);

                // Date of birth
                textViewDateOfBirth.setVisibility(View.VISIBLE);
                editTextDay.setVisibility(View.VISIBLE);
                editTextMonth.setVisibility(View.VISIBLE);
                editTextYear.setVisibility(View.VISIBLE);
            } else {
                // Change name of slider
                switchLoginRegister.setText(R.string.login_button);
                //Change the login button
                buttonLoginRegister.setText(R.string.login_button);

                // Show confirm password
                editTextConfirmPassword.setVisibility(View.GONE);
                editTextEmail.setVisibility(View.GONE);

                // Show gender pick
                imageViewFemale.setVisibility(View.GONE);
                textViewFemale.setVisibility(View.GONE);

                imageViewMale.setVisibility(View.GONE);
                textViewMale.setVisibility(View.GONE);

                imageViewDivers.setVisibility(View.GONE);
                textViewDivers.setVisibility(View.GONE);

                // Date of birth
                textViewDateOfBirth.setVisibility(View.GONE);
                editTextDay.setVisibility(View.GONE);
                editTextMonth.setVisibility(View.GONE);
                editTextYear.setVisibility(View.GONE);
            }

        });

    }

    private void selectGender(String gender) {
        resetTints();
        switch (gender) {
            case "female":
                imageViewFemale.setColorFilter(SELECTED_TINT_COLOR);
                textViewFemale.setTextColor(SELECTED_TEXT_COLOR);
                break;
            case "male":
                imageViewMale.setColorFilter(SELECTED_TINT_COLOR);
                textViewMale.setTextColor(SELECTED_TEXT_COLOR);
                break;
            case "divers":
                imageViewDivers.setColorFilter(SELECTED_TINT_COLOR);
                textViewDivers.setTextColor(SELECTED_TEXT_COLOR);
                break;
        }

    }

    private void resetTints() {
        imageViewFemale.setColorFilter(DEFAULT_TINT_COLOR);
        imageViewMale.setColorFilter(DEFAULT_TINT_COLOR);
        imageViewDivers.setColorFilter(DEFAULT_TINT_COLOR);

        textViewFemale.setTextColor(DEFAULT_TEXT_COLOR);
        textViewMale.setTextColor(DEFAULT_TEXT_COLOR);
        textViewDivers.setTextColor(DEFAULT_TEXT_COLOR);

    }
}
