package com.solutions.medadhere.medadheresolutionsapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Yeshy on 3/8/2016.
 */
public class LoginActivity extends Activity {

    public static final String PREFS_NAME = "MyPrefsFile";
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    CheckBox check;
    CheckBox checkPass;
    EditText mEdit;
    EditText mPass;
    Context ctx;
    View v;
    String USER_FILENAME = "user_file";
    private String REM_FILENAME = "rem_file";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ctx = this;



        mEdit = (EditText) findViewById(R.id.username1);
        check = (CheckBox) findViewById(R.id.checkbox);


        mPass = (EditText) findViewById(R.id.pass);
        checkPass = (CheckBox) findViewById(R.id.checkPass);
        checkPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                else{
                    Log.e("Bool","FALSE");
                    mPass.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });


            FirebaseAuth.getInstance().signOut();
            mAuth = FirebaseAuth.getInstance();
            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {

                        ((MyApplication) LoginActivity.this.getApplication()).setUID(user.getUid());

                        //Intent intent = new Intent(LoginActivity.this, ReminderService.class);
                        //startActivity(intent);

                        Intent i = new Intent(LoginActivity.this, firstTimeLogin.class);//MainActivity.class);
                        startActivity(i);
                        finish(); //dunno if this'll fuck stuff up. shouldn't be a problem if the phone isn't shit
                    } else {
                        // User is signed out
                        //Log.d("auth", "onAuthStateChanged:signed_out");
                    }
                    // ...
                }
            };



        try {
            FileInputStream fin = openFileInput(USER_FILENAME);
            FileInputStream finpass = openFileInput(REM_FILENAME);
            int c;
            String temp = "";
            while ((c = fin.read()) != -1) {
                temp = temp + Character.toString((char) c);
            }
            int d;
            String tempPass = "";
            while ((d = finpass.read()) != -1) {
                tempPass = tempPass + Character.toString((char) d);
            }

            //Log.e("Login Attempt", temp);
            if (temp.length() > 1) {
                mEdit.setText(temp);
                check.setChecked(true);
                deleteFile(USER_FILENAME);
                //login(temp+ "@mercer.edu","password",v);
            } else {
                check.setChecked(false);
            }
            if (tempPass.length() > 1) {
                mPass.setText(tempPass);
                deleteFile(REM_FILENAME);
                //login(temp+ "@mercer.edu","password",v);
            } else {
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //fucntion that uses silent setSilent(silent);
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);


    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void startMain(View v) {

        mEdit = (EditText) findViewById(R.id.username1);
        check = (CheckBox) findViewById(R.id.checkbox);
        mPass = (EditText) findViewById(R.id.pass);
        checkPass = (CheckBox) findViewById(R.id.checkPass);


        if(mEdit.getText().length()>0&mPass.getText().length()>0){


            String email = mEdit.getText().toString() + "@medadheresolutions.com";
            String password =  mPass.getText().toString();
            //Log.d("username", email);
            //Log.d("password", password);
                login(email, password, v);

        }
        else{
            Toast.makeText(ctx,"Please Enter Login Credentials", Toast.LENGTH_LONG).show();

        }
    }

    public void login(String email, String password, View v) {
        final View view = v;

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("authorization", "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            //Log.w("authorization", "signInWithEmail:failed", task.getException());
                            if(task.getException().toString().contains("InvalidUserException")) {
                                Toast.makeText(ctx, "Login Does Not Exist", Toast.LENGTH_LONG).show();
                            }
                            else if(task.getException().toString().contains("InvalidCredentialsException")){
                                Toast.makeText(ctx, "Incorrect Password", Toast.LENGTH_LONG).show();
                            }
                            Log.e("EXCEPTION",task.getException().toString());


                        }
                        else{
                            deleteFile(REM_FILENAME);
                            deleteFile(USER_FILENAME);
                            if (check.isChecked()) {

                                mEdit = (EditText) findViewById(R.id.username1);
                                check = (CheckBox) findViewById(R.id.checkbox);
                                mPass = (EditText) findViewById(R.id.pass);

                                try {
                                    FileOutputStream fosPass = openFileOutput(REM_FILENAME, Context.MODE_APPEND);
                                    fosPass.write(mPass.getText().toString().getBytes());
                                    fosPass.close();

                                    FileOutputStream fos = openFileOutput(USER_FILENAME, Context.MODE_APPEND);
                                    fos.write(mEdit.getText().toString().getBytes());
                                    fos.close();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        // ...
                    }
                });
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public void onBackPressed() {

        check = (CheckBox) findViewById(R.id.checkbox);

        if (check.isChecked()) {

            mEdit = (EditText) findViewById(R.id.username1);
            //check = (CheckBox) findViewById(R.id.checkbox);
            mPass = (EditText) findViewById(R.id.pass);

            deleteFile(REM_FILENAME);
            deleteFile(USER_FILENAME);
            try {
                FileOutputStream fosPass = openFileOutput(REM_FILENAME, Context.MODE_APPEND);
                fosPass.write(mPass.getText().toString().getBytes());
                fosPass.close();

                FileOutputStream fos = openFileOutput(USER_FILENAME, Context.MODE_APPEND);
                fos.write(mEdit.getText().toString().getBytes());
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        onDestroy();

    }

}

