package com.anvit.stepdetectorandcounter;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;

import static com.anvit.stepdetectorandcounter.EmergencyContact.contactList;
import static com.anvit.stepdetectorandcounter.R.id.settings;

public class MainActivity extends AppCompatActivity implements SensorEventListener, StepListener {
    private TextView textView;
    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private static final String TEXT_NUM_STEPS = "Number of Steps: ";
    private int numSteps;
   // private TextView TvSteps;
    private Button BtnStart;
    private Button BtnStop;

    private String m_Text;
    private Integer pauseTime;
    private Integer warningTime;

    private Ringtone r;
    AlertDialog alert;

    private AlertDialog.Builder builder;

    static String primaryContact;

    private TextView textViewStepCounter;
    private TextView textViewStepTimer;
    private Button start;
    private Button pause;
    private Button resume;
    private TextView heading;

    private boolean isStarted=false;
    private boolean isPaused = false;
    private boolean isSensorChanged = false;
    private long timeRemaining = 0;

    private PendingIntent pending_intent;

    long millisInFuture;
    //get from settings
    //long millisInFuture = 10000;
    long countDownInterval = 1000;

    private CountDownTimer timer;
    private  CountDownTimer pauseTimer;
    private  CountDownTimer warningTimer;


    private boolean isRunning = true;

    int count = 0;

    float steps = 0;

    private Thread thread;

    private AlarmReceiver alarm;

    AlarmManager alarmManager;


    MainActivity inst;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity OnCreate", "Oncreate started");
        setContentView(R.layout.activity_main);
        // Initialize setting class
        SharedPreferences mySharedPreferences = getApplicationContext().getSharedPreferences("com.anvit.stepdetectorandcounter", Context.MODE_PRIVATE);
        millisInFuture = mySharedPreferences.getInt("idleTime", 10000);
        final long countdown = millisInFuture/1000;


        // Get an instance of the SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);


        //TvSteps = (TextView) findViewById(R.id.tv_steps);
        /*BtnStart = (Button) findViewById(R.id.btn_start);
        BtnStop = (Button) findViewById(R.id.btn_stop);*/
        textViewStepTimer = (TextView) findViewById(R.id.timer);
        start = (Button) findViewById(R.id.start);
        pause = (Button) findViewById(R.id.pause);
        resume = (Button) findViewById(R.id.resume);
        heading = (TextView) findViewById(R.id.textView);

        // time at which alarm will be scheduled here alarm is scheduled at 1 day from current time,
        // we fetch  the current time in milliseconds and added 1 day time
        // i.e. 24*60*60*1000= 86,400,000   milliseconds in a day
        int sleeps =  mySharedPreferences.getInt("sleepStart", 1000);
        int sleepe =  mySharedPreferences.getInt("sleepEnd", 600);

        int sleeph= sleeps/100;
        int sleepm = sleeps%100;

        Long time = new GregorianCalendar().getTimeInMillis()+10000;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 6);
        calendar.set(Calendar.MINUTE, 24);


        // create an Intent and set the class which will execute when Alarm triggers, here we have
        // given AlarmReciever in the Intent, the onRecieve() method of this class will execute when
        // alarm triggers and
        //we will write the code to send SMS inside onRecieve() method pf Alarmreciever class
        Intent intentAlarm = new Intent(this, AlarmReceiver.class);

        // create the object
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //set the alarm for particular time
        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), PendingIntent.getBroadcast(this,1,  intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));


        /*Intent myIntent = new Intent(getApplicationContext(), AlarmReceiver.class);

        Calendar calendar = Calendar.getInstance();


        //System.out.println("Current time =&gt; "+c.getTime());

        *//*SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        String formattedDate = df.format(calendar.getTime());*//*

        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY,10);
        calendar.set(Calendar.MINUTE,35);

        pending_intent = PendingIntent.getBroadcast(MainActivity.this,0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pending_intent);


*/

        /*alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        pending_intent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

// Set the alarm to start at 21:32 PM
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 21);
        calendar.set(Calendar.MINUTE, 32);

// setRepeating() lets you specify a precise custom interval--in this case,
// 1 day
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pending_intent);
        */





        final int REQUEST_PHONE_CALL = 1;

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_PHONE_CALL);
        }


        createAlert();

        start.setVisibility(View.VISIBLE);
        pause.setVisibility(View.GONE);

        timer = new CountDownTimer(millisInFuture,countDownInterval){
            public void onTick(long millisUntilFinished){

                timeRemaining = millisUntilFinished;
                long count = countdown - millisUntilFinished/1000;
                setTimer(count);

            }
            public void onFinish(){

                alert = builder.create();
                alert.setTitle("Warning!");
                r.play();
                alert.show();


                createWarningTimer();
                warningTimer.start();

                heading.setText("Done");
                start.setVisibility(View.VISIBLE);
                pause.setVisibility(View.GONE);
            }
        };

        start.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                numSteps = 0;
                sensorManager.registerListener(MainActivity.this, accel, SensorManager.SENSOR_DELAY_FASTEST);
                isStarted = true;
                isPaused = false;
                start.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
                timer.start();
                numSteps = 0;
                Log.d("Start Button", "Clicked on start button");
                heading.setText("Idle Time");

            }
        });

