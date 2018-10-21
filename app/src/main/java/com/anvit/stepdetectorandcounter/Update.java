package com.anvit.stepdetectorandcounter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by anvit on 10/17/2018.
 */

public class Update extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            MainActivity.getInstace().updateBtns();
        } catch (Exception e) {

        }
    }
}
