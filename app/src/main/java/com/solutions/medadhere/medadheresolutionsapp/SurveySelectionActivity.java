package com.solutions.medadhere.medadheresolutionsapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

//import android.icu.text.DateFormat;
//import android.icu.util.GregorianCalendar;

public class SurveySelectionActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DatabaseReference mDatabase;
    Context ctx;
    public static final String HSURVEY_FILENAME = "hsurvey_file";
    public static final String LSURVEY_FILENAME = "lsurvey_file";
    public static final String PREFS_NAME = "MyPrefsFile";
    int surYear;
    int surMonth;
    int surDay;
    int curYear;
    int curMonth;
    int curDay;
    String surveyDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_selection);
        ctx = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setSubtitle("Survey List");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FileInputStream finLife = null;
        FileInputStream finHealth = null;

           if (((MyApplication) SurveySelectionActivity.this.getApplication()).getLifestyleSurveyAnswers()[0]!=-1) {
               ((TextView) findViewById(R.id.life)).setText("Lifestyle Survey: " + ((MyApplication) SurveySelectionActivity.this.getApplication()).getLifestyleDate());
           }
            else{
               ((TextView) findViewById(R.id.life)).setText("Lifestyle Survey: Not Taken Yet");

           }

        if (((MyApplication) SurveySelectionActivity.this.getApplication()).getLiteracySurveyAnswers()[0]!=-1) {
            ((TextView) findViewById(R.id.health)).setText("Literacy Survey: " + ((MyApplication) SurveySelectionActivity.this.getApplication()).getLiteracyDate());
        }
        else{
            ((TextView) findViewById(R.id.health)).setText("Literacy Survey: Not Taken Yet");
        }



        initializeSurveyButtons();

    }

    public void initializeSurveyButtons() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        Log.e("printed data",dateFormat.format(cal.getTime()));
        final String currDate = dateFormat.format(cal.getTime());
        Log.e("currentDateSurvSelect",currDate);

        LinearLayout surveys = (LinearLayout) findViewById(R.id.surveys);

        Button lifestyleSurvey = (Button) findViewById(R.id.lifestylesurveybutton);
        Button healthLiteracySurvey = (Button) findViewById(R.id.healthliteracysurveybutton);
        Button appSatisfactionSurvey = (Button) findViewById(R.id.appsatisfactionbutton);

        lifestyleSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alert = new AlertDialog.Builder(SurveySelectionActivity.this);

                alert.setTitle("Take Survey or See Feedback");
                alert.setMessage("Would you like to take the lifestyle survey or see previous feedback?");

                alert.setPositiveButton("Survey", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent i = new Intent(SurveySelectionActivity.this, LifestyleSurvey.class);
                        //i.putExtra("name", 1); //number corresponds to survey
                        startActivity(i);

                    }
                });

                alert.setNegativeButton("Feedback", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int whichButton) {

                        Log.e("Date Life",((MyApplication) SurveySelectionActivity.this.getApplication()).getLifestyleDate());

                        if (((MyApplication) SurveySelectionActivity.this.getApplication()).getLifestyleSurveyAnswers()[0]!=-1) {
                            String lastDate = ((MyApplication) SurveySelectionActivity.this.getApplication()).getLifestyleDate();
                            Log.e("Life Date", lastDate);
                            Intent i = new Intent(SurveySelectionActivity.this, LifestyleFeedbackActivity.class);
                            i.putExtra("date", lastDate); //number corresponds to survey
                            startActivity(i);
                        } else {
                            Toast.makeText(ctx, "No Previous FeedBack", Toast.LENGTH_LONG).show();
                            dialog.cancel();
                        }

                    }
                });

                AlertDialog alertDialog = alert.create();


                alertDialog.show();

            }
        });


        healthLiteracySurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SurveySelectionActivity.this, HealthLitParagraphActivity.class);
                startActivity(i);
            }
        });

        appSatisfactionSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SurveySelectionActivity.this, AppSatisfactionSurveyActivity.class);
                startActivity(i);
            }
        });

        final ArrayList<String> medication = ((MyApplication) this.getApplication()).getMeds();
        for (int i = 0; i < medication.size(); i++) {
            final String medName = medication.get(i);
            Button med = new Button(this);
            med.setText("Medication Adherence: "+medName);
            med.setBackgroundResource(R.drawable.button_bg);
            med.setId(i);
            med.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SurveySelectionActivity.this, MedicationAdherenceSurvey.class);
                    intent.putExtra("medName",medName);
                    startActivity(intent);
                }
            });

            surveys.addView(med);

        }


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    public long getDifferenceDays(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        String tel = ((MyApplication) this.getApplication()).getPharmaPhone();

        int id = item.getItemId();
        if (id  == R.id.nav_home){
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }
        else if (id == R.id.nav_bloodpressure) {
            Intent i = new Intent(this, BPCalendarActivity.class);
            startActivity(i);
            finish();
        }else if(id == R.id.nav_weight){
            Intent i = new Intent(this, WeightCalendarActivity.class);
            startActivity(i);
            finish();
        } else if (id == R.id.nav_medication) {
            Intent i = new Intent(this, MedicationActivity.class);
            startActivity(i);
            finish();
        }else if (id == R.id.nav_surveys) {
            //Intent i = new Intent(this, SurveySelectionActivity.class);
            //startActivity(i);

        } else if (id == R.id.nav_callmypharmacist) {
            Intent i = new Intent(Intent.ACTION_DIAL);
            i.setData(Uri.parse("tel:"+tel));
            startActivity(i);
            finish();
        } else if (id == R.id.nav_logout) {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            String UIDstored = settings.getString("UID", "Default");
            //Log.e("logout", UIDstored);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("UID", "Default");
            editor.commit();
            UIDstored = settings.getString("UID", "Default");
            //Log.e("logout", UIDstored);
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        }
        else if (id == R.id.nav_study_contact) {
            Intent i = new Intent(this, StudyContactsActivity.class);
            startActivity(i);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
