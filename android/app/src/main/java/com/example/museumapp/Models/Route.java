package com.example.museumapp.Models;

import java.util.List;

public class Route {


    private String _id;
    private Museum museo;
    private List<Obra> arts;
    private String user;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Museum getMuseo() {
        return museo;
    }

    public void setMuseo(Museum museo) {
        this.museo = museo;
    }

    public List<Obra> getArts() {
        return arts;
    }

    public void setArts(List<Obra> arts) {
        this.arts = arts;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Route{" +
                "_id='" + _id + '\'' +
                ", museo=" + museo +
                ", arts=" + arts +
                ", user='" + user + '\'' +
                '}';
    }
}
