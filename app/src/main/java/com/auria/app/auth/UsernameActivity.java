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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsernameActivity extends AppCompatActivity {

    private MaterialCardView suggestion1Card, suggestion2Card, suggestion3Card;
    private MaterialButton nextBtn;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private String selectedUsername = "";
    private List<String> suggestions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        suggestion1Card = findViewById(R.id.suggestion1Card);
        suggestion2Card = findViewById(R.id.suggestion2Card);
        suggestion3Card = findViewById(R.id.suggestion3Card);
        nextBtn = findViewById(R.id.nextBtn);

        fetchUsernameSuggestions();
        setupSelectionListeners();

        nextBtn.setOnClickListener(v -> saveUsername());
    }

    private void fetchUsernameSuggestions() {
        // In reality, you might have a Firestore collection of pre-generated usernames
        // Here I'm generating 3 random suggestions based on email
        String email = currentUser.getEmail();
        String base = email.split("@")[0].toLowerCase();

        suggestions.add(base + "_123");
        suggestions.add("aura_" + base.substring(0, Math.min(4, base.length())));
        suggestions.add(base + "007");

        // Set texts
        suggestion1Card.findViewById(R.id.suggestionText1).setVisibility(View.VISIBLE);
        suggestion2Card.findViewById(R.id.suggestionText2).setVisibility(View.VISIBLE);
        suggestion3Card.findViewById(R.id.suggestionText3).setVisibility(View.VISIBLE);

        ((com.google.android.material.textview.MaterialTextView) suggestion1Card.findViewById(R.id.suggestionText1))
                .setText(suggestions.get(0));
        ((com.google.android.material.textview.MaterialTextView) suggestion2Card.findViewById(R.id.suggestionText2))
                .setText(suggestions.get(1));
        ((com.google.android.material.textview.MaterialTextView) suggestion3Card.findViewById(R.id.suggestionText3))
                .setText(suggestions.get(2));
    }

    private void setupSelectionListeners() {
        suggestion1Card.setOnClickListener(v -> selectSuggestion(0));
        suggestion2Card.setOnClickListener(v -> selectSuggestion(1));
        suggestion3Card.setOnClickListener(v -> selectSuggestion(2));
    }

    private void selectSuggestion(int index) {
        selectedUsername = suggestions.get(index);
        highlightSelectedCard(index);
    }

    private void highlightSelectedCard(int selectedIndex) {
        int normalColor = getColor(R.color.auria_bg_light);
        int selectedColor = getColor(R.color.auria_primary_light);

        suggestion1Card.setCardBackgroundColor(selectedIndex == 0 ? selectedColor : normalColor);
        suggestion2Card.setCardBackgroundColor(selectedIndex == 1 ? selectedColor : normalColor);
        suggestion3Card.setCardBackgroundColor(selectedIndex == 2 ? selectedColor : normalColor);
    }

    private void saveUsername() {
        if (selectedUsername.isEmpty()) {
            Toast.makeText(this, "Please select a username", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if username already exists in Firestore
        db.collection("users").whereEqualTo("username", selectedUsername).get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        Toast.makeText(this, "Username already taken", Toast.LENGTH_SHORT).show();
                    } else {
                        // Save username to Firestore
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("email", currentUser.getEmail());
                        userData.put("username", selectedUsername);
                        userData.put("uid", currentUser.getUid());

                        db.collection("users").document(currentUser.getUid())
                                .set(userData, SetOptions.merge())
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this, "Username saved!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(UsernameActivity.this, MainActivity.class));
                                    finish();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                                );
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Check failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}