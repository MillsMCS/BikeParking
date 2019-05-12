package edu.mills.cs115.bikeparking;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    private static final int SELECT_IMAGE = 5871;
    private static Activity activity;
    private static boolean imageUploaded = false;
    private static Toast uploadFailureMessage;
    private static Bitmap image;
    protected Uri photoURI;
    private View layout;
    private Toast failureMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            ImageView imageView = layout.findViewById(R.id.selected_image);
            if (requestCode == CAMERA_REQUEST) {
                try {
                    // much thanks to https://stackoverflow.com/questions/3879992/how-to-get-bitmap-from-an-uri
                    image = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), photoURI);
                } catch (IOException e) {
                    failureMessage.show();
                }
            }
            if (requestCode == SELECT_IMAGE) {
                try {
                    photoURI = data.getData();
                    image = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), photoURI);
                } catch (IOException e) {
                    uploadFailureMessage.show();
                }
            }
            imageUploaded = true;
            imageView.setImageBitmap(image);
        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_CANCELED) {
            failureMessage.show();
        } else if (requestCode == SELECT_IMAGE && resultCode == RESULT_CANCELED) {
            uploadFailureMessage.show();
        } else {
            Toast.makeText(activity, this.getString(R.string.error_unknown), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_new_photo, container, false);
        activity = getActivity();
        image = null;
        failureMessage = Toast.makeText(activity, getString(R.string.take_photo_error),
                Toast.LENGTH_LONG);
        uploadFailureMessage = Toast.makeText(activity, getString(R.string.select_photo_error),
                Toast.LENGTH_LONG);
        ImageButton cameraButton = layout.findViewById(R.id.upload_photo);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    getIntent.setType("image/*");

                    Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pickIntent.setType("image/*");

                    Intent chooserIntent = Intent.createChooser(getIntent, getString(R.string.insert_photo));
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

                    startActivityForResult(chooserIntent, SELECT_IMAGE);
                } else {
                    Toast.makeText(activity, activity.getString(R.string.select_photo_versionerror),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        ImageButton uploadButton = layout.findViewById(R.id.take_photo);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        Button confirmButton = layout.findViewById(R.id.confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (imageUploaded) {
                    new uploadImageTask().execute(image);
                } else {
                    Toast.makeText(activity, activity.getString(R.string.image_not_uploaded_error),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        return layout;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                failureMessage.show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(activity,
                        "edu.mills.cs115.bikeparking.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        }
    }

    static class uploadImageTask extends AsyncTask<Bitmap, Void, String> {

        @Override
        protected String doInBackground(Bitmap... params) {
            // https://stackoverflow.com/questions/2938502/sending-post-data-in-android?noredirect=1&lq=1
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Bitmap bitmap = params[0];
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] bitmapArray = baos.toByteArray();
            String data = Base64.encodeToString(bitmapArray, 0);
            try {
                URL url = new URL("https://naclo.cs.umass.edu/cgi-bin/bikeparkingserver/test.py");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setChunkedStreamingMode(0);
                // Prepare the data to go out
                OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                BufferedWriter w = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
                //Log.d("NewPhotoFragment", data);
                w.write(data);
                w.flush();
                w.close();
                out.close();
                baos.close();

                InputStream responseStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));

                String line;
                StringBuilder stringBuilder = new StringBuilder();

                while ((line = responseStreamReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                responseStreamReader.close();

                String response = stringBuilder.toString();
                Log.d("NewPhotoFragment", "Input:" + response);
                responseStream.close();
                urlConnection.disconnect();
                return response;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                uploadFailureMessage.show();
            } else {
                // TODO: Return to calling activity
            }
        }
    }
}


