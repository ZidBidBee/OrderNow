package com.example.android.ordernow;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.example.android.ordernow.onclasses.ONItem;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;

public class EditMenu extends AppCompatActivity {

    private long id;
    FirebaseDatabase db;
    EditText itemNameET;
    EditText itemPriceET;
    FirebaseRemoteConfig mRemoteConfig;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_menu);

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

        db = FirebaseDatabase.getInstance();

        itemNameET = (EditText) findViewById(R.id.item_id);
        itemPriceET = (EditText) findViewById(R.id.price_id);

    }

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        //respond to menu item selection
        switch (item.getItemId()) {
            case R.id.save:
                SaveInfo();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void SaveInfo(){

        String itemName = itemNameET.getText().toString();
        int itemPrice = Integer.parseInt( itemPriceET.getText().toString());

        DatabaseReference newItemRef = db.getReference("items/" + itemName);

        ONItem newItem = new ONItem(itemName,itemPrice);

        newItemRef.setValue(newItem);

        /*id = 0;

        OrderDbHelper dbHelper = new OrderDbHelper(this.getBaseContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        EditText itemEdit = (EditText) findViewById(R.id.item_id);
        EditText priceEdit = (EditText) findViewById(R.id.price_id);

        String actitem = String.valueOf(itemEdit.getText());
        int price = Integer.parseInt(String.valueOf(priceEdit.getText()));

        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_NAME_ITEM, actitem);
        values.put(FeedEntry.COLUMN_NAME_PRICE, price);

        id = db.insert(
                FeedEntry.TABLE_NAME,
                null,
                values
        );

        Toast.makeText(this, id + "rows inserted successfully", Toast.LENGTH_SHORT).show();*/
    }
}
