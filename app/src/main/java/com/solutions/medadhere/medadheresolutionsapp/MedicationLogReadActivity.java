package com.solutions.medadhere.medadheresolutionsapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
public class MedicationLogReadActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    String medicine;
    String date;
    String UID;
    ArrayList<Medication> medicationList;
    ArrayAdapter<String> adapter;
    Context ctx = this;
    Boolean past = false;

    protected void onCreate(Bundle savedInstanceState) {
        Intent i = getIntent();
        date = i.getStringExtra("date");

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");


        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication_log_read);


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

        mDatabase = FirebaseDatabase.getInstance().getReference();
        UID = ((MyApplication) this.getApplication()).getUID();

        //getWindow().setLayout((int)(width*.8),(int)(height*.55));

        update();

    }

    public static long getDifferenceDays(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public void update() {
        mDatabase.child("app").child("users").child(UID).child("medicineLog").child(date).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<String> records = new ArrayList<>();
                        records.add("Date: "+date);
                        //Log.e("hey","hey");
                        Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                        System.out.println(dataSnapshot);
                        if (it.hasNext()){
                            records.add(" ");
                        }
                        final ArrayList<String> medTimes = new ArrayList<String>();
                        while (it.hasNext()) {
                            DataSnapshot medicine = it.next();
                            //Log.e("reading2", medicine.toString());
                            //Log.e("reading3", medicine.getKey());
                            //Log.e("reading4", medicine.getValue().toString());

                            String MedTime = medicine.getKey().toString();//.substring(0,medicine.getKey().toString().lastIndexOf(":"));

                            medTimes.add(MedTime);
                            //Log.e("MEDTIME",MedTime);
                            int colonNdx = MedTime.indexOf(":");
                            String subKey = MedTime.substring(0,colonNdx);
                            int HR =Integer.parseInt(subKey);

                            if(HR >= 12){
                                if(HR > 12){
                                    HR = HR - 12;
                                }
                                records.add(HR + medicine.getKey().substring(colonNdx, colonNdx + 3) + "PM " + medicine.getValue().toString());
                                //Log.e("Corrected Time Reading", HR + medicine.getKey().substring(colonNdx, colonNdx + 3));
                            }else{
                                records.add(medicine.getKey().toString().substring(0, colonNdx + 3)+ "AM " + medicine.getValue().toString());
                            }

                        }
                        String [] timeArray = new String[medTimes.size()];
                        timeArray = medTimes.toArray(timeArray);

                        String [] mArray = new String[records.size()];
                        mArray = records.toArray(mArray);
                        final ListView lv = (ListView) findViewById(R.id.medicationListView);
                        adapter = new ArrayAdapter<String>(MedicationLogReadActivity.this,R.layout.log_read,mArray);
                        //setListAdapter(adapter);
                       // lv.setAdapter(new MedicationAdapter(MedicationLogReadActivity.this, mArray));
                        lv.setAdapter(adapter);
                        final String[] finalMArray = mArray;
                        final String[] finalTimeArray =timeArray;
                        //Log.e("What to log", Arrays.toString(finalTimeArray));

                        if(!past) {
                            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, final View view, final int item, long l) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                                    builder.setTitle("Delete Medication Log");

                                    // set dialog message
                                    builder
                                            .setMessage("Would you like to delete " + finalMArray[item] + "?")
                                            .setCancelable(false)
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {

                                                    //Log.e("FinalmArray", finalMArray[item]);
                                                    mDatabase.child("app").child("users").child(UID).child("medicineLog").child(date).removeValue();
                                                    for (int i = 2; i < finalMArray.length; i++) {
                                                        if (i != item) {
                                                            int space = finalMArray[i].lastIndexOf(" ");
                                                            String meds = finalMArray[i].substring(space + 1);
                                                            //Log.e("MEDS", meds);
                                                            mDatabase.child("app").child("users").child(UID).child("medicineLog").child(date).child(finalTimeArray[i - 2]).setValue(meds);
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

    public void backToMedCal(View v) {
        Intent i = new Intent(this, MedicationActivity.class);
        startActivity(i);
        finish();
    }

}