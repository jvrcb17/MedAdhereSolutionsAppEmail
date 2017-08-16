package com.solutions.medadhere.medadheresolutionsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

//import java.sql.Time;

/**
 * Created by Yeshy on 7/12/2016.
 */
public class MedicationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private MedicationActivity medAct;
    private DatabaseReference mDatabase;
    private NfcAdapter nfcAdapter;
    Intent intent;
    Context ctx;
    public static final String PREFS_NAME = "MyPrefsFile";
    int curYear;
    int curMonth;
    int curDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        setContentView(R.layout.activity_medication);
        ctx = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setSubtitle("Medication Records");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        TextView meds = (TextView) findViewById(R.id.meds);
        String userMeds = "";
        ArrayList<String> medication = ((MyApplication) this.getApplication()).getMeds();
        ArrayList<String> frequency = ((MyApplication) this.getApplication()).getFreqList();
        for (int i = 0; i < medication.size(); i++) {
            userMeds += medication.get(i) + ": " + frequency.get(i) + "\n\r";
        }
        meds.setText(userMeds);
        meds.setTextSize(18);

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
            LinearLayout ll = (LinearLayout) findViewById(R.id.NFCLayout);
            Button currentDateButton = new Button(this);
            currentDateButton.setVisibility(View.VISIBLE);
            currentDateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String data = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

                    String currDate = data;
                    curYear = Integer.parseInt(currDate.substring(0, currDate.indexOf("-")));
                    curMonth = Integer.parseInt(currDate.substring(currDate.indexOf("-") + 1, currDate.lastIndexOf("-")));
                    curDay = Integer.parseInt(currDate.substring(currDate.lastIndexOf("-") + 1, currDate.length()));
                    Intent i = new Intent(ctx, MedicationLogActivity.class);
                    //old.set(GregorianCalendar.MONTH, date.getMonth()-1);
                    Log.e("Current Date", data);
                    i.putExtra("date", data);//Log.e("nrp",String.format("%d-%d", date.getMonth(), date.getDay()));
                    startActivity(i);
                }
            });
            ll.addView(currentDateButton);
        }
        initializeCalendar();

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this,
                    "NFC NOT supported on this devices!",
                    Toast.LENGTH_LONG).show();
        } else if (nfcAdapter.isEnabled()) {
            Toast.makeText(this,
                    "NFC supported!",
                    Toast.LENGTH_LONG).show();

            Button btnTag = new Button(this);
            LinearLayout nfcLayout = (LinearLayout) findViewById(R.id.NFCLayout);
            btnTag.setText("NFC Available");
            btnTag.setVisibility(View.VISIBLE);
            btnTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(MedicationActivity.this, nfcTag.class);
                    startActivity(i);
                }

            });
            nfcLayout.addView(btnTag);
        } else if (!nfcAdapter.isEnabled()) { //Your device doesn't support NFC
            Toast.makeText(this,
                    "NFC NOT Enabled!",
                    Toast.LENGTH_LONG).show();
        }
        //finish();
    }

    public void initializeCalendar() {
        CalendarView calendarMed = (CalendarView) findViewById(R.id.calendarMed);
        //calendarMed.setBackground(getResources().getDrawable(R.drawable.common_google_signin_btn_icon_dark));

        calendarMed.setSelectedDateVerticalBar(getResources().getDrawable(R.drawable.common_google_signin_btn_text_dark_normal));
        //calendarMed.setDateTextAppearance(R.style.TextTheme);
        calendarMed.setOnDateChangeListener(new OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,int day) {

                String currentDate;

                    String data = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

                    String currDate = data;
                    curYear = Integer.parseInt(currDate.substring(0, currDate.indexOf("-")));
                    curMonth = Integer.parseInt(currDate.substring(currDate.indexOf("-") + 1, currDate.lastIndexOf("-")));
                    curDay = Integer.parseInt(currDate.substring(currDate.lastIndexOf("-") + 1, currDate.length()));



                Date current = new Date(curYear,curMonth,curDay);// some Dat
                Date survey = new Date(year,month+1,day);// some Date
                Date oldDateTime = new Date(1,1,2010);
                //Log.e("surveyMonth",Integer.toString(month));
                int one = (int)getDifferenceDays(oldDateTime,current);
                int two = (int)getDifferenceDays(oldDateTime,survey);

                String mon;
                String d;
                if (month+1<10) {
                    mon = "0" + Integer.toString(month+1);
                    Log.e("Less Than","10: "+mon);
                }else
                {
                    mon = Integer.toString(month+1);
                    Log.e("Greater Than","10: " +mon);
                }

                if (day<10) {
                    d = "0" + Integer.toString(day);
                }else
                {
                    d = Integer.toString(day);
                }
                int daysSince = one-two;

                if (daysSince==0) {
                    Intent i = new Intent(ctx, MedicationLogActivity.class);
                    //old.set(GregorianCalendar.MONTH, date.getMonth()-1);
                    i.putExtra("date", Integer.toString(year)+"-"+mon+"-"+d);//Log.e("nrp",String.format("%d-%d", date.getMonth(), date.getDay()));
                    startActivity(i);
                    //Snackbar.make(view, String.format("%d-%d", date.getMonth(), date.getDay()), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
                else if(daysSince>0){
                    Intent i = new Intent(ctx, MedicationLogReadActivity.class);
                    //old.set(GregorianCalendar.MONTH, date.getMonth()-1);
                    i.putExtra("date", Integer.toString(year)+"-"+mon+"-"+d);//Log.e("nrp",String.format("%d-%d", date.getMonth(), date.getDay()));
                    startActivity(i);
                    //Snackbar.make(view, String.format("%d-%d", date.getMonth(), date.getDay()), Snackbar.LENGTH_LONG).setAction("Action", null).show();

                }
                else{
                    Snackbar.make(view,"Please only edit the current day.",Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }

            }
        });
    }

    public static long getDifferenceDays(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
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

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        String tel = ((MyApplication) this.getApplication()).getPharmaPhone();

        // Handle navigation view item clicks here.
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
            //Intent i = new Intent(this, MedicationActivity.class);
            //startActivity(i);

        }else if (id == R.id.nav_surveys) {
            Intent i = new Intent(this, com.solutions.medadhere.medadheresolutionsapp.SurveySelectionActivity.class);
            startActivity(i);
            finish();
        } else if (id == R.id.nav_callmypharmacist) {
            Intent i = new Intent(Intent.ACTION_DIAL);
            i.setData(Uri.parse("tel:"+tel));
            startActivity(i);
            finish();
        } else if (id == R.id.nav_logout) {
            finish();
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
            Intent i = new Intent(this, com.solutions.medadhere.medadheresolutionsapp.StudyContactsActivity.class);
            startActivity(i);
            finish();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
