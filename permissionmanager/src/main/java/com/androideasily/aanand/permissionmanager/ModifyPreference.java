package com.androideasily.aanand.permissionmanager;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Anand on 15-12-2017.
 */

class ModifyPreference {
    private final SharedPreferences sharedPreferences;

    ModifyPreference(Context context) {
        sharedPreferences = context.getSharedPreferences("PERMISSION_STATUS", Context.MODE_PRIVATE);
    }
    boolean checkPreference(String permission) {
        return sharedPreferences.getBoolean(permission, true);
    }
    void updatePreference(String permission, boolean permissionStatus) {
        SharedPreferences.Editor sharedPreferenceEditor = sharedPreferences.edit();
        sharedPreferenceEditor.putBoolean(permission, permissionStatus);
        sharedPreferenceEditor.apply();
    }
}
