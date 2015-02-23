package com.example.celien.drivemycar.utils;


public enum Action {

    SAVE_USER("saveUser");

    private final String name;

    private Action(final String name){
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
