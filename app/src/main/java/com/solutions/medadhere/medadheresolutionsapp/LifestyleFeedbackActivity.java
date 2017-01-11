package com.solutions.medadhere.medadheresolutionsapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class LifestyleFeedbackActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    Context ctx;
    public static final String LSURVEY_FILENAME = "lsurvey_file";
    public static final String PREFS_NAME = "MyPrefsFile";
    ArrayList<String> responsePosArray = new ArrayList<>();
    ArrayList<String> responseNegArray = new ArrayList<>();
    ArrayList<String> responseArray = new ArrayList<>();
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lifestyle_feedback);
        //ctx = this;

        final TextView tv = (TextView) findViewById(R.id.TitleFeedback);
        tv.setText("Lifestyle Survey Feedback");

        final String[] posArray = getResources().getStringArray(R.array.LifestylePositiveMessagesArray);
        final String[] negArray = getResources().getStringArray(R.array.LifestyleNegativeMessagesArray);
        final int[] surveyResponse = new int[8];
        final int[] correctChoice = {4,1,2,2,1,2,2,4};
        final int[] wrongChoice = {0,0,1,1,0,1,1,0};

        Intent i = getIntent();
        String surDate = i.getStringExtra("date");

        Log.e("DATE",surDate);

        deleteFile(LSURVEY_FILENAME);
        try {
            FileOutputStream fos = openFileOutput(LSURVEY_FILENAME, Context.MODE_APPEND);
            fos.write(surDate.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Log.e("Feedback surDate",surDate);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        String UID = ((MyApplication) LifestyleFeedbackActivity.this.getApplication()).getUID();
        mDatabase.child("app").child("users").child(UID).child("lifestylesurveyanswersRW").child(surDate).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String response = dataSnapshot.getValue().toString();
                        ArrayList<String> responses = new ArrayList<String>(Arrays.asList(response.split(",")));
                        for (int i=0;i<responses.size();i++) {
                            if(i==responses.size()-1) {
                                surveyResponse[i] =  Integer.parseInt(responses.get(i).substring(1, (responses.get(i)).length()-1));
                            }
                            else {
                                surveyResponse[i] = Integer.parseInt(responses.get(i).substring(1, (responses.get(i)).length()));
                            }
                        }

                        //Log.e("eee", Arrays.toString(surveyResponse));

                        for(int ind=0; ind<surveyResponse.length;ind++){
                            if (surveyResponse[ind]==correctChoice[ind]){
                                responsePosArray.add(Integer.toString(ind+1)+". "+posArray[ind]);
                            }
                            else if (wrongChoice[ind]==0 | surveyResponse[ind]==wrongChoice[ind]){
                                responseNegArray.add(Integer.toString(ind+1)+". "+negArray[ind]);
                            }
                        }

                        if( !responsePosArray.isEmpty()) {
                            responseArray.add("Positive Feedback");
                            responseArray.addAll(responsePosArray);
                        }
                        if( !responsePosArray.isEmpty()&!responseNegArray.isEmpty()) {
                            responseArray.add(" ");
                        }
                        if(!responseNegArray.isEmpty()) {
                            responseArray.add("Educational Feedback");
                            responseArray.addAll(responseNegArray);
                        }

                        final ListView lv = (ListView) findViewById(R.id.LifestyleFeedbackView);
                        adapter = new ArrayAdapter<String>(LifestyleFeedbackActivity.this,R.layout.tip_of_the_day,responseArray);
                        //setListAdapter(adapter);
                        lv.setAdapter(adapter);


                        Button feedbackComplete = (Button) findViewById(R.id.finishButton);

                        feedbackComplete.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                Intent i = new Intent(LifestyleFeedbackActivity.this, com.solutions.medadhere.medadheresolutionsapp.SurveySelectionActivity.class);
                                startActivity(i);
                                finish();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });

    }

}
