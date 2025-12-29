package com.auria.app.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.auria.app.MainActivity;
import com.auria.app.R;
import com.auria.app.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Start animations
        startAnimations();

        // Wait 2 seconds then navigate
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();

            if (auth.getCurrentUser() != null) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
            finish();
        }, 2000);
    }

    private void startAnimations() {
        // Logo animation
        android.widget.ImageView logo = findViewById(R.id.logoImage);
        Animation zoomIn = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
        logo.startAnimation(zoomIn);

        // Text animation
        android.widget.TextView appName = findViewById(R.id.appName);
        Animation fadeInUp = AnimationUtils.loadAnimation(this, R.anim.fade_in_up);
        appName.startAnimation(fadeInUp);
    }
}