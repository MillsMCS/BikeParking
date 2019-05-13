package edu.mills.cs115.bikeparking;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Enables the user to view the details of
 * a bike rack.
 */
public class RackFragment extends Fragment {

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
        return layout;

    }

}
