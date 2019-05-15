package edu.mills.cs115.bikeparking;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
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
    private static Bitmap image;
    protected Uri photoURI;
    private View layout;

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
                    showToast(R.string.take_photo_error, Toast.LENGTH_LONG);
                }
            }
            if (requestCode == SELECT_IMAGE) {
                try {
                    photoURI = data.getData();
                    image = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), photoURI);
                } catch (IOException e) {
                    showToast(R.string.error_unknown, Toast.LENGTH_LONG);
                }
            }
            imageUploaded = true;
            imageView.setImageBitmap(image);
        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_CANCELED) {
            showToast(R.string.take_photo_error, Toast.LENGTH_LONG);
        } else if (requestCode == SELECT_IMAGE && resultCode == RESULT_CANCELED) {
            showToast(R.string.select_photo_error, Toast.LENGTH_LONG);
        } else {
            showToast(R.string.error_unknown, Toast.LENGTH_LONG);
        }
    }

    private void showToast(int stringResource, int length) {
        Toast toast = Toast.makeText(activity, activity.getString(stringResource), length);
        toast.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_new_photo, container, false);
        activity = getActivity();
        image = null;
        Button confirmButton = layout.findViewById(R.id.confirm_new_photo);
        // If the add photo was called by a existing bike rack, show the button to confirm
        if (MapsActivity.currentMarker != null) {
            confirmButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (imageUploaded) {
                        PostDataHelper pdh = new PostDataHelper(activity);
                        PostDataHelper.runUploadImage(image);
                    } else {
                        showToast(R.string.image_not_uploaded_error, Toast.LENGTH_SHORT);
                    }
                }
            });
        } else {
            confirmButton.setVisibility(View.GONE);
        }
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
                    showToast(R.string.select_photo_versionerror, Toast.LENGTH_LONG);
                }
            }
        });
        ImageButton uploadButton = layout.findViewById(R.id.take_photo);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispatchTakePictureIntent();
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
        //String currentPhotoPath = image.getAbsolutePath();
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
                showToast(R.string.take_photo_error, Toast.LENGTH_LONG);
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
}