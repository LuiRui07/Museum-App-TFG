package com.example.museumapp.Models;

public class Museum {

    private String _id;
    private String map;
    private String name;
    private Location location;
    private String image;
    private String address;

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Museum{" +
                "_id='" + _id + '\'' +
                ", map='" + map + '\'' +
                ", name='" + name + '\'' +
                ", location=" + location +
                ", image='" + image + '\'' +
                '}';
    }
}
