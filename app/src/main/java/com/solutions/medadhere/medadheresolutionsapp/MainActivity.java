package com.solutions.medadhere.medadheresolutionsapp;

import android.content.Intent;
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

import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Arrays;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


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




        setLifestyleResponse(((MyApplication)this.getApplication()).getLifestyleSurveyAnswers());


    }


    private void initializeMessagesList(String [] surveyResponse) {
        String [] mArray = {""};
        int count=0;
        for(int i=0;i<surveyResponse.length;i++){
            if(surveyResponse[i]==null){
                count++;
            }
        }

        if(count!=8) {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_YEAR);
            Log.e("Cal", "the Day");
            //Random generator = new Random();
            int num = day % surveyResponse.length;
            //Log.e("num",Integer.toString(num));

            //String [] mArrayBefore = new String[messages.size()];
            //mArrayBefore = messages.toArray(mArrayBefore);
            //Log.e("list",Arrays.toString(surveyResponse));
            if (num < surveyResponse.length) {
                while (surveyResponse[num] == null & num < surveyResponse.length - 1) {
                    if (surveyResponse[num] == null) {
                    }
                    num++;
                    if (num == surveyResponse.length - 1) {
                        num = 0;
                    }
                }
                mArray[0] = surveyResponse[num];
            }
        }
        else{
           mArray[0] = "Please take the Lifestyle Survey to get a tip of the day!";
        }

        Log.e("Tip of","the Day");
       //Log.e("list",Arrays.toString(surveyResponse));
        adapter = new ArrayAdapter<String>(this, R.layout.tip_of_the_day, mArray);
        //setListAdapter(adapter);
        final ListView lv = (ListView) findViewById(R.id.messagesListView);
        lv.setAdapter(adapter);

    }

    public void setLifestyleResponse(int[] intArray1) {
        Log.e("Survey Response", Arrays.toString(intArray1));
        String[] surveyResponse = new String[8];
        String[] posArray = getResources().getStringArray(R.array.LifestylePositiveMessagesArray);
        String[] negArray = getResources().getStringArray(R.array.LifestyleNegativeMessagesArray);
        final int[] correctChoice = {4,1,2,2,1,2,2,4};
        final int[] wrongChoice = {0,0,1,1,0,1,1,0};

        for (int ind = 0; ind < intArray1.length; ind++) {
            if (intArray1[ind] == correctChoice[ind]) {
                //Log.e("Int Array1",Integer.toString(intArray1[ind]));
                surveyResponse[ind] = posArray[ind];
            } else if ((wrongChoice[ind]==0&intArray1[ind]!=-1) | intArray1[ind] == wrongChoice[ind]) {
                //Log.e("Int Array1",Integer.toString(intArray1[ind]));
                surveyResponse[ind] = negArray[ind];
            }
        }

        Log.e("To Init",Arrays.toString(surveyResponse));
        initializeMessagesList(surveyResponse);
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
            Intent i = new Intent(this, BPCalendarActivity.class);
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


}
