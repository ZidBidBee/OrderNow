package com.example.android.ordernow;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    Button takeOrder;
    Button currentOrders;
    Button menu;
    Button signOut;
    FirebaseRemoteConfig mRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        }catch (NullPointerException e){
            ActionBar ab = getSupportActionBar();
            ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor(String.valueOf(R.color.colorPrimary))));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.parseColor(String.valueOf(R.color.colorPrimaryDark)));
            }
        }

        Button takeOrder = (Button) findViewById(R.id.takeOrderBTN);
        Button currentOrders = (Button) findViewById(R.id.currentOrdersBTN);
        Button menu = (Button) findViewById(R.id.menuBTN);
        Button signOut = (Button) findViewById(R.id.signOutBTN);


        takeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takeOrderIntent = new Intent(MainActivity.this, TakeOrder.class);
                startActivity(takeOrderIntent);
            }
        });

        currentOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent currentOrdersIntent = new Intent(MainActivity.this, CurrentOrders.class);
                startActivity(currentOrdersIntent);
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menuIntent = new Intent(MainActivity.this, Menu.class);
                startActivity(menuIntent);
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences preferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                editor.putString("LOGGED", "OUT");

                editor.apply();

                Intent menuIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(menuIntent);
            }
        });
    }

    private void setActionColor() {
        ActionBar ab = getSupportActionBar();
        String abColor = mRemoteConfig.getString("action_bar_color");
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor(abColor)));
    }


    private void setStatusColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            String statusColor = mRemoteConfig.getString("status_bar_color");
            window.setStatusBarColor(Color.parseColor(statusColor));
        }

    }
}




