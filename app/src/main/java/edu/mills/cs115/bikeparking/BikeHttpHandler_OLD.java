package edu.mills.cs115.bikeparking;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

public class BikeHttpHandler_OLD {

    private static final String TAG = BikeHttpHandler_OLD.class.getSimpleName();
        private static final String FIREFOX =
                "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.0.10) Gecko/2009042316 Firefox/3.0.10 (.NET CLR 3.5.30729)";
        private static final String KONQUEROR =
                "Konqueror/3.0-rc4; (Konqueror/3.0-rc4; i686 Linux;;datecode)";
        private static final String OPERA = "Opera/9.25 (Windows NT 6.0; U; en)";
        private static final String INTERNET_EXPLORER =
                "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)";
        private static final String SAFARI =
                "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US) AppleWebKit/525.28 (KHTML, like Gecko) Version/3.2.2 Safari/525.28.1";
        private static final String USER_AGENT =
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.131 Safari/537.36";

    public BikeHttpHandler_OLD(){

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

    private String getUserAgent(HttpURLConnection conn){
        String oldUserAgent = conn.getRequestProperty("User-Agent");
        String userAgent = "";
        boolean works = false;
        int test = 0;
        while(!works){
            while(test < 5) {
                switch (test) {
                    case 0:
                        userAgent = FIREFOX;
                        break;
                    case 1:
                        userAgent = KONQUEROR;
                        break;
                    case 2:
                        userAgent = OPERA;
                        break;
                    case 3:
                        userAgent = INTERNET_EXPLORER;
                        break;
                    case 4:
                        userAgent = SAFARI;
                        break;
                    default:
                        userAgent = oldUserAgent;
                        Log.d("BikeHttpHandler", "Unsuccessful user agent choices");
                        break;
                }
                test++;

                try{
                    conn.setRequestProperty("User-Agent", userAgent);
                    conn.connect();
                    Log.d("BikeHttpHandler", "Connection successful! Agent: "
                            + userAgent);
                    works = true;
                } catch (IOException e){
                    Log.d("BikeHttpHandler", "Incorrect user agent");
                }
            }
        }
        return userAgent;
    }

    public LatLng startConnection(String reqUrl, String name){
        LatLng coords = new LatLng(0,0);
        try {
            Log.d("BikeHttpHandler", "Creating URL");
            URL url = new URL(reqUrl + "?&" + name);
            Log.d("BikeHttpHandler", "URL Created");
            Log.d("BikeHttpHandler", "Connecting...");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            Log.d("BikeHttpHandler", "Setting connection agent");
            conn.setRequestProperty("User-Agent", USER_AGENT);
            Log.d("BikeHttpHandler", "Connection agent set to: \n"
                    + conn.getRequestProperty("User-Agent"));
            conn.setRequestProperty("Content-length", "0");
            conn.setUseCaches(false);
            conn.setAllowUserInteraction(false);
            conn.setConnectTimeout(50000);
            conn.setReadTimeout(50000);
            Log.d("BikeHttpHandler", "Connection opened...");
            conn.connect();
            Log.d("BikeHttpHandler", "Connected!");
            //coords = makeServerArray(conn, name);
            conn.disconnect();
        } catch(IOException e){
            e.printStackTrace();
        }
        return coords;
    }

    public void testConnect(){
        try {
            URL url = new URL("http://stackoverflow.com/about");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            if(HttpURLConnection.HTTP_OK == conn.getResponseCode()){
                Log.d("testConnect", "Connection successful!");
            }
            conn.disconnect();
        } catch(IOException e){
            Log.d("testConnect", "Connection failed");
        }
    }

    public LatLng makeServerArray(URL url, String name) {
        LatLng coords = new LatLng(0,0);
        try {
            url = new URL(url.toString() + "?&name=" + name);
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
