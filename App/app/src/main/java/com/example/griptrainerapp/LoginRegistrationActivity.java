package com.example.griptrainerapp;

public class LoginRegistrationActivity extends AppCompatActivity {

    private Switch switchLoginRegister;
    private EditText editTextEmail;
    private Button buttonLogin, buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.your_layout_file_name);

        switchLoginRegister = findViewById(R.id.switchLoginRegister);
        editTextEmail = findViewById(R.id.editTextEmail);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegister = findViewById(R.id.buttonRegister);

        switchLoginRegister.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Switch to Register Mode
                switchLoginRegister.setText(R.string.register);
                editTextEmail.setVisibility(View.VISIBLE); // Show email field
                buttonLogin.setVisibility(View.GONE); // Hide Login button
                buttonRegister.setVisibility(View.VISIBLE); // Show Register button
            } else {
                // Switch to Login Mode
                switchLoginRegister.setText(R.string.login);
                editTextEmail.setVisibility(View.GONE); // Hide email field
                buttonLogin.setVisibility(View.VISIBLE); // Show Login button
                buttonRegister.setVisibility(View.GONE); // Hide Register button
            }
        });
    }
}
