package com.auria.app;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.auria.app.adapters.PostsAdapter;
import com.auria.app.adapters.UsersAdapter;
import com.auria.app.models.Post;
import com.auria.app.models.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText searchEditText;
    private RecyclerView resultsRecyclerView;
    private ProgressBar progressBar;
    private TextView noResultsText;

    private PostsAdapter postsAdapter;
    private UsersAdapter usersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchEditText = findViewById(R.id.searchEditText);
        resultsRecyclerView = findViewById(R.id.resultsRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        noResultsText = findViewById(R.id.noResultsText);

        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        postsAdapter = new PostsAdapter(this, new ArrayList<>());
        usersAdapter = new UsersAdapter(this, new ArrayList<>());

        resultsRecyclerView.setAdapter(postsAdapter); // Initially show posts

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                if (!query.isEmpty()) {
                    searchPostsAndUsers(query);
                } else {
                    postsAdapter.updatePosts(new ArrayList<>());
                    usersAdapter.updateUsers(new ArrayList<>());
                }
            }
        });
    }

    private void searchPostsAndUsers(String query) {
        progressBar.setVisibility(View.VISIBLE);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // 1️⃣ Search Posts
        db.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    List<Post> postResults = new ArrayList<>();
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (DocumentSnapshot doc : task.getResult()) {
                            Post post = doc.toObject(Post.class);
                            if (post != null && post.getTitle() != null &&
                                    post.getTitle().toLowerCase().contains(query.toLowerCase())) {
                                post.setPostId(doc.getId());
                                postResults.add(post);
                            }
                        }
                    }

                    // 2️⃣ Search Users
                    db.collection("users")
                            .get()
                            .addOnCompleteListener(task2 -> {
                                List<User> userResults = new ArrayList<>();
                                if (task2.isSuccessful() && task2.getResult() != null) {
                                    for (DocumentSnapshot doc : task2.getResult()) {
                                        User user = doc.toObject(User.class);
                                        if (user != null && (
                                                (user.getName() != null && user.getName().toLowerCase().contains(query.toLowerCase())) ||
                                                        (user.getEmail() != null && user.getEmail().toLowerCase().contains(query.toLowerCase()))
                                        )) {
                                            userResults.add(user);
                                        }
                                    }
                                }

                                progressBar.setVisibility(View.GONE);

                                if (postResults.isEmpty() && userResults.isEmpty()) {
                                    noResultsText.setVisibility(View.VISIBLE);
                                    noResultsText.setText("No results found");
                                    resultsRecyclerView.setVisibility(View.GONE);
                                } else {
                                    noResultsText.setVisibility(View.GONE);
                                    resultsRecyclerView.setVisibility(View.VISIBLE);
                                    postsAdapter.updatePosts(postResults);
                                    // Optionally: switch to usersAdapter if showing users
                                }
                            });
                });
    }
}
