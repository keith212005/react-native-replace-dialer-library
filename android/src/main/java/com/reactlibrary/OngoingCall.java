package com.reactlibrary;

import android.os.Build;

import android.telecom.Call;
import android.telecom.Conference;
import android.telecom.VideoProfile;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.facebook.react.bridge.ReactContext;

import java.util.List;

import io.reactivex.subjects.BehaviorSubject;

public class OngoingCall {

    public static BehaviorSubject<Integer> state = BehaviorSubject.create();
    private static Call call;

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
    public void answer() {
        assert call != null;
        call.answer(VideoProfile.STATE_AUDIO_ONLY);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public void hangup() {
        assert call != null;
        call.disconnect();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public static void hold() {
        assert call != null;
        call.hold();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public static void unhold() {
        assert call != null;
        call.unhold();

    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public static void makeConferenceCall(){
        assert call != null;
        Log.d("conferencecalls",""+call.getDetails());
    }

}
