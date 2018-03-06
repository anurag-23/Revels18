package mitrev.in.mitrev18.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import mitrev.in.mitrev18.R;
import mitrev.in.mitrev18.activities.FavouritesActivity;
import mitrev.in.mitrev18.activities.LoginActivity;
import mitrev.in.mitrev18.activities.MainActivity;
import mitrev.in.mitrev18.activities.ProfileActivity;
import mitrev.in.mitrev18.adapters.RevelsCupAdapter;
import mitrev.in.mitrev18.models.events.RevelsCupEventModel;
import mitrev.in.mitrev18.models.events.RevelsCupEventsListModel;
import mitrev.in.mitrev18.network.APIClient;
import mitrev.in.mitrev18.utilities.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RevelsCupFragment extends Fragment{
    RecyclerView revelsCupRV;
    LinearLayout noData;
    LinearLayout noConnection;
    boolean noDataB= false;
    boolean revelsCupRVB = false;
    boolean noConnectionB=false;
    private SwipeRefreshLayout swipeRefreshLayout;
    RevelsCupAdapter adapter;
    View view;
    Context context;
    AppBarLayout appBarLayout;
    List<RevelsCupEventModel> eventScheduleList = new ArrayList<>();
    private Realm realm;
    private String TAG = "RevelsCupFragment";
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
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.bottom_nav_revels_cup);
        realm = Realm.getDefaultInstance();
        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getActivity().findViewById(R.id.toolbar).setElevation((4 * getResources().getDisplayMetrics().density + 0.5f));
                getActivity().findViewById(R.id.app_bar).setElevation((4 * getResources().getDisplayMetrics().density + 0.5f));
                appBarLayout=(AppBarLayout)getActivity().findViewById(R.id.app_bar);
                appBarLayout.setExpanded(true,true);
            }
        }catch(NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: RevelsCupFrag");
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_revels_cup, container, false);
        revelsCupRV = (RecyclerView)view.findViewById(R.id.rc_recycler_view);
        noData=(LinearLayout)view.findViewById(R.id.no_revels_cup_data_layout);
        noConnection=(LinearLayout)view.findViewById(R.id.revels_cup_no_connection);
        swipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.revelscup_results_swipe_refresh_layout);
        adapter = new RevelsCupAdapter(eventScheduleList);
        context=getContext();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        revelsCupRV.setLayoutManager(layoutManager);
        revelsCupRV.setItemAnimator(new DefaultItemAnimator());
        revelsCupRV.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reload();
            }
        });
        swipeRefreshLayout.setRefreshing(true);
        loadRevelsCupEventsFromInternet();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: noDataB "+noDataB+" noConectionB "+noConnectionB+" revelscuprv "+revelsCupRVB);
        if(noDataB)
            noData.setVisibility(View.VISIBLE);
        else if(noConnectionB)
            noConnection.setVisibility(View.VISIBLE);
        else if(revelsCupRVB)
            revelsCupRV.setVisibility(View.VISIBLE);
        else{
        }
    }

    private void loadRevelsCupEventsFromInternet(){
        Call<RevelsCupEventsListModel> revelsCupCall = APIClient.getAPIInterface().getRevelsCupEventsList();
        revelsCupCall.enqueue(new Callback<RevelsCupEventsListModel>() {
            @Override
            public void onResponse(Call<RevelsCupEventsListModel> call, Response<RevelsCupEventsListModel> response) {
                if (response.isSuccess() && response.body() != null) {
                    swipeRefreshLayout.setRefreshing(false);
                    eventScheduleList.clear();
                    eventScheduleList.addAll(response.body().getEvents());
                    sortFixtures(eventScheduleList);
                    Log.d(TAG,"RevelsCup Events updated in background");
                    if(eventScheduleList.isEmpty()){
                        noConnection.setVisibility(View.GONE);
                        revelsCupRV.setVisibility(View.GONE);
                        noData.setVisibility(View.VISIBLE);
                    }
                    else{
                        noConnection.setVisibility(View.GONE);
                        noData.setVisibility(View.GONE);
                        revelsCupRV.setVisibility(View.VISIBLE);
                        Log.d(TAG, "onResponse: RevelsCup set visible");
                    }
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<RevelsCupEventsListModel> call, Throwable t) {
                Log.d(TAG, "onFailure: RevelsCup Events not updated");
                swipeRefreshLayout.setRefreshing(false);
                if(! NetworkUtils.isInternetConnected(context)){
                    noData.setVisibility(View.GONE);
                    revelsCupRV.setVisibility(View.GONE);
                    noConnection.setVisibility(View.VISIBLE);
                    Button retry=(Button)view.findViewById(R.id.retry);
                    retry.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            swipeRefreshLayout.setRefreshing(true);
                            reload();
                        }
                    });
                }
                else {
                    noConnection.setVisibility(View.GONE);
                    revelsCupRV.setVisibility(View.GONE);
                    noData.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    void sortFixtures (List<RevelsCupEventModel> eventScheduleList){
        try{
            Collections.sort(eventScheduleList, new Comparator<RevelsCupEventModel>() {
                @Override
                public int compare(RevelsCupEventModel t1, RevelsCupEventModel t2) {
                    int dateComp = t1.getDate().compareTo(t2.getDate());
                    if (dateComp < 0)
                        return -1;
                    else if (dateComp > 0)
                        return 1;
                    else{
                        SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm aa", Locale.US);
                        try {
                            Date d1 = sdf1.parse(t1.getTime());
                            Date d2 = sdf1.parse(t2.getTime());
                            long timeComp = d1.getTime() - d2.getTime();
                            if (timeComp < 0)
                                return -1;
                            else if (timeComp > 0)
                                return 1;
                            else {
                                int nameComp = t1.getSportName().compareTo(t2.getSportName());
                                if (nameComp < 0)
                                    return -1;
                                else if (nameComp > 0)
                                    return 1;
                                else
                                    return 0;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                            return -1;
                        }
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void reload(){
        if(! NetworkUtils.isInternetConnected(context)){
            Snackbar.make(view, "Check connection!", Snackbar.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
            noData.setVisibility(View.GONE);
            revelsCupRV.setVisibility(View.GONE);
            noConnection.setVisibility(View.VISIBLE);
            Button retry=(Button)view.findViewById(R.id.retry);
            retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    swipeRefreshLayout.setRefreshing(true);
                    reload();
                }
            });
        }
        else {
            loadRevelsCupEventsFromInternet();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_results, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_refresh:{
                swipeRefreshLayout.setRefreshing(true);
                reload();
                return true;
            }

            case R.id.menu_profile:{
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                if (sp.getBoolean("loggedIn", false)) startActivity(new Intent(getActivity(), ProfileActivity.class));
                else{
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                return true;
            }
            case R.id.menu_favourites: {
                startActivity(new Intent((MainActivity)getActivity(), FavouritesActivity.class));
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: RevelsCupFrag");
        swipeRefreshLayout.setRefreshing(false);
        noDataB=revelsCupRVB=noConnectionB=false;
        if(noData.getVisibility()==View.VISIBLE)
            noDataB=true;
        else if(revelsCupRV.getVisibility()==View.VISIBLE)
            revelsCupRVB=true;
        else if(noConnection.getVisibility()==View.VISIBLE)
            noConnectionB=true;
        else{
            noDataB=revelsCupRVB=noConnectionB=false;
        }
        revelsCupRV.setVisibility(View.GONE);
        noData.setVisibility(View.GONE);
        noConnection.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(realm!=null) {
            realm.close();
            realm = null;
        }
    }
}
