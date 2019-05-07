package edu.mills.cs115.bikeparking;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * The top-level activity for Bike Parking. The accompanying view
 * enables users to display {@link MapsActivity2} and {@link RackFragment}.
 */
public class MapsActivity2 extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ShareActionProvider shareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //getBikeRack();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        setShareActionIntent("Here is the closest bike rack to you:");
        return super.onCreateOptionsMenu(menu);
    }

    private void setShareActionIntent(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        shareActionProvider.setShareIntent(intent);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng Court_Stevenson = new LatLng(37.781292, -122.186266);
        LatLng Court_Stevenson2 = new LatLng(37.7814257, -122.1863674);
        LatLng Underwood_BuildingA = new LatLng(37.7810884, -122.185789);
        LatLng WarrenOlney = new LatLng(37.782181, -122.182206);
        LatLng MillsCollege = new LatLng(37.781004, -122.182827);

        BitmapDescriptor bdf = BitmapDescriptorFactory.fromResource(R.drawable.bikecon);
        mMap.addMarker(new MarkerOptions()
                .position(Court_Stevenson)
                .icon(bdf)
        );
        mMap.addMarker(new MarkerOptions()
                .position(Court_Stevenson2)
                .icon(bdf)
        );
        mMap.addMarker(new MarkerOptions()
                .position(Underwood_BuildingA)
                .icon(bdf)
        );
        mMap.addMarker(new MarkerOptions()
                .position(WarrenOlney)
                .icon(bdf)
        );

        float zoomLevel = 15.7f; //This goes up to 21
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MillsCollege, zoomLevel));
    }

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
                Log.d("MapsActivity2", "No record was found");
            }
        } catch (SQLiteException e) {
            Toast toast = Toast.makeText(this,
                    "Database unavailable",
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}