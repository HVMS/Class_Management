package com.globalitians.employees.utility;

import android.Manifest;

public class Permissions {

    public static final String[] STORAGE_PERMISSIONS = new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA
    };

    public static final String[] LOCATION_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION
    };
}
