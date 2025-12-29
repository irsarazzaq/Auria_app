package com.auria.app.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.auria.app.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class NameActivity extends AppCompatActivity {

    private TextInputEditText firstNameEt, lastNameEt;
    private MaterialButton nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        firstNameEt = findViewById(R.id.firstNameEt);
        lastNameEt = findViewById(R.id.lastNameEt);
        nextBtn = findViewById(R.id.nextBtn);

        nextBtn.setOnClickListener(v -> validateAndGoNext());
    }

    private void validateAndGoNext() {

        String firstName = firstNameEt.getText().toString().trim();
        String lastName = lastNameEt.getText().toString().trim();

        if (TextUtils.isEmpty(firstName)) {
            firstNameEt.setError("First name required");
            return;
        }

        if (TextUtils.isEmpty(lastName)) {
            lastNameEt.setError("Last name required");
            return;
        }

        Intent intent = new Intent(NameActivity.this, DobActivity.class);
        intent.putExtra("first_name", firstName);
        intent.putExtra("last_name", lastName);
        startActivity(intent);
    }
}
