package com.anvit.stepdetectorandcounter;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import static com.anvit.stepdetectorandcounter.EmergencyContact.contactList;

public class BackgroundTimer extends Service implements SensorEventListener,StepListener{

    private static final int SECOND = 1000;
    private static final int MINUTE = 60 * SECOND;
    private static final int HOUR = 60 * MINUTE;
    private static final int DAY = 24 * HOUR;


    long hrs;
    long min;

    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    // private TextView TvSteps;


    private Ringtone r;

    static String primaryContact;

    private boolean isStarted=false;
    private boolean isSensorChanged = false;
    private long timeRemaining = 0;

    private boolean iswarning= false;

    private PendingIntent pending_intent;

    long millisInFuture;
    //get from settings
    //long millisInFuture = 10000;
    long countDownInterval = 1000;

    private CountDownTimer timer;


    private boolean isRunning = true;
    private CountDownTimer warningTimer;

    private long count_s;

    private Thread thread;


    AlarmManager alarmManager;


    MainActivity inst;
    Context context;


    public BackgroundTimer() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");

    }

    @Override
    public void onCreate() {
        super.onCreate();



        SharedPreferences mySharedPreferences = getApplicationContext().getSharedPreferences("com.anvit.stepdetectorandcounter", Context.MODE_PRIVATE);
        millisInFuture = mySharedPreferences.getInt("idleTime", 1800000);
        final long countdown = millisInFuture/60000;

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);


        timer = new CountDownTimer(millisInFuture,countDownInterval){
            public void onTick(long millisUntilFinished){

                timeRemaining = millisUntilFinished;
                count_s = countdown - millisUntilFinished/1000;

            }
            public void onFinish(){

                isStarted = false;

                

                Calendar cal = Calendar.getInstance();
                Date date=cal.getTime();
                DateFormat dateFormat = new SimpleDateFormat("HHmm");
                String formattedDate=dateFormat.format(date);

                int time = Integer.parseInt(formattedDate);

                SharedPreferences mySharedPreferences = getApplicationContext().getSharedPreferences("com.anvit.stepdetectorandcounter", Context.MODE_PRIVATE);
                int sleeps =  mySharedPreferences.getInt("sleepStart", 1000);
                int sleepe =  mySharedPreferences.getInt("sleepEnd", 600);

                if (((time<sleeps)&&(time>=sleepe))||((time>=sleepe)||(time<sleeps)&&(sleeps<sleepe)))
                {

                    createnotif();

                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);


                    r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                    r.play();

                    SharedPreferences pref = getApplicationContext().getSharedPreferences("com.anvit.stepdetectorandcounter", Context.MODE_PRIVATE);
                    long warningTime = pref.getInt("warningTime", 600000);

                    warningTimer = new CountDownTimer(warningTime,countDownInterval){
                        public void onTick(long millisUntilFinished){

                            long timeRemaining = millisUntilFinished;
                            long count = 10 - millisUntilFinished/60000;

                        }

                        public void onFinish(){

                            r.stop();

                            finishState();

                            contactList = getContactList();
                            primaryContact = getPrimaryContact();
                            String phone = getPhone(primaryContact);

                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + phone));
                            if (ActivityCompat.checkSelfPermission(BackgroundTimer.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            startActivity(callIntent);


                            for(int i = 0;i < contactList.size();i++ ){
                                Log.d("mytag",Integer.toString(i));

                                String mPhone = getPhone(contactList.get(i));


                                if (ActivityCompat.checkSelfPermission(BackgroundTimer.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                                    return;
                                }

                                sendSMS(mPhone,"Hello");
                            }
                        }
                    };

                    sensorManager.registerListener(BackgroundTimer.this, accel, SensorManager.SENSOR_DELAY_FASTEST);
                    iswarning = true;
                    warningTimer.start();


                }
                else{
                    isStarted = true;

                    timer.start();
                }

            }
        };
    }

    public String getPhone(String contact){

        String[] splited = contact.split("\\n+");
        String ph = splited[1];

        return ph;
    }

    public ArrayList<String> getContactList(){

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.anvit.stepdetectorandcounter", Context.MODE_PRIVATE);
        HashSet<String> set1 = (HashSet<String>) sharedPreferences.getStringSet("contactList",null);
        ArrayList<String> cList = new ArrayList(set1);
        return (cList);
    }

    public String getPrimaryContact() {

        SharedPreferences mySharedPreferences = getApplicationContext().getSharedPreferences("com.anvit.stepdetectorandcounter", Context.MODE_PRIVATE);
        return mySharedPreferences.getString("primaryContact","N/A");
    }

    private void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        isStarted=true;
        sensorManager.registerListener(BackgroundTimer.this, accel, SensorManager.SENSOR_DELAY_FASTEST);
        timer.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }


    @Override
    public void step(long timeNs) {
        if (iswarning){
            warningTimer.cancel();
            r.stop();
            timer.start();
            iswarning = false;
        }
        else if (isStarted){
            timer.cancel();
            timer.start();
        }

    }


    @Override
    public void onDestroy() {
        timer.cancel();
        isStarted = false;
        super.onDestroy();
    }
    
    public void createnotif(){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Warning")
                .setContentText("Shake phone to dismiss alarm")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(001, mBuilder.build());
    }



    public void finishState(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("com.anvit.stepdetectorandcounter", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isPaused",false);
        editor.putBoolean("isStarted",false);
        editor.commit();
    }

}
