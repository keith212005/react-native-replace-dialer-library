package com.reactlibrary;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MyPhoneStateListener extends PhoneStateListener {

  public static Boolean phoneRinging = false;
  String TAG = "MyPhoneStateListener";

  @Override
  public void onCallStateChanged(int state, String phoneNumber) {
    super.onCallStateChanged(state, phoneNumber);
    switch (state) {
      case TelephonyManager.CALL_STATE_IDLE:
        Log.d(TAG, "IDLE"+" phonenumber number = " + phoneNumber);
        phoneRinging = false;
        break;
      case TelephonyManager.CALL_STATE_OFFHOOK:
        Log.d(TAG, "OFFHOOK"+" phonenumber number = " + phoneNumber);
        phoneRinging = false;
        break;
      case TelephonyManager.CALL_STATE_RINGING:
        Log.d(TAG, "RINGING"+" phonenumber number = " + phoneNumber);
        phoneRinging = true;
        break;
    }
  }

}