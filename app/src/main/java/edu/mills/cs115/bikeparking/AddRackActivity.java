package edu.mills.cs115.bikeparking;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Enables a user to add a new bike rack to the app's database.
 */
public class AddRackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rack);
        Button addButton = this.findViewById(R.id.confirm_add_rack);

        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int filled = 0;
                String message;
                switch (filled) {
                    case 1:
                        message = "Latitude or longitude not selected.";
                    case 2:
                        message = "Name cannot be empty";
                    case 3:
                        message = "Photo must be uploaded";
                    default:
                        PostDataHelper pdh = new PostDataHelper(AddRackActivity.this);
                        //pdh.runAddRack("params", image); // TODO: Find params
                }
                //Toast.makeText(AddRackActivity.this,
//                        getString(R.string.image_not_uploaded_error),
//                        Toast.LENGTH_SHORT).show(); TODO: What to do about this next
            }
        });
    }
}