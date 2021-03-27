package com.reactlibrary;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MyPhoneStateListener extends PhoneStateListener {

  public static Boolean phoneRinging = false;
  String TAG = "MyPhoneStateListener";
  private boolean wasAppInOffHook = false;
  private boolean wasAppInRinging = false;

  @Override
  public void onCallStateChanged(int state, String phoneNumber) {

    switch (state) {
      //Hangup
      case TelephonyManager.CALL_STATE_IDLE:
//        Log.d(TAG, "IDLE"+" phonenumber number = " + phoneNumber);
        if(wasAppInOffHook == true) { // if there was an ongoing call and the call state switches to idle, the call must have gotten disconnected
          Log.d(TAG, "Disconnected"+phoneNumber);
        } else if(wasAppInRinging == true) { // if the phone was ringing but there was no actual ongoing call, it must have gotten missed
          Log.d(TAG, "Missed"+ phoneNumber);
        }
        //reset device state
        wasAppInRinging = false;
        wasAppInOffHook = false;
        break;

      //Outgoing
      case TelephonyManager.CALL_STATE_OFFHOOK:
//        Log.d(TAG, "OFFHOOK"+" phonenumber number = " + phoneNumber);
        //Device call state: Off-hook. At least one call exists that is dialing, active, or on hold, and no calls are ringing or waiting.
        wasAppInOffHook = true;
        Log.d(TAG, "Offhook"+phoneNumber);
        break;

      //Incoming
      case TelephonyManager.CALL_STATE_RINGING:
//        Log.d(TAG, "RINGING"+" phonenumber number = " + phoneNumber);
        // Device call state: Ringing. A new call arrived and is ringing or waiting. In the latter case, another call is already active.
        wasAppInRinging = true;
       Log.d(TAG, "Incoming"+phoneNumber);
        break;
    }

    super.onCallStateChanged(state, phoneNumber);
  }

}