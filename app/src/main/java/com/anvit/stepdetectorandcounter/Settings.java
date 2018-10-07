package com.anvit.stepdetectorandcounter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import static com.anvit.stepdetectorandcounter.R.id.idleTime;
import static com.anvit.stepdetectorandcounter.R.id.sleepStart;
import static com.anvit.stepdetectorandcounter.R.id.sleepStop;
import static com.anvit.stepdetectorandcounter.R.id.warningTime;

public class Settings extends AppCompatActivity {

    EditText editIdleTime;
    EditText editWarningTime;
    EditText editSleepStart;
    EditText editSleepStop;

    static ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        editIdleTime = (EditText) findViewById(idleTime);

        editWarningTime = (EditText) findViewById(warningTime);

        editSleepStart = (EditText) findViewById(sleepStart);

        editSleepStop = (EditText) findViewById(sleepStop);



    }

    public void save(View view) {
        int idleTime = Integer.parseInt(editIdleTime.getText().toString());
        int warningTime = Integer.parseInt(editWarningTime.getText().toString());
        int sleepStart = Integer.parseInt(editSleepStart.getText().toString());
        int sleepStop = Integer.parseInt(editSleepStop.getText().toString());

        SharedPreferences pref = getApplicationContext().getSharedPreferences("com.anvit.stepdetectorandcounter", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("idleTime", idleTime);
        editor.putInt("warningTime",warningTime);
        editor.putInt("sleepStart", sleepStart);
        editor.putInt("sleepEnd",sleepStop);

        editor.commit();

        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);

    }
}



