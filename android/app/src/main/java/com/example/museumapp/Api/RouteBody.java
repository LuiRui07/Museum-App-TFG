package com.example.museumapp.Api;

import com.example.museumapp.Models.Obra;

import java.util.List;

public class RouteBody {

    String name;
    String museum;
    String user;
    List<Obra> arts;

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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public List<Obra> getArts() {
        return arts;
    }

    public void setArts(List<Obra> arts) {
        this.arts = arts;
    }

    @Override
    public String toString() {
        return "RouteBody{" +
                "name='" + name + '\'' +
                ", museum='" + museum + '\'' +
                ", user='" + user + '\'' +
                ", arts=" + arts +
                '}';
    }
}
