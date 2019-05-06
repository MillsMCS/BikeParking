package edu.mills.cs115.bikeparking;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 *
 */
public class RackFragment extends Fragment {

    private static SQLiteDatabase db;
    private static Cursor cursor;

    public RackFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rack, container, false);
    }

    private static class fetchBikeRack extends AsyncTask<Object, Void, Boolean> {

        @Override
                protected void onPreExecute(){

        }

        @Override
                protected Boolean doInBackground(Object... params) {
            SQLiteOpenHelper bikeRackDatabaseHelper = (SQLiteOpenHelper) params[1];

            try{

                db = bikeRackDatabaseHelper.getReadableDatabase();
                cursor = db.query("BIKE_RACK",
                        new String[] {"NAME"},
                        null, null, null, null, null);
                /*
                SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(MapsActivity.this,
                        android.R.layout.simple_gallery_item
                );//*/
                return true;

            } catch (SQLiteException e) {
                return false;
            }
        }

    }

}
