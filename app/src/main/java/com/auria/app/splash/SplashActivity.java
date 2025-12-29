package com.auria.app.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.auria.app.MainActivity;
import com.auria.app.R;
import com.auria.app.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000; // 2 seconds (professional apps keep it short)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            FirebaseAuth auth = FirebaseAuth.getInstance();

            if (auth.getCurrentUser() != null) {
                // ✅ Already logged in
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            } else {
                // ✅ Not logged in → Login first
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }

            finish(); // splash should never come back

        }, SPLASH_DELAY);
    }
}
