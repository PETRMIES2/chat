package com.sope.domain;

import org.joda.time.Hours;

//@Component
public class ResourceService {
// Add to .properties-file
//    https://console.firebase.google.com/project/sope-111111/settings/general/android:com.sope
    public static final String FIREBASE_TARGET_URL = "https://fcm.googleapis.com/fcm/send";
    public static final String FIREBASE_AUTHORIZATION_KEY = "AIzaSyCsv4i_Orqsm3VhAGnx1_7raEG4m2K6Pkc";

    public static final int EXTENDED_BAN_MINUTES = Hours.EIGHT.toStandardMinutes().getMinutes();
    public static final int DEFAULT_BAN_MINUTES = 30;

    public static final int EXTENDED_BAN = 5;
    public static final int BAN_LIMIT = 3;
    
    public static final String UTF_8 = "UTF-8";
    
}
