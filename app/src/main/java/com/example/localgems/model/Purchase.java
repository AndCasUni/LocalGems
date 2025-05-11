package com.example.localgems.model;

import java.util.Date;

public class Purchase {
    private String user_id;
    private String id; // verrà mappato a mano con doc.getId()
    private Date timestamp;
    private double total_price;

    public Purchase() {}

    public String getUser_id() { return user_id; }
    public void setUser_id(String user_id) { this.user_id = user_id; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public double getTotal_price() { return total_price; }
    public void setTotal_price(double total_price) { this.total_price = total_price; }
}

