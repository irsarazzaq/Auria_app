package com.auria.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.auria.app.adapters.PostsAdapter;
import com.auria.app.auth.LoginActivity;
import com.auria.app.models.Post;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    private RecyclerView postsRecyclerView;
    private ProgressBar progressBar;
    private TextView noPostsText;
    private PostsAdapter postAdapter;

    private ListenerRegistration postsListener;
    private static final int ADD_POST_REQUEST = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        postsRecyclerView = findViewById(R.id.postsRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        noPostsText = findViewById(R.id.noPostsText);

        setupRecyclerView();
        setupBottomNavigation();
        attachPostsListener(); // ✅ Only one listener for posts
    }

    /* ================= RECYCLER VIEW ================= */

    private void setupRecyclerView() {
        postAdapter = new PostsAdapter(this, new ArrayList<>());
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        postsRecyclerView.setAdapter(postAdapter);

        postsRecyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        noPostsText.setText("Loading posts...");
    }

    /* ================= POSTS FIRESTORE ================= */

    private void attachPostsListener() {
        if (postsListener != null) return; // Prevent multiple listeners

        postsListener = FirebaseFirestore.getInstance()
                .collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {

                    progressBar.setVisibility(View.GONE);

                    if (error != null) {
                        Log.e("MainActivity", "Error loading posts", error);
                        noPostsText.setText("Error loading posts");
                        return;
                    }

                    if (value == null || value.isEmpty()) {
                        noPostsText.setText("No posts yet");
                        postsRecyclerView.setVisibility(View.GONE);
                        return;
                    }

                    List<Post> posts = new ArrayList<>();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        Post post = doc.toObject(Post.class);
                        if (post != null) {
                            post.setPostId(doc.getId());
                            posts.add(post);
                        }
                    }

                    if (posts.isEmpty()) {
                        noPostsText.setText("No posts to show");
                        postsRecyclerView.setVisibility(View.GONE);
                    } else {
                        noPostsText.setVisibility(View.GONE);
                        postsRecyclerView.setVisibility(View.VISIBLE);
                        postAdapter.updatePosts(posts);
                    }
                });
    }

    /* ================= BOTTOM NAVIGATION ================= */

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // ⚡ Make it always clickable & visible
        bottomNav.bringToFront();
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemReselectedListener(item -> {});

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                return true; // Already home
            }
            else if (id == R.id.nav_search) {
                startActivity(new Intent(this, SearchActivity.class));
                return true;
            }
            else if (id == R.id.nav_add) {
                startActivityForResult(
                        new Intent(this, AddPostActivity.class),
                        ADD_POST_REQUEST
                );
                return true;
            }
            else if (id == R.id.nav_notifications) {
                Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show();
                return true;
            }
            else if (id == R.id.nav_profile) {
                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
                return true;
            }

            return false;
        });
    }

    /* ================= ACTIVITY RESULT ================= */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_POST_REQUEST && resultCode == RESULT_OK) {
            Toast.makeText(this, "Post added", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (postsListener != null) postsListener.remove(); // 🧹 Cleanup
    }
}