/*
        BtnStop.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                sensorManager.unregisterListener(MainActivity.this);

            }
        });*/






        //registerForSensorEvents();





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
        numSteps++;
        timer.cancel();
        timer.start();
        //TvSteps.setText(TEXT_NUM_STEPS + numSteps);
    }



    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater= getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        /*if(item.getItemId() == R.id.emergency_contact){

            Intent intent = new Intent(getApplicationContext(),EmergencyContact.class);
            startActivity(intent);

            return true;

        }

        if(item.getItemId() == R.id.settings){

            Intent intent = new Intent(getApplicationContext(),Settings.class);
            startActivity(intent);

            return true;

        }
        return false;*/
        switch (item.getItemId()) {
            case R.id.emergency_contact:
                startActivity(new Intent(this, EmergencyContact.class));
                return true;
            case settings:
                startActivity(new Intent(this, Settings.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String getPhone(String contact){

        String[] splited = contact.split("\\n+");
        String ph = splited[1];

        return ph;
    }

    public String getPrimaryContact() {

        SharedPreferences mySharedPreferences = getApplicationContext().getSharedPreferences("com.anvit.stepdetectorandcounter", Context.MODE_PRIVATE);
        return mySharedPreferences.getString("primaryContact","N/A");
    }

    public ArrayList<String> getContactList(){

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.anvit.stepdetectorandcounter", Context.MODE_PRIVATE);
        HashSet<String> set1 = (HashSet<String>) sharedPreferences.getStringSet("contactList",null);
        ArrayList<String> cList = new ArrayList(set1);
        return (cList);
    }

    private void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }

    public void setTimer(long count){

        if(count<10){

            textViewStepTimer.setText("00:0"+String.valueOf(count));
        }
        else if(count<60){

            textViewStepTimer.setText("00:"+String.valueOf(count));
        }
        else {

            long mins = count/60;
            long secs = count%60;
            if(count<600){
                if(secs<10){

                    textViewStepTimer.setText("0"+String.valueOf(mins)+":0"+String.valueOf(secs));
                }
                else{

                    textViewStepTimer.setText("0"+String.valueOf(mins)+":"+String.valueOf(secs));
                }
            }
            else{
                if(secs<10){

                    textViewStepTimer.setText(String.valueOf(mins)+":0"+String.valueOf(secs));
                }
                else{
                    textViewStepTimer.setText(String.valueOf(mins)+":"+String.valueOf(secs));
                }
            }
        }
    }
/*

    public void startTimer(View view) {

        isStarted = true;
        isPaused = false;
        start.setVisibility(View.GONE);
        pause.setVisibility(View.VISIBLE);
        timer.start();
        numSteps = 0;

        heading.setText("Idle Time");
    }*/

    public void pauseTimer(View view) {

        timer.cancel();
        createPause();
    }

    public void restartTimer(View view) {

        restart();
    }

    public void restart(){

        isStarted = true;
        isPaused = false;
        resume.setVisibility(View.GONE);
        pause.setVisibility(View.VISIBLE);
        pauseTimer.cancel();
        timer.start();
        heading.setText("Idle Time");
        //sensorManager.registerListener(MainActivity.this, accel, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void pause(){
        if (isStarted){
            isStarted = false;
            pause.setVisibility(View.GONE);
            resume.setVisibility(View.VISIBLE);
            timer.cancel();
            createPauseTimer();
            pauseTimer.start();
            heading.setText("App will resume in...");
        }


        //sensorManager.unregisterListener(MainActivity.this);

    }


    public void createAlert(){

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        r = RingtoneManager.getRingtone(getApplicationContext(), notification);


        builder = new AlertDialog.Builder(this);

        builder.setTitle("Warning!");
        builder.setMessage("Are you okay?");

        builder.setPositiveButton("CALL", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                r.stop();
                dialog.dismiss();
                warningTimer.cancel();

                contactList = getContactList();
                primaryContact = getPrimaryContact();
                String phone = getPhone(primaryContact);

                /*Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phone));
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(callIntent);*/

                for(int i = 0;i < contactList.size();i++ ){

                    String mPhone = getPhone(contactList.get(i));

                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                    sendSMS(mPhone,"Hello");

                }
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                r.stop();
                dialog.dismiss();
                warningTimer.cancel();
            }
        });
    }

    public void createPause(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pause");
        builder.setMessage("How long do you wish to pause the app for (in minutes)?");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                pauseTime = Integer.parseInt(m_Text)*60000;
                pause();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isStarted = true;
                isPaused = false;
                resume.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
                timer.start();
                heading.setText("Idle Time");

                dialog.cancel();
            }
        });

        builder.show();
    }

    public void createPauseTimer(){
        pauseTimer = new CountDownTimer(pauseTime,countDownInterval){
            public void onTick(long millisUntilFinished){

                timeRemaining = millisUntilFinished;
                long count = millisUntilFinished/1000;
                setTimer(count);

            }
            public void onFinish(){

                pause.setVisibility(View.VISIBLE);
                resume.setVisibility(View.GONE);
                timer.start();

                heading.setText("Idle Time");
            }
        };
    }


    public void createWarningTimer(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("com.anvit.stepdetectorandcounter", Context.MODE_PRIVATE);
        warningTime = pref.getInt("warningTime", 5000);

        warningTimer = new CountDownTimer(warningTime,countDownInterval){
            public void onTick(long millisUntilFinished){

                timeRemaining = millisUntilFinished;
                long count = 10 - millisUntilFinished/1000;
                setTimer(count);

            }

            public void onFinish(){

                r.stop();
                alert.dismiss();

                contactList = getContactList();
                primaryContact = getPrimaryContact();
                String phone = getPhone(primaryContact);

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phone));
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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


                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                    sendSMS(mPhone,"Hello");

                }

            }
        };
    }

}