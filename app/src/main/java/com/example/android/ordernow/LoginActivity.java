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
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;

    FirebaseRemoteConfig mRemoteConfig;

    EditText mEmailET;
    EditText mPasswordET;
    Button mLoginButton;

    TextView mNoAccountTv;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

        mEmailET = (EditText) findViewById(R.id.login_email_et);
        mPasswordET = (EditText) findViewById(R.id.login_password_et);
        mLoginButton = (Button) findViewById(R.id.login_login_button);
        mNoAccountTv = (TextView) findViewById(R.id.login_no_account_tv);

        Intent getEmail = getIntent();
        String userEmail = getEmail.getStringExtra("userEmail");
        mEmailET.setText(userEmail);

        //SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        //SharedPreferences.Editor editor = sharedPref.edit();
        //editor.putInt("2", 2);
        //editor.commit();
        //String sharedTest = String.valueOf(sharedPref.getInt("2", 3));
        //mEmailET.setText(sharedTest);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String thisEmail = mEmailET.getText().toString();
                final String thisPassword = mPasswordET.getText().toString();

                final String mail = thisEmail.replaceAll("[.]", "%");

                DatabaseReference ref = database.getReference("Users/" + mail);

                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User attemptedLoginUser;
                        String attempteduserActualPassword;
                        try {
                            attemptedLoginUser = dataSnapshot.getValue(User.class);
                            attempteduserActualPassword = attemptedLoginUser.password;
                            if(thisPassword.equals(attempteduserActualPassword)){
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("LOGGED", "IN");
                                editor.putString("USER_EMAIL", attemptedLoginUser.email);
                                editor.putString("USER_NAME", attemptedLoginUser.name);
                                editor.putString("USER_SURNAME", attemptedLoginUser.surname);
                                editor.putLong("USER_PHONE", attemptedLoginUser.phoneNo);
                                editor.apply();
                            }else{
                                AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                                alertDialog.setTitle("Error");
                                alertDialog.setMessage("Incorrect Password");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Dismiss",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.show();
                            }
                        }catch (NullPointerException e){
                            Log.w("login", e);
                            AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                            alertDialog.setTitle("Error");
                            alertDialog.setMessage("No account exists with that email");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Dismiss",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        mNoAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setActionColor(){
        ActionBar ab = getSupportActionBar();
        String abColor = mRemoteConfig.getString("action_bar_color");
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor(abColor)));
    }


    private void setStatusColor(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            String statusColor = mRemoteConfig.getString("status_bar_color");
            window.setStatusBarColor(Color.parseColor(statusColor));
        }
    }
}
