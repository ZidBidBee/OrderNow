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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.ordernow.onclasses.ONItem;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;

public class CurrentIndivOrder extends AppCompatActivity {

    FirebaseRemoteConfig mRemoteConfig;
    ListView ItemsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_indiv_order);

        ItemsListView = (ListView) findViewById(R.id.avio_lv);

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
        String username = prefs.getString("USER_EMAIL", "Error1");

        String abColor = prefs.getString("action_bar_color", String.valueOf(R.color.colorPrimary));
        String statColor = prefs.getString("status_bar_color", String.valueOf(R.color.colorPrimaryDark));

        ActionBar ab = getSupportActionBar();
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor(abColor)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(statColor));
        }

        Intent getInfoIntent = getIntent();

        setTitle("#" + String.valueOf(getInfoIntent.getIntExtra("orderNumber", 0)));

        String orderNoStr = String.valueOf(getInfoIntent.getIntExtra("orderNumber", 0));

        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Users/" + username + "/orders/" + orderNoStr + "/Items");

        ListAdapter mAdapter = new FirebaseListAdapter<ONItem>(this, ONItem.class, R.layout.menu_item_view_only_layout, orderRef) {

            @Override
            protected void populateView(View view, ONItem item, int position) {

                TextView itemNameTv = (TextView) view.findViewById(R.id.item_name);
                TextView itemPriceTv = (TextView) view.findViewById(R.id.item_price);

                itemNameTv.setText(item.itemName);
                itemPriceTv.setText("R" + String.valueOf(item.itemPrice));

            }
        };

        ItemsListView.setAdapter(mAdapter);
    }
}
