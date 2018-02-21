package mitrev.in.mitrev18.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import mitrev.in.mitrev18.R;
import mitrev.in.mitrev18.activities.FavouritesActivity;
import mitrev.in.mitrev18.activities.LoginActivity;
import mitrev.in.mitrev18.activities.MainActivity;
import mitrev.in.mitrev18.activities.ProfileActivity;
import mitrev.in.mitrev18.adapters.RevelsCupResultsAdapter;
import mitrev.in.mitrev18.models.sports.SportsListModel;
import mitrev.in.mitrev18.models.sports.SportsModel;
import mitrev.in.mitrev18.models.sports.SportsResultModel;
import mitrev.in.mitrev18.network.APIClient;
import mitrev.in.mitrev18.utilities.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RevelsCupResultsFragment extends Fragment {
    Realm mDatabase;
    View view;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout rootLayout;
    private LinearLayout noResultsLayout;
    private FrameLayout resultsAvailable;
    private RevelsCupResultsAdapter adapter;
    private List<SportsResultModel> resultsList = new ArrayList<>();
    String TAG="RevelsCupResultsFrag";

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
        mDatabase= Realm.getDefaultInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_revels_cup_results, container, false);
        rootLayout=(LinearLayout) view.findViewById(R.id.revels_cup_results_root_layout);
        resultsAvailable=(FrameLayout)view.findViewById(R.id.revels_cup_results_available);
        noResultsLayout = (LinearLayout) view.findViewById(R.id.revels_cup_no_results_layout);
        RecyclerView resultsRecyclerView = (RecyclerView)view.findViewById(R.id.revels_cup_results_recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.revels_cup_results_swipe_refresh_layout);
        adapter = new RevelsCupResultsAdapter(resultsList ,getActivity());
        resultsRecyclerView.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 4);
        resultsRecyclerView.setLayoutManager(gridLayoutManager);
        displayData();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData(view);
            }
        });
        return view;
    }
    private void refreshData(View view){
        boolean isConnectedTemp = NetworkUtils.isInternetConnected(getContext());
        if(isConnectedTemp){updateData();}
        else{
            if (mDatabase == null){
                resultsAvailable.setVisibility(View.GONE);
                noResultsLayout.setVisibility(View.VISIBLE);
            }
            Snackbar.make(view, "Check connection!", Snackbar.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);}
    }
    private void displayData() {
        if (mDatabase != null) {
            resultsAvailable.setVisibility(View.VISIBLE);
            noResultsLayout.setVisibility(View.GONE);
        } else {
            resultsAvailable.setVisibility(View.GONE);
            noResultsLayout.setVisibility(View.VISIBLE);
            return;
        }

        RealmResults<SportsModel> results = mDatabase.where(SportsModel.class).findAllSorted("eventName", Sort.ASCENDING, "position", Sort.ASCENDING);

        if (!results.isEmpty()){
            resultsList.clear();
            List<String> eventNamesList = new ArrayList<>();

            for (SportsModel result : results){
                String eventName = result.getEventName()+" "+result.getRound();
                if (eventNamesList.contains(eventName)){
                    resultsList.get(eventNamesList.indexOf(eventName)).eventResultsList.add(result);
                }
                else{
                    SportsResultModel eventResult = new SportsResultModel();
                    eventResult.eventName = result.getEventName();
                    eventResult.eventRound = result.getRound();
                    eventResult.eventCatID = result.getCatID();
                    eventResult.eventResultsList.add(result);
                    resultsList.add(eventResult);
                    eventNamesList.add(eventName);
                }
            }
            resultsAvailable.setVisibility(View.VISIBLE);
            noResultsLayout.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        }
        else{
            resultsAvailable.setVisibility(View.GONE);
            noResultsLayout.setVisibility(View.VISIBLE);
        }
    }
    public void updateData(){
        Call<SportsListModel> call = APIClient.getAPIInterface().getSportsResults();
        call.enqueue(new Callback<SportsListModel>() {
            @Override
            public void onResponse(Call<SportsListModel> call, Response<SportsListModel> response) {
                Log.d(TAG, "onResponse: Called RevelsCupResults");
                if (response.body() != null && mDatabase != null){
                    mDatabase.beginTransaction();
                    mDatabase.where(SportsModel.class).findAll().deleteAllFromRealm();
                    mDatabase.copyToRealm(response.body().getData());
                    mDatabase.commitTransaction();
                    noResultsLayout.setVisibility(View.GONE);
                    resultsAvailable.setVisibility(View.VISIBLE);
                    displayData();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<SportsListModel> call, Throwable t) {
                Log.d(TAG, "onFailure: Revels Cup Results not updated");
                if (mDatabase == null){
                    resultsAvailable.setVisibility(View.GONE);
                    noResultsLayout.setVisibility(View.VISIBLE);
                }
                Snackbar.make(rootLayout, "Error fetching results", Snackbar.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
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
                refreshData(view);
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
        swipeRefreshLayout.setRefreshing(false);
        resultsAvailable.setVisibility(View.GONE);
        noResultsLayout.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDatabase.close();
        mDatabase = null;
    }
}
