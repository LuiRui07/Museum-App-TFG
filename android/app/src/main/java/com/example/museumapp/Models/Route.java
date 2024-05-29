package com.example.museumapp.Models;

import java.util.List;

public class Route {


    private String _id;
    private String name;
    private String museum;
    private List<String> arts;
    private String user;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMuseum() {
        return museum;
    }

    public void setMuseum(String museum) {
        this.museum = museum;
    }

    public List<String> getArts() {
        return arts;
    }

    public void setArts(List<String> arts) {
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
                ", name='" + name + '\'' +
                ", museum='" + museum + '\'' +
                ", arts=" + arts +
                ", user='" + user + '\'' +
                '}';
    }
}
