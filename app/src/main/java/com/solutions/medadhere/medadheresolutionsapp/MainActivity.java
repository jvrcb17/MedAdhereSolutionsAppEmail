package com.solutions.medadhere.medadheresolutionsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import static android.util.Log.e;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String MED_FILENAME = "med_file";
    String FREQ_FILENAME = "freq_file";
    public static final String LSURVEY_FILENAME = "lsurvey_file";
    public static final String HSURVEY_FILENAME = "hsurvey_file";
    private DatabaseReference mDatabase;

    String[] surveyResponse = new String[8];
    ArrayList<String> medArray= new ArrayList<String>();
    ArrayList<String> medFrequency= new ArrayList<String>();
    //private ArrayList<Medication> medicationList = new ArrayList<>();
    public static final String PREFS_NAME = "MyPrefsFile";
    ArrayAdapter<String> adapter;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        mDatabase = FirebaseDatabase.getInstance().getReference();
        String UID = ((MyApplication) this.getApplication()).getUID();
        //String attempt2 = ((MyApplication) MainActivity.this.getApplication()).getPhone();
        Log.e("UID",UID);

        Log.e("Enters","Main");
        //Log.e("Phone2",attempt2);
        //getMeds(); // this creates a meds array and a frequency array from those meds
        findLifestyleFeedback();
        findHealthDate();

        Date d = new Date();
        d.getDate();
        d.getYear();
        mDatabase.child("app").child("users").child(UID).child("pharmanumber").addValueEventListener(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String phonenumber = dataSnapshot.getValue().toString();
                        ((MyApplication) MainActivity.this.getApplication()).setPharmaPhone(phonenumber);
                        Log.e("Enters","Main");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });

    }


    private void initializeMessagesList() {

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_YEAR);
        Log.e("Cal","the Day");
        //Random generator = new Random();
        int num = day % surveyResponse.length;
        //Log.e("num",Integer.toString(num));

        //String [] mArrayBefore = new String[messages.size()];
        //mArrayBefore = messages.toArray(mArrayBefore);
        String[] mArray = {"Messages will appear here once the survey data has loaded."};
        //Log.e("list",Arrays.toString(surveyResponse));
        if (num < surveyResponse.length) {
            while(surveyResponse[num]==null & num<surveyResponse.length-1){
                if(surveyResponse[num]==null) {
                    //Log.e("surveyRes", "null"+Integer.toString(num));
                }
                num++;
                if (num == surveyResponse.length-1){
                    num =0;
                }
            }
            mArray[0] = surveyResponse[num];
        }

        Log.e("Tip of","the Day");
       //Log.e("list",Arrays.toString(surveyResponse));
        adapter = new ArrayAdapter<String>(this, R.layout.tip_of_the_day, mArray);
        //setListAdapter(adapter);
        final ListView lv = (ListView) findViewById(R.id.messagesListView);
        lv.setAdapter(adapter);

    }

    public void setLifestyleResponse(int[] intArray1) {
        String[] posArray = getResources().getStringArray(R.array.LifestylePositiveMessagesArray);
        String[] negArray = getResources().getStringArray(R.array.LifestyleNegativeMessagesArray);
        final int[] correctChoice = {4,1,2,2,1,2,2,4};
        final int[] wrongChoice = {0,0,1,1,0,1,1,0};

        for (int ind = 0; ind < intArray1.length; ind++) {
            if (intArray1[ind] == correctChoice[ind]) {
                surveyResponse[ind] = posArray[ind];
            } else if (wrongChoice[ind]==0 | intArray1[ind] == wrongChoice[ind]) {
                surveyResponse[ind] = negArray[ind];
            }
        }
        initializeMessagesList();
    }


    public void findLifestyleFeedback() {
        String UID = ((MyApplication) this.getApplication()).getUID();
        final ArrayList<String> responseArray = new ArrayList<>();
        final int[] surveyResponses = new int[8];
        final String[] lifestyledate = new String[1];
        mDatabase.child("app").child("users").child(UID).child("lifestylesurveyanswersRW").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                        int g = 0;
                        if(it.hasNext()) {
                            while (it.hasNext()) {
                                DataSnapshot medicine = it.next();
                                String attempts = medicine.getValue().toString();
                                //Log.e("Hopeful Date",medicine.getKey().toString());
                                responseArray.add(attempts);
                                g++;
                                if (!it.hasNext()) {
                                    lifestyledate[0] = medicine.getKey().toString();
                                }
                            }
                            ArrayList<String> finalresponses = new ArrayList<String>(Arrays.asList(responseArray.get(responseArray.size() - 1).split(",")));
                            for (int i = 0; i < finalresponses.size(); i++) {
                                if (i == finalresponses.size() - 1) {
                                    surveyResponses[i] = Integer.parseInt(finalresponses.get(i).substring(1, (finalresponses.get(i)).length() - 1));
                                } else {
                                    surveyResponses[i] = Integer.parseInt(finalresponses.get(i).substring(1, (finalresponses.get(i)).length()));
                                }
                            }
                            setLifestyleResponse(surveyResponses);
                            //Log.e("again final date",lifestyledate[0]);

                            ((TextView) findViewById(R.id.life)).setText("Lifestyle Survey: " + lifestyledate[0]);

                            deleteFile(LSURVEY_FILENAME);
                            try {
                                FileOutputStream fos = openFileOutput(LSURVEY_FILENAME, Context.MODE_APPEND);
                                fos.write(lifestyledate[0].getBytes());
                                fos.close();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            deleteFile(LSURVEY_FILENAME);
                            try {
                                FileOutputStream fos = openFileOutput(LSURVEY_FILENAME, Context.MODE_APPEND);
                                fos.write("Not Taken Yet".getBytes());
                                fos.close();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            String [] array = {"Please take the Lifestyle Survey to get daily tips."};
                            adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.tip_of_the_day, array);
                            //setListAdapter(adapter);
                            final ListView lv = (ListView) findViewById(R.id.messagesListView);
                            lv.setAdapter(adapter);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }

                });

    }

    public void findHealthDate() {
        String UID = ((MyApplication) this.getApplication()).getUID();
        final ArrayList<String> responseArray = new ArrayList<>();
        final int[] surveyResponses = new int[8];
        final String[] literacydate = new String[1];
        mDatabase.child("app").child("users").child(UID).child("literacysurveyanswersRW").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                        int g = 0;
                        if(it.hasNext()) {
                            while (it.hasNext()) {
                                DataSnapshot medicine = it.next();
                                if (!it.hasNext()) {
                                    literacydate[0] = medicine.getKey().toString();
                                }
                            }


                            ((TextView) findViewById(R.id.health)).setText("Literacy Survey: " + literacydate[0]);

                            deleteFile(HSURVEY_FILENAME);
                            try {
                                FileOutputStream fos = openFileOutput(HSURVEY_FILENAME, Context.MODE_APPEND);
                                fos.write(literacydate[0].getBytes());
                                fos.close();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            deleteFile(HSURVEY_FILENAME);
                            try {
                                FileOutputStream fos = openFileOutput(HSURVEY_FILENAME, Context.MODE_APPEND);
                                fos.write("Not Taken Yet".getBytes());
                                fos.close();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }

                });

        //first notification at 11 AM week from last Survey Date
        //startWeeklyAlarm(this);

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            //finish();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        String tel = ((MyApplication) this.getApplication()).getPharmaPhone();
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_home) {
        } else if (id == R.id.nav_bloodpressure) {
            Intent i = new Intent(this, BloodPressureActivity.class);
            startActivity(i);
        } else if(id == R.id.nav_weight){
            Intent i = new Intent(this, WeightCalendarActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_medication) {
            Intent i = new Intent(this, MedicationActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_surveys) {
            Intent i = new Intent(this, SurveySelectionActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_callmypharmacist) {
            Intent i = new Intent(Intent.ACTION_DIAL);
            i.setData(Uri.parse("tel:" + tel));
            startActivity(i);
        } else if (id == R.id.nav_logout) {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            String UIDstored = settings.getString("UID", "Default");
            e("logout", UIDstored);

            SharedPreferences.Editor editor = settings.edit();
            editor.putString("UID", "Default");
            editor.commit();

            UIDstored = settings.getString("UID", "Default");
            e("logout", UIDstored);
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        } else if (id == R.id.nav_study_contact) {
            Intent i = new Intent(this, StudyContactsActivity.class);
            startActivity(i);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /*
    public void getMeds() {
        String UID = ((MyApplication) this.getApplication()).getUID();
        mDatabase.child("app").child("users").child(UID).child("medicine").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //e("reading", dataSnapshot.toString());
                        initializeMessagesList();
                        Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                        while (it.hasNext()) {
                            DataSnapshot medicine = it.next();
                            //e("reading2", medicine.toString());
                            //e("reading3", medicine.getKey());
                            medArray.add(medicine.getKey().toString());
                        }
                        //Log.e("meds", medArray.toString());

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

    }


*/



}
