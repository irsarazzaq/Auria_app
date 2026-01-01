package com.auria.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.auria.app.CommentActivity;
import com.auria.app.R;
import com.auria.app.models.Post;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private final Context context;
    private final List<Post> postList;

    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private final String currentUserId;

    public PostsAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
        this.currentUserId = auth.getCurrentUser() != null
                ? auth.getCurrentUser().getUid()
                : null;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Post post = postList.get(position);

        /* ---------- USERNAME ---------- */
        holder.tvUsername.setText(
                post.getUserName() != null ? post.getUserName() : "User"
        );

        /* ---------- CAPTION ---------- */
        if (post.getCaption() != null && !post.getCaption().isEmpty()) {
            holder.tvCaption.setText(post.getCaption());
            holder.tvCaption.setVisibility(View.VISIBLE);
        } else {
            holder.tvCaption.setVisibility(View.GONE);
        }

        /* ---------- IMAGE ---------- */
        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            holder.postImage.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(post.getImageUrl())
                    .into(holder.postImage);
        } else {
            holder.postImage.setVisibility(View.GONE);
        }

        /* ---------- LIKES & COMMENTS ---------- */
        holder.tvLikes.setText("❤️ " + post.getLikesCount() + " likes");
        holder.tvComments.setText("💬 " + post.getCommentsCount() + " comments");

        /* ---------- LIKE STATE ---------- */
        boolean isLiked = currentUserId != null
                && post.getLikesMap() != null
                && post.getLikesMap().containsKey(currentUserId);

        holder.btnLike.setImageResource(
                isLiked
                        ? android.R.drawable.btn_star_big_on
                        : android.R.drawable.btn_star_big_off
        );

        /* ---------- LIKE CLICK ---------- */
        holder.btnLike.setOnClickListener(v -> {
            if (currentUserId == null) {
                Toast.makeText(context, "Login required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (post.getLikesMap().containsKey(currentUserId)) {
                unlikePost(post.getPostId());
                post.getLikesMap().remove(currentUserId);
                post.setLikesCount(post.getLikesCount() - 1);
            } else {
                likePost(post.getPostId());
                post.getLikesMap().put(currentUserId, true);
                post.setLikesCount(post.getLikesCount() + 1);
            }

            notifyItemChanged(position);
        });

        /* ---------- COMMENT ---------- */
        holder.btnComment.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommentActivity.class);
            intent.putExtra("postId", post.getPostId());
            context.startActivity(intent);
        });

        /* ---------- SHARE ---------- */
        holder.btnShare.setOnClickListener(v -> {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");

            String text = post.getUserName() + ":\n" + post.getCaption();
            if (post.getImageUrl() != null) {
                text += "\n\n" + post.getImageUrl();
            }

            share.putExtra(Intent.EXTRA_TEXT, text);
            context.startActivity(Intent.createChooser(share, "Share post"));
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    /* ---------- UPDATE POSTS (FIX FOR YOUR ERROR) ---------- */
    public void updatePosts(List<Post> newPosts) {
        postList.clear();
        postList.addAll(newPosts);
        notifyDataSetChanged();
    }

    /* ---------- FIRESTORE LIKE ---------- */
    private void likePost(String postId) {
        DocumentReference ref = db.collection("posts").document(postId);
        Map<String, Object> update = new HashMap<>();
        update.put("likesMap." + currentUserId, true);
        update.put("likesCount", FieldValue.increment(1));
        ref.update(update);
    }

    private void unlikePost(String postId) {
        DocumentReference ref = db.collection("posts").document(postId);
        Map<String, Object> update = new HashMap<>();
        update.put("likesMap." + currentUserId, FieldValue.delete());
        update.put("likesCount", FieldValue.increment(-1));
        ref.update(update);
    }

    /* ---------- VIEW HOLDER ---------- */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvUsername, tvCaption, tvLikes, tvComments;
        ImageView postImage;
        ImageButton btnLike, btnComment, btnShare;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUsername = itemView.findViewById(R.id.tv_username);
            tvCaption = itemView.findViewById(R.id.tv_content);
            tvLikes = itemView.findViewById(R.id.tv_likes);
            tvComments = itemView.findViewById(R.id.tv_comments);

            postImage = itemView.findViewById(R.id.post_image);

            btnLike = itemView.findViewById(R.id.btn_like);
            btnComment = itemView.findViewById(R.id.btn_comment);
            btnShare = itemView.findViewById(R.id.btn_share);
        }
    }
}
