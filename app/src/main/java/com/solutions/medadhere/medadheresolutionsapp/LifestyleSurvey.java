package com.solutions.medadhere.medadheresolutionsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.LayoutParams;
import android.support.v4.widget.Space;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

//import android.icu.text.DateFormat;
//import android.icu.text.SimpleDateFormat;


/**
 * Created by Javier 9/18/16.
 */
public class LifestyleSurvey extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DatabaseReference mDatabase ;

    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String LSURVEY_FILENAME = "lsurvey_file";
    Button btnTag;
    int totalAnswered=0;
    int number;
    int qnum;
    int numRadBut;
    int prevClick=-1;
    Context ctx;
    final ArrayList<String> questions = new ArrayList<>();
    String[] questionArray = new String[questions.size()];
    String buttonID;
    Toolbar toolbar;
    TextView amountOfQuestions;
    ProgressBar progressBar;
    final static int GET_RESULT_TEXT = 0;
    ArrayList<String[]> questionchoices = new ArrayList<>();
    ArrayList<String[]> key = new ArrayList<>();
    int [] naArray;
    int [] answers;
    int [] rightorwrong;
    float textsize = 20;
    int selected =0;
    public int questCount = 0;
    int Lindsay = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lifestyle_survey);
        //Button testButton = (Button) findViewById(R.id.TESTBUTTON);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);



        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        toolbar.setSubtitle("Lifestyle Survey");

        LinearLayout layout_main = (LinearLayout) findViewById(R.id.main_ll);
        //get questions from xml file
        String[] mArray = getResources().getStringArray(R.array.LifestyleSurvey);
        final ArrayList<String> questions = new ArrayList<>();


        for (String full : mArray) {
            String[] split = full.split("_");
            questions.add(split[0]);
            questionchoices.add(Arrays.copyOfRange(split, 1, split.length));
        }

        System.out.println(mArray.toString());
        questionArray = new String[questions.size()];
        questionArray = questions.toArray(questionArray);

        //get key from xml file
        final String[] keyArray = getResources().getStringArray(R.array.LifestyleSurveyAnswers);
        for (String full : keyArray) {
            String[] split = full.split("_");
            key.add(Arrays.copyOfRange(split, 0, split.length));
        }

        //get notApplicable list from xml file
        String[] naArray1 = getResources().getStringArray(R.array.LifestyleSurveyAnswersNA);
        naArray = new int[naArray1.length];
        int counter = 0;
        for (String full : naArray1) {
            naArray[counter] = Integer.parseInt(full);
            counter++;
        }

        //initialize results arrays
        answers = new int[mArray.length];
        Arrays.fill(answers, -1); //default for unanswered
        rightorwrong = new int[mArray.length];
        Arrays.fill(rightorwrong, 1); //default for wrong

        final HorizontalScrollView horScrollView = (HorizontalScrollView) findViewById(R.id.horizontal_scrollview);
        final TextView questionsView = (TextView) layout_main.findViewById(R.id.questionView);
        //questionsView.setText("Click an above button to start the survey.");
        final Button button = (Button) findViewById(R.id.submitButton);
        final Button buttonNext = (Button) findViewById(R.id.nextButton);
        final Button buttonPrev = (Button) findViewById(R.id.prevButton);
        final RadioGroup radiogr = (RadioGroup) findViewById(R.id.radiogroup);
        LinearLayout layout = (LinearLayout) findViewById(R.id.lv);

        for (int i = 0; i < 8; i++) {
            final Button btnTag = new Button(this);
            btnTag.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            btnTag.setText("Question " + (i + 1));
            btnTag.setId(Integer.parseInt(Integer.toString(i)));
            layout.addView(btnTag);
            btnTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    qnum = btnTag.getId();
                    for(int i=0;i<answers.length;i++){
                        if (answers[i]!=-1){
                            totalAnswered++;
                        }
                    }
                    if(qnum!=prevClick) {
                        numRadBut = radiogr.getChildCount();
                        if (numRadBut > 0) {
                            radiogr.removeAllViews();
                        }
                    horScrollView.scrollTo(qnum * 388, 0);
                    if (qnum < 7) {
                        if (qnum == 0) {
                            buttonPrev.setVisibility(View.INVISIBLE);
                        }
                        buttonNext.setVisibility(View.VISIBLE);
                    }
                    if (qnum > 0) {
                        if (qnum == 7) {
                            buttonNext.setVisibility(View.INVISIBLE);
                        }
                        buttonPrev.setVisibility(View.VISIBLE);
                    }
                    button.setVisibility(view.VISIBLE);

                    LinearLayout rblayout = (LinearLayout) findViewById(R.id.rblayout);
                    questionsView.setText(Integer.toString(qnum + 1) + ".  " + questionArray[qnum]);

                    number = questionchoices.get(qnum).length - 1;

                    addRadioButtons(number, qnum);
                }

                prevClick=qnum;
            }

            });
        }

        buttonNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                questCount++;
                btnTag = (Button) findViewById(qnum+1);
                if (questCount < 8){
                    btnTag.performClick();
                }

            }
        });

        buttonPrev.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                questCount--;
                btnTag = (Button) findViewById(qnum-1);
                if (questCount >= 0) {
                    btnTag.performClick();
                }
            }
        });



        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mDatabase = FirebaseDatabase.getInstance().getReference();
                String UID = ((MyApplication) LifestyleSurvey.this.getApplication()).getUID();



                selected=0;
                int nonselected = 0;
                for (int x : answers) {
                    if (x != -1) {
                        selected++;
                    }
                    else{
                        nonselected++;
                    }
                }
                if (selected>7) {
                    Snackbar.make(v, "Lifestyle Survey Successfully Completed", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();


                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    System.out.println(dateFormat.format(cal.getTime()));
                    final String currentDate = dateFormat.format(cal.getTime());

                    mDatabase.child("app").child("users").child(UID).child("lifestylesurveyanswersRW").child(currentDate) .setValue(Arrays.toString(answers));
                    deleteFile(LSURVEY_FILENAME);
                    try {
                        FileOutputStream fos = openFileOutput(LSURVEY_FILENAME, Context.MODE_APPEND);
                        fos.write(currentDate.getBytes());
                        fos.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Intent i = new Intent(LifestyleSurvey.this, LifestyleFeedbackActivity.class);
                    i.putExtra("date",currentDate);
                    startActivity(i);
                    finish();
                }
                else{
                    Snackbar.make(v, "Please complete all 8 questions. "+nonselected+" questions remain. Please scroll through the buttons above.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });



    }


    public void addRadioButtons(int number, int question) {

        final RadioGroup rg = ((RadioGroup) findViewById(R.id.radiogroup));

        final int numberRB = number;
        final int questionnumber = question;
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                answers[questionnumber]=checkedId-8;
                rg.check(checkedId);
                ((Button)findViewById(questionnumber)).setBackgroundColor(Color.TRANSPARENT);
            }
        });

        for (int row = 0; row < 1; row++) {

            for (int i = 8; i <= number+8; i++) {
                final RadioButton rdbtn = new RadioButton(this);
                //rdbtn.setId((row * 2) + i);
                rdbtn.setText(questionchoices.get(qnum)[i-8]+" ");
                int textColor = Color.parseColor("#A9A9A9");
                rdbtn.setButtonTintList(ColorStateList.valueOf(textColor));
                rdbtn.setTextSize(20);
                rdbtn.setId(i);

                rg.addView(rdbtn);
                if (i-8!=number) {
                    Space space = new Space(this);
                    space.setMinimumHeight(50);
                    rg.addView(space);
                }

                if(answers[question]==i-8){
                    Log.e("Set","Blue");
                    rdbtn.setChecked(true);
                    rdbtn.setBackgroundColor(Color.parseColor("#70FFFF"));
                }

                rdbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        Log.e("RBID", Integer.toString(compoundButton.getId()));
                        if (b){
                            for (int i=8;i<=numberRB+8;i++){
                                if(i!=(int)compoundButton.getId()){
                                    Log.e("Color"+i,"TRANS"+numberRB);
                                    ((RadioButton)findViewById(i)).setBackgroundColor(Color.TRANSPARENT);
                                }
                            }
                            Log.e("Color"+compoundButton.getId(),"BLUE");
                            compoundButton.setBackgroundColor(Color.parseColor("#70FFFF"));
                        }
                    }
                });
            }
        }


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
                        ((MyApplication) LifestyleSurvey.this.getApplication()).setPharmaPhone(phonenumber);
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
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }
        else if (id == R.id.nav_bloodpressure) {
            Intent i = new Intent(this, BloodPressureActivity.class);
            startActivity(i);
            finish();
        }else if(id == R.id.nav_weight){
            Intent i = new Intent(this, WeightCalendarActivity.class);
            startActivity(i);
        }else if (id == R.id.nav_medication) {
            Intent i = new Intent(this, MedicationActivity.class);
            startActivity(i);
            finish();
        }else if (id == R.id.nav_surveys) {
            Intent i = new Intent(this, SurveySelectionActivity.class);
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
