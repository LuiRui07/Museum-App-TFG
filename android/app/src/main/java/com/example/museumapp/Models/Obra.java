package com.example.museumapp.Models;

public class Obra {

    private String _id;
    private String name;
    private String description;
    private String author;
    private String date;
    private String century;
    private String image;
    private String category;
    private String Museum;



    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCentury() {
        return century;
    }

    public void setCentury(String century) {
        this.century = century;
    }

    public String getMuseum() {
        return Museum;
    }

    public void setMuseum(String museum) {
        Museum = museum;
    }

    @Override
    public String toString() {
        return "Obra{" +
                "id='" + _id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", author='" + author + '\'' +
                ", date='" + date + '\'' +
                ", century='" + century + '\'' +
                ", image='" + image + '\'' +
                ", category='" + category + '\'' +
                ", Museum='" + Museum + '\'' +
                '}';
    }
}
