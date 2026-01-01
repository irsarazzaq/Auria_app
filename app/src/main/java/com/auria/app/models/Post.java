package com.auria.app.models;

import java.util.HashMap;
import java.util.Map;

public class Post {

    private String postId;
    private String title;
    private String userId;
    private String userName;
    private String caption;
    private String imageUrl;
    private long timestamp;

    private int likesCount = 0;
    private int commentsCount = 0;

    private Map<String, Boolean> likesMap = new HashMap<>();

    public Post() {
        // REQUIRED for Firestore
    }

    /* ---------- GETTERS ---------- */

    public String getPostId() { return postId; }
    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getCaption() { return caption; }
    public String getImageUrl() { return imageUrl; }
    public long getTimestamp() { return timestamp; }
    public String getTitle() {
        return title != null ? title : "";
    }

    // Setter
    public void setTitle(String title) {
        this.title = title;
    }

    public int getLikesCount() { return likesCount; }
    public int getCommentsCount() { return commentsCount; }

    public Map<String, Boolean> getLikesMap() {
        if (likesMap == null) {
            likesMap = new HashMap<>();
        }
        return likesMap;
    }

    /* ---------- SETTERS ---------- */

    public void setPostId(String postId) { this.postId = postId; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setCaption(String caption) { this.caption = caption; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public void setLikesCount(int likesCount) {
        this.likesCount = Math.max(likesCount, 0);
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = Math.max(commentsCount, 0);
    }
}
