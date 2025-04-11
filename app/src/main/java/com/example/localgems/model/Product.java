package com.example.localgems.model;

public class Product {
    private String name;
    private double price;

    // Costruttore
    public Product(String name, double price) {
        this.name = name;
        this.price = price;
    }

    // Getter e Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}