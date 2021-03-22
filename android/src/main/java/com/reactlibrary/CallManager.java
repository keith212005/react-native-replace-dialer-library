package com.reactlibrary;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.telecom.Call;
import android.telecom.VideoProfile;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import io.reactivex.subjects.BehaviorSubject;

@RequiresApi(api = Build.VERSION_CODES.R)
public class CallManager {

    public static BehaviorSubject state = BehaviorSubject.create();

    private static Call currentCall;
    private static CallManager mInstance;

    private CallManager(){}

    public static CallManager getInstance() {
        if (mInstance == null) {
            mInstance = new CallManager();
        }
        return mInstance;
    }

    private Object callback = new Call.Callback() {
        @Override
        public void onStateChanged(Call call, int newState) {
            Log.d("OngoingCall","new call state = " + newState);
            super.onStateChanged(call, newState);
            state.onNext(newState);
        }
    };

//    public static String getPhoneNumber() {
//        return currentCall.getDetails().getHandle().getSchemeSpecificPart();
//    }

    public static String getCallId() {

        Bundle bundle = currentCall.getDetails().getExtras();
        Log.d("Bundle>>","capablities = "+currentCall.getDetails().getCallCapabilities());

        if (bundle != null) {
            Set<String> keys = bundle.keySet();
            Iterator<String> it = keys.iterator();
            Log.d("Bundle>>","Dumping Intent start");
            while (it.hasNext()) {
                String key = it.next();
                Log.d("Bundle>>","[" + key + " = " + bundle.get(key)+"]");
            }
            Log.d("Bundle>>","Dumping Intent end");
        }

        Bundle bundle2 = currentCall.getDetails().getIntentExtras();

        if (bundle2 != null) {
            Set<String> keys = bundle2.keySet();
            Iterator<String> it = keys.iterator();
            Log.d("Bundle>>","Dumping Intent start");
            while (it.hasNext()) {
                String key = it.next();
                Log.d("Bundle2>>","[" + key + " = " + bundle2.get(key)+"]");
            }
            Log.d("Bundle2>>","Dumping Intent end");
        };
        return "";
    }

    public Call getLastCallInConferenceList(){
        List<Call> conferenceableCalls = currentCall.getConferenceableCalls();
        Call lastCall = null;
        if(conferenceableCalls!=null && !conferenceableCalls.isEmpty()) {
            lastCall = conferenceableCalls.get(conferenceableCalls.size() - 1);
            Log.d("CallManager", "Last call number: " + lastCall.getDetails().getHandle().getSchemeSpecificPart());
        }
        return lastCall;
    }

    public void merge(boolean z2) {
        boolean z3;
        Call.Details details;
        List<Call> conferenceableCalls = currentCall.getConferenceableCalls();
        Log.d("CallManager","currentcall1"+currentCall.getDetails().getHandle().getSchemeSpecificPart());
        Log.d("CallManager","currentcall1"+conferenceableCalls.get(0).getDetails().getHandle().getSchemeSpecificPart());
        if (conferenceableCalls != null && !conferenceableCalls.isEmpty()) {
            Call call = conferenceableCalls.get(0);
            Call parent = call.getParent();
            if (z2) {
                if (parent == null || (details = parent.getDetails()) == null) {
                    z3 = false;
                } else {
                    z3 = details.can(128);
                }
            }
            Log.d("CallManager","currentcall insideed");
            currentCall.conference(call);
        } else {
            Log.d("CallManager","currentcall2"+currentCall.getDetails().getHandle().getSchemeSpecificPart());
            currentCall.mergeConference();
            Log.d("CallManager","currentcall3"+currentCall.getConferenceableCalls().get(0).getDetails().getHandle().getSchemeSpecificPart());
        }
    }

    public boolean isCallActive(Context context){
        AudioManager manager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        return manager.getMode() == AudioManager.MODE_IN_CALL;
    }

    public final void setCall(@Nullable Call value) {
        if (value != null) {
            state.onNext(value.getState());
        }
        currentCall = value;
    }

    public static void updateCall(@org.jetbrains.annotations.Nullable Call call) {
        currentCall = call;
        if (call != null) {
            state.onNext(MappersJava.toGsmCall(call));
        }
    }

    public int getCallState(){
        if (currentCall != null) {
            return currentCall.getState();
        }
        return Call.STATE_NEW;
    }

    public Call getCurrentCall(){
        return currentCall;
    }

    public void answer() {
        assert currentCall != null;
        if(currentCall != null) {
            currentCall.answer(VideoProfile.STATE_AUDIO_ONLY);
        }
    }

    public void hangup() {
        assert currentCall != null;
        if(currentCall != null) {
            if(currentCall.getState() == Call.STATE_RINGING) {
                Log.d("CallManager","rejected number : " +currentCall.getDetails().getHandle().getSchemeSpecificPart());
                currentCall.reject(false,"");
            } else {
                Log.d("CallManager","disconnect number = " + currentCall.getDetails().getHandle().getSchemeSpecificPart());
                currentCall.disconnect();
            }
        }
        else {
            Log.d("CallManager","current call is null");
        }
    }

    public static void hold() {
        assert currentCall != null;
        if(currentCall != null) {
            currentCall.hold();
        }
    }

    public static void unhold() {
        assert currentCall != null;
        if(currentCall != null) {
            currentCall.unhold();
        }
    }

    public static String getPhoneNumber(){
        String phoneNumber = null;
        if(currentCall!=null){
            phoneNumber = currentCall.getDetails().getHandle().getSchemeSpecificPart();
        }
        return phoneNumber;
    }

    public static String getCallType(){
        String callType=null;
        if(currentCall!=null) {
            if (currentCall.getState() == Call.STATE_RINGING) {
                callType = "Incoming";
            } else {
                callType = "Calling...";
            }
        }
        return callType;
    }
}