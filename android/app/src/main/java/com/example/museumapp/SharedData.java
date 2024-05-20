package com.example.museumapp;

import android.net.Uri;

import com.example.museumapp.Models.Museum;

import java.net.URI;

public class SharedData {

    private static SharedData instance;

    public String user;

    public String mail;

    public Uri photo;

    public Museum museo = null;

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPhoto(Uri photo) {
        this.photo = photo;
    }

    public static synchronized SharedData getInstance() {
        if (instance == null) {
            instance = new SharedData();
        }
        return instance;
    }

}
