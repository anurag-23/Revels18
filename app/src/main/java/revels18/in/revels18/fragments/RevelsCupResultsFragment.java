package revels18.in.revels18.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import revels18.in.revels18.R;

public class RevelsCupResultsFragment extends Fragment {
    public RevelsCupResultsFragment() {
        // Required empty public constructor
    }

    public static RevelsCupResultsFragment newInstance() {
        RevelsCupResultsFragment fragment = new RevelsCupResultsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_revels_cup_results, container, false);
    }
}
