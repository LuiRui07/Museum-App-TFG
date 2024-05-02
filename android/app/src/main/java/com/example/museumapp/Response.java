package com.example.museumapp;

public class Response {
    private String message;

    private String user;

    private String correo;

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
}

