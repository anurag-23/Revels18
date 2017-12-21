package revels18.in.revels18.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import revels18.in.revels18.R;


public class RevelsCupFragment extends Fragment{
    public RevelsCupFragment() {
        // Required empty public constructor
    }


    public static RevelsCupFragment newInstance() {
        RevelsCupFragment fragment = new RevelsCupFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.bottom_nav_revels_cup);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_revels_cup, container, false);
    }




}
