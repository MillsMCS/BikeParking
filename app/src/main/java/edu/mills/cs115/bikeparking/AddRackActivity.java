package edu.mills.cs115.bikeparking;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Enables a user to add a new bike rack to the app's database.
 */
public class AddRackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rack);
    }
}