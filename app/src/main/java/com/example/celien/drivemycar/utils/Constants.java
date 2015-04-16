package com.example.celien.drivemycar.utils;


public class Constants {

    private final static String ngrokTunnel = ".ngrok.com";
    private final static String localTunnel = ".localtunnel.me";
    private final static String tunnelToUse = localTunnel;

    /*Url to use to access to Play! server*/
    public final static String SAVE_USER_URL            = "https://cafca"+tunnelToUse+"/register";
    public final static String AUTHENTICATE_URL         = "https://cafca"+tunnelToUse+"/android/login";
    public final static String SAVE_CAR_URL             = "https://cafca"+tunnelToUse+"/android/save_car";
    public final static String MODIFY_CAR_URL           = "https://cafca"+tunnelToUse+"/android/modify_car";
    public final static String DELETE_CAR_URL           = "https://cafca"+tunnelToUse+"/android/delete_car";
    public final static String CONFIRM_RENT_URL         = "https://cafca"+tunnelToUse+"/android/confirm_rent";
    public static final String LOAD_USER_URL            = "https://cafca"+tunnelToUse+"/android/get_user";
    public static final String LOAD_CARS_URL            = "https://cafca"+tunnelToUse+"/android/get_cars";
    public static final String CHECK_USER_UNIQUE_URL    = "https://cafca"+tunnelToUse+"/android/username_unique";
    public static final String LOAD_ALL_CARS_BRAND      = "https://cafca"+tunnelToUse+"/android/get_all_cars_brand";
    public static final String LOAD_SPECIFIC_CARS_URL   = "https://cafca"+tunnelToUse+"/android/get_specific_cars";
    public static final String GET_NOTIFS_URL           = "https://cafca"+tunnelToUse+"/android/get_notifs";
    public static final String SAVE_REQUEST_URL         = "https://cafca"+tunnelToUse+"/android/set_request";

}
