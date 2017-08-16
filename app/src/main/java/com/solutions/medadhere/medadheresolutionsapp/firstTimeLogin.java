package com.solutions.medadhere.medadheresolutionsapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;


public class firstTimeLogin extends Activity {

    private String MED_FILENAME = "med_file";
    private String FREQ_FILENAME = "freq_file";
    private DatabaseReference mDatabase;
    ArrayList<String> medArray= new ArrayList<String>();
    ArrayList<String> medFrequency= new ArrayList<String>();
    //private ArrayList<Medication> medicationList = new ArrayList<>();
    public static final String PREFS_NAME = "MyPrefsFile";
    private View mProgressView;
    String UID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_progress);


        mProgressView = findViewById(R.id.login_progress);
        showProgress(true);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        UID = ((MyApplication) this.getApplication()).getUID();
        getPharmaPhone();
        findLifestyleFeedback();
        findLiteracyFeedback();
        getMeds();


    }

    public void getPharmaPhone(){
        mDatabase.child("app").child("users").child(UID).child("pharmanumber").addValueEventListener(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String phonenumber = dataSnapshot.getValue().toString();
                        ((MyApplication) firstTimeLogin.this.getApplication()).setPharmaPhone(phonenumber);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }


    public void findLifestyleFeedback() {
        String UID = ((MyApplication) this.getApplication()).getUID();
        final ArrayList<String> responseArray = new ArrayList<>();
        mDatabase.child("app").child("users").child(UID).child("lifestylesurveyanswersRW").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                        String lifedate="";
                        while (it.hasNext()) {
                            DataSnapshot medicine = it.next();
                            lifedate = medicine.getKey().toString();
                            String attempts = medicine.getValue().toString();
                            responseArray.add(attempts);
                            //Log.e("repsonse",attempts);
                        }
                        ((MyApplication) firstTimeLogin.this.getApplication()).setLifestyleDate(lifedate);
                        if(responseArray.size()==0) {
                            ArrayList<String> norepsonse = new ArrayList<String>(Collections.nCopies(36, "-1"));
                            ((MyApplication) firstTimeLogin.this.getApplication()).setLiteracySurveyAnswersString(norepsonse);
                        }
                        else {
                            ((MyApplication) firstTimeLogin.this.getApplication()).setLifestyleSurveyAnswersString(responseArray);
                        }

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }
    public void findLiteracyFeedback(){
        String UID = ((MyApplication) this.getApplication()).getUID();
        final ArrayList<String> responseArray1 = new ArrayList<>();
        mDatabase.child("app").child("users").child(UID).child("literacysurveyanswersRW").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                        String litdate="";
                        while (it.hasNext()) {
                            DataSnapshot medicine = it.next();
                            litdate = medicine.getKey().toString();
                            String attempts = medicine.getValue().toString();
                            responseArray1.add(attempts);
                        }
                        ((MyApplication)firstTimeLogin.this.getApplication()).setLiteracyDate(litdate);

                        if(responseArray1.size()==0) {
                            int [] norepsonse = new int[36];
                            for (int ind = 0;ind<36;ind++){
                                norepsonse[ind]=-1;
                            }
                            ((MyApplication) firstTimeLogin.this.getApplication()).setLiteracySurveyAnswers(norepsonse);
                        }
                        else {
                            ((MyApplication) firstTimeLogin.this.getApplication()).setLiteracySurveyAnswersString(responseArray1);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    public void getMeds() {
        String UID = ((MyApplication) this.getApplication()).getUID();
        mDatabase.child("app").child("users").child(UID).child("medicine").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //e("reading", dataSnapshot.toString());
                        Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                        while (it.hasNext()) {
                            DataSnapshot medicine = it.next();
                            //e("reading2", medicine.toString());
                            //e("reading3", medicine.getKey());
                            medArray.add(medicine.getKey().toString());
                        }
                        //Log.e("meds", medArray.toString());
                        ((MyApplication)firstTimeLogin.this.getApplication()).setMeds(medArray);
                        getMedFrequency(medArray);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }
    public void getMedFrequency(final ArrayList<String> medArray){
        String UID = ((MyApplication) this.getApplication()).getUID();
        for(int i=0;i<medArray.size();i++) {
            final int finalI = i;
            mDatabase.child("app").child("users").child(UID).child("medicine").child(medArray.get(i)).child("frequency").addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //e("currentMed", medArray.get(finalI));
                            //e("reading", dataSnapshot.getValue().toString());
                            medFrequency.add(dataSnapshot.getValue().toString());
                            //e("medFrequency", medFrequency.toString());
                            ((MyApplication)firstTimeLogin.this.getApplication()).addFrequency(dataSnapshot.getValue().toString());
                            if (finalI==medArray.size()-1){
                                //e("medFrequencyFinal", medFrequency.toString());
                                //TODO: Add Alarm function here based on frequency array
                                fileIOMeds(medArray,medFrequency);//to add the name of the medicine as well
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        }
                    });
        }
    }


    public void fileIOMeds(ArrayList<String> medArray, ArrayList<String> medFrequency) {
        deleteFile(MED_FILENAME);
        try {
            FileOutputStream fos = openFileOutput(MED_FILENAME, Context.MODE_APPEND);
            String text = "";
            for (int i = 0; i < medArray.size(); i++) {
                text = text.concat(medArray.get(i));
                if (i != medArray.size() - 1) {
                    text = text.concat("\n");
                }
            }
            fos.write(text.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Frequency Array
        deleteFile(FREQ_FILENAME);
        try {
            FileOutputStream fos = openFileOutput(FREQ_FILENAME, Context.MODE_APPEND);
            String text = "";
            for (int i = 0; i < medFrequency.size(); i++) {
                text = text.concat(medFrequency.get(i));
                if (i != medFrequency.size() - 1) {
                    text = text.concat("\n");
                }
            }
            //Log.e("Freq",text);
            fos.write(text.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        startAlarm(this);

    }

    public void startAlarm(Context context) {

        //first notification at 10 AM next day, this should always fire if they have prescribed medication
        Intent intent1 = new Intent(this,ReminderService.class);
        int type = intent1.getIntExtra("type", 0);
        intent1.putExtra("type", type);
        startService(intent1);

        Intent i = new Intent(firstTimeLogin.this, MainActivity.class);
        startActivity(i);
        finish();

    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {


            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);


    }






}
