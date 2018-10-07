package com.anvit.stepdetectorandcounter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by anvit on 7/28/2018.
 */

public class AlarmReceiver extends BroadcastReceiver {

    private MainActivity activity = new MainActivity();
    public void AlarmReceiver() {
        activity.pause();
    }
    @Override
    public void onReceive(Context context, Intent intent)
    {
        // TODO Auto-generated method stub


        // here you can start an activity or service depending on your need
        // for ex you can start an activity to vibrate phone or to ring the phone

        /*String phoneNumberReciver="919611875198";// phone number to which SMS to be send
        String message="Hi I will be there later, See You soon";// message to send
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumberReciver, null, message, null, null);*/
        // Show the toast  like in above screen shot
        Toast.makeText(context, "Going to sleep. Good Night", Toast.LENGTH_LONG).show();
        //AlarmReceiver();

    }
}
