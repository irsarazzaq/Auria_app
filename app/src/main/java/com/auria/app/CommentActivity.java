package com.auria.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.auria.app.R;
import com.auria.app.adapters.CommentsAdapter;
import com.auria.app.models.Comment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CommentActivity extends AppCompatActivity {

    private RecyclerView commentsRecyclerView;
    private EditText commentEditText;
    private ImageButton sendButton, backButton;
    private TextView postCaptionText;

    private String postId;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private CommentsAdapter commentsAdapter;
    private List<Comment> commentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        postId = getIntent().getStringExtra("postId");

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        commentList = new ArrayList<>();

        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        commentEditText = findViewById(R.id.commentEditText);
        sendButton = findViewById(R.id.sendButton);
        backButton = findViewById(R.id.backButton);
        postCaptionText = findViewById(R.id.postCaptionText);

        commentsRecyclerView.setHasFixedSize(true);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsAdapter = new CommentsAdapter(this, commentList);
        commentsRecyclerView.setAdapter(commentsAdapter);

        backButton.setOnClickListener(v -> finish());
        sendButton.setOnClickListener(v -> postComment());

        loadPostDetails();
        loadComments();
    }

    private void loadPostDetails() {
        db.collection("posts").document(postId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String caption = documentSnapshot.getString("caption");
                        postCaptionText.setText(caption);
                    }
                });
    }

    private void loadComments() {
        db.collection("comments")
                .whereEqualTo("postId", postId)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Error loading comments", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    commentList.clear();
                    if (value != null) {
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Comment comment = doc.toObject(Comment.class);
                            commentList.add(comment);
                        }
                        commentsAdapter.notifyDataSetChanged();

                        // Scroll to bottom
                        if (commentList.size() > 0) {
                            commentsRecyclerView.smoothScrollToPosition(commentList.size() - 1);
                        }
                    }
                });
    }

    private void postComment() {
        String commentText = commentEditText.getText().toString().trim();

        if (commentText.isEmpty()) {
            Toast.makeText(this, "Please write a comment", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUser == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        String commentId = UUID.randomUUID().toString();
        String userName = currentUser.getEmail() != null ?
                currentUser.getEmail().split("@")[0] : "User";

        Comment comment = new Comment(commentId, postId, currentUser.getUid(),
                userName, commentText, System.currentTimeMillis());

        // Save comment to Firestore
        db.collection("comments").document(commentId)
                .set(comment)
                .addOnSuccessListener(aVoid -> {
                    // Update comments count in post
                    db.collection("posts").document(postId)
                            .update("commentsCount", FieldValue.increment(1))
                            .addOnSuccessListener(aVoid1 -> {
                                commentEditText.setText("");
                                Toast.makeText(this, "Comment posted", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to post comment: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
}