package com.example.cashwalk.dto;

public class PostReactionCountResponse {
    private int likeCount;
    private int dislikeCount;

    public PostReactionCountResponse(int likeCount, int dislikeCount) {
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public int getDislikeCount() {
        return dislikeCount;
    }
}
