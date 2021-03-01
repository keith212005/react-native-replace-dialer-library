package com.example;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telecom.Call;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import com.facebook.react.PackageList;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactPackage;
import com.facebook.react.ReactRootView;
import com.facebook.react.bridge.Callback;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.soloader.SoLoader;

import java.util.List;

public class CallActivity extends Activity implements DefaultHardwareBackBtnHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                .setJSMainModulePath("callActivity")
                .addPackages(packages)
                .setUseDeveloperSupport(BuildConfig.DEBUG)
                .setInitialLifecycleState(LifecycleState.RESUMED)
                .build();
        // The string here (e.g. "MyReactNativeApp") has to match
        // the string in AppRegistry.registerComponent() in index.js
        mReactRootView.startReactApplication(mReactInstanceManager, "example", null);
        setContentView(mReactRootView);

        // Phone state listener
        StateListener phoneStateListener = new StateListener(this);
        TelephonyManager telephonyManager =(TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
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
}

class StateListener extends PhoneStateListener {

    Activity activity;
    StateListener(Activity activity){
        this.activity = activity;
    }


    //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
    //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
    @Override
    public void onCallStateChanged(int state, String incomingNumber) {

        int lastState = TelephonyManager.CALL_STATE_IDLE;
        boolean isIncoming = false;

        super.onCallStateChanged(state, incomingNumber);
        if(lastState == state){
            //No change, debounce extras
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                // on incoming call started
                Log.d("kcall1",""+state+incomingNumber);
                isIncoming = true;
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                // on outgoing call started
                Log.d("kcall2 (2)",""+state+incomingNumber);
                if(lastState != TelephonyManager.CALL_STATE_RINGING){
                    isIncoming = false;
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                // went to idle this is end of call. What type depends on previous states
                Log.d("kcall3 (0)",""+state+incomingNumber);
                if(lastState == TelephonyManager.CALL_STATE_RINGING){
                    //Ring but no pickup-  a miss
                    Log.d("kcall missed call","");
                    activity.finish();
                }
                else if(isIncoming==true){
                    Log.d("kcall","incoming call ended.");
                }
                else{
                    Log.d("kcall","outgoing call ended.");
                    activity.finish();
                }
                break;
        }
        lastState = state;
    }
}
