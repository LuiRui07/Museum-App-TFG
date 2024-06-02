package com.example.museumapp.Api;

public class GoogleLoginRequest {

    private String mail;
    private String user;
    private String photo;

    public GoogleLoginRequest(String mail, String user, String photo) {
        this.mail = mail;
        this.user = user;
        this.photo = photo;
    }
}
