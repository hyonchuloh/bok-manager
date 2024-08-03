package com.bok.iso.mngr.dao.dto;

public class BokManagerUserDto {

    private String userId;
    private String userPw;
    private String email;
    
    public String getUserId() {
        return userId;
    }
    public String getUserPw() {
        return userPw;
    }
    public String getEmail() {
        return email;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setUserPw(String userPw) {
        this.userPw = userPw;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}