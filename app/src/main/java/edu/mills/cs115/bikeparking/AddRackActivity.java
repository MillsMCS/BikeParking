package edu.mills.cs115.bikeparking;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Enables a user to add a new bike rack to the app's database.
 */
public class AddRackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rack);
        final ImageView imageView = findViewById(R.id.selected_image);
        imageView.setTag(R.id.image_upload_status, 0);
        Button addButton = this.findViewById(R.id.confirm_add_rack);

        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String message = "";

                EditText getLatitude = findViewById(R.id.latitude);
                String latiValue = getLatitude.getText().toString().trim();

                EditText getLongitude = findViewById(R.id.longitude);
                String longiValue = getLatitude.getText().toString().trim();
                if (longiValue == "" || latiValue == "") {
                    message = "Latitude or longitude not selected.";
                    Toast.makeText(AddRackActivity.this, message, Toast.LENGTH_SHORT).show();
                }
                EditText getName = findViewById(R.id.name_bike_rack);
                String nameValue = getName.getText().toString().trim();
                if (nameValue == "") {
                    message = "Name cannot be empty";
                    Toast.makeText(AddRackActivity.this, message, Toast.LENGTH_SHORT).show();
                }
                int resultCode = (Integer) imageView.getTag(R.id.image_upload_status);
                if (resultCode == NewPhotoFragment.IMAGE_NOT_UPLOADED) {
                    message = "Photo must be uploaded";
                    Toast.makeText(AddRackActivity.this, message, Toast.LENGTH_SHORT).show();
                } else {
                    EditText getNotes = findViewById(R.id.get_notes);
                    String notesValue = getNotes.getText().toString().trim();
                    String[] param = {nameValue, latiValue, longiValue, "1", notesValue};
                    imageView.buildDrawingCache();
                    Bitmap bitmap = imageView.getDrawingCache();
                    PostDataHelper pdh = new PostDataHelper(AddRackActivity.this);
                    PostDataHelper.runAddRack(param, bitmap);
                }
            }
        });
    }
}