package com.example.griptrainerapp.loginregistration;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import com.example.griptrainerapp.LandingPages.LandingActivity;
import com.example.griptrainerapp.R;
import com.example.griptrainerapp.database.User;
import com.example.griptrainerapp.database.AppDatabase;
import java.util.concurrent.Executors;
import java.util.concurrent.Executor;

public class LoginRegistrationActivity extends AppCompatActivity {
    // Declare class variables
    private ImageView imageViewFemale, imageViewMale, imageViewDivers;
    private TextView textViewFemale, textViewMale, textViewDivers;

    private String selectedGender = ""; // Store selected gender
    private AppDatabase db; // Database instance

    private int SELECTED_TINT_COLOR;
    private int DEFAULT_TINT_COLOR;
    private int DEFAULT_TEXT_COLOR;
    private int SELECTED_TEXT_COLOR;

    private final Executor executor = Executors.newSingleThreadExecutor(); // Executor for background tasks

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_registration);

        // Initialize the database
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "database-name").build();

        // Get switching colors for genders
        SELECTED_TINT_COLOR = ContextCompat.getColor(this, R.color.lime);
        DEFAULT_TINT_COLOR = ContextCompat.getColor(this,R.color.white);

        DEFAULT_TEXT_COLOR = ContextCompat.getColor(this, R.color.white);
        SELECTED_TEXT_COLOR = ContextCompat.getColor(this, R.color.lime);

        // Initialize components

        // Login or registration switch
        SwitchCompat switchLoginRegister = findViewById(R.id.switchLoginRegister);

        // Username and password fields
        EditText editTextUsername = findViewById(R.id.editTextUsername);
        EditText editTextPassword = findViewById(R.id.editTextPassword);

        // Registration Specific Information fields
        EditText editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        EditText editTextEmail = findViewById(R.id.editTextEmail);

        // Gender selection
        imageViewFemale = findViewById(R.id.imageFemale);
        textViewFemale = findViewById(R.id.textViewFemale);
        imageViewFemale.setOnClickListener(v -> selectGender("female"));

        imageViewMale = findViewById(R.id.imageMale);
        textViewMale = findViewById(R.id.textViewMale);
        imageViewMale.setOnClickListener(v -> selectGender("male"));

        imageViewDivers = findViewById(R.id.imageDivers);
        textViewDivers = findViewById(R.id.textViewDivers);
        imageViewDivers.setOnClickListener(v -> selectGender("divers"));

        // Date of birth fields
        TextView textViewDateOfBirth = findViewById(R.id.date_of_birthText);
        EditText editTextDay = findViewById(R.id.editTextDay);
        EditText editTextMonth = findViewById(R.id.editTextMonth);
        EditText editTextYear = findViewById(R.id.editTextYear);

        // Button Login/Register
        Button buttonLoginRegister = findViewById(R.id.buttonLoginRegister);
        buttonLoginRegister.setOnClickListener(v -> {

            // Get user input
            String username = editTextUsername.getText().toString();
            String password = editTextPassword.getText().toString();
            String confirm_password = editTextConfirmPassword.getText().toString();
            String e_mail = editTextEmail.getText().toString();
            String gender = selectedGender;
            String d_o_b = editTextDay.getText().toString() + "-" +
                    editTextMonth.getText().toString() + "-" +
                    editTextYear.getText().toString();

            // Check if registration is selected
            if (switchLoginRegister.isChecked()) {
                // Check if passwords match
                if (!password.equals(confirm_password)) {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Execute registration in the background
                executor.execute(() -> performRegistration(username, password, e_mail, gender, d_o_b));
            } else { // Login is selected
                // Execute login in the background
                executor.execute(() -> performLogin(username, password));
            }
        });

        // Close the application
        Button buttonClose = findViewById(R.id.buttonClose);
        buttonClose.setOnClickListener(v -> finish(
                // Close event
        ));

        // Initially hide some elements

        // Hide confirm password and email fields
        editTextConfirmPassword.setVisibility(View.GONE);
        editTextEmail.setVisibility(View.GONE);

        // Hide gender selection
        imageViewFemale.setVisibility(View.GONE);
        textViewFemale.setVisibility(View.GONE);

        imageViewMale.setVisibility(View.GONE);
        textViewMale.setVisibility(View.GONE);

        imageViewDivers.setVisibility(View.GONE);
        textViewDivers.setVisibility(View.GONE);

        // Hide date of birth fields
        textViewDateOfBirth.setVisibility(View.GONE);
        editTextDay.setVisibility(View.GONE);
        editTextMonth.setVisibility(View.GONE);
        editTextYear.setVisibility(View.GONE);

        //if (!isValidPassword(password)) {
        //    Toast.makeText(LoginRegistrationActivity.this, "Password must be 8-16 characters, letters, numbers, special character.", Toast.LENGTH_LONG).show();
        //    return;
        //}

        // Setup Switch Listener
        switchLoginRegister.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Handle switch change
            if (isChecked) {
                // Change switch text and button text for registration
                switchLoginRegister.setText(R.string.register_button);
                buttonLoginRegister.setText(R.string.register_button);

                // Show confirm password and email fields
                editTextConfirmPassword.setVisibility(View.VISIBLE);
                editTextEmail.setVisibility(View.VISIBLE);

                // Show gender selection
                imageViewFemale.setVisibility(View.VISIBLE);
                textViewFemale.setVisibility(View.VISIBLE);

                imageViewMale.setVisibility(View.VISIBLE);
                textViewMale.setVisibility(View.VISIBLE);

                imageViewDivers.setVisibility(View.VISIBLE);
                textViewDivers.setVisibility(View.VISIBLE);

                // Show date of birth fields
                textViewDateOfBirth.setVisibility(View.VISIBLE);
                editTextDay.setVisibility(View.VISIBLE);
                editTextMonth.setVisibility(View.VISIBLE);
                editTextYear.setVisibility(View.VISIBLE);
            } else {
                // Change switch text and button text for login
                switchLoginRegister.setText(R.string.login_button);
                buttonLoginRegister.setText(R.string.login_button);

                // Hide confirm password and email fields
                editTextConfirmPassword.setVisibility(View.GONE);
                editTextEmail.setVisibility(View.GONE);

                // Hide gender selection
                imageViewFemale.setVisibility(View.GONE);
                textViewFemale.setVisibility(View.GONE);

                imageViewMale.setVisibility(View.GONE);
                textViewMale.setVisibility(View.GONE);

                imageViewDivers.setVisibility(View.GONE);
                textViewDivers.setVisibility(View.GONE);

                // Hide date of birth fields
                textViewDateOfBirth.setVisibility(View.GONE);
                editTextDay.setVisibility(View.GONE);
                editTextMonth.setVisibility(View.GONE);
                editTextYear.setVisibility(View.GONE);
            }
        });

    }

    // Gender selection
    private void selectGender(String gender) {
        resetTints(); // Reset color tints
        selectedGender = gender; // Update selected gender
        switch (gender) {
            case "female":
                // Highlight female selection
                imageViewFemale.setColorFilter(SELECTED_TINT_COLOR);
                textViewFemale.setTextColor(SELECTED_TEXT_COLOR);
                break;
            case "male":
                // Highlight male selection
                imageViewMale.setColorFilter(SELECTED_TINT_COLOR);
                textViewMale.setTextColor(SELECTED_TEXT_COLOR);
                break;
            case "divers":
                // Highlight divers selection
                imageViewDivers.setColorFilter(SELECTED_TINT_COLOR);
                textViewDivers.setTextColor(SELECTED_TEXT_COLOR);
                break;
        }
    }

    // Reset color tints
    private void resetTints() {
        imageViewFemale.setColorFilter(DEFAULT_TINT_COLOR);
        imageViewMale.setColorFilter(DEFAULT_TINT_COLOR);
        imageViewDivers.setColorFilter(DEFAULT_TINT_COLOR);

        textViewFemale.setTextColor(DEFAULT_TEXT_COLOR);
        textViewMale.setTextColor(DEFAULT_TEXT_COLOR);
        textViewDivers.setTextColor(DEFAULT_TEXT_COLOR);
    }

    // Perform user registration
    private void performRegistration(String username, String password, String e_mail, String gender, String d_o_b) {
        // Registration logic (previously in doInBackground)
        User user = new User();
        user.username = username;
        user.password = password; // Consider hashing the password
        user.e_mail = e_mail;
        user.gender = gender;
        user.date_of_birth = d_o_b; // Ensure the field names match in the User class
        db.userDao().insert(user);

        // Switch to the main thread to update UI
        runOnUiThread(() -> {
            // Update UI, e.g., navigate to another activity or show a toast
            Intent LandingPageIntent = new Intent(LoginRegistrationActivity.this, LandingActivity.class);
            startActivity(LandingPageIntent);
        });
    }

    // Perform user login
    private void performLogin(String username, String password) {
        // Login logic (previously in doInBackground)
        User user = db.userDao().getUser(username, password);

        // Switch to the main thread to update UI
        runOnUiThread(() -> {
            if (user != null) {
                // Successful login, navigate to another activity
                Intent bluetoothTestIntent = new Intent(LoginRegistrationActivity.this, LandingActivity.class);
                startActivity(bluetoothTestIntent);
            } else {
                // Display an error message if login fails
                Toast.makeText(LoginRegistrationActivity.this, "Passwords or Username is wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
