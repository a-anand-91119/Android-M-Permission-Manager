package com.androideasily.aanand.permissionmanager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Anand on 15-12-2017.
 */

public class PermissionManager {
    private final Context context;
    private final ModifyPreference storedPreference;
    private static PermissionManager permissionManager;
    private LayoutInflater layoutInflater;
    private View dialogView;
    public static final int GROUP_PERMISSION_REQUEST = 102;
    public static final int SINGLE_PERMISSION_REQUEST = 101;

    private PermissionManager(Context context) {
        this.context = context;
        storedPreference = new ModifyPreference(context);
        layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public static synchronized PermissionManager getInstance(Context context) {
        if (permissionManager == null)
            permissionManager = new PermissionManager(context);
        return permissionManager;
    }

    public boolean checkPermission(String permission) {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    public void getPermission(final String permission) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        dialogView = layoutInflater.inflate(R.layout.message_dialog, null);
        builder.setView(dialogView);
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                performGetPermission(permission);
            }
        });
        builder.setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        TextView permissionMessage = dialogView.findViewById(R.id.tv_message);
        ImageView permissionIcon = dialogView.findViewById(R.id.ic_permission);
        permissionIcon.setImageResource(getIcon(permission));
        permissionIcon.setMaxHeight(50);
        permissionIcon.setMaxWidth(50);
        permissionMessage.setText(R.string.default_message);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        Point size = new Point();

        Display display = Objects.requireNonNull(window).getWindowManager().getDefaultDisplay();
        display.getSize(size);

        int width = size.x;

        window.setLayout((int) (width * 0.8), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
    }

    public void getPermission(final String permission, String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        dialogView = layoutInflater.inflate(R.layout.message_dialog, null);
        builder.setView(dialogView);
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                performGetPermission(permission);
            }
        });
        builder.setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        TextView permissionMessage = dialogView.findViewById(R.id.tv_message);
        ImageView permissionIcon = dialogView.findViewById(R.id.ic_permission);
        permissionIcon.setImageResource(getIcon(permission));
        permissionMessage.setText(message);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        Point size = new Point();

        Display display = Objects.requireNonNull(window).getWindowManager().getDefaultDisplay();
        display.getSize(size);

        int width = size.x;

        window.setLayout((int) (width * 0.8), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
    }

    private void performGetPermission(String permission) {
        if (storedPreference.checkPreference(permission)) {
            requestSinglePermission(permission);
        } else {
            openPermissionSettings();
        }
    }

    private void openPermissionSettings() {
        Intent permissionIntent = new Intent();
        permissionIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        permissionIntent.setData(uri);
        context.startActivity(permissionIntent);
    }

    private void requestSinglePermission(String permission) {
        ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, SINGLE_PERMISSION_REQUEST);
    }

    private void updatePreference(String permission, boolean b) {
        storedPreference.updatePreference(permission, b);
    }

    public void requestGroupPermission(String[] permissions) {
        ArrayList<String> permissionNeeded = new ArrayList<>();
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED && storedPreference.checkPreference(permission))
                permissionNeeded.add(permission);
        }
        String[] groupRequest = new String[permissionNeeded.size()];
        permissionNeeded.toArray(groupRequest);
        if (groupRequest.length == 0) {
            Toast.makeText(context, "No Permission Can Be Requested In Group", Toast.LENGTH_SHORT).show();
            openPermissionSettings();
        } else
            ActivityCompat.requestPermissions((Activity) context, groupRequest, GROUP_PERMISSION_REQUEST);
    }

<<<<<<< HEAD
    public JSONArray updatePermissions(int requestCode, String[] permissions, int[] grantResults) {
        JSONArray returnArray = new JSONArray();
        if (requestCode == GROUP_PERMISSION_REQUEST) {
            if (grantResults.length > 0) {
                for (int i = 0; i < grantResults.length; i++) {
                    JSONObject permissionObject = new JSONObject();
                    try {
                        permissionObject.accumulate("Permission", permissions[i]);
                        if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                            permissionObject.accumulate("Status", "PERMISSION DENIED");
                            if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permissions[i])) {
                                updatePreference(permissions[i], false);
                                permissionObject.accumulate("Don\'t Ask Again", "CHECKED");
                            }
                        } else {
                            permissionObject.accumulate("Status", "PERMISSION GRANTED");
                            updatePreference(permissions[i], true);
                        }
                        returnArray.put(permissionObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (requestCode == SINGLE_PERMISSION_REQUEST) {
            JSONObject permissionObject = new JSONObject();
            try {
                permissionObject.accumulate("Permission", permissions[0]);
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updatePreference(permissions[0], true);
                    permissionObject.accumulate("Status", "PERMISSION GRANTED");
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    permissionObject.accumulate("Status", "PERMISSION DENIED");
                    if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permissions[0])) {
                        updatePreference(permissions[0], false);
                        permissionObject.accumulate("Don\'t Ask Again", "CHECKED");
                    }
                }
                returnArray.put(permissionObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return returnArray;
    }

    private int getIcon(String permission) {
        switch (permission) {
            case Manifest.permission.READ_CALENDAR:
            case Manifest.permission.WRITE_CALENDAR:
                return R.drawable.round_calendar_today_white_18dp;
            case Manifest.permission.CAMERA:
                return R.drawable.round_camera_enhance_white_18dp;
            case Manifest.permission.READ_CONTACTS:
            case Manifest.permission.WRITE_CONTACTS:
            case Manifest.permission.GET_ACCOUNTS:
                return R.drawable.round_contacts_white_18dp;
            case Manifest.permission.ACCESS_FINE_LOCATION:
            case Manifest.permission.ACCESS_COARSE_LOCATION:
                return R.drawable.round_location_on_white_18dp;
            case Manifest.permission.RECORD_AUDIO:
                return R.drawable.round_record_voice_over_white_18dp;
            case Manifest.permission.READ_PHONE_STATE:
            case Manifest.permission.READ_PHONE_NUMBERS:
            case Manifest.permission.READ_CALL_LOG:
            case Manifest.permission.WRITE_CALL_LOG:
            case Manifest.permission.CALL_PHONE:
            case Manifest.permission.ANSWER_PHONE_CALLS:
            case Manifest.permission.PROCESS_OUTGOING_CALLS:
                return R.drawable.round_phone_white_18dp;
            case Manifest.permission.ADD_VOICEMAIL:
                return R.drawable.round_voicemail_white_18dp;
            case Manifest.permission.USE_SIP:
                return R.drawable.round_dialer_sip_white_18dp;
            case Manifest.permission.BODY_SENSORS:
                return R.drawable.round_wifi_white_18dp;
            case Manifest.permission.SEND_SMS:
            case Manifest.permission.READ_SMS:
            case Manifest.permission.RECEIVE_SMS:
            case Manifest.permission.RECEIVE_MMS:
            case Manifest.permission.RECEIVE_WAP_PUSH:
                return R.drawable.round_sms_white_18dp;
            case Manifest.permission.READ_EXTERNAL_STORAGE:
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                return R.drawable.round_sd_storage_white_18dp;
            default:
                return R.drawable.baseline_warning_white_18dp;
        }
    public boolean updatePermissions(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            updatePreference(requestCode, true);
            return true;
        } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED)
            if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permissions[0]))
                updatePreference(requestCode, false);
        return false;
    }
}
