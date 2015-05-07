package com.example.celien.drivemycar.utils;


public class Constants {

    private final static String ngrokTunnel = ".ngrok.io";
    private final static String localTunnel = ".localtunnel.me";
    private final static String tunnelToUse = ngrokTunnel;

    /*Url to use to access to Play! server*/
    public final static String SAVE_USER_URL                = "https://cafca"+tunnelToUse+"/register";
    public final static String AUTHENTICATE_URL             = "https://cafca"+tunnelToUse+"/android/login";
    public final static String SAVE_CAR_URL                 = "https://cafca"+tunnelToUse+"/android/save_car";
    public final static String MODIFY_CAR_URL               = "https://cafca"+tunnelToUse+"/android/modify_car";
    public final static String DELETE_CAR_URL               = "https://cafca"+tunnelToUse+"/android/delete_car";
    public final static String CONFIRM_RENT_URL             = "https://cafca"+tunnelToUse+"/android/confirm_rent";
    public static final String REFFUTE_RENT_URL             = "https://cafca"+tunnelToUse+"/android/refute_rent";
    public static final String LOAD_USER_URL                = "https://cafca"+tunnelToUse+"/android/get_user";
    public static final String LOAD_CARS_URL                = "https://cafca"+tunnelToUse+"/android/get_cars";
    public static final String CHECK_USER_UNIQUE_URL        = "https://cafca"+tunnelToUse+"/android/username_unique";
    public static final String LOAD_ALL_CARS_BRAND          = "https://cafca"+tunnelToUse+"/android/get_all_cars_brand";
    public static final String LOAD_SPECIFIC_CARS_URL       = "https://cafca"+tunnelToUse+"/android/get_specific_cars";
    public static final String GET_NOTIFS_URL               = "https://cafca"+tunnelToUse+"/android/get_notifs";
    public static final String SAVE_REQUEST_URL             = "https://cafca"+tunnelToUse+"/android/set_request";
    public static final String UPDATE_REQUEST_URL           = "https://cafca"+tunnelToUse+"/android/update_request";
    public static final String GET_REQUEST_BY_DATE_URL      = "https://cafca"+tunnelToUse+"/android/get_request_by_date";
    public static final String GET_REQUEST_DATA_URL         = "https://cafca"+tunnelToUse+"/android/get_request_data";
    public static final String GET_AGREED_OWNERS_URL        = "https://cafca"+tunnelToUse+"/android/get_agreed_owners";
    public static final String NOTIFY_SELECTED_OWNER_URL    = "https://cafca"+tunnelToUse+"/android/notify_selected_owner";
    public static final String GET_TRANSACTIONS_URL         = "https://cafca"+tunnelToUse+"/android/get_transactions";
    public static final String SET_ODOMETER_URL             = "https://cafca"+tunnelToUse+"/android/set_odometer";
    public static final String GET_TRANSACTION_STATE_URL    = "https://cafca"+tunnelToUse+"/android/get_transaction";
}
