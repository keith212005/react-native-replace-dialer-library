package com.reactlibrary;

import android.os.Build;
import android.telecom.Call;
import android.telecom.InCallService;
import android.util.Log;

import androidx.annotation.RequiresApi;

import io.reactivex.subjects.BehaviorSubject;



@RequiresApi(api = Build.VERSION_CODES.M)
public class CallService extends InCallService {

  public static BehaviorSubject state = BehaviorSubject.create();

  @Override
  public void onCallAdded(Call call) {
    super.onCallAdded(call);
    call.registerCallback((Call.Callback) callCallback);
    Log.d("CallService", "isCallActive = " + CallManager.getInstance().isCallActive(getApplicationContext()));
    if (CallManager.getInstance().getCurrentCall() == null) {
      CallManager.getInstance().setCall(call);
      ReplaceDialerModule replaceDialerModule = new ReplaceDialerModule(this);
      replaceDialerModule.openCallActivity(getApplicationContext());
    } else {
      CallManager.getInstance().setCall(call);
    }
  }

  @Override
  public void onCallRemoved(Call call) {
    super.onCallRemoved(call);
    call.unregisterCallback((Call.Callback) callCallback);
    CallManager.getInstance().setCall(null);
  }

  @Override
  public void onCanAddCallChanged(boolean canAddCall) {
    Log.d("CallService", "canAddCall = " + canAddCall);
    super.onCanAddCallChanged(canAddCall);
  }

  private Object callCallback = new Call.Callback() {
    @Override
    public void onStateChanged(Call call, int newState) {
      Log.d("OngoingCall", "new call state = " + newState);
      super.onStateChanged(call, newState);
      state.onNext(newState);
    }
  };

}
