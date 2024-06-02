package com.example.museumapp.Api;

import com.example.museumapp.Models.Obra;
import com.example.museumapp.Models.User;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Response {

    private String message;

    private String mail;

    private List<Obra> obras;

    private User user;

    public Object getMessage() {
        return message;
    }

    public String getMail() {
        return mail;
    }

    public User getUser() {
        return user;
    }

    public Response(String message) {
        this.message = message;
    }

    public List<Obra> getObras() {
        return obras;
    }

    public String getMessageAsString() {
        if (message instanceof String) {
            return (String) message;
        } else {
            return message.toString();
        }
    }

}

