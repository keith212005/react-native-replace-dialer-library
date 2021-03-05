package com.example;

import android.net.Uri;
import android.os.Build;
import android.telecom.Call;
import android.telecom.CallAudioState;
import android.telecom.InCallService;
import android.util.Log;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.M)
public class CallService extends InCallService {


    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);
        Uri uri = call.getDetails().getHandle();
        Log.d("detailssssss","callservice : " + uri);
        new OngoingCall().setCall(call);
        CallActivity.start(this, call);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);
        new OngoingCall().setCall(null);
    }

    @Override
    public void onBringToForeground(boolean showDialpad) {
        super.onBringToForeground(showDialpad);
    }
}
