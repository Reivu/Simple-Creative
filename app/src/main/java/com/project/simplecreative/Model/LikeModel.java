package com.project.simplecreative.Model;

public class LikeModel {

    private String UserID;

    public LikeModel(String userID) {
        UserID = userID;
    }

    public LikeModel() {
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    @Override
    public String toString() {
        return "LikeModel{" +
                "UserID='" + UserID + '\'' +
                '}';
    }
}
