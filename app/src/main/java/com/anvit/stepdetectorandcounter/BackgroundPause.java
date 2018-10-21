package com.anvit.stepdetectorandcounter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.IBinder;
import static com.anvit.stepdetectorandcounter.MainActivity.pauseTime;

public class BackgroundPause extends Service {

    private long timeRemaining = 0;
    long countDownInterval = 1000;
    private long count;

    private CountDownTimer pauseTimer;

    //static boolean isPaused;

    Context context;


    public BackgroundPause() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onCreate() {
        super.onCreate();

        //isPaused = true;

        /*SharedPreferences prefPaused = getApplicationContext().getSharedPreferences("com.anvit.stepdetectorandcounter", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefPaused.edit();
        editor.putBoolean("isPaused",isPaused);*/

        pauseTimer = new CountDownTimer(pauseTime,countDownInterval){
            public void onTick(long millisUntilFinished){

                timeRemaining = millisUntilFinished;
                count = millisUntilFinished/1000;
                //setTimer(count);

            }
            public void onFinish(){

                /*MainActivity mn = new MainActivity();

                mn.setpauseEnd(1);*/
                final String BROADCAST_ACTION = "com.unitedcoders.android.broadcasttest.SHOWTOAST";
                Intent intent = new Intent(BROADCAST_ACTION);
                sendBroadcast(intent);

                startState();


                Intent startServiceIntent = new Intent(BackgroundPause.this, BackgroundTimer.class);
                startService(startServiceIntent);
                //isPaused = false;

                /*editor.putBoolean("isPaused",isPaused);*/
                stopSelf();


            }
        };

        pauseTimer.start();
    }

    @Override
    public void onDestroy() {
        pauseTimer.cancel();
        super.onDestroy();

    }

    public void startState(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("com.anvit.stepdetectorandcounter", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isPaused",false);
        editor.putBoolean("isStarted",true);
        editor.commit();
    }


}
