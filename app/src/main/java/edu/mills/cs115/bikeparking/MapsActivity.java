package edu.mills.cs115.bikeparking;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * The top-level activity for Bike Parking. The accompanying view
 * enables users to display {@link MapsActivity} and {@link RackFragment}.
 */
public class MapsActivity extends AppCompatActivity implements
        OnMarkerClickListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private ShareActionProvider shareActionProvider;
    private Marker currentMarker;
    private LatLng currentCoords;
    private Boolean clicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if(savedInstanceState != null){
            clicked = savedInstanceState.getBoolean("clicked");
            currentCoords = new LatLng(savedInstanceState.getDouble("lat"), savedInstanceState.getDouble("lng"));
        }
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

        if (servicesOK()) {
            try {
                String url = "https://naclo.cs.umass.edu/cgi-bin/bikeparkingserver/get-rack.py";
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
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MillsCollege, zoomLevel));
                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
                        View v = getLayoutInflater().inflate(R.layout.fragment_rack, null);
                        // Other information to set the data goes here
                        return v;
                    }
                });

            } catch (Exception e) {
                Toast toast = Toast.makeText(this,
                        "Google Play Services is not installed for this device",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        }

        if(currentCoords != null) {
            currentMarker = mMap.addMarker(new MarkerOptions().position(currentCoords));
            currentMarker.setVisible(true);
            currentMarker.showInfoWindow();
        }
    }

    /**
     * Called when the user clicks a marker.
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.
        Integer clickCount = (Integer) marker.getTag();

        // Check if a click count was set, then display the click count.
        if (clickCount != null) {
            marker.showInfoWindow();
            currentMarker = marker;
            clicked = true;
        }
        return false;
    }

    public boolean servicesOK() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int result = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (result == ConnectionResult.SUCCESS) {
            return true;
        } else {
            Toast.makeText(this, "Cannot connect to Google Play services", Toast.LENGTH_LONG).show();
        }
        return false;
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
                Log.d("MapsActivity", "No record was found");
            }
            cursor.close();
        } catch (SQLiteException e) {
            /*Toast toast = Toast.makeText(this,
                    "Database unavailable",
                    Toast.LENGTH_SHORT);
            toast.show();*/
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Integer saveMarker = null;
        LatLng coords = null;
        if(currentMarker != null) {
            saveMarker = (Integer) currentMarker.getTag();
            coords = currentMarker.getPosition();
        }
        if(saveMarker != null) {
            savedInstanceState.putInt("markerTag", saveMarker);
        }
        if(coords != null){
            savedInstanceState.putDouble("lat", coords.latitude);
            savedInstanceState.putDouble("lng", coords.longitude);
        }
        savedInstanceState.putBoolean("clicked", clicked);
    }

    /*@Override
    protected void onPause(){

    }*/

    @Override
    protected void onResume(){
        super.onResume();
        Log.d("onResume", "Activity is being resumed");
    }
}