package com.example.griptrainerapp.LandingPages;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.griptrainerapp.R;
import com.example.griptrainerapp.TrainingActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TrainingsConfigActivity extends AppCompatActivity {
        private Button addBlockButton, backButton, sendButton, minusBlockButton; // Added minusBlockButton
        private EditText lengthInput, stepsInput;
        private ListView listView;
        private ArrayAdapter<String> adapter;
        private List<String> itemList;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_start_training_config);

                addBlockButton = findViewById(R.id.addBlockButton);
                backButton = findViewById(R.id.backButton);
                backButton.setOnClickListener(v -> {
                        // This will close the current activity and take you back to the previous activity
                        finish();
                });
                sendButton = findViewById(R.id.sendButton);
                sendButton.setOnClickListener(v -> {
                        Intent intent = new Intent(TrainingsConfigActivity.this, TrainingActivity.class);
                        intent.putStringArrayListExtra("trainingInstructions", new ArrayList<>(itemList));
                        intent.putExtra("isConfiguredTraining", true); // Additional flag
                        startActivity(intent);
                });


                minusBlockButton = findViewById(R.id.minusBlockButton); // Initialize minusBlockButton
                lengthInput = findViewById(R.id.lengthInput);
                stepsInput = findViewById(R.id.stepsInput);
                listView = findViewById(R.id.Listoftraining);

                itemList = new ArrayList<>();
                adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, itemList) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                TextView textView = (TextView) view.findViewById(android.R.id.text1);

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



        private void addItemToList() {
                String length = lengthInput.getText().toString();
                String steps = stepsInput.getText().toString();

                if (steps.isEmpty()) {
                        steps = "0";
                }

                if (!length.isEmpty()) {
                        String item = "Block " + (itemList.size() + 1) + ": Delay: " + length + ", Steps: " + steps;
                        itemList.add(item);
                        adapter.notifyDataSetChanged();
                        lengthInput.setText("");
                        stepsInput.setText("");
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

        private void removeLastItem() {
                if (!itemList.isEmpty()) {
                        itemList.remove(itemList.size() - 1);
                        adapter.notifyDataSetChanged();
                }
        }
}
