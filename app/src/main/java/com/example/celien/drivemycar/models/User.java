package com.example.celien.drivemycar.models;


public class User {

    private String name;
    private String username;
    private String email;
    private String password;
    private String specificity; // Cf. DMC, for example if a smoker.

    public User(String name, String email, String username, String password, String specificity){
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    /*Getters and Setters*/
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSpecificity() {
        return specificity;
    }

    public void setSpecificity(String specificity) {
        this.specificity = specificity;
    }

}