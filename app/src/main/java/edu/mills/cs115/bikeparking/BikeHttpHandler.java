package edu.mills.cs115.bikeparking;

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
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class BikeHttpHandler {

    private static final String TAG = BikeHttpHandler.class.getSimpleName();

    public BikeHttpHandler(){

    }

    public String readAll(BufferedReader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        /*while((cp = rd.read()) != -1){
            sb.append((char) cp);
        }*/
        while(rd.readLine() != null){
            Log.d("BikeHttpHandler:", "Reading server");
            sb.append(rd.readLine());
        }
        return sb.toString();
    }

    public LatLng makeServerArray(String reqUrl, String name) {
        Log.d("BikeHttpHandler", "Log test");
        JSONArray bikeRack = new JSONArray();
        LatLng coords = null;
        try{
            URL url = new URL(reqUrl + "&" + name);
            URLConnection conn = url.openConnection();
            Log.d("BikeHttpHandler", "Connection created");
            /*HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn*/
            try{
                conn.connect();
                Log.d("BikeHttpHandler", "Connection established");
            } catch(Exception e){
                Log.d("BikeHttpHandler", "Error: "
                        + e.getStackTrace().toString());
            }
            JSONObject server = (JSONObject) conn.getContent();
            //InputStream is = conn.getInputStream();
            //JSONObject server = new JSONObject(reqUrl);
            //JSONArray nameArray = server.names();
            Iterator<String> keys = server.keys();
            //while(keys.hasNext()){
                Log.d("BikeHttpHandler", keys.toString());
            //}

            int c = 0;

            if(bikeRack.length() == c){
                Log.d("BikeHttpHandler",
                        "bikeRack was not initialized correctly");
            } else {
                while (c < bikeRack.length()) {

                }
            }
            /*while(c < bikeRack.length()){
                JSONObject arr = bikeRack.getJSONObject(c);
                if(arr.getString("name") == name){
                    Double lat = arr.getDouble("latitude");
                    Double lng = arr.getDouble("longitude");
                    Log.d("BikeHttpHandler", "Lat "
                    + lat
                    + " and Lng "
                    + lng);
                    coords = new LatLng(lat, lng);
                }
                c++;
            }*/
            //is.close();
        } catch(Exception e){
        }
        return coords;
    }

    public JSONObject makeServiceCall(String reqUrl) {
        JSONObject server = new JSONObject();
        try {
            InputStream is = new URL(reqUrl).openStream();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is));
                    //, Charset.forName("UTF-8")));
            String jsonText = readAll(reader);
            server = new JSONObject(jsonText);
            Log.d("Server contents: ", server.toString());
            is.close();
        } catch(Exception e){

        }
        return server;
    }
        /*String response = null;
        try{
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);
        } catch(Exception e){

        }
        return response;
    }

    private String convertStreamToString(InputStream in){
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();

        String line;
        try{
            while((line = reader.readLine()) != null){
                sb.append(line).append('\n');
            }
        } catch(Exception e){

        }
        return sb.toString();
    }*/

}
