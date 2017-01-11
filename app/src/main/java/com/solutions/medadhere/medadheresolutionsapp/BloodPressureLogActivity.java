package com.solutions.medadhere.medadheresolutionsapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


/**
 * Created by Yeshy on 7/13/2016.
 */
public class BloodPressureLogActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    String medicine;
    String date;
    String bpDiastolic;
    String bpSystolic;
    ArrayList<Medication> medicationList;
    protected void onCreate(Bundle savedInstanceState) {
        Intent i = getIntent();
        date = i.getStringExtra("date");
        //Log.e("date",date);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_pressure_log);
/*
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
*/
        //getWindow().setLayout((int)(width*.8),(int)(height*.55));

        //medicationList = ((MyApplication) this.getApplication()).getMedicationList();
        //String [] medNames = ((MyApplication) this.getApplication()).getMedicationNames();

        //System.out.println(Arrays.toString(medNames));

        //Spinner spinner = (Spinner) findViewById(R.id.medicationSpinner);

        //ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, medNames);
        //spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        //spinner.setAdapter(spinnerArrayAdapter);

        /*
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                Object item = arg0.getItemAtPosition(arg2);
                if (item!=null) {
                    medicine = item.toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
        */

        //mDatabase = FirebaseDatabase.getInstance().getReference();
        //String UID = ((MyApplication) this.getApplication()).getUID();
        //mDatabase.child("app").child("users").child(UID).child("medicine");
    }

    public void submit(View v) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        String UID = ((MyApplication) this.getApplication()).getUID();

        EditText bp_systolic = (EditText) findViewById(R.id.bp_systolic);
        bpSystolic = bp_systolic.getText().toString();
        EditText bp_diastolic = (EditText) findViewById(R.id.bp_diastolic);
        bpDiastolic = bp_diastolic.getText().toString();

        TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
        String time;
        if (Build.VERSION.SDK_INT >= 23 ) {
            time = timePicker.getHour() + ":";
            int minute = timePicker.getMinute();
            if (minute <10){
                time+="0"+minute;
            }
            else{
                time+=minute;
            }

        } else {
            time = timePicker.getCurrentHour() + ":";
            int minute = timePicker.getCurrentMinute();
            if (minute <10){
                time+="0"+minute;
            }
            else{
                time+=minute;
            }
        }

        if (bp_systolic.length()>0&bp_diastolic.length()>0) {
            mDatabase.child("app").child("users").child(UID).child("bloodPressureLog").child(date).child(time).setValue(bpSystolic + "-" + bpDiastolic);

            Snackbar.make(v, "Your blood pressure has been logged.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        else {
            Snackbar.make(v, "Please fill in the Systolic and Diastolic readings.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    public void returnToCal(View v){
        Intent i = new Intent(this, com.solutions.medadhere.medadheresolutionsapp.BloodPressureActivity.class);
        startActivity(i);
        finish();
    }


    public void checkLogs(View v) {
        Intent i = new Intent(this, com.solutions.medadhere.medadheresolutionsapp.BloodPressureLogReadActivity.class);
        i.putExtra("date", date);
        startActivity(i);
    }
}