package com.solutions.medadhere.medadheresolutionsapp;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by lindsayherron on 8/30/16.
 */
public class HealthLitParagraphActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference mDatabase;
    public static final String PREFS_NAME = "MyPrefsFile";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_lit_paragraph);
        //setTheme(R.style.myDialog);
        //showLocationDialog();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setSubtitle("STOFHLA Information");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final Button button = (Button) findViewById(R.id.btnNext);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(HealthLitParagraphActivity.this, healthLiteracyExampleActivity.class);
                startActivity(i);
                finish();
            }
        });

    }
    @SuppressWarnings("StatementWithEmptyBody")
    public boolean onNavigationItemSelected(MenuItem item) {

        mDatabase = FirebaseDatabase.getInstance().getReference();
        String UID = ((MyApplication) this.getApplication()).getUID();

        mDatabase.child("app").child("users").child(UID).child("pharmanumber").addValueEventListener(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String phonenumber = dataSnapshot.getValue().toString();
                        //Log.e("Phone",phonenumber);
                        ((MyApplication) HealthLitParagraphActivity.this.getApplication()).setPharmaPhone(phonenumber);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
        String tel = ((MyApplication) this.getApplication()).getPharmaPhone();

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id  == R.id.nav_home){
            Intent i = new Intent(this, com.solutions.medadhere.medadheresolutionsapp.MainActivity.class);
            startActivity(i);
            finish();
        }
        else if (id == R.id.nav_bloodpressure) {
            Intent i = new Intent(this, BPCalendarActivity.class);
            startActivity(i);
            finish();
        }else if (id == R.id.nav_medication) {
            Intent i = new Intent(this, com.solutions.medadhere.medadheresolutionsapp.MedicationActivity.class);
            startActivity(i);
            finish();
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
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            String UIDstored = settings.getString("UID", "Default");
            //Log.e("logout", UIDstored);

            SharedPreferences.Editor editor = settings.edit();
            editor.putString("UID", "Default");
            editor.commit();

            UIDstored = settings.getString("UID", "Default");
            //Log.e("logout", UIDstored);
            Intent i = new Intent(this, com.solutions.medadhere.medadheresolutionsapp.LoginActivity.class);
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
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}
