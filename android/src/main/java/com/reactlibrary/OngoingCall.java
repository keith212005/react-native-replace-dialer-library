package com.reactlibrary;

import android.app.Application;
import android.os.Build;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telecom.Call;
import android.telecom.Conference;
import android.telecom.VideoProfile;
import android.telephony.TelephonyManager;
import android.util.Log;


import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.List;

import io.reactivex.subjects.BehaviorSubject;

public class OngoingCall implements InCallPhoneListener{

    public static BehaviorSubject<Integer> state = BehaviorSubject.create();
    private static Call call;
    private Phone mPhone;

    @RequiresApi(api = Build.VERSION_CODES.R)
    private Object callback = new Call.Callback() {
        @Override
        public void onStateChanged(Call call, int newState) {
            super.onStateChanged(call, newState);
            state.onNext(newState);
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.R)
    public final void setCall(@Nullable Call value) {
        if (call != null) {
            call.unregisterCallback((Call.Callback)callback);
        }

        if (value != null) {
            value.registerCallback((Call.Callback)callback);
            state.onNext(value.getState());
        }
        call = value;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public static int getCallState(){
        return call.getState();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public void answer() {
        assert call != null;
        if(call != null) {
            call.answer(VideoProfile.STATE_AUDIO_ONLY);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public void hangup() {
        assert call != null;
        if(call != null) {
            call.disconnect();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public static void hold() {
        assert call != null;
        if(call != null) {
            call.hold();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public static void unhold() {
        assert call != null;
        if(call != null) {
            call.unhold();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public static void makeConferenceCall(){
        assert call != null;
        Log.d("conferencecalls",""+call.getDetails());
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    void merge(String callId) {
        if ( mPhone != null) {
            Call call = getTelecommCallById(callId);
            List<android.telecom.Call> conferenceable = call.getConferenceableCalls();
            if (!conferenceable.isEmpty()) {
                call.conference(conferenceable.get(0));
            } else {
                call.mergeConference();
            }
        } else {
            Log.e("OngoingCall", "error merge, mPhone is null.");
        }
    }

    private Call getTelecommCallById(String callId) {
//        final Call call = CallList.getInstance().getCallById(callId);
//        return call == null ? null : call.getTelecommCallId();
        return null;
    }



    @Override
    public void setPhone(Phone phone) {
        mPhone = phone;
    }

    @Override
    public void clearPhone() {
        mPhone = null;
    }
}
