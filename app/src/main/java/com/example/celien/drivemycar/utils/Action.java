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
    SAVE_REQUEST("saveRequest"),
    GET_NOTIFS("getNotifications"),
    GET_REQUEST_BY_DATE("getRequestByDate"),
    GET_REQUEST_DATA("getRequestData"),
    UPDATE_REQUEST_STATE("udpateRequestState"),
    CONFIRM_RENT("confirmRent"),
    REFUTE_RENT("refuteRent"),
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
