package com.example.museumapp.Models;

public class User {

    String user;

    String mail;

    String password;

    String photo;

    Boolean isGoogleUser;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getGoogleUser() {
        return isGoogleUser;
    }

    public void setGoogleUser(Boolean googleUser) {
        isGoogleUser = googleUser;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public User(String user, String mail, String password, String photo, Boolean isGoogleUser) {
        this.user = user;
        this.mail = mail;
        this.photo = photo;
        this.password = password;
        this.isGoogleUser = isGoogleUser;
    }

    @Override
    public String toString() {
        return "User{" +
                "user='" + user + '\'' +
                ", mail='" + mail + '\'' +
                ", password='" + password + '\'' +
                ", isGoogleUser=" + isGoogleUser +
                '}';
    }
}
