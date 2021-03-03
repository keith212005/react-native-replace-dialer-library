package com.reactlibrary;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import android.Manifest;
import android.app.Activity;
import android.app.role.RoleManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;

import android.content.Context;
import android.content.Intent;

import com.facebook.react.modules.core.PermissionAwareActivity;
import com.facebook.react.modules.core.PermissionListener;


import android.os.IBinder;
import android.provider.CallLog;
import android.telecom.Call;
import android.telecom.CallScreeningService;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import static android.Manifest.permission.CALL_PHONE;

public class ReplaceDialerModule extends ReactContextBaseJavaModule implements PermissionListener {

    private final ReactApplicationContext mContext;

    // for default dialer
    AudioManager audioManager;
    TelecomManager telecomManager;
    private static final int RC_DEFAULT_PHONE = 3289;

    public ReplaceDialerModule(ReactApplicationContext context) {
        super(context);
        this.mContext = context;
        audioManager = (AudioManager) this.mContext.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        audioManager.setMode(AudioManager.MODE_IN_CALL);

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
    @RequiresApi(api = Build.VERSION_CODES.Q)
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

    @ReactMethod
    public void makeConferenceCall(final Callback callback){

    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @ReactMethod
    public void holdCall(Call.Details details){

    }


    @RequiresApi(api = Build.VERSION_CODES.R)
    @ReactMethod
    public void onScreenCall(Callback callback) {
//        final Call call = getCurrentActivity().getci
//            if(CallLog.Calls.getLastOutgoingCall .getCallDirection() == Call.Details.DIRECTION_INCOMING) {
//                CallScreeningService.CallResponse.Builder response = new CallScreeningService.CallResponse.Builder();
//                response.setDisallowCall(false);
//                response.setRejectCall(false);
//                response.setSilenceCall(false);
//                response.setSkipCallLog(false);
//                response.setSkipNotification(false);
//                details.getHandle(); //This is the calling number
//                callback.invoke(response.build());
//        }
    }




}
