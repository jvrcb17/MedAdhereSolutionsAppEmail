package com.solutions.medadhere.medadheresolutionsapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;


/**
 * Created by Yeshy on 7/13/2016.
 */
public class WeightLogReadActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    String medicine;
    String date;
    String UID;
    Context ctx = this;
    ArrayList<Medication> medicationList;
    ArrayAdapter<String> adapter;
    Boolean past = false;

    protected void onCreate(Bundle savedInstanceState) {
        Intent i = getIntent();
        date = i.getStringExtra("date");


        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_log_read);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        UID = ((MyApplication) this.getApplication()).getUID();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");

        try {
            Date clicked = formatter.parse(date);
            Date today = new Date();

            if((int)getDifferenceDays(clicked,today)>0){
                past = true;
            }
            else{
                past = false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        update();
    }

    public static long getDifferenceDays(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public void update() {
        mDatabase.child("app").child("users").child(UID).child("weightLog").child(date).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Log.e("In","Database "+date);
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

                        final ListView lv = (ListView) findViewById(R.id.weightListView);
                        adapter = new ArrayAdapter<String>(WeightLogReadActivity.this,R.layout.log_read,mArray);
                        //setListAdapter(adapter);
                        lv.setAdapter(adapter);







                        final String[] finalMArray = mArray;
                        if(!past) {
                            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, final View view, final int item, long l) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                                    builder.setTitle("Delete Weight Log");

                                    // set dialog message
                                    builder
                                            .setMessage("Would you like to delete " + finalMArray[item] + "?")
                                            .setCancelable(false)
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {

                                                    mDatabase.child("app").child("users").child(UID).child("weightLog").child(date).removeValue();
                                                    for (int i = 2; i < finalMArray.length; i++) {
                                                        if (i != item) {
                                                            int space = finalMArray[i].lastIndexOf(" ");
                                                            String time = finalMArray[i].substring(0, space);
                                                            String weight = finalMArray[i].substring(space + 1);
                                                            String ampm = time.substring(time.length() - 2);
                                                            int hour = Integer.parseInt(time.substring(0, time.indexOf(":")));
                                                            if (ampm.contains("PM")) {
                                                                hour = hour + 12;
                                                            }

                                                            mDatabase.child("app").child("users").child(UID).child("weightLog").child(date).child(Integer.toString(hour) + ":" + time.substring(time.indexOf(":") + 1, time.length() - 2)).setValue(weight);
                                                        }
                                                    }
                                                    update();
                                                }
                                            })
                                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
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


                     }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }


    public void back(View v) {
        finish();
    }

    public void backToWeightCal(View v) {
        Intent i = new Intent(this, WeightCalendarActivity.class);
        startActivity(i);
        finish();
    }
}