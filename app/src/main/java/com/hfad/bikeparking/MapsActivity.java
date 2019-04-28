package com.hfad.bikeparking;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Spinner;
import android.support.v7.widget.ShareActionProvider;
import android.support.v4.view.MenuItemCompat;



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




}
