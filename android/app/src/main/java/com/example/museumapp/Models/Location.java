package com.example.museumapp.Models;

public class Location {
    private String type;
    private double[] coordinates;

    // Constructor
    public Location(String type, double[] coordinates) {
        this.type = type;
        this.coordinates = coordinates;
    }

    // Getters y setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[] coordinates) {
        this.coordinates = coordinates;
    }
}

