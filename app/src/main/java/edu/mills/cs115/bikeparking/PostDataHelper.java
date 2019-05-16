package edu.mills.cs115.bikeparking;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

/*
 * Sends data to the server. Uploads images, adds bike racks.
 */
class PostDataHelper {
    private static final String URL_ROOT = "https://naclo.cs.umass.edu/cgi-bin/bikeparkingserver/";
    private static final String CRLF = "\r\n"; // Line separator required by multipart/form-data.
    private static Activity activity;
    private static String boundary;

    PostDataHelper(Activity currentActivity) {
        boundary = Long.toHexString(System.currentTimeMillis());
        activity = currentActivity;
    }

    static void runUploadImage(Bitmap bitmap) {
        new uploadImageTask().execute(bitmap);
    }

    static void runAddRack(String[] strings, Bitmap bitmap) {
        new addBikeRackTask().execute(strings); // First add the other rack details
        runUploadImage(bitmap); // Then upload its image

    }

    private static void addStringParams(PrintWriter writer, String[] label, String[] data) {
        // Send normal param of strings only.
        for (int i = 0; i < label.length; i++) {
            writer.append(String.format("--%s%sContent-Disposition: form-data; name=\"%s\"%s" +
                            "Content-Type: text/plain; charset=UTF-8%s%s%s%s",
                    boundary, CRLF, label[i], CRLF, CRLF, CRLF, data[i], CRLF));
        }
        writer.flush();
    }

    private static String getResponseText(URLConnection connection) throws IOException {
        InputStream responseStream = new BufferedInputStream(connection.getInputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream));

        String line;
        StringBuilder stringBuilder = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }

        String response = stringBuilder.toString();
        Log.d("PostDataHelper", "Input:" + response);
        reader.close();

        responseStream.close();
        return response;
    }

    private static void displayResultToast(String result, int successMessage, int failureMessage) {
        if (result == null) {
            Toast.makeText(activity, activity.getString(successMessage), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(activity, activity.getString(failureMessage), Toast.LENGTH_SHORT).show();
        }
    }

    private static class addBikeRackTask extends AsyncTask<String, Void, String> {
        @Override
        // THE STRINGS HAVE TO BE IN THE CORRECT ORDER in the array
        protected String doInBackground(String... params) {

            try {
                String urlString = URL_ROOT + "test.py?"; // TODO change to add-rack.py
                URL url = new URL(urlString);
                URLConnection urlConnection = url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                OutputStream output = urlConnection.getOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8), true);

                String[] labels = {"name", "lat", "long", "added-by", "notes"};
                addStringParams(writer, labels, params);

                // End of multipart/form-data.
                writer.append("--" + boundary + "--").append(CRLF).flush();

                // Read the response
                return getResponseText(urlConnection);
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            displayResultToast(result, R.string.add_rack_error, R.string.success_add_rack);
        }
    }

    private static class uploadImageTask extends AsyncTask<Bitmap, Void, String> {

        @Override
        protected String doInBackground(Bitmap... params) {
            // https://stackoverflow.com/questions/2938502/sending-post-data-in-android?noredirect=1&lq=1
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Bitmap bitmap = params[0];
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] bitmapArray = baos.toByteArray();
            String data = Base64.encodeToString(bitmapArray, 0);
            try {
                //TODO: Redirect test.py
                String urlString = URL_ROOT + "test.py?"; //TODO change to upload_photo.py
                URL url = new URL(urlString);
                URLConnection urlConnection = url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                OutputStream output = urlConnection.getOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8), true);

                String[] labels = {"rackid", "data"};
                String[] fields = {"RACK ID NEEDED", data};
                addStringParams(writer, labels, fields);

                // End of multipart/form-data.
                writer.append(String.format("--%s--%s", boundary, CRLF)).flush();

                // Read the response
                return getResponseText(urlConnection);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            displayResultToast(result, R.string.select_photo_error, R.string.success_upload_photo);
        }
    }
}
