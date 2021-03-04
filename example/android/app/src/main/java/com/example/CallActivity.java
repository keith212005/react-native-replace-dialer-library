package com.example;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Phone state listener
        startPhoneStateListener();

        // Getting phone numnber when this activity is started
        String phoneNumber = getIntent().getStringExtra("phoneNumber");
        Log.d("CallActivity","onCreeate() = "+ phoneNumber);

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
                .setJSMainModulePath("index.android")
                .addPackages(packages)
                .setUseDeveloperSupport(BuildConfig.DEBUG)
                .setInitialLifecycleState(LifecycleState.RESUMED)
                .build();
        // The string here (e.g. "MyReactNativeApp") has to match
        // the string in AppRegistry.registerComponent() in index.js
        Bundle initialProps = new Bundle();
        initialProps.putString("initialScreenName", "CallScreen");
        initialProps.putString("outgoingNumber", phoneNumber);
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


    @RequiresApi(api = Build.VERSION_CODES.R)
    public static void start(Context context, Call call) {
        // passing phone number when when the call is incoming / outgoing
        Intent intent = new Intent(context, com.example.CallActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        String phoneNumber = getPhoneNumber(call);
        Log.d("callActivity","void start class : " + phoneNumber);
        intent.putExtra("phoneNumber", phoneNumber);
        context.startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private static String getPhoneNumber(Call call) {
        Uri uri = call.getDetails().getHandle();
        Log.d("callActivity","void start class : " + uri);
        String phoneNumber = uri.toString();
        if(phoneNumber.contains("%2B"))
        {
            phoneNumber = phoneNumber.replace("%2B","+");
        }
        phoneNumber = phoneNumber.replace("tel:", "");
        return phoneNumber;
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
