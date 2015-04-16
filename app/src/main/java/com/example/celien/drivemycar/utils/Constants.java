package com.example.celien.drivemycar.utils;


public class Constants {

    private final static String ngrokTunnel = ".ngrok.com";
    private final static String localTunnel = ".localtunnel.me";
    private final static String tunnelToUse = localTunnel;

    public final static String SAVE_USER_URL            = "http://cafca"+tunnelToUse+"/register";
    public final static String AUTHENTICATE_URL         = "http://cafca"+tunnelToUse+"/android/login";
    public final static String SAVE_CAR_URL             = "http://cafca"+tunnelToUse+"/android/save_car";
    public final static String MODIFY_CAR_URL           = "http://cafca"+tunnelToUse+"/android/modify_car";
    public final static String DELETE_CAR_URL           = "http://cafca"+tunnelToUse+"/android/delete_car";
    public final static String CONFIRM_RENT_URL         = "http://cafca"+tunnelToUse+"/android/confirm_rent";
    public static final String LOAD_USER_URL            = "http://cafca"+tunnelToUse+"/android/get_user";
    public static final String LOAD_CARS_URL            = "http://cafca"+tunnelToUse+"/android/get_cars";
    public static final String CHECK_USER_UNIQUE_URL    = "http://cafca"+tunnelToUse+"/android/username_unique";
    public static final String LOAD_ALL_CARS_BRAND      = "http://cafca"+tunnelToUse+"/android/get_all_cars_brand";
    public static final String LOAD_SPECIFIC_CARS_URL   = "http://cafca"+tunnelToUse+"/android/get_specific_cars";
    public static final String GET_NOTIFS_URL           = "http://cafca"+tunnelToUse+"/android/get_notifs";
    public static final String SAVE_REQUEST_URL         = "http://cafca"+tunnelToUse+"/android/set_request";

}
