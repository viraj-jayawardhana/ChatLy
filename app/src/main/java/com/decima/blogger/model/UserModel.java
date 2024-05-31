package com.decima.blogger.model;

import java.io.Serializable;

public class UserModel implements Serializable {

    private String userName;
    private int image;
    private int id;

    public UserModel(String userName, int image, int id) {
        this.userName = userName;
        this.image = image;
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
