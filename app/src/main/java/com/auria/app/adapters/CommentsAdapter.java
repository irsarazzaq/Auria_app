package com.auria.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.auria.app.R;
import com.auria.app.models.Comment;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private Context context;
    private List<Comment> commentList;

    public CommentsAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = commentList.get(position);

        holder.userNameTextView.setText(comment.getUserName());
        holder.commentTextView.setText(comment.getCommentText());
        holder.timeTextView.setText(getTimeAgo(comment.getTimestamp()));

        // Set profile emoji
        holder.profileEmoji.setText("👤");
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    private String getTimeAgo(long timestamp) {
        long now = System.currentTimeMillis();
        long diff = now - timestamp;

        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (seconds < 60) return "Just now";
        else if (minutes < 60) return minutes + "m ago";
        else if (hours < 24) return hours + "h ago";
        else if (days < 7) return days + "d ago";
        else return "Week ago";
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView profileEmoji, userNameTextView, commentTextView, timeTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileEmoji = itemView.findViewById(R.id.profileEmoji);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
        }
    }
}