package com.example.museumapp.Api;

import com.example.museumapp.Models.Obra;

import java.util.List;

public class Response {
    private String message;

    private String user;

    private String correo;

    private List<Obra> obras;

    public String getMessage() {
        return message;
    }

    public String getCorreo() {
        return correo;
    }

    public String getUser() {
        return user;
    }

    public Response() {}

    public Response(String message) {
        this.message = message;
    }


    public List<Obra> getObras() {
        return obras;
    }
}

