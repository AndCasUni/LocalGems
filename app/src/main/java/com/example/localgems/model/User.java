package com.example.localgems.model;

public class User {
    private String id;
    private String name;

    private String surname;

    private String tax_code;

    private String birth_date;
  
  private String email;

    private String[] purchases;


    public User(){}

    public String getId(){ return id;}

    public String[] getPurchases() { return purchases;}
  
      public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getPassword() {
        return password;
    }
      public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

}
