package edu.mills.cs115.bikeparking;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class BikeHttpHandler extends Activity {

    public LatLng finalCoords;
    private URL query;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        GetCoordinatesTask goCoords = new GetCoordinatesTask(query, name);
    }

    public BikeHttpHandler(URL url, String queryName){
        query = url;
        name = queryName;
    }

private class GetCoordinatesTask extends AsyncTask<URL, String, Boolean> {

        private URL query;
        private String name;

    public GetCoordinatesTask(URL url, String name) {
        this.query = url;
        this.name = name;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Boolean doInBackground(URL... params) {
        Log.d("GetCoordinatesTask", "Starting doInBackground");
        URL url = this.query;
        try {
            //URL url = this.query;
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            //if(HttpURLConnection.HTTP_OK == conn.getResponseCode()){
                Log.d("testConnect", "Connection successful!");
            //}
            conn.disconnect();
        } catch(IOException e){
            Log.d("testConnect", "Connection failed");
        }

        LatLng coords = new LatLng(0,0);
        try {
            url = new URL(this.query.toString() + "?&name=" + this.name);
        } catch(MalformedURLException e){
            Log.d("BikeHttpHandler", e.getMessage());
        }
        try{
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            Log.d("BikeHttpHandler", conn.getURL().toString());
            conn.connect();
            Log.d("BikeHttpHandler", "Connection successful!");
            JSONArray bikeRack = new JSONArray();
            JSONObject server = (JSONObject) conn.getContent();
            Iterator<String> keys = server.keys();
            //while(keys.hasNext()){
            Log.d("BikeHttpHandler", keys.toString());
            //}

            int c = 0;

            if(bikeRack.length() == c){
                Log.d("BikeHttpHandler",
                        "bikeRack was not initialized correctly");
            } else {
                //}
                ///*
                while (c < bikeRack.length()) {

                    JSONObject arr = bikeRack.getJSONObject(c);
                    if (arr.getString("name") == name) {
                        Double lat = arr.getDouble("latitude");
                        Double lng = arr.getDouble("longitude");
                        Log.d("BikeHttpHandler", "Lat "
                                + lat
                                + " and Lng "
                                + lng);
                        coords = new LatLng(lat, lng);
                    }
                    c++;
                }
            }
            //is.close();
        } catch(Exception e){
            Log.d("BikeHttpHandler", "Connection failed");
            Log.d("BikeHttpHandler", e.getMessage());
        }
        return null;
    }
}

}
