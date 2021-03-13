package com.reactlibrary;

import android.Manifest;
import android.app.Activity;
import android.app.role.RoleManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telecom.Call;
import android.telecom.TelecomManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.PermissionAwareActivity;
import com.facebook.react.modules.core.PermissionListener;

import java.util.Set;


public class ReplaceDialerModule extends ReactContextBaseJavaModule implements PermissionListener, LifecycleEventListener,ActivityEventListener {

    private ReactApplicationContext mContext;

    // for default dialer
    AudioManager audioManager;
    private static final int RC_DEFAULT_PHONE = 3289;

    public ReplaceDialerModule() {
    }

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

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void openCallActivity(Context applicationContext, Call call) {

        try {
            Intent intent = null;
            Class cls = Class.forName("com.example.CallActivity");
            Log.d("classname", "" + cls);
            intent = new Intent(applicationContext, cls).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            String phoneNumber = getPhoneNumber(call);
            Log.d("callActivity", "void start class : " + phoneNumber);
            intent.putExtra("phoneNumber", phoneNumber);
            applicationContext.startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private String getPhoneNumber(Call call) {
        Uri uri = call.getDetails().getHandle();
        Log.d("callActivity", "void start class : " + uri);
        String phoneNumber = uri.toString();
        if (phoneNumber.contains("%2B")) {
            phoneNumber = phoneNumber.replace("%2B", "+");
        }
        phoneNumber = phoneNumber.replace("tel:", "");
        return phoneNumber;
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

    @ReactMethod
    public void toggleSpeakerOnOff() {
        audioManager.setSpeakerphoneOn(audioManager.isSpeakerphoneOn() ? false : true);
    }

    @ReactMethod
    public void toggleMicOnOff() {
        audioManager.setMicrophoneMute(audioManager.isMicrophoneMute() ? false : true);
    }

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

    @RequiresApi(api = Build.VERSION_CODES.R)
    @ReactMethod
    public void acceptCall() {
        new OngoingCall().answer();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @ReactMethod
    public void disconnectCall() {
        new OngoingCall().hangup();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @ReactMethod
    public void closeCurrentView() {
//        RecordService.getInstance().stopRecording();
        mContext.getCurrentActivity().finishAndRemoveTask();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @ReactMethod
    public void makeConferenceCall() {
        Intent intent = null;
        Class cls = null;
        try {
            cls = Class.forName("com.example.MainActivity");
            Log.d("classname", "" + cls);
            intent = new Intent(getReactApplicationContext(), cls).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getReactApplicationContext().startActivity(intent);
            OngoingCall.makeConferenceCall();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @ReactMethod
    public void holdCall(boolean status) {
        Log.d("holdstatus", "" + status);
        if (status) {
            OngoingCall.hold();
        } else {
            OngoingCall.unhold();
        }
    }

    @ReactMethod
    public void startRecord(boolean status) {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };

        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions(getCurrentActivity(), PERMISSIONS, PERMISSION_ALL);
        }  else {
            if (status == true) {
                RecordService.getInstance().startRecording();
            } else {
                RecordService.getInstance().stopRecording();
            }
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public void onHostResume() {
        Log.d("LFC","onHostResume");
    }

    @Override
    public void onHostPause() {
        Log.d("LFC","onHostPause");
    }

    @Override
    public void onHostDestroy() {
        Log.d("LFC","onHostDestroy");
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onNewIntent(Intent intent) {

    }
}

