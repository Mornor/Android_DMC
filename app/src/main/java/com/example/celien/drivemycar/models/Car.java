package com.example.celien.drivemycar.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Car implements Parcelable {
    private int id;
    private String brand;
    private String model;
    private String licencePlate;
    private String fuel;
    private int nbSits;
    private double avg_cons; // Average consumption
    private double c02_cons;
    private double htva_price;
    private double leasing_price;

    public Car(){}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(brand);
        dest.writeString(model);
        dest.writeInt(nbSits);
        dest.writeString(licencePlate);
        dest.writeString(fuel);
        dest.writeDouble(avg_cons);
        dest.writeDouble(c02_cons);
        dest.writeDouble(htva_price);
        dest.writeDouble(leasing_price);
    }

    public static final Creator<Car> CREATOR = new Creator<Car>() {
        @Override
        public Car createFromParcel(Parcel source) {
            Car car             = new Car();
            car.id              = source.readInt();
            car.brand           = source.readString();
            car.model           = source.readString();
            car.id              = source.readInt();
            car.licencePlate    = source.readString();
            car.fuel            = source.readString();
            car.avg_cons        = source.readDouble();
            car.c02_cons        = source.readDouble();
            car.htva_price      = source.readDouble();
            car.leasing_price   = source.readDouble();
            return car;
        }

        @Override
        public Car[] newArray(int size) {
            return new Car[size];
        }
    };

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

    public String getLicencePlate() {
        return licencePlate;
    }

    public void setLicencePlate(String licencePlate) {
        this.licencePlate = licencePlate;
    }

    public int getNbSits() {
        return nbSits;
    }

    public void setNbSits(int nbSits) {
        this.nbSits = nbSits;
    }
}
