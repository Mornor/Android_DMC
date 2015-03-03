package com.example.celien.drivemycar.models;

public class Car {
    private int id;
    private String brand;
    private String model;
    private String fuel;
    private double avg_cons; // Average consumption
    private double c02_cons;
    private double htva_price;
    private double leasing_price;

    public Car(){}

    /*Getters and Setter*/
    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getFuel() {
        return fuel;
    }

    public void setFuel(String fuel) {
        this.fuel = fuel;
    }

    public double getAvg_cons() {
        return avg_cons;
    }

    public void setAvg_cons(double avg_cons) {
        this.avg_cons = avg_cons;
    }

    public double getC02_cons() {
        return c02_cons;
    }

    public void setC02_cons(double c02_cons) {
        this.c02_cons = c02_cons;
    }

    public double getHtva_price() {
        return htva_price;
    }

    public void setHtva_price(double htva_price) {
        this.htva_price = htva_price;
    }

    public double getLeasing_price() {
        return leasing_price;
    }

    public void setLeasing_price(double leasing_price) {
        this.leasing_price = leasing_price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
