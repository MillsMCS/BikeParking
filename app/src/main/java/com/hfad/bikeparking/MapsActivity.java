package com.hfad.bikeparking;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Spinner;
import android.support.v7.widget.ShareActionProvider;
import android.support.v4.view.MenuItemCompat;
import android.widget.TextView;
import android.widget.Toast;


public class MapsActivity extends AppCompatActivity {

    private ShareActionProvider shareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
//        setShareActionIntent("Here is the closest bike rake to you.");
//        return super.onCreateOptionsMenu(menu);
//    }

    private void setShareActionProvider(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        shareActionProvider.setShareIntent(intent);
    }

    ///*
    private void getBikeRack() {
        SQLiteOpenHelper bikeRackDatabaseHelper = new BikeParkingDatabaseHelper(this);
        try {
            SQLiteDatabase db = bikeRackDatabaseHelper.getReadableDatabase();
            Cursor cursor = db.query("BIKE_RACK",
                    new String[]{"NAME", "NOTES",
                            "IMAGE_ID"},
                    null, null, null, null, null);
            if (cursor.moveToFirst()) {
                String nameText = cursor.getString(0);
                Boolean notes = false;
                if (cursor.getInt(1) == 1) {
                    notes = true;
                }
                int photoId = cursor.getInt(2);

                ///*
                TextView name = findViewById(R.id.name);
                name.setText(nameText);

                ImageView photo = findViewById(R.id.photo);
                photo.setImageResource(photoId);
                photo.setContentDescription(nameText);
                ///

            } else {
                Log.d("MapsActivity", "No record was found");
            }
        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(this,
                    "Database unavailable",
                    Toast.LENGTH_SHORT);
            toast.show();
        }//*/
    }
}
