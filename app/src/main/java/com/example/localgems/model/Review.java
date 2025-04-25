package com.example.localgems.model;

public class Review
{
    private String userId;
    private String comment;
    private String date;
    private Integer rating;

    public Review(){}

    public String getUserId(){ return userId;}
    public void setUserId(String user) { userId = user;}

    public String getComment(){ return comment;}
    public void setComment(String comment) { this.comment = comment;}

    public String getDate (){ return date;}
    public void setDate(String date) { this.date = date;}

    public Integer getRating(){ return rating;}
    public void setRating(Integer rating) { this.rating = rating ;}

}
