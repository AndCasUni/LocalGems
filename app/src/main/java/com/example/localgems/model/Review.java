package com.example.localgems.model;

public class Review {
    private String author;
    private String content;
    private String stars;

    public Review() {
        // Costruttore vuoto richiesto da Firestore
    }

    public Review(String author, String content, String stars) {
        this.author = author != null ? author : "Anonimo";
        this.content = content != null ? content : "Nessun contenuto";
        this.stars = stars != null ? stars : "0";
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStars() {
        return stars;
    }

    public void setStars(String stars) {
        this.stars = stars;
    }
}
