package com.example.museumapp.Models;

public class Museum {

    private String _id;
    private String geojson;
    private String name;
    private Location location;
    private String image;

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getGeojson() {
        return geojson;
    }

    public void setGeojson(String geojson) {
        this.geojson = geojson;
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

    @Override
    public String toString() {
        return "Museum{" +
                "_id='" + _id + '\'' +
                ", geojson='" + geojson + '\'' +
                ", name='" + name + '\'' +
                ", location=" + location +
                ", image='" + image + '\'' +
                '}';
    }
}
