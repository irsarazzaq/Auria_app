package com.auria.app.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.auria.app.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerifyCodeActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;

    private TextInputEditText codeEt;
    private MaterialButton verifyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        codeEt = findViewById(R.id.codeEt);
        verifyBtn = findViewById(R.id.verifyBtn);

        verifyBtn.setOnClickListener(v -> verifyEmail());
    }

    private void verifyEmail() {
        if (user == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            return;
        }

        user.reload().addOnSuccessListener(unused -> {
            if (user.isEmailVerified()) {
                startActivity(new Intent(this, UsernameActivity.class));
                finish();
            } else {
                Toast.makeText(this,
                        "Email not verified yet. Check your inbox.",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
