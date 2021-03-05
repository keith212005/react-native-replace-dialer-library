package com.reactlibrary;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.role.RoleManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.telecom.Call;
import android.telecom.TelecomManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.PermissionAwareActivity;
import com.facebook.react.modules.core.PermissionListener;

import java.util.Set;

import static android.content.Context.POWER_SERVICE;

public class ReplaceDialerModule extends ReactContextBaseJavaModule implements PermissionListener, LifecycleEventListener {

    private final ReactApplicationContext mContext;

    // for default dialer
    AudioManager audioManager;
    TelecomManager telecomManager;
    private static final int RC_DEFAULT_PHONE = 3289;

    @RequiresApi(api = Build.VERSION_CODES.R)
    public ReplaceDialerModule(ReactApplicationContext context) {
        super(context);
        this.mContext = context;
        audioManager = (AudioManager) this.mContext.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        telecomManager = (TelecomManager) this.mContext.getSystemService(Context.TELECOM_SERVICE);
        mContext.addLifecycleEventListener(this);
    }

    @Override
    public String getName() {
        return "ReplaceDialer";
    }

    // returns true if app set as default dialer else false
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

    // set default dialer alert
    @ReactMethod
    public void setDefaultDialer(Callback myCallback) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
            Intent intent = new Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER);
            intent.putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, this.mContext.getPackageName());
            this.mContext.startActivityForResult(intent, RC_DEFAULT_PHONE, new Bundle());
            myCallback.invoke(true);
        } else {
            RoleManager roleManager = (RoleManager) mContext.getSystemService(Context.ROLE_SERVICE);
            Intent intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER);
            mContext.startActivityForResult(intent, RC_DEFAULT_PHONE, new Bundle()); // you need to define CHANGE_DEFAULT_DIALER as a static final int
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
        intent.putExtra("phone_number", phoneNumber);
        this.mContext.startActivity(intent);
        myCallback.invoke(phoneNumber);
    }

    @Override
    public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        return false;
    }

    // disconnects call
    @RequiresApi(api = Build.VERSION_CODES.R)
    @ReactMethod
    public void disconnectCall() {
        try {
            TelecomManager tm = null;
            tm = (TelecomManager) mContext.getSystemService(Context.TELECOM_SERVICE);
            if (tm != null) {
                boolean success = false;
                ActivityCompat.requestPermissions(mContext.getCurrentActivity(),
                        new String[]{Manifest.permission.ANSWER_PHONE_CALLS},
                        110);
                Thread.sleep(100);
                if (mContext.checkSelfPermission(Manifest.permission.ANSWER_PHONE_CALLS) == PackageManager.PERMISSION_GRANTED) {
                    success = tm.endCall();
                }
                Log.d("TAG", "disconnectCall: " + success);

            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, "FATAL ERROR: could not connect to telephony subsystem", Toast.LENGTH_LONG).show();
        }
    }

    // Turn speaker on/off
    @ReactMethod
    public void toggleSpeakerOnOff() {
        audioManager.setSpeakerphoneOn(audioManager.isSpeakerphoneOn() ? false : true);
    }

    // Turn mic on/off
    @ReactMethod
    public void toggleMicOnOff() {
        audioManager.setMicrophoneMute(audioManager.isMicrophoneMute() ? false : true);
    }

    // Retun the name of the connected device
    @ReactMethod
    public void getBluetoothName(Callback callback) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                callback.invoke(device.getName());
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @ReactMethod
    public void toggleBluetoothOnOff() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                String name = device.getName();
            }
        }
    }

    // Accept incoming call
    @RequiresApi(api = Build.VERSION_CODES.R)
    @ReactMethod
    public void acceptCall() {
        TelecomManager tm = (TelecomManager) mContext
                .getSystemService(Context.TELECOM_SERVICE);

        if (tm == null) {
            throw new NullPointerException("tm == null");
        }

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        tm.acceptRingingCall();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @ReactMethod
    public void closeCurrentView() {
        mContext.getCurrentActivity().finishAndRemoveTask();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @ReactMethod
    public void makeConferenceCall() {
        TelecomManager tm = (TelecomManager) mContext
                .getSystemService(Context.TELECOM_SERVICE);

        if (tm == null) {
            throw new NullPointerException("tm == null");
        }

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Uri uri = Uri.fromParts("tel", "12345", null);
        Bundle extras = new Bundle();
        extras.putBoolean(TelecomManager.EXTRA_OUTGOING_CALL_EXTRAS, true);
        tm.placeCall(uri, extras);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @ReactMethod
    public void holdCall(Call.Details details) {

    }

    @Override
    public void onHostResume() {
        Log.d("LFC","resume");
    }

    @Override
    public void onHostPause() {
        Log.d("LFC","puaese");

    }

    @Override
    public void onHostDestroy() {
        Log.d("LFC","destroy");

    }
}
