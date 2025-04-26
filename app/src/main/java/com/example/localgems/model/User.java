package com.example.localgems.model;

public class User {
    private String id;
    private String first_name;

    private String last_name;

    private String tax_code;

    private String birth_date;

  private String email;

    private String[] purchases;


    public User(){}

    // Just for demo purposes
    public User(String email, String first_name, String last_name, String birth_date, String password) {
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.birth_date = birth_date;
        this.purchases = new String[0];
    }

    public String getId(){ return id;}
    public void setId(String id) {
        this.id = id;
    }
    public String[] getPurchases() { return purchases;}

      public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return first_name;
    }

    public String getLastName() {
        return last_name;
    }

    public String getBirthDate() {
        return birth_date;
    }

      public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.first_name = firstName;
    }

    public void setLastName(String lastName) {
        this.last_name = lastName;
    }

    public void setBirthDate(String birthDate) {
        this.birth_date = birthDate;
    }

    
}
