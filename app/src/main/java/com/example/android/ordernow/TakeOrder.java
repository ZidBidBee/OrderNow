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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.ordernow.onclasses.ONItem;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.HashMap;

public class TakeOrder extends AppCompatActivity {

    ListView takeOrderLV;
    MenuItem menuPrice;
    Menu m;

    static double gT;

    FirebaseRemoteConfig mRemoteConfig;
    SharedPreferences sharedPreferences;

    static ArrayList<ONItem> orderedItems = new ArrayList<ONItem>();

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.take_order_menu, menu);
        m = menu;
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_order);

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

        takeOrderLV = (ListView) findViewById(R.id.take_order_list_view);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("items");

        ListAdapter mAdapter = new FirebaseListAdapter<ONItem>(this, ONItem.class, R.layout.take_order_layout, ref) {


            @Override
            protected void populateView(View view, final ONItem onItem, int position) {

                Button addButton = (Button) view.findViewById(R.id.add_item_button);
                Button minusButton = (Button) view.findViewById(R.id.remove_item_button);

                TextView takeOrderNameTv = (TextView) view.findViewById(R.id.take_order_item_name);
                TextView takeOrderPriceTv = (TextView) view.findViewById(R.id.take_order_item_price);

                final TextView indivitemTotalPrice = (TextView) view.findViewById(R.id.semi_total_text);
                final TextView indivItemAmount = (TextView) view.findViewById(R.id.item_amount_text);

                indivItemAmount.setText("0");

                takeOrderNameTv.setText(onItem.itemName);
                takeOrderPriceTv.setText(String.valueOf(onItem.itemPrice));

                indivItemAmount.setText("0");

                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int oldAmnt = Integer.parseInt(indivItemAmount.getText().toString());
                        int newAmnt = oldAmnt + 1;

                        indivitemTotalPrice.setText("R" + String.valueOf(newAmnt * onItem.itemPrice + "0"));


                        gT += onItem.itemPrice;

                        indivItemAmount.setText(String.valueOf(newAmnt));

                         menuPrice = m.findItem(R.id.to_menu_price);
                         menuPrice.setTitle("R" + String.valueOf(gT) + "0");

                         orderedItems.add(onItem);

                    }
                });

                minusButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int oldAmnt = Integer.parseInt(indivItemAmount.getText().toString());
                        int newAmnt = oldAmnt - 1;

                        indivitemTotalPrice.setText("R" + String.valueOf(newAmnt * onItem.itemPrice + "0"));

                        gT -= onItem.itemPrice;

                        indivItemAmount.setText(String.valueOf(newAmnt));

                        MenuItem menuPrice = m.findItem(R.id.to_menu_price);
                        menuPrice.setTitle("R" + String.valueOf(gT) + "0");

                        orderedItems.remove(onItem);

                    }
                });
            }
        };

        takeOrderLV.setAdapter(mAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        super.onOptionsItemSelected(menuItem);

        switch (menuItem.getItemId()){

            case R.id.send_to_chef:
                if (orderedItems.size() == 0){

                    final AlertDialog alertDialog = new AlertDialog.Builder(TakeOrder.this).create();
                    alertDialog.setTitle("Oops!");
                    alertDialog.setMessage("It appears you do not have any dishes selected for table. Please add dishes to proceed.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Add Items",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }else{
                    Intent intent = new Intent(TakeOrder.this, ConfirmOrderActivity.class);
                }
        }
    }

}
