package edu.mills.cs115.bikeparking;

import android.app.Activity;
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
import android.widget.Toast;

/**
 * Enables the user to view the details of
 * a bike rack.
 */
public class RackFragment extends Fragment {

    private static SQLiteDatabase db;
    private static Cursor cursor;
    private static Activity activity;

    /**
     * Creates a new instance of RackFragment.
     */
    public RackFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_rack, container, false);
        activity = getActivity();
        return layout;
    }

    private static class fetchBikeRack extends AsyncTask<Object, Void, Boolean> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(Object... params) {
            SQLiteOpenHelper bikeRackDatabaseHelper = (SQLiteOpenHelper) params[1];

            try {

                db = bikeRackDatabaseHelper.getReadableDatabase();
                cursor = db.query("BIKE_RACK",
                        new String[]{"NAME"},
                        null, null, null, null, null);

                return true;

            } catch (SQLiteException e) {
                Toast toast = Toast.makeText(activity,
                        "Database unavailable",
                        Toast.LENGTH_SHORT);
                toast.show();
                return false;
            }
        }

    }

}
