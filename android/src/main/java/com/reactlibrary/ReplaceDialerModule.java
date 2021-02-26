package com.reactlibrary;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import android.Manifest;
import android.app.Activity;
import android.app.role.RoleManager;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;

import android.content.Context;
import android.content.Intent;

import com.facebook.react.modules.core.PermissionAwareActivity;
import com.facebook.react.modules.core.PermissionListener;


import android.os.IBinder;
import android.telecom.TelecomManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.lang.reflect.Method;

import static android.Manifest.permission.CALL_PHONE;

public class ReplaceDialerModule extends ReactContextBaseJavaModule implements PermissionListener {

    private final ReactApplicationContext mContext;
    private static Callback setCallback;

    // for default dialer
    private TelecomManager telecomManager;
    private static final int RC_DEFAULT_PHONE = 3289;
    private static final int RC_PERMISSION = 3810;
    private static final int PERMISSION_REQUEST_CODE = 200;

    private static final int REQUEST_CODE_SET_DEFAULT_DIALER = 123;

    public ReplaceDialerModule(ReactApplicationContext context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public String getName() {
        return "ReplaceDialer";
    }

    @ReactMethod
    public void isDefaultDialer(Callback myCallback) {

        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.R) {
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

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @ReactMethod
    public void setDefaultDialer(Callback myCallback) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            RoleManager roleManager = (RoleManager) mContext.getSystemService(Context.ROLE_SERVICE);
            Intent intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER);
            mContext.startActivityForResult(intent, RC_DEFAULT_PHONE, new Bundle()); // you need to define CHANGE_DEFAULT_DIALER as a static final int
            myCallback.invoke(true);
        } else {
            Intent intent = new Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER);
            intent.putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, this.mContext.getPackageName());
            this.mContext.startActivityForResult(intent, RC_DEFAULT_PHONE, new Bundle());
            myCallback.invoke(true);
        }
    }

    @ReactMethod
    public void callPhoneNumber(String phoneNumber, Callback myCallback) {

      PermissionAwareActivity activity = (PermissionAwareActivity) getCurrentActivity();
        if (activity == null) {
            // Handle null case
        }
            Toast.makeText(mContext, "" + phoneNumber, Toast.LENGTH_SHORT).show();
            Uri uri = Uri.parse("tel:" + phoneNumber.trim());
            Intent intent = new Intent(Intent.ACTION_CALL, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("phone_number",phoneNumber);
            this.mContext.startActivity(intent);
            myCallback.invoke(phoneNumber);
    }

    @Override
    public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Toast.makeText(mContext, "requestcode : "+requestCode, Toast.LENGTH_SHORT).show();
        return false;
    }

    @ReactMethod
    public void disconnectCall() {
        try {
            TelecomManager tm = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                tm = (TelecomManager) mContext.getSystemService(Context.TELECOM_SERVICE);
                Log.d("TAG", "disconnectCall: 1");
                if (tm != null) {
                    boolean success = false;
                    Log.d("TAG", "disconnectCall: 2");
                    ActivityCompat.requestPermissions(mContext.getCurrentActivity(),
                            new String[]{Manifest.permission.ANSWER_PHONE_CALLS},
                            110);
                    Thread.sleep(100);
                    Log.d("TAG", "disconnectCall: 3");
                    if (mContext.checkSelfPermission(Manifest.permission.ANSWER_PHONE_CALLS) == PackageManager.PERMISSION_GRANTED) {
                        Log.d("TAG", "disconnectCall: 4");
                        success = tm.endCall();
                    }
                    Log.d("TAG", "disconnectCall: 5");
                   Log.d("TAG", "disconnectCall: " + success);
                   // success == true if call was terminated.
               }
           } else {
               String serviceManagerName = "android.os.ServiceManager";
               String serviceManagerNativeName = "android.os.ServiceManagerNative";
               String telephonyName = "com.android.internal.telephony.ITelephony";
               Class telephonyClass;
               Class telephonyStubClass;
               Class serviceManagerClass;
               Class serviceManagerNativeClass;
               Method telephonyEndCall;
               Object telephonyObject;
               Object serviceManagerObject;

               telephonyClass = Class.forName(telephonyName);
               telephonyStubClass = telephonyClass.getClasses()[0];
               serviceManagerClass = Class.forName(serviceManagerName);
               serviceManagerNativeClass = Class.forName(serviceManagerNativeName);

               Method getService =
                       serviceManagerClass.getMethod("getService", String.class);

               Method tempInterfaceMethod = serviceManagerNativeClass.getMethod(
                       "asInterface", IBinder.class);

               Binder tmpBinder = new Binder();
               tmpBinder.attachInterface(null, "fake");

               serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
               IBinder retbinder = (IBinder) getService.invoke(serviceManagerObject, "phone");
               Method serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder.class);

               telephonyObject = serviceMethod.invoke(null, retbinder);
               telephonyEndCall = telephonyClass.getMethod("endCall");
               telephonyEndCall.invoke(telephonyObject);

           }
       } catch (Exception e) {
           e.printStackTrace();
           Toast.makeText(mContext , "FATAL ERROR: could not connect to telephony subsystem", Toast.LENGTH_LONG).show();
       }
   }



}
