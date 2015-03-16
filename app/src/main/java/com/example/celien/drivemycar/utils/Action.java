package com.example.celien.drivemycar.utils;


public enum Action {

    SAVE_USER("saveUser"),
    AUTHENTICATE("authenticate"),
    SAVE_CAR("saveCar"),
    MODIFY_CAR("modifyCar"),
    LOAD_USER("loadUser"),
    CHECK_USERNAME("checkUsername"),
    DELETE_CAR("deleteCar"),
    GET_BRAND("getBrand"),
    LOAD_SPECIFIC_CARS("loadSpecificCars"),
    LOAD_CARS("loadCars");

    private final String name;

    private Action(final String name){
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
