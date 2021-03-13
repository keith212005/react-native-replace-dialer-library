package com.example;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.telecom.Call;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

import com.facebook.react.PackageList;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactPackage;
import com.facebook.react.ReactRootView;
import com.facebook.react.bridge.Callback;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.modules.core.PermissionAwareActivity;
import com.facebook.react.modules.core.PermissionListener;
import com.facebook.soloader.SoLoader;
import com.reactlibrary.BuildConfig;
import com.reactlibrary.OngoingCall;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CallActivity extends Activity implements DefaultHardwareBackBtnHandler, PermissionAwareActivity {

    // --------- for proximity sensor ---------------
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private int field = 0x00000020;
    // ----------------------------------------------
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Phone state listener
        startPhoneStateListener();

        // Start Proximity sensor
        startProximitySensorService();

//        Bundle extras = getIntent().getExtras();
//        if (extras != null) {
//            Set<String> keys = extras.keySet();
//            Iterator<String> it = keys.iterator();
//            Log.e("callActivity","Dumping Intent start");
//            while (it.hasNext()) {
//                String key = it.next();
//                Log.e("callActivity","[" + key + "=" + extras.get(key)+"]");
//            }
//            Log.e("callActivity","Dumping Intent end");
//        }
//        String phoneNumber = extras.getString("phoneNumber");
//
//        Log.d("callActivity","onCreeate() = "+ phoneNumber  );

        SoLoader.init(this, false);
        ReactRootView mReactRootView = new ReactRootView(this);
        List<ReactPackage> packages = new PackageList(getApplication()).getPackages();

        ReactInstanceManager mReactInstanceManager = ReactInstanceManager.builder()
                .setApplication(getApplication())
                .setCurrentActivity(this)
                .setBundleAssetName("index.android.bundle")
                .setJSMainModulePath("index.android")
               .addPackages(packages)
                .setUseDeveloperSupport(BuildConfig.DEBUG)
                .setInitialLifecycleState(LifecycleState.RESUMED)
                .build();
        // The string here (e.g. "MyReactNativeApp") has to match
        // the string in AppRegistry.registerComponent() in index.js
        Bundle initialProps = new Bundle();
        initialProps.putString("initialScreenName", "CallScreen");
        initialProps.putString("outgoingNumber", getIntent().getStringExtra("phoneNumber"));
        initialProps.putString("callType", getIntent().getStringExtra("callType"));
        mReactRootView.startReactApplication(mReactInstanceManager, "example", initialProps);
        setContentView(mReactRootView);

        // this will show incoming screen while device is locked
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

    }

    private void startProximitySensorService() {
        try {
            field = PowerManager.class.getClass().getField("PROXIMITY_SCREEN_OFF_WAKE_LOCK").getInt(null);
        } catch (Throwable ignored) {
        }

        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(field, getLocalClassName());
        if (!wakeLock.isHeld()) {
            wakeLock.acquire();
        }
    }

    public void startPhoneStateListener() {
        TelephonyManager telephony = (TelephonyManager) this.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                System.out.println("kcal : " + state + incomingNumber);
                Log.d("kcal", "numbersss : " +"state: " +state + "tel:"+incomingNumber);
                AudioManager manager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                boolean x = manager.getMode() == AudioManager.MODE_IN_CALL;
                Log.e("kcal","" + x);
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);
    }

    public static void start(Context context, Call call) {
        Intent intent = new Intent(context, com.example.CallActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public void invokeDefaultOnBackPressed() {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void requestPermissions(String[] permissions, int requestCode, PermissionListener listener) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!wakeLock.isHeld()) {
            wakeLock.acquire();
        }
    }







}
