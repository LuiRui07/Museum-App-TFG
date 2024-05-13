package com.example.museumapp.Api;

public class UserBody {
    String user;

    String mail;

    String password;

    public UserBody (String user, String mail, String password){
        this.user = user;
        this.mail = mail;
        this.password = password;
    }
}
