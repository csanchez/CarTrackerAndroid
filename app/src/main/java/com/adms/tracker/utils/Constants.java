package com.adms.tracker.utils;

import android.content.Context;
import android.content.Intent;

/**
 * Created by adms on 09/11/14.
 */
public class Constants {

    public static final String DEVELOPMENT_MODE = "DEV";

    //initial configuration
    //public static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 10/60; // 10 segundos

    // The minimum distance to change Updates in meters
    //public static final long MIN_DISTANCE_CHANGE_FOR_UPDATES_GPS = 10; // 0 metros

    // The minimum time between updates in milliseconds for GPS
    public static final long LOCATION_INTERVAL = 60000; // 0 minutos
    public static final int FASTEST_INTERVAL = 50000;

    //Configuration for GCM
    //public static String SENDER_ID = "1058856050718";  //"AIzaSyDbX5n76NZ1hXDqQdlGtMK2KQO3ip2WKP0";
    //public static String SERVER_API_ID = "AIzaSyAUwwmdHNvtOtIWgVVPA1Q9GvyIlIklqAs";
    //public static final String DISPLAY_MESSAGE_ACTION ="com.androidhive.pushnotifications.DISPLAY_MESSAGE";

    /**
     * Tag used on log messages.
     */
    public static final String WELCOME_TAG = "WELCOME";
    public static final String LOGIN_TAG = "LOGIN";
    public static final String SYSTEM_REQUERIMENTS_TAG = "LOGIN";
    public static final String TRACKER = "TRACKER";
    public static final String TAG_GCM = "GCM_TRACKER";
    public static final String TAG_REGISTRATION_INTENT_SERVICE = "REG_INTENT_SERVICE";
    public static final String TAG_GCM_INTENT_SERVICE = "GCM_INTENT_SERVICE";
    public static final String TAG_LOCATION_SERVICE = "LOCATION_SERVICE";
    public static final String TAG_LOCATION_SERVICE_WITHOUT_GOOGLE_PLAY = "LOCATION_SERVICE_W_GP";



    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String PROPERTY_APP_VERSION = "appVersion";
    public static final String EXTRA_MESSAGE = "message";



    //Constantas for User

    public static final String GCM_TOKEN            = "com.adms.tracker.GCM_TOKEN";
    public static final String INSTANCE_ID          = "com.adms.tracker.INSTANCE_ID";
    public final static String USER_NAME            = "com.adms.tracker.USER_NAME";
    public final static String USER_STATUS          = "com.adms.tracker.USER_STATUS_KEY";
    public final static String USER_PLAN            = "com.adms.tracker.USER_PLAN";
    public static final String AUTH_TOKEN           = "com.adms.tracker.AUTH_TOKEN";
    public final static String LOGED_IN             = "com.adms.tracker.LOGEND_IN";
    public final static String UPDATE_RATE          = "com.adms.tracker.UPDATE_RATE";

    //flags for app status
    public static final String APP_REGISTER_TO_GCM  = "com.adms.tracker.APP_REGISTER_TO_GCM";
    public final static String CONFIGURED            = "com.adms.tracker.CONFIGURED";
    public static final String TOKEN_SENT_TO_SERVER = "com.adms.tracker.TOKEN_SENT_TO_SERVER";
    public static final String REGISTER_COMPLETE    = "com.adms.tracker.REGISTER_COMPLETE";

    //ids for receivers and services
    public final static String REGISTRATION_RECEIVER = "com.adms.tracker.RegistrationBroadcastReceiver";
    public final static String LOCATION_RECEIVER = "com.adms.tracker.LocationBroadcastReceiver";


    //URLS for services
    public final static String USERS_SERVICE_URL    = "http://car-tracker-1000.herokuapp.com";
    public final static String LOCATION_SERVICE_URL = "http://car-tracker-1000.herokuapp.com";

    public final static String USERS_AUTH_URL = "/api/v1/auth";
    public final static String DEVICE_GCM_URL = "/api/v1/gcm/update";
    public final static String LOCATION_POST_URL = "/api/v1/location/update";



    //User status
    public final static int USER_ACTIVE=1;
    public final static int USER_TEST_MODE=2;
    public final static int USER_INACTIVE=3;
    public final static int USER_SUSPENDED=4;
    public final static int USER_CANCEL=5;

    //Type of plan
    public final static int USER_PLAN_A=1;
    public final static int USER_PLAN_B=2;




}
