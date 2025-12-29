package com.auria.app.auth;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.auria.app.R;
import com.google.android.material.button.MaterialButton;

import java.util.Calendar;

public class DobActivity extends AppCompatActivity {

    private TextView dobText;
    private MaterialButton nextBtn;

    private int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dob);

        dobText = findViewById(R.id.dobText);
        nextBtn = findViewById(R.id.nextBtn);

        dobText.setOnClickListener(v -> openDatePicker());

        nextBtn.setOnClickListener(v -> {
            if (dobText.getText().toString().equals("Select your date of birth")) {
                Toast.makeText(this, "Please select date of birth", Toast.LENGTH_SHORT).show();
                return;
            }

            // Next step → Gender
            startActivity(new Intent(this, GenderActivity.class));
        });
    }

    private void openDatePicker() {
        Calendar calendar = Calendar.getInstance();

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, y, m, d) -> {

                    int age = year - y;

                    if (age < 13) {
                        Toast.makeText(this, "You must be at least 13 years old", Toast.LENGTH_LONG).show();
                        return;
                    }

                    dobText.setText(d + "/" + (m + 1) + "/" + y);
                },
                year, month, day
        );

        dialog.show();
    }
}
