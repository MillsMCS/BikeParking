package edu.mills.cs115.bikeparking;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class BikeHttpHandler {

    private static final String TAG = BikeHttpHandler.class.getSimpleName();

    public BikeHttpHandler(){

    }

    public String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while((cp = rd.read()) != -1){
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public JSONObject makeServiceCall(String reqUrl) {
        JSONObject server = null;
        try {
            InputStream is = new URL(reqUrl).openStream();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(reader);
            server = new JSONObject(jsonText);
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
