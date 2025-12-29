package com.auria.app.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.auria.app.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PasswordActivity extends AppCompatActivity {

    private TextInputEditText passwordEt;
    private TextView lengthTick, uppercaseTick, numberTick, specialTick;
    private MaterialButton nextBtn;

    private FirebaseAuth auth;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        auth = FirebaseAuth.getInstance();

        // Get email from previous activity
        email = getIntent().getStringExtra("email");

        passwordEt = findViewById(R.id.passwordEt);
        lengthTick = findViewById(R.id.lengthTick);
        uppercaseTick = findViewById(R.id.uppercaseTick);
        numberTick = findViewById(R.id.numberTick);
        specialTick = findViewById(R.id.specialTick);
        nextBtn = findViewById(R.id.nextBtn);

        passwordEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString();
                lengthTick.setText(password.length() >= 8 ? "✔ At least 8 chars" : "❌ At least 8 chars");
                uppercaseTick.setText(password.matches(".*[A-Z].*") ? "✔ 1 uppercase" : "❌ 1 uppercase");
                numberTick.setText(password.matches(".*\\d.*") ? "✔ 1 number" : "❌ 1 number");
                specialTick.setText(password.matches(".*[!@#$%^&*+=?-].*") ? "✔ 1 special" : "❌ 1 special");
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        nextBtn.setOnClickListener(v -> {
            String password = passwordEt.getText().toString().trim();

            if (password.length() < 8 ||
                    !password.matches(".*[A-Z].*") ||
                    !password.matches(".*\\d.*") ||
                    !password.matches(".*[!@#$%^&*+=?-].*")) {
                Toast.makeText(this, "Password does not meet requirements", Toast.LENGTH_LONG).show();
                return;
            }

            // Create Firebase user and send verification email
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                user.sendEmailVerification()
                                        .addOnCompleteListener(verifyTask -> {
                                            if (verifyTask.isSuccessful()) {
                                                Toast.makeText(this, "Verification email sent!", Toast.LENGTH_LONG).show();
                                                // Move to VerifyCodeActivity
                                                startActivity(new Intent(PasswordActivity.this, VerifyCodeActivity.class));
                                                finish();
                                            } else {
                                                Toast.makeText(this, "Failed to send email: " + verifyTask.getException(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(this, "Error: " + task.getException(), Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}
