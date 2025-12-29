package com.auria.app.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.auria.app.R;
import com.google.android.material.button.MaterialButton;

public class GenderActivity extends AppCompatActivity {

    private Spinner genderSpinner;
    private MaterialButton nextBtn;

    private String selectedGender = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gender);

        genderSpinner = findViewById(R.id.genderSpinner);
        nextBtn = findViewById(R.id.nextBtn);

        String[] genders = {
                "Select gender",
                "Male",
                "Female",
                "Prefer not to say"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                genders
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    selectedGender = "";
                } else {
                    selectedGender = genders[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedGender = "";
            }
        });

        nextBtn.setOnClickListener(v -> {
            if (selectedGender.isEmpty()) {
                Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show();
                return;
            }

            // NEXT STEP
            startActivity(new Intent(GenderActivity.this, PhoneActivity.class));
        });
    }
}
