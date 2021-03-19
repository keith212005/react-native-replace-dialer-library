package com.reactlibrary;

import android.os.Build;
import android.telecom.Call;
import android.telecom.InCallService;
import android.util.Log;

import androidx.annotation.RequiresApi;


@RequiresApi(api = Build.VERSION_CODES.R)
public class CallService extends InCallService {

    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);
        CallManager.getInstance().setCall(call);
        ReplaceDialerModule replaceDialerModule = new ReplaceDialerModule();
        replaceDialerModule.openCallActivity(getApplicationContext(),call);
    }

    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);
        CallManager.getInstance().setCall(null);
    }

    @Override
    public void onBringToForeground(boolean showDialpad) {
        super.onBringToForeground(showDialpad);
    }

    @Override
    public void onCanAddCallChanged(boolean canAddCall) {
        Log.d("CallService","canAddCall = " + canAddCall);
        super.onCanAddCallChanged(canAddCall);
    }





}
