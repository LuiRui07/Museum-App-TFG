package com.example.museumapp;

import android.net.Uri;

import com.example.museumapp.Models.Museum;
import com.example.museumapp.Models.User;

import java.net.URI;

public class SharedData {

    protected static SharedData instance;

    public User user;

    public Museum museo = null;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Museum getMuseo() {
        return museo;
    }

    public void setMuseo(Museum museo) {
        this.museo = museo;
    }

    public static synchronized SharedData getInstance() {
        if (instance == null) {
            instance = new SharedData();
        }
        return instance;
    }

    public void destroy() {
        this.user = null;
        this.museo = null;
        instance = null; // También nulifica la instancia única
    }

}
