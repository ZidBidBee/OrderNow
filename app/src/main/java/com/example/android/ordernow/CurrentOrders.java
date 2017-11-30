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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.ordernow.onclasses.Order;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;

public class CurrentOrders extends AppCompatActivity {

    ListView ordersLv;

    FirebaseRemoteConfig mRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_orders);

        ordersLv = (ListView) findViewById(R.id.currentOrdersLV);

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
        setTitle(username);

        String abColor = prefs.getString("action_bar_color", String.valueOf(R.color.colorPrimary));
        String statColor = prefs.getString("status_bar_color", String.valueOf(R.color.colorPrimaryDark));

        ActionBar ab = getSupportActionBar();
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor(abColor)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(statColor));
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users/" + username + "/orders");

        ListAdapter mAdapter = new FirebaseListAdapter<Order>(this, Order.class, R.layout.current_orders_item_layout, ref) {

            @Override
            protected void populateView(View view, final Order order, int position) {

                LinearLayout parentLL = (LinearLayout) view.findViewById(R.id.mivo_ll);

                TextView orderNoTv = (TextView) view.findViewById(R.id.item_name);
                TextView amountItemsTv = (TextView) view.findViewById(R.id.item_price);

                final String orderNoText = "#" + order.OrderNumber;
                final String amountIemsText = String.valueOf(order.Items.size()) + " items";

                orderNoTv.setText(orderNoText);
                amountItemsTv.setText(amountIemsText);


                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final AlertDialog alertDialog = new AlertDialog.Builder(CurrentOrders.this).create();
                        alertDialog.setTitle(orderNoText);
                        alertDialog.setMessage(amountIemsText);
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Dismiss",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "View Order",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(CurrentOrders.this, CurrentIndivOrder.class);
                                        intent.putExtra("orderNumber", order.OrderNumber);
                                        intent.putExtra("itemsInList", order.Items);
                                        intent.putExtra("totalPrice", order.TotalPrice);
                                        startActivity(intent);
                                    }
                                });
                        alertDialog.show();

                    }
                });
            }
        };

        ordersLv.setAdapter(mAdapter);
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(CurrentOrders.this, MainActivity.class));
        finish();
        super.onBackPressed();
    }
}
