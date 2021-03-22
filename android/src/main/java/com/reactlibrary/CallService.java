package com.reactlibrary;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.telecom.Call;
import android.telecom.InCallService;
import android.util.Log;

import androidx.annotation.RequiresApi;

import io.reactivex.subjects.BehaviorSubject;


@RequiresApi(api = Build.VERSION_CODES.R)
public class CallService extends InCallService {

    public static BehaviorSubject state = BehaviorSubject.create();

    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);
        call.registerCallback((Call.Callback) callCallback);
        Log.d("isactivee","isactivee"+CallManager.getInstance().isCallActive(getApplicationContext()));
        if(CallManager.getInstance().getCurrentCall() == null){
            CallManager.getInstance().setCall(call);
            ReplaceDialerModule replaceDialerModule = new ReplaceDialerModule();
            replaceDialerModule.openCallActivity(getApplicationContext(),call);
        } else {
            CallManager.getInstance().setCall(call);
        }
    }

    public boolean isCallActive(Context context){
        AudioManager manager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        return manager.getMode() == AudioManager.MODE_IN_CALL;
    }

    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);
        call.unregisterCallback((Call.Callback) callCallback);
        CallManager.getInstance().setCall(null);
    }

    @Override
    public void onBringToForeground(boolean showDialpad) {
        super.onBringToForeground(showDialpad);
    }

    @Override
    public void onCanAddCallChanged(boolean canAddCall) {
        Log.d("CallService","canAddCall = " + canAddCall);
        super.onCanAddCallChanged(canAddCall);
    }

//    private Call.Callback callCallback = new Call.Callback() {
//        @Override
//        public void onStateChanged(Call call, int state) {
//            CallManager.updateCall(call);
//        }
//    };

    private Object callCallback = new Call.Callback() {
        @Override
        public void onStateChanged(Call call, int newState) {
            Log.d("OngoingCall","new call state = " + newState);
            super.onStateChanged(call, newState);
            state.onNext(newState);
        }
    };





}
