package com.anvit.stepdetectorandcounter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.HashSet;

import static com.anvit.stepdetectorandcounter.EmergencyContact.arrayAdapter;
import static com.anvit.stepdetectorandcounter.EmergencyContact.contactList;
import static com.anvit.stepdetectorandcounter.MainActivity.primaryContact;

public class ContactEditor extends AppCompatActivity {

    EditText editName;
    EditText editPhone;
    EditText editEmail;
    CheckBox checkBox;
    //static String primaryContact;


    private static String TAG = MainActivity.class.getSimpleName();
    int contactId;
    private boolean mIsCancelling;
    private boolean mIsNewContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_editor);

        checkBox = (CheckBox) findViewById(R.id.checkBox);

        editName= (EditText) findViewById(R.id.name);
        editPhone= (EditText) findViewById(R.id.phone);
        editEmail= (EditText) findViewById(R.id.email);

        Intent intent = getIntent();
        contactId = intent.getIntExtra("contactID",-1);

        if(contactId != -1){

            mIsNewContact = false;

            String thisContact = contactList.get(contactId);
            String[] splited = thisContact.split("\\n+");
            editName.setText(splited[0]);
            editPhone.setText(splited[1]);
            editEmail.setText(splited[2]);

            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.anvit.stepdetectorandcounter", Context.MODE_PRIVATE);
            primaryContact = sharedPreferences.getString("primaryContact","N/A");

            /*if (contactList.size() > -1 && primaryContact.isEmpty()){
                primaryContact = contactList.get(0);
            }
            */
            if (primaryContact.equals(thisContact)){
                checkBox.setChecked(true);
            }

            else if(checkBox.isChecked()){
                updatePrimaryContact();
            }
        }
        else{

            mIsNewContact = true;
            contactList.add("");
            contactId = contactList.size()-1;
            arrayAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();

        if (mIsCancelling){
            if(mIsNewContact){

                contactList.remove(contactId);
                arrayAdapter.notifyDataSetChanged();
            }

        }
        else {
            if (checkBox.isChecked()) {

                updatePrimaryContact();
            }
            saveContact();
        }
    }

    private void saveContact() {

        Contact mContact=new Contact(editName.getText().toString(),editPhone.getText().toString(),editEmail.getText().toString());

        String strContact = mContact.getName()+"\n"+mContact.getPhone()+"\n"+mContact.getEmail();
        contactList.set(contactId,strContact);
        arrayAdapter.notifyDataSetChanged();

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.anvit.stepdetectorandcounter", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        HashSet<String> set = new HashSet(contactList);
        sharedPreferences.edit().putStringSet("contactList",set).apply();
    }

    public void deleteContact(View view) {

        mIsCancelling = true;
        finish();

    }

    public void updatePrimaryContact(){

        primaryContact = contactList.get(contactId);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.anvit.stepdetectorandcounter", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("primaryContact", primaryContact);
        editor.apply();
    }

}
