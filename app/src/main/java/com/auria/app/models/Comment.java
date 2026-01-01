package com.auria.app.models;

public class Comment {
    private String commentId;
    private String postId;
    private String userId;
    private String userName;
    private String commentText;
    private long timestamp;

    public Comment() {}

    public Comment(String commentId, String postId, String userId,
                   String userName, String commentText, long timestamp) {
        this.commentId = commentId;
        this.postId = postId;
        this.userId = userId;
        this.userName = userName;
        this.commentText = commentText;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getCommentId() { return commentId; }
    public void setCommentId(String commentId) { this.commentId = commentId; }

    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getCommentText() { return commentText; }
    public void setCommentText(String commentText) { this.commentText = commentText; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}