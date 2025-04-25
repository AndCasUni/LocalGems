package com.example.localgems.model;

public class User {
    private String id;
    private String name;

    private String surname;

    private String tax_code;

    private String birth_date;

    private String[] purchases;


    public User(){}

    public String getId(){ return id;}

    public String[] getPurchases() { return purchases;}
}
