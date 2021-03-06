package com.reactlibrary;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.telecom.Call;
import android.telecom.CallAudioState;
import android.telecom.InCallService;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.RequiresApi;

import io.reactivex.subjects.BehaviorSubject;



@RequiresApi(api = Build.VERSION_CODES.M)
public class CallService extends InCallService {

  public static BehaviorSubject state = BehaviorSubject.create();
  private static CallService sInstance;
  TelephonyManager telephony;
  AudioManager audioManager;
  String TAG = "CallService";


  @Override
  public void onCreate() {
    super.onCreate();
    sInstance = this;
  }
  public static CallService getInstance(){
    return sInstance;
  }

  @Override
  public void onCallAdded(Call call) {
    super.onCallAdded(call);
    call.registerCallback((Call.Callback) callCallback);
    Log.d(TAG, "isCallActive = " + CallManager.getInstance().isCallActive(getApplicationContext()));
    if (CallManager.getInstance().getCurrentCall() == null) {
      CallManager.getInstance().setCall(call);
      ReplaceDialerModule replaceDialerModule = new ReplaceDialerModule(this);
      replaceDialerModule.openCallActivity(getApplicationContext());
    } else {
      CallManager.getInstance().setCall(call);
    }

//    setAudioRoute(CallAudioState.ROUTE_SPEAKER);    // working but turns on as soon as call is started.
  }


  private void startPhoneStateListener() {
    MyPhoneStateListener phoneListener = new MyPhoneStateListener();
    telephony = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
    telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
  }

  private void stopPhoneStateListener() {
    telephony.listen(null, PhoneStateListener.LISTEN_NONE);
  }

  @Override
  public void onCallRemoved(Call call) {
    super.onCallRemoved(call);
    call.unregisterCallback((Call.Callback) callCallback);
    CallManager.getInstance().setCall(null);

  }

  @Override
  public void onCanAddCallChanged(boolean canAddCall) {
    Log.d(TAG, "canAddCall = " + canAddCall);
    super.onCanAddCallChanged(canAddCall);
  }

  private Object callCallback = new Call.Callback() {
    @Override
    public void onStateChanged(Call call, int newState) {
      Log.d(TAG, "new call state = " + newState);
      super.onStateChanged(call, newState);
      state.onNext(newState);
    }
  };

  @Override
  public void onCallAudioStateChanged(CallAudioState audioState) {
    super.onCallAudioStateChanged(audioState);

  }

//  public void onCallAudioStateChanged(CallAudioState callAudioState) {
//    super.onCallAudioStateChanged(callAudioState);
//    this.a.P(callAudioState);
//  }




}
