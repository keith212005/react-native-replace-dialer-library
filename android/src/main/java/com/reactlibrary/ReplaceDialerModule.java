package com.reactlibrary;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import android.Manifest;
import android.app.Activity;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.Window;
import android.view.WindowManager;
import android.os.Bundle;

import android.content.Context;
import android.content.Intent;

import com.facebook.react.bridge.*;
import com.facebook.react.ReactActivity;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.modules.core.PermissionListener;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.react.bridge.Callback;


import android.telecom.TelecomManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import static android.Manifest.permission.CALL_PHONE;

public class ReplaceDialerModule extends ReactContextBaseJavaModule implements PermissionListener {

    private final ReactApplicationContext mContext;
    private static Callback setCallback;

    // for default dialer
    private TelecomManager telecomManager;
    private static final int RC_DEFAULT_PHONE = 3289;
    private static final int RC_PERMISSION = 3810;

    private static final int REQUEST_CODE_SET_DEFAULT_DIALER = 123;

    public ReplaceDialerModule(ReactApplicationContext context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public String getName() {
        return "ReplaceDialer";
    }

    // @ReactMethod
    // public void sampleMethod(String stringArgument, int numberArgument, Callback callback) {
    //     // TODO: Implement some actually useful functionality
    //     callback.invoke("Received numberArgument: " + numberArgument + " stringArgument: " + stringArgument);
    // }

    @ReactMethod
    public void isDefaultDialer(Callback myCallback) {

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.R) {
            myCallback.invoke(false);
            return;
        }

        TelecomManager telecomManager = (TelecomManager) this.mContext.getSystemService(Context.TELECOM_SERVICE);
        if (telecomManager.getDefaultDialerPackage().equals(this.mContext.getPackageName())) {
            myCallback.invoke(true);
        } else {
            myCallback.invoke(false);
        }
    }

    @ReactMethod
    public void setDefaultDialer(Callback myCallback) {
        setCallback = myCallback;
        Intent intent = new Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER);
        intent.putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, this.mContext.getPackageName());
        this.mContext.startActivityForResult(intent, RC_DEFAULT_PHONE, new Bundle());
        myCallback.invoke(true);
    }

    @ReactMethod
    public void callPhoneNumber(String phoneNumber, Callback myCallback) {
        Toast.makeText(mContext, "" + phoneNumber, Toast.LENGTH_SHORT).show();
        Uri uri = Uri.parse("tel:" + phoneNumber.trim());
        Intent intent = new Intent(Intent.ACTION_CALL, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.mContext.startActivity(intent);
        myCallback.invoke(phoneNumber);
    }

    @ReactMethod
    public void endCall(Callback myCallback) {
//        Activity activity = getCurrentActivity();
//        if (activity != null) {
//          Intent intent = new Intent(this, CallActivity.class);
//          intent.putExtra("Call Status", "end call");
//          this.mContext.startActivity(intent);
//          activity.finish();
//        }
    }


    @Override
    public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        return false;
    }
}
