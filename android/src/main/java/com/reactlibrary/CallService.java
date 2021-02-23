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

public class CallService extends ReactContextBaseJavaModule {

    private final ReactApplicationContext mContext;
    private static Callback setCallback;


    public CallService(ReactApplicationContext context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public String getName() {
        return "CallService";
    }


}
