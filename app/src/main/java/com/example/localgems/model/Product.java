package com.example.localgems.model;

public class Product {

    private String id;
    private String name;
    private String description;
    private Double price;

    private Double rating;
    private Integer stock;
    private String company_id;
    private String image_url;
    private Integer quantity; // Nuovo campo per la quantità

    public Product() {}

    public Product(String name,Double price) {
        this.name = name;
        this.price = price;

    }

    public Product(String s, String s1, double v) {
        this.name = s;
        this.description = s1;
        this.price = v;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public String getCompany_id() { return company_id; }
    public void setCompany_id(String company_id) { this.company_id = company_id; }

    public String getImage_url() { return image_url; }
    public void setImage_url(String image_url) { this.image_url = image_url; }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
