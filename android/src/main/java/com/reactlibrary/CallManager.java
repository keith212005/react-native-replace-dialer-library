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


public class CallManager {

  public static BehaviorSubject state = BehaviorSubject.create();

  private static Call currentCall;
  private static CallManager mInstance;
  String TAG = "CallManager";

  private CallManager() {
  }

  public static CallManager getInstance() {
    if (mInstance == null) {
      mInstance = new CallManager();
    }
    return mInstance;
  }


  @RequiresApi(api = Build.VERSION_CODES.M)
  private Object callback = new Call.Callback() {
    @Override
    public void onStateChanged(Call call, int newState) {
      Log.d(TAG, "new call state = " + newState);
      super.onStateChanged(call, newState);
      state.onNext(newState);
    }
  };

  @RequiresApi(api = Build.VERSION_CODES.M)
  public Call getLastCallInConferenceList() {
    List<Call> conferenceableCalls = currentCall.getConferenceableCalls();
    Call lastCall = null;
    if (conferenceableCalls != null && !conferenceableCalls.isEmpty()) {
      lastCall = conferenceableCalls.get(conferenceableCalls.size() - 1);
      Log.d(TAG, "Last call number: " + lastCall.getDetails().getHandle().getSchemeSpecificPart());
    }
    return lastCall;
  }


  public boolean isCallActive(Context context) {
    AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    return manager.getMode() == AudioManager.MODE_IN_CALL;
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
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

  @RequiresApi(api = Build.VERSION_CODES.M)
  public int getCallState() {
    if (currentCall != null) {
      return currentCall.getState();
    }
    return Call.STATE_NEW;
  }

  public Call getCurrentCall() {
    return currentCall;
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  public void answer() {
    assert currentCall != null;
    if (currentCall != null) {
      currentCall.answer(VideoProfile.STATE_AUDIO_ONLY);
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  public void hangup() {
    assert currentCall != null;
    if (currentCall != null) {
      if (currentCall.getState() == Call.STATE_RINGING) {
        currentCall.reject(false, "");
      } else {
        currentCall.disconnect();

      }
    } else {
      Log.d(TAG, "current call is null");
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  public void merge() {
    List<Call> conferenceableCalls = currentCall.getConferenceableCalls();
    Log.d(TAG, "current call number = " + currentCall.getDetails().getHandle().getSchemeSpecificPart());

    if (!conferenceableCalls.isEmpty()) {
      Call call = conferenceableCalls.get(0);
      Log.d(TAG, "conferencable call number = " + conferenceableCalls.get(0).getDetails().getHandle().getSchemeSpecificPart());
      currentCall.conference(call);
      Log.d(TAG, "after conference number = " + currentCall.getDetails().getHandle().getSchemeSpecificPart());
    } else {
      if (currentCall.getDetails().can(Call.Details.CAPABILITY_MERGE_CONFERENCE)) {
        Log.d(TAG, "currentcall2" + currentCall.getDetails().getHandle().getSchemeSpecificPart());
        currentCall.mergeConference();
        Log.d(TAG, "currentcall3" + currentCall.getConferenceableCalls().get(0).getDetails().getHandle().getSchemeSpecificPart());
      }
    }
  }


  @RequiresApi(api = Build.VERSION_CODES.M)
  public static void hold() {
    assert currentCall != null;
    if (currentCall != null) {
      currentCall.hold();
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  public static void unhold() {
    assert currentCall != null;
    if (currentCall != null) {
      currentCall.unhold();
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  public static String getPhoneNumber() {
    String phoneNumber = null;
    if (currentCall != null) {
      phoneNumber = currentCall.getDetails().getHandle().getSchemeSpecificPart();
    }
    return phoneNumber;
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  public static String getCallType() {
    String callType = null;
    if (currentCall != null) {
      if (currentCall.getState() == Call.STATE_RINGING) {
        callType = "Incoming";
      } else {
        callType = "Calling...";
      }
    }
    return callType;
  }
}