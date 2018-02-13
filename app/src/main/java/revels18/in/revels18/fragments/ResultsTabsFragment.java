package revels18.in.revels18.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import revels18.in.revels18.R;
import revels18.in.revels18.adapters.ResultPagerAdapter;

public class ResultsTabsFragment extends Fragment {

    public ResultsTabsFragment() {
        // Required empty public constructor
    }
    public static ResultsTabsFragment newInstance(){
        ResultsTabsFragment fragment = new ResultsTabsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.bottom_nav_results);
        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getActivity().findViewById(R.id.toolbar).setElevation(0);
                AppBarLayout appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.app_bar);
                appBarLayout.setElevation(0);
                appBarLayout.setTargetElevation(0);
            }
        }catch(NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_results_tabs, container, false);
        final TabLayout tabLayout = (TabLayout)rootView.findViewById(R.id.events_tab_layout);
        ViewPager viewPager = (ViewPager)rootView.findViewById(R.id.events_view_pager);

        ResultPagerAdapter pagerAdapter = new ResultPagerAdapter(getChildFragmentManager());
        pagerAdapter.add(new ResultsFragment(),"Event Results");
        pagerAdapter.add(new RevelsCupResultsFragment(),"Revels Cup");
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);
        return rootView;
    }
}
