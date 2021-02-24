package com.example;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class ControlCalling extends ReactContextBaseJavaModule {

    ReactApplicationContext mContext;
    private static Callback setCallback;

    public ControlCalling(ReactApplicationContext context) {
        super(context);
        this.mContext = context;
    }

    @NonNull
    @Override
    public String getName() {
        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @ReactMethod
    public void endCall(Callback myCallback) {
        new OngoingCall().hangup();
        myCallback.invoke("Ketan");

    }

}