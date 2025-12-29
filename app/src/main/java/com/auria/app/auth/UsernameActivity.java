package com.auria.app.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.auria.app.MainActivity;
import com.auria.app.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

public class UsernameActivity extends AppCompatActivity {

    private MaterialCardView suggestion1Card, suggestion2Card, suggestion3Card;
    private MaterialButton nextBtn;
    private String selectedUsername = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username);

        suggestion1Card = findViewById(R.id.suggestion1Card);
        suggestion2Card = findViewById(R.id.suggestion2Card);
        suggestion3Card = findViewById(R.id.suggestion3Card);
        nextBtn = findViewById(R.id.nextBtn);

        // Setup suggestions
        setupSuggestions();

        // Setup click listeners for cards
        suggestion1Card.setOnClickListener(v -> {
            selectedUsername = "user_" + System.currentTimeMillis() % 1000;
            highlightCard(suggestion1Card);
        });

        suggestion2Card.setOnClickListener(v -> {
            selectedUsername = "auria_" + System.currentTimeMillis() % 1000;
            highlightCard(suggestion2Card);
        });

        suggestion3Card.setOnClickListener(v -> {
            selectedUsername = "ai_" + System.currentTimeMillis() % 1000;
            highlightCard(suggestion3Card);
        });

        // NEXT BUTTON - DIRECT TO MAIN ACTIVITY
        nextBtn.setOnClickListener(v -> {
            if (selectedUsername.isEmpty()) {
                // Auto-select first if nothing selected
                selectedUsername = "user_default";
            }

            // DIRECT NAVIGATION - NO FIREBASE, NO CHECKS
            Intent intent = new Intent(UsernameActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // AUTO-SELECT FIRST OPTION
        suggestion1Card.performClick();
    }

    private void setupSuggestions() {
        MaterialTextView tv1 = suggestion1Card.findViewById(R.id.suggestionText1);
        MaterialTextView tv2 = suggestion2Card.findViewById(R.id.suggestionText2);
        MaterialTextView tv3 = suggestion3Card.findViewById(R.id.suggestionText3);

        tv1.setText("user_cool");
        tv2.setText("auria_fan");
        tv3.setText("ai_lover");

        tv1.setVisibility(View.VISIBLE);
        tv2.setVisibility(View.VISIBLE);
        tv3.setVisibility(View.VISIBLE);
    }

    private void highlightCard(MaterialCardView selectedCard) {
        // Reset all cards
        suggestion1Card.setCardBackgroundColor(getColor(R.color.auria_bg_light));
        suggestion2Card.setCardBackgroundColor(getColor(R.color.auria_bg_light));
        suggestion3Card.setCardBackgroundColor(getColor(R.color.auria_bg_light));

        // Highlight selected
        selectedCard.setCardBackgroundColor(getColor(R.color.auria_primary_light));
    }
    private void saveUsernameAndNavigate() {
        // Save to Firestore (if needed later)
        // db.collection("users").document(uid).set(data)...

        // IMMEDIATE NAVIGATION
        Intent intent = new Intent(UsernameActivity.this, MainActivity.class);
        intent.putExtra("username", selectedUsername); // Pass username if needed
        startActivity(intent);
        finish();
    }
}