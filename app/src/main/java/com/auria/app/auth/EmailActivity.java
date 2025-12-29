package com.auria.app.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.auria.app.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class EmailActivity extends AppCompatActivity {

    private TextInputEditText emailEt;
    private MaterialButton nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        emailEt = findViewById(R.id.emailEt);
        nextBtn = findViewById(R.id.nextBtn);

        nextBtn.setOnClickListener(v -> {
            String email = emailEt.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                emailEt.setError("Email required");
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEt.setError("Enter valid email");
                return;
            }

            // Pass email to PasswordActivity
            Intent intent = new Intent(EmailActivity.this, PasswordActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });
    }
}
