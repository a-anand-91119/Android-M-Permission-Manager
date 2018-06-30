package com.androideasily.permissionlibraryproject;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.androideasily.aanand.permissionmanager.PermissionManager;

import org.json.JSONArray;

public class MainActivity extends AppCompatActivity {
    PermissionManager permissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permissionManager = PermissionManager.getInstance(MainActivity.this);
    }

    public void camera(View v) {
        if (permissionManager.checkPermission(Manifest.permission.CAMERA)) {
            Toast.makeText(this, "GRANTED", Toast.LENGTH_SHORT).show();
        } else {
            permissionManager.getPermission(Manifest.permission.CAMERA);
        }
    }

    public void sms(View v) {
        if (permissionManager.checkPermission(Manifest.permission.READ_SMS)) {
            Toast.makeText(this, "GRANTED", Toast.LENGTH_SHORT).show();
        } else {
            permissionManager.getPermission(Manifest.permission.READ_SMS);
        }
    }

    public void rCalender(View v) {
        if (permissionManager.checkPermission(Manifest.permission.READ_CALENDAR)) {
            Toast.makeText(this, "GRANTED", Toast.LENGTH_SHORT).show();
        } else {
            permissionManager.getPermission(Manifest.permission.READ_CALENDAR);
        }
    }

    public void wCalender(View v) {
        if (permissionManager.checkPermission(Manifest.permission.WRITE_CALENDAR)) {
            Toast.makeText(this, "GRANTED", Toast.LENGTH_SHORT).show();
        } else {
            permissionManager.getPermission(Manifest.permission.WRITE_CALENDAR);
        }
    }

    public void location(View v) {
        if (permissionManager.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(this, "GRANTED", Toast.LENGTH_SHORT).show();
        } else {
            permissionManager.getPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    public void group(View v) {
        String permissions[] = {
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_SMS,
                Manifest.permission.CAMERA};
        permissionManager.requestGroupPermission(permissions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        JSONArray permissionsReturn = permissionManager.updatePermissions(requestCode, permissions, grantResults);
        Log.i("PERMISSION RETURN", String.valueOf(permissionsReturn));
    }
}
