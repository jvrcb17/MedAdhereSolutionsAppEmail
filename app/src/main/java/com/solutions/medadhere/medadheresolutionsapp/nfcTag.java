package com.solutions.medadhere.medadheresolutionsapp;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

//import android.widget.TimePicker;

/**
 * Created by lindsayherron on 10/28/16.
 */

public class nfcTag extends AppCompatActivity {
    Tag detectedTag;
    TextView txtType,txtSize,txtWrite,txtRead,txtID;
    NfcAdapter nfcAdapter;
    IntentFilter[] readTagFilters;
    PendingIntent pendingIntent;
    String UID;
    String medicine;
    String date;
    String [] mArray;
    Boolean didLog = false;
    private DatabaseReference mDatabase;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .65));
        setContentView(R.layout.nfc_tag);///////////////////////////////////////////////////////////////////


        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        detectedTag =getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);

        txtType  = (TextView) findViewById(R.id.txtType);
        txtSize  = (TextView) findViewById(R.id.txtsize);
        txtWrite = (TextView) findViewById(R.id.txtwrite);
        txtRead  = (TextView) findViewById(R.id.txtread);
        txtID  = (TextView) findViewById(R.id.txtid);

        pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(this,getClass()).
                        addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter filter2     = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        readTagFilters = new IntentFilter[]{tagDetected,filter2};

        final Intent i = getIntent();
        String action = i.getAction();
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //setContentView(R.layout.activity_medication_log);///////////////////////////////////////////////////////////////
        setContentView(R.layout.nfc_tag);

        final String [] medNames = new String[((MyApplication) this.getApplication()).getMeds().size()];
        for(int ind=0;ind<((MyApplication) this.getApplication()).getMeds().size();ind++){
            medNames[ind] = ((MyApplication) this.getApplication()).getMeds().get(ind);
        }
        setArray(medNames);
    }


    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        if(getIntent().getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)){
            detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            readFromTag(getIntent());
        }
    }

    @Override
    protected void onResume() {

        super.onResume();
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, readTagFilters, null);
    }


    public void readFromTag(Intent intent){
        Ndef ndef = Ndef.get(detectedTag);
        try{
            ndef.connect();
            ndef.getNdefMessage();
            String value = new String(ndef.getTag().getId(), "UTF-8");
            txtID.setText(value);

            mDatabase = FirebaseDatabase.getInstance().getReference();
            UID = ((MyApplication) this.getApplication()).getUID();

            Log.v("NFC:","Detected");
            mDatabase.child("app").child("users").child(UID).child("medicine")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String nfcID = txtID.getText().toString();
                            Log.v("NFC ID:",nfcID);
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Medication MedName = snapshot.getValue(Medication.class);
                                System.out.println(MedName.name);
                                System.out.println(MedName.id);

                                for(int retry = 0;retry < 2;retry++) {
                                    try {

                                        if (MedName.id.equals(nfcID)) {
                                            Log.v("NFC:", "Identified");
                                            Log.v("NFC MedID:", MedName.name);
                                            medicine = MedName.name;

                                            Calendar rightNow = Calendar.getInstance();
                                            int iyear = rightNow.get(Calendar.YEAR);
                                            int imonth = rightNow.get(Calendar.MONTH) + 1;
                                            int iday = rightNow.get(Calendar.DAY_OF_MONTH);
                                            int ihour = rightNow.get(Calendar.HOUR_OF_DAY);
                                            int iminute = rightNow.get(Calendar.MINUTE);

                                            String year = Integer.toString(iyear);
                                            String month;
                                            if (imonth<10) {
                                                month = "0" + Integer.toString(imonth);
                                            }else
                                            {
                                                month = Integer.toString(imonth);
                                            }
                                            String day;
                                            if (iday<10) {
                                                day = "0" + Integer.toString(iday);
                                            }else
                                            {
                                                day = Integer.toString(iday);
                                            }



                                            String date = year + "-" + month + "-" + day;
                                            Log.e("DATE:",date);

                                            com.solutions.medadhere.medadheresolutionsapp.TimeLog time = new com.solutions.medadhere.medadheresolutionsapp.TimeLog();
                                            

                                            mDatabase.child("app").child("users").child(UID).child("medicineLog").child(date).child(time.getTimeStamp()).setValue(medicine);
                                            didLog = true;
                                            TextView RWmode = (TextView) findViewById(R.id.NFC_RW);
                                            RWmode.setText("NFC tag recognized. Logging "+medicine);
                                        }
                                    }catch(NullPointerException e){
                                        mDatabase.child("app").child("users").child(UID).child("medicine").child(medicine).child("id").setValue("PlaceHolderIDvalue");
                                    }
                                }
                            }
                            if (didLog == false) {
                                Log.v("NFC","Unrecognized");
                                TextView RWmode = (TextView) findViewById(R.id.NFC_RW);
                                RWmode.setText("NFC tag unrecognized, select the medication you would like to assign to this unique NFC tag.");
                                Spinner NFCspinner = (Spinner) findViewById(R.id.NFCmedicationSpinner);
                                NFCspinner.setVisibility(View.VISIBLE);
                                NFCspinner.setClickable(true);
                                Button submitNFC = (Button) findViewById(R.id.btnSubmit);
                                submitNFC.setVisibility(View.VISIBLE);
                                submitNFC.setClickable(true);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
            /*
            txtType.setText(ndef.getType().toString());
            txtSize.setText(String.valueOf(ndef.getMaxSize()));
            txtWrite.setText(ndef.isWritable() ? "True" : "False");
            Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if (messages != null) {
                NdefMessage[] ndefMessages = new NdefMessage[messages.length];
                for (int i = 0; i < messages.length; i++) {
                    ndefMessages[i] = (NdefMessage) messages[i];
                }
                NdefRecord record = ndefMessages[0].getRecords()[0];

                byte[] payload = record.getPayload();
                String text = new String(payload);
                txtRead.setText(text);

                ndef.close();
            }*/
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Cannot Read From Tag.", Toast.LENGTH_LONG).show();
        }
    }

    public void setArray(String [] x){

        final Spinner NFCspinner = (Spinner) findViewById(R.id.NFCmedicationSpinner);//////////////////////////////////////////////////////////////////////////////
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(nfcTag.this, android.R.layout.simple_spinner_item, x);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        NFCspinner.setAdapter(spinnerArrayAdapter);

        NFCspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                medicine = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    public void submitID(View view){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        String UID = ((MyApplication) this.getApplication()).getUID();
        String nfcID = txtID.getText().toString();
        if (medicine != " ") {
            mDatabase.child("app").child("users").child(UID).child("medicine").child(medicine).child("id").setValue(nfcID);
        }
        else{
            //Snackbar.make(v,
            //        "Please select a medication.",
            //       Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }

    }

    public void onClose(View view){finish();}
}