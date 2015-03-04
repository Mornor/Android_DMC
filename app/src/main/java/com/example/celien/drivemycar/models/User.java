package com.example.celien.drivemycar.models;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class User implements Parcelable{

    private int id;
    private String name;
    private String username;
    private String email;
    private String password;
    private String phoneNumber;
    private List<Car> cars;

    public User(String name, String email, String username, String password){
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public User(String name, String email, String username, String password, List<Car> cars){
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.cars = cars;
    }

    public User(Parcel source){
        name = source.readString();
        username = source.readString();
        email = source.readString();
        password = source.readString();
        cars = new ArrayList<Car>();
        source.readList(cars, null);
    }

    public User(){}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(password);
        dest.writeList(cars);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String toString(){

        String[]cars = new String[this.cars.size()];
        for(int i = 0 ; i < cars.length ; i++){
            cars[i] += this.cars.get(i).getBrand();
        }

        return id + " - " +name+ " - " +username+ " - " +email+ " - " +phoneNumber+ " - " +cars;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Car> getCars() {
        return cars;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
