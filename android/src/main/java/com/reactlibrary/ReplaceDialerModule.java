package com.reactlibrary;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.role.RoleManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telecom.Call;
import android.telecom.PhoneAccount;
import android.telecom.TelecomManager;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.PermissionListener;

import java.util.Set;


public class ReplaceDialerModule extends ReactContextBaseJavaModule implements PermissionListener,
        LifecycleEventListener, ActivityEventListener {

  private ReactApplicationContext mContext;
  private CallService mContext2;
  String TAG = "ReplaceDialer";


  // for default dialer
  AudioManager audioManager;
  private static final int RC_DEFAULT_PHONE = 3289;

  public ReplaceDialerModule(CallService callService) {
    this.mContext2 = callService;
  }


  public ReplaceDialerModule(ReactApplicationContext context) {
    super(context);
    this.mContext = context;
    audioManager = (AudioManager) this.mContext.getSystemService(Context.AUDIO_SERVICE);
    audioManager.setMode(AudioManager.MODE_IN_CALL); // required
    Log.d(TAG,"audio mode = "+audioManager.getMode());

    this.mContext.addLifecycleEventListener(this);
    this.mContext.addActivityEventListener(this);

    //register broadcast receiver for bluetooth
    IntentFilter filter = new IntentFilter();
    filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
    filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
    filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
    mContext.registerReceiver(mReceiver, filter);
  }

  @Override
  public String getName() {
    return "ReplaceDialer";
  }


  @RequiresApi(api = Build.VERSION_CODES.M)
  public void openCallActivity(Context applicationContext) {
    try {
      Class cls = Class.forName("com.example.CallActivity");
      Intent intent = new Intent(applicationContext, cls).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.putExtra("phoneNumber",call.getDetails().getHandle().getSchemeSpecificPart());
//            intent.putExtra("callType",call.getState() == Call.STATE_RINGING ? "Incoming" : "Calling...");
      applicationContext.startActivity(intent);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  @ReactMethod
  private void getCallDetails(Callback callback) {
    callback.invoke(getBluetoothName());
  }


  // returns true if app set as default dialer else false
  @ReactMethod
  public void isDefaultDialer(Callback myCallback) {
    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
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


  @RequiresApi(api = Build.VERSION_CODES.M)
  @ReactMethod
  public void callPhoneNumber(String phoneNumber, Callback myCallback) {

    TelecomManager telecomManager =
            (TelecomManager) mContext.getSystemService(Context.TELECOM_SERVICE);
    Bundle extras = new Bundle();
    Uri uri = Uri.fromParts(PhoneAccount.SCHEME_TEL, phoneNumber, null);

    Bundle callExtras = new Bundle();
    callExtras.putString("phoneNumber", phoneNumber);
    extras.putParcelable(TelecomManager.EXTRA_OUTGOING_CALL_EXTRAS, callExtras);
    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
      return;
    }
    telecomManager.placeCall(uri, extras);
    myCallback.invoke(true);


//    Uri uri = Uri.parse("tel:" + phoneNumber.trim());
//    Intent intent = new Intent(Intent.ACTION_CALL, uri);
//    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//    this.mContext.startActivity(intent);
//    myCallback.invoke(phoneNumber);
  }


  @ReactMethod
  public void toggleSpeaker(Callback callback) {
    Log.d(TAG,"speaker on = "+audioManager.isSpeakerphoneOn());
    if (audioManager.isSpeakerphoneOn()) {
      audioManager.setSpeakerphoneOn(false);

      callback.invoke(audioManager.isSpeakerphoneOn());
    } else {
      audioManager.setSpeakerphoneOn(true);
      callback.invoke(audioManager.isSpeakerphoneOn());
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  @ReactMethod
  public void toggleMute(Callback callback) {
    int state = CallManager.getInstance().getCallState();
    if (state == Call.STATE_ACTIVE) {
      if (audioManager.isMicrophoneMute()) {
        audioManager.setMicrophoneMute(false);
        callback.invoke(false);
      } else {
        audioManager.setMicrophoneMute(true);
        callback.invoke(true);
      }
    } else {
      callback.invoke(false);
    }
  }

  @ReactMethod
  public String getBluetoothName() {
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
    if (pairedDevices.size() > 0) {
      for (BluetoothDevice device : pairedDevices) {
        return device.getName();
      }
    }
    return null;
  }


  @ReactMethod
  public void toggleBluetooth() {
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    if (mBluetoothAdapter == null) {
      // Device does not support Bluetooth
      Log.d(TAG, "Device not supported for bluetooth");
    } else if (!mBluetoothAdapter.isEnabled()) {
      // Bluetooth is not enabled :)
      Log.d(TAG, "Bluetooth is not enabled");
      final Intent intent = new Intent(Intent.ACTION_MAIN, null);
      intent.addCategory(Intent.CATEGORY_LAUNCHER);
      ComponentName cn = new ComponentName("com.android.settings",
              "com.android.settings.bluetooth.BluetoothSettings");
      intent.setComponent(cn);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      mContext.startActivity(intent);
    } else {
      // Bluetooth is enabled
      Log.d(TAG, "Bluetooth is enabled");
    }
  }

  //The BroadcastReceiver that listens for bluetooth broadcasts
  private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
        //Do something if connected
        Log.d(TAG, "Bluetooth connected........");
      } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
        //Do something if disconnected
        Log.d(TAG, "Bluetooth disconnected........");
      }
    }
  };

  @RequiresApi(api = Build.VERSION_CODES.M)
  @ReactMethod
  public void acceptCall() {
    CallManager.getInstance().answer();
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  @ReactMethod
  public void getPhoneNumber(Callback callback) {
    callback.invoke(CallManager.getPhoneNumber());
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  @ReactMethod
  public void getCallType(Callback callback) {
    callback.invoke(CallManager.getCallType());
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  @ReactMethod
  public static void getCallState(Callback callback) {
    Integer state = CallManager.getInstance().getCallState();
    callback.invoke(state);
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  @ReactMethod
  public void disconnectCall() {
    CallManager.getInstance().hangup();
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  @ReactMethod
  public void closeCurrentView() {
    int permissionRecord = ContextCompat.checkSelfPermission(getCurrentActivity(), Manifest.permission.RECORD_AUDIO);
    if (permissionRecord == PackageManager.PERMISSION_GRANTED) {
      if (RecordService.getInstance().isRecording == true) {
        RecordService.getInstance().stopRecording();
      }
    }
    mContext.getCurrentActivity().finishAndRemoveTask();
  }


  @RequiresApi(api = Build.VERSION_CODES.M)
  @ReactMethod
  public void mergeCall(Callback callback) {
    CallManager.getInstance().merge();
    callback.invoke(true);
  }

//  @RequiresApi(api = Build.VERSION_CODES.M)
//  @ReactMethod
//  public void makeConferenceCall(Callback callback) {
//    TelecomManager telecomManager =
//            (TelecomManager) mContext.getSystemService(Context.TELECOM_SERVICE);
//    Bundle extras = new Bundle();
//    Uri uri = Uri.fromParts(PhoneAccount.SCHEME_TEL, "07961606161", null);
//
//    Bundle callExtras = new Bundle();
//    callExtras.putString("phoneNumber", "07961606161");
//    extras.putParcelable(TelecomManager.EXTRA_OUTGOING_CALL_EXTRAS, callExtras);
//    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//      return;
//    }
//    telecomManager.placeCall(uri, extras);
//    callback.invoke(true);
//  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  @ReactMethod
  public void holdCall(Callback callback) {
    int state = CallManager.getInstance().getCallState();
    if (state == Call.STATE_HOLDING) {
      CallManager.unhold();
      callback.invoke(false);
    } else if (state == Call.STATE_ACTIVE) {
      CallManager.hold();
      callback.invoke(true);
    } else {
      callback.invoke(false);
    }
  }

  @ReactMethod
  public void recordCall(Callback callback) {
    int ALL_PERMISSIONS = 101;
    final String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    ActivityCompat.requestPermissions(getCurrentActivity(), permissions, ALL_PERMISSIONS);

    int permissionRecord = ContextCompat.checkSelfPermission(getCurrentActivity(), Manifest.permission.RECORD_AUDIO);
    int permissionStorage = ContextCompat.checkSelfPermission(getCurrentActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
    if (permissionRecord == PackageManager.PERMISSION_GRANTED && permissionStorage == PackageManager.PERMISSION_GRANTED) {
      if (!RecordService.getInstance().isRecording) {
        RecordService.getInstance().startRecording();
        callback.invoke(true);
      } else {
        RecordService.getInstance().stopRecording();
        callback.invoke(false);
      }
    } else {
      callback.invoke(false);
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
  }

  @Override
  public void onHostPause() {
  }

  @Override
  public void onHostDestroy() {
  }

  @Override
  public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
    Log.d(TAG, "onActivityResult called ... " + requestCode + resultCode);
  }

  @Override
  public void onNewIntent(Intent intent) {
  }

  @Override
  public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    Log.d(TAG, "requestCode = " + requestCode);
    return true;
  }
}

