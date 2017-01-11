package com.solutions.medadhere.medadheresolutionsapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;




/**
 * Created by Yeshy on 7/13/2016.
 */
public class BloodPressureLogReadActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    String medicine;
    String date;
    String UID;
    Context ctx;
    ArrayList<Medication> medicationList;
    ArrayAdapter<String> adapter;
    protected void onCreate(Bundle savedInstanceState) {
        Intent i = getIntent();
        date = i.getStringExtra("date");
ctx=this;

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_pressure_log_read);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        mDatabase = FirebaseDatabase.getInstance().getReference();
        UID = ((MyApplication) this.getApplication()).getUID();

        update();

        //mDatabase = FirebaseDatabase.getInstance().getReference();
        //String UID = ((MyApplication) this.getApplication()).getUID();
        //mDatabase.child("app").child("users").child(UID).child("medicine");
    }

    public void update() {
        mDatabase.child("app").child("users").child(UID).child("bloodPressureLog").child(date).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<String> records = new ArrayList<>();
                        records.add("Date: "+date);
                        //Log.e("hey","hey");
                        Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                        //System.out.println(dataSnapshot);
                        if (it.hasNext()){
                            records.add(" ");
                        }
                        while (it.hasNext()) {
                            DataSnapshot medicine = it.next();
                            //Log.e("reading2", medicine.toString());
                            //Log.e("reading3", medicine.getKey());
                            int colonNdx = medicine.getKey().toString().indexOf(":");
                            String subKey = medicine.getKey().toString().substring(0,colonNdx);
                            int HR =Integer.parseInt(subKey);

                            if(HR >= 12){
                                if(HR > 12){
                                    HR = HR - 12;
                                }
                                records.add(HR + medicine.getKey().substring(colonNdx, colonNdx + 3) + "PM " + medicine.getValue().toString());
                                //Log.e("Corrected Time Reading", HR + medicine.getKey().substring(colonNdx, colonNdx + 3));
                            }else{
                                records.add(medicine.getKey().toString()+ "AM " + medicine.getValue().toString());
                            }

                            //Log.e("reading4", medicine.getValue().toString());


                        }
                        String [] mArray = new String[records.size()];
                        mArray = records.toArray(mArray);
                        //final TextView tv = (TextView) findViewById(R.id.bloodPressureTextView);
                        //tv.setText(date);
                        final ListView lv = (ListView) findViewById(R.id.bloodPressureListView);
                        adapter = new ArrayAdapter<String>(BloodPressureLogReadActivity.this,R.layout.log_read,mArray);
                        //setListAdapter(adapter);
                        lv.setAdapter(adapter);
                        final String[] finalMArray = mArray;
                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, final View view, final int item, long l) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                                builder.setTitle("Delete Blood Pressure Log");

                                // set dialog message
                                builder
                                        .setMessage("Would you like to delete "+ finalMArray[item]+"?")
                                        .setCancelable(false)
                                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,int id) {

                                                mDatabase.child("app").child("users").child(UID).child("bloodPressureLog").child(date).removeValue();
                                                for (int i=2;i<finalMArray.length;i++) {
                                                    if(i!=item) {
                                                        int space = finalMArray[i].lastIndexOf(" ");
                                                        String time = finalMArray[i].substring(0,space);
                                                        String sysdia = finalMArray[i].substring(space+1);
                                                        Log.e("SYSDIA",sysdia);
                                                        String ampm = time.substring(time.length()-2);
                                                        int hour = Integer.parseInt(time.substring(0,time.indexOf(":")));
                                                        if(ampm.contains("PM")){
                                                            hour=hour+12;
                                                        }

                                                        mDatabase.child("app").child("users").child(UID).child("bloodPressureLog").child(date).child(Integer.toString(hour)+":"+time.substring(time.indexOf(":")+1,time.length()-2)).setValue(sysdia);
                                                    }
                                                }
                                                update();
                                            }
                                        })
                                        .setNegativeButton("No",new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,int id) {
                                                // if this button is clicked, just close
                                                // the dialog box and do nothing
                                                dialog.cancel();
                                            }
                                        });

                                // create alert dialog
                                AlertDialog alertDialog = builder.create();

                                // show it
                                alertDialog.show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }


    public void back(View v) {finish();}

    public void backToBPCal(View v) {
        Intent i = new Intent(this, BloodPressureActivity.class);
        startActivity(i);
        finish();
    }
}