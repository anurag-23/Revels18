package revels18.in.revels18.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import revels18.in.revels18.R;
import revels18.in.revels18.activities.FavouritesActivity;
import revels18.in.revels18.activities.LoginActivity;
import revels18.in.revels18.activities.MainActivity;
import revels18.in.revels18.activities.ProfileActivity;
import revels18.in.revels18.adapters.RevelsCupAdapter;
import revels18.in.revels18.models.events.RevelsCupEventModel;
import revels18.in.revels18.models.events.RevelsCupEventsListModel;
import revels18.in.revels18.network.APIClient;
import revels18.in.revels18.utilities.NetworkUtils;


public class RevelsCupFragment extends Fragment{
    RecyclerView revelsCupRV;
    LinearLayout noData;
    LinearLayout noConnection;
    private SwipeRefreshLayout swipeRefreshLayout;
    RevelsCupAdapter adapter;
    View view;
    List<RevelsCupEventModel> eventScheduleList = new ArrayList<>();
    Realm realm = Realm.getDefaultInstance();
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
        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getActivity().findViewById(R.id.toolbar).setElevation((4 * getResources().getDisplayMetrics().density + 0.5f));
                getActivity().findViewById(R.id.app_bar).setElevation((4 * getResources().getDisplayMetrics().density + 0.5f));
            }
        }catch(NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_revels_cup, container, false);
        revelsCupRV = (RecyclerView)view.findViewById(R.id.rc_recycler_view);
        noData=(LinearLayout)view.findViewById(R.id.no_revels_cup_data_layout);
        noConnection=(LinearLayout)view.findViewById(R.id.revels_cup_no_connection);
        swipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.revelscup_results_swipe_refresh_layout);
        adapter = new RevelsCupAdapter(eventScheduleList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        revelsCupRV.setLayoutManager(layoutManager);
        revelsCupRV.setItemAnimator(new DefaultItemAnimator());
        revelsCupRV.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(true);
        loadRevelsCupEventsFromInternet();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reload();
            }
        });
        return view;
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
                    Log.d(TAG,"RevelsCup Events updated in background");
                    if(eventScheduleList.isEmpty()){
                        noConnection.setVisibility(View.GONE);
                        revelsCupRV.setVisibility(View.GONE);
                        noData.setVisibility(View.VISIBLE);
                    }
                    else{
                        noConnection.setVisibility(View.GONE);
                        revelsCupRV.setVisibility(View.VISIBLE);
                        Log.d(TAG, "onResponse: RevelsCup set visible");
                        noData.setVisibility(View.GONE);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<RevelsCupEventsListModel> call, Throwable t) {
                Log.d(TAG, "onFailure: RevelsCup Events not updated");
                swipeRefreshLayout.setRefreshing(false);
                if(! NetworkUtils.isInternetConnected(getContext())){
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
            }
        });
    }

    private void reload(){
        if(! NetworkUtils.isInternetConnected(getContext())){
            Snackbar.make(view, "Check connection!", Snackbar.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        }
        else {
            loadRevelsCupEventsFromInternet();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_others, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_registrations:{
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
}
