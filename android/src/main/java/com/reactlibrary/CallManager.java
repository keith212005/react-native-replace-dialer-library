package com.reactlibrary;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telecom.Call;
import android.telecom.VideoProfile;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import io.reactivex.subjects.BehaviorSubject;

@RequiresApi(api = Build.VERSION_CODES.R)
public class CallManager {

    public static BehaviorSubject<Integer> state = BehaviorSubject.create();

//    private static Call call;
    private static Call currentCall;
    private List<Call> callList = new ArrayList<Call>();
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

    public static String getPhoneNumber() {
        String phoneNumber = null;
        if (currentCall != null) {
            Uri uri = currentCall.getDetails().getHandle();
            String number = uri.toString();
            if (number.contains("%2B")) {
                number = number.replace("%2B", "+");
            }
            number = number.replace("tel:", "");
            phoneNumber = number;
        }
        return phoneNumber;
    }

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

    public void merge(boolean z2) {
        boolean z3;
        Call.Details details;
        List<Call> conferenceableCalls = currentCall.getConferenceableCalls();
        if (conferenceableCalls != null && !conferenceableCalls.isEmpty()) {
            Call call = conferenceableCalls.get(0);
            Call parent = call.getParent();
            if (z2) {
                if (parent == null || (details = parent.getDetails()) == null) {
                    z3 = false;
                } else {
                    z3 = details.can(128);
                }
//                if (z3 && parent.getState() == Call.STATE_HOLDING) {
//                    OngoingCall d2 = this.d.g.d(parent);
//                    OngoingCall d2 =
//                    qv1.g("yh0", "%s merge with conference onHold %s", this.b, d2);
//                    if (d2 != null) {
//                        d2.p(new i(), 3500);
//                        d2.W();
//                        return;
//                    }
//                    qv1.u("yh0", "%s merge with holding conference fail, skip", this.b);
//                }
            }
//            yh0 d3 = this.d.g.d(call);
//            qv1.g("yh0", "%s conference with %s", this.b, d3);
//            if (d3 != null) {
//                G();
//                d3.G();
//            }
            this.currentCall.conference(call);
        } else{
            currentCall.mergeConference();
        }
    }

//    private android.telecom.Call getTelecommCallById(String callId) {
//        final Call call = CallList.getInstance().getCallById(callId);
//        return call == null ? null : call.getTelecommCall();
//    }


    public final void setCall(@Nullable Call value) {
        if (currentCall != null) {
            currentCall.unregisterCallback((Call.Callback)callback);
        }

        if (value != null) {
            value.registerCallback((Call.Callback)callback);
            state.onNext(value.getState());
        }
        currentCall = value;
    }

    public int getCallState(){
        if (currentCall != null) {
            return currentCall.getState();
        }
        return Call.STATE_NEW;
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
            currentCall.disconnect();
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
}
