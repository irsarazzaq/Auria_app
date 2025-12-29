package com.auria.app.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.auria.app.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class PhoneActivity extends AppCompatActivity {

    private TextInputEditText phoneEt;
    private MaterialButton nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        phoneEt = findViewById(R.id.phoneEt);
        nextBtn = findViewById(R.id.nextBtn);

        nextBtn.setOnClickListener(v -> {
            String phone = phoneEt.getText().toString().trim();

            if (TextUtils.isEmpty(phone)) {
                phoneEt.setError("Phone number required");
                return;
            }

            if (phone.length() < 10) {
                phoneEt.setError("Enter valid phone number");
                return;
            }

            // NEXT STEP → Email screen
            startActivity(new Intent(PhoneActivity.this, EmailActivity.class));
        });
    }
}
