package com.example;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

import java.util.List;

public class CallActivity extends Activity implements DefaultHardwareBackBtnHandler, PermissionAwareActivity {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Phone state listener
        startPhoneStateListener();

        SoLoader.init(this, false);
        ReactRootView mReactRootView = new ReactRootView(this);
        List<ReactPackage> packages = new PackageList(getApplication()).getPackages();
        // Packages that cannot be autolinked yet can be added manually here, for example:
        // packages.add(new MyReactNativePackage());
        // Remember to include them in `settings.gradle` and `app/build.gradle` too.

        ReactInstanceManager mReactInstanceManager = ReactInstanceManager.builder()
                .setApplication(getApplication())
                .setCurrentActivity(this)
                .setBundleAssetName("index.android.bundle")
                .setJSMainModulePath("index")
                .addPackages(packages)
                .setUseDeveloperSupport(BuildConfig.DEBUG)
                .setInitialLifecycleState(LifecycleState.RESUMED)
                .build();
        // The string here (e.g. "MyReactNativeApp") has to match
        // the string in AppRegistry.registerComponent() in index.js
        Bundle initialProps = new Bundle();
        initialProps.putString("initialScreenName", "CallScreen");
        mReactRootView.startReactApplication(mReactInstanceManager, "example", initialProps);
        setContentView(mReactRootView);

        // this will show incoming screen while device is locked
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


    }

    public void startPhoneStateListener() {
        TelephonyManager telephony = (TelephonyManager) this.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                System.out.println("incomingNumber : " + state + incomingNumber);
                Log.d("kcal", "numbersss : " + state + incomingNumber);
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public void endCall(Callback myCallback) {
        new OngoingCall().hangup();
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


}
