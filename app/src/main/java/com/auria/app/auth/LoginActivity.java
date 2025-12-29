package com.auria.app.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.auria.app.MainActivity;
import com.auria.app.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText identifierEt, passwordEt;
    private MaterialButton loginBtn;
    private TextView createAccountBtn, forgotPassword;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        identifierEt = findViewById(R.id.identifierEt);
        passwordEt = findViewById(R.id.passwordEt);
        loginBtn = findViewById(R.id.loginBtn);
        createAccountBtn = findViewById(R.id.createAccountBtn);
        forgotPassword = findViewById(R.id.forgotPassword);

        loginBtn.setOnClickListener(v -> loginUser());

        // CREATE ACCOUNT BUTTON FIXED
        createAccountBtn.setOnClickListener(v -> {
            // Go to NameActivity (first step of registration)
            startActivity(new Intent(LoginActivity.this, NameActivity.class));
        });

        forgotPassword.setOnClickListener(v -> {
            Toast.makeText(this, "Reset password feature coming soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void loginUser() {
        String identifier = identifierEt.getText().toString().trim();
        String password = passwordEt.getText().toString().trim();

        if (TextUtils.isEmpty(identifier)) {
            identifierEt.setError("Email or Username required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEt.setError("Password required");
            return;
        }

        // Check if identifier is email
        if (identifier.contains("@")) {
            // Login with email
            auth.signInWithEmailAndPassword(identifier, password)
                    .addOnSuccessListener(authResult -> {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                    );
        } else {
            // Identifier is username → find email from Firestore
            db.collection("users")
                    .whereEqualTo("username", identifier)
                    .limit(1)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            String email = querySnapshot.getDocuments().get(0).getString("email");
                            auth.signInWithEmailAndPassword(email, password)
                                    .addOnSuccessListener(authResult -> {
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                    );
                        } else {
                            Toast.makeText(this, "Username not found", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Database error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                    );
        }
    }
}