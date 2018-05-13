package com.codespurt.chatusingfirebase.chatModule.pojo;

import java.io.Serializable;

/**
 * Created by Code Spurt on 12-05-18.
 */

public class UserDetails implements Serializable {

    private String username;
    private String password;
    private String chatWith;
    private String userImage;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getChatWith() {
        return chatWith;
    }

    public void setChatWith(String chatWith) {
        this.chatWith = chatWith;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }
}
