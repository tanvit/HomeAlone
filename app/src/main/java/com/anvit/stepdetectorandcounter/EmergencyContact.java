package com.anvit.stepdetectorandcounter;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;

public class EmergencyContact extends AppCompatActivity {

    private static String TAG = MainActivity.class.getSimpleName();
    static ArrayList<Contact> contacts = new ArrayList<Contact>();
    static ArrayAdapter arrayAdapter;
    static ArrayList<String> contactList = new ArrayList<String>();
    private boolean mIsNewContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        ListView listView= (ListView) findViewById(R.id.listView);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.anvit.stepdetectorandcounter", Context.MODE_PRIVATE);

        HashSet<String> set = (HashSet<String>) sharedPreferences.getStringSet("contactList",null);

        if(set != null){
            contactList = new ArrayList(set);
        }


        arrayAdapter= new ArrayAdapter(this,android.R.layout.simple_list_item_1,contactList);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(),ContactEditor.class);
                intent.putExtra("contactID",i);
                startActivity(intent);
            }

        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                final int deleteItem= i;

                new AlertDialog.Builder(EmergencyContact.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Are you sure")
                        .setMessage("Do you want to delete this note")
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                contactList.remove(deleteItem);
                                arrayAdapter.notifyDataSetChanged();


                                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.anvit.stepdetectorandcounter", Context.MODE_PRIVATE);

                                HashSet<String> set = new HashSet(EmergencyContact.contactList);
                                sharedPreferences.edit().putStringSet("contactList",set).apply();

                            }
                        })
                        .setNegativeButton("No",null)
                        .show();
                return true;
            }

        });
    }


    public void addContact(View view) {
        //mIsNewContact= true;
        Intent intent = new Intent(getApplicationContext(),ContactEditor.class);
        startActivity(intent);
    }

}
