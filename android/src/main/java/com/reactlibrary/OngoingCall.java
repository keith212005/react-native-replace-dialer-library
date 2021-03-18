package com.reactlibrary;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telecom.Call;
import android.telecom.VideoProfile;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import io.reactivex.subjects.BehaviorSubject;

public class OngoingCall implements InCallPhoneListener{

    public static BehaviorSubject<Integer> state = BehaviorSubject.create();
    private static Call call;
    private Call anotherCall;
    private List<Call> callList = new ArrayList<Call>();
    private Phone mPhone;
    private static OngoingCall mInstance;

    private OngoingCall(){}

    public static OngoingCall getInstance() {
        if (mInstance == null) {
            mInstance = new OngoingCall();
        }
        return mInstance;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private Object callback = new Call.Callback() {
        @Override
        public void onStateChanged(Call call, int newState) {
            Log.d("OngoingCall","new call state = " + newState);
            super.onStateChanged(call, newState);
            state.onNext(newState);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.R)
    public static String getPhoneNumber() {
        String phoneNumber = null;
        if (call != null) {
            Uri uri = call.getDetails().getHandle();
            String number = uri.toString();
            if (number.contains("%2B")) {
                number = number.replace("%2B", "+");
            }
            number = number.replace("tel:", "");
            phoneNumber = number;
        }
        return phoneNumber;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public static String getCallId() {

        Bundle bundle = call.getDetails().getExtras();
        Log.d("Bundle>>","capablities = "+call.getDetails().getCallCapabilities());

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

        Bundle bundle2 = call.getDetails().getIntentExtras();

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

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void makePreviousCallReadyForConference() {
        if(call!=null){
            call.getParent();
            Log.d("Bundle>>","getConferencatble calls =  "+call.getConferenceableCalls().size());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public void merge(String callId) {
//        if (mPhone != null) {
//            android.telecom.Call call = getTelecommCallById(callId);
//            List<android.telecom.Call> conferenceable = call.getConferenceableCalls();
//            if (!conferenceable.isEmpty()) {
//                call.conference(conferenceable.get(0));
//            } else {
//                int capabilities = call.getDetails().getCallCapabilities();
//                if (0 != (capabilities & PhoneCapabilities.MERGE_CONFERENCE)) {
//                    call.mergeConference();
//                }
//            }
//        } else {
//            Log.e(this, "error merge, mPhone is null.");
//        }
    }

//    private android.telecom.Call getTelecommCallById(String callId) {
//        final Call call = CallList.getInstance().getCallById(callId);
//        return call == null ? null : call.getTelecommCall();
//    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public static void addCallToConference() {

    }

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
    public int getCallState(){
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

    @Override
    public void setPhone(Phone phone) {
        mPhone = phone;
    }

    @Override
    public void clearPhone() {
        mPhone = null;
    }
}
