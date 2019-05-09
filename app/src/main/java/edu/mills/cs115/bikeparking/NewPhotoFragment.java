package edu.mills.cs115.bikeparking;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Enables a user to add a new photo to a bike rack, or upload one if one does not exist.
 */
public class NewPhotoFragment extends Fragment {
    private static final int CAMERA_REQUEST = 1888;
    private static final int UPLOAD_REQUEST = 5555;
    private static int requestCode;
    private static Activity activity;
    private static String urlString;
    protected Uri photoURI;
    private View layout;
    private Toast failureMessage = Toast.makeText(getActivity(), getString(R.string.take_photo_error), Toast.LENGTH_LONG);


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    // https://stackoverflow.com/questions/9941637/android-how-to-save-camera-images-in-database-and-display-another-activity-in-li#
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            String imagePath = photoURI.getPath(); // todo: delete this if we don't need it
            ImageView imageView = layout.findViewById(R.id.selected_image);
            Bitmap bitmap = null;
            try {
                // much thanks to https://stackoverflow.com/questions/3879992/how-to-get-bitmap-from-an-uri
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), photoURI);
            } catch (IOException e) {
                failureMessage.show();
            }
            imageView.setImageBitmap(bitmap);
            //new uploadImageTask().execute(""); // TODO: upload the image to server
        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_CANCELED) {
            failureMessage.show();
        } else {
            // todo
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_new_photo, container, false);
        activity = getActivity();
        urlString = getString(R.string.server_url_root);
        Button cameraButton = layout.findViewById(R.id.take_photo);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        Button uploadButton = layout.findViewById(R.id.upload_photo);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } // TODO: UPLOAD PHOTO needs to be done
        });
        return layout;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        String currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                failureMessage.show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Activity activity = this.getActivity();
                photoURI = FileProvider.getUriForFile(activity,
                        "edu.mills.cs115.bikeparking.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        }
    }

    private static class uploadImageTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            // https://stackoverflow.com/questions/2938502/sending-post-data-in-android?noredirect=1&lq=1
            String data = params[0]; //data to post
            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
                writer.write(data);
                writer.flush();
                writer.close();
                out.close();

                urlConnection.connect();
                return "some information here";

            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Toast.makeText(activity, "Upload failed", Toast.LENGTH_LONG).show();
            }
        }
    }
}

