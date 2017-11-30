package com.example.android.ordernow;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    EditText mEmailEditText;
    EditText mNameEditText;
    EditText mSurnameEditText;
    EditText mPhoneNoEditText;
    EditText mPasswordEditText;
    EditText mConfirmPasswordEditText;
    Button mSignUpButton;
    FirebaseRemoteConfig mRemoteConfig;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings remoteConfigSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(true)
                .build();
        mRemoteConfig.setConfigSettings(remoteConfigSettings);

        HashMap<String, Object> defaults = new HashMap<>();

        defaults.put("action_bar_color", R.color.colorPrimary);
        defaults.put("status_bar_color", R.color.colorPrimaryDark);
        mRemoteConfig.setDefaults(defaults);

        final SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String abColor = prefs.getString("action_bar_color", String.valueOf(R.color.colorPrimary));
        String statColor = prefs.getString("status_bar_color", String.valueOf(R.color.colorPrimaryDark));

        try {
            ActionBar ab = getSupportActionBar();
            ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor(abColor)));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.parseColor(statColor));
            }
        }catch (IllegalArgumentException ie){
            ActionBar ab = getSupportActionBar();
            ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff00ff")));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.parseColor("#ff00ff"));
            }
        }

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        mEmailEditText = (EditText) findViewById(R.id.signup_email_et);
        mNameEditText = (EditText) findViewById(R.id.signup_name_et);
        mSurnameEditText = (EditText) findViewById(R.id.signup_surname_et);
        mPhoneNoEditText = (EditText) findViewById(R.id.signup_phone_et);
        mPasswordEditText = (EditText) findViewById(R.id.signup_password_et);
        mConfirmPasswordEditText = (EditText) findViewById(R.id.signup_password_confirm_et);
        mSignUpButton = (Button) findViewById(R.id.signup_button);

        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);


        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean allchecked = true;

                final String email;
                final String name;
                final String surname;
                final long phoneNo;
                final String password;


                if(mEmailEditText.getText().toString().trim().length() > 0 ){
                    email = mEmailEditText.getText().toString();
                }else {
                    email = "";
                    allchecked = false;
                }

                if(mNameEditText.getText().toString().trim().length() > 0 ){
                    name = mNameEditText.getText().toString();
                }else {
                    name = null;
                    allchecked = false;
                }

                if(mSurnameEditText.getText().toString().trim().length() > 0 ){
                    surname = mSurnameEditText.getText().toString();
                }else {
                    surname = null;
                    allchecked = false;
                }

                if(mPhoneNoEditText.getText().toString().trim().length() == 10){
                    phoneNo = Long.parseLong(mPhoneNoEditText.getText().toString());
                }else {
                    phoneNo = 0;
                    allchecked = false;
                }

                if(mPasswordEditText.getText().toString().trim().length() > 0 ){
                    password = mPasswordEditText.getText().toString();
                }else {
                    password = null;
                    allchecked = false;
                }
                final String newmail = email.replaceAll("[.]", "%");
                String pass = mPasswordEditText.getText().toString();
                String confpass = mConfirmPasswordEditText.getText().toString();
                if(allchecked && pass.equals(confpass)){
                    final DatabaseReference ref = database.getReference("Users");
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.hasChild(newmail)) {
                                Log.i("child", "has child");
                                AlertDialog alertDialog = new AlertDialog.Builder(SignUpActivity.this).create();
                                alertDialog.setTitle("Error");
                                alertDialog.setMessage("this email is already associated with another account");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Dismiss",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.show();
                            }else{
                                Log.i("child", "new child successfully added");
                                DatabaseReference newUser = database.getReference("Users").child(newmail);
                                User user = new User(newmail, name, surname, phoneNo, password);
                                newUser.setValue(user);

                                Intent intent  = new Intent(SignUpActivity.this, LoginActivity.class);
                                intent.putExtra("userEmail", email);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    //old code, kept in case for no real reason
                    /*ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(newmail).exists()){
                                Log.i("child", "has child");
                                AlertDialog alertDialog = new AlertDialog.Builder(SignUpActivity.this).create();
                                alertDialog.setTitle("Error");
                                alertDialog.setMessage("One or more fields are empty or have incorrect inputs");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Dismiss",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.show();
                            }else{
                                Log.i("nochild", "onDataChange: no child");
                                User user = new User(newmail, name, surname, phoneNo, password);
                                ref.setValue(user);

                                Intent intent  = new Intent(SignUpActivity.this, LoginActivity.class);
                                intent.putExtra("userEmail", email);
                                startActivity(intent);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError firebaseError) {

                        }
                    });*/

                }else{
                    AlertDialog alertDialog = new AlertDialog.Builder(SignUpActivity.this).create();
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage("One or more fields are empty or have incorrect inputs");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Dismiss",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            }
        });
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }
}