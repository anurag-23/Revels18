package revels18.in.revels18.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import revels18.in.revels18.R;
import revels18.in.revels18.activities.FavouritesActivity;
import revels18.in.revels18.activities.MainActivity;
import revels18.in.revels18.activities.RegistrationsActivity;
import revels18.in.revels18.adapters.EventsAdapter;
import revels18.in.revels18.adapters.RevelsCupAdapter;
import revels18.in.revels18.models.categories.CategoryModel;
import revels18.in.revels18.models.events.RevelsCupEventModel;
import revels18.in.revels18.models.events.RevelsCupEventsListModel;
import revels18.in.revels18.models.events.ScheduleModel;
import revels18.in.revels18.network.APIClient;


public class RevelsCupFragment extends Fragment{
    RecyclerView revelsCupRV;
    RevelsCupAdapter adapter;
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
        loadRevelsCupEventsFromInternet();
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
        View view = inflater.inflate(R.layout.fragment_revels_cup, container, false);
        revelsCupRV = (RecyclerView)view.findViewById(R.id.rc_recycler_view);

        adapter = new RevelsCupAdapter(eventScheduleList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        revelsCupRV.setLayoutManager(layoutManager);
        revelsCupRV.setItemAnimator(new DefaultItemAnimator());
        revelsCupRV.setAdapter(adapter);
        return view;
    }
    private void loadRevelsCupEventsFromInternet(){
        Call<RevelsCupEventsListModel> revelsCupCall = APIClient.getAPIInterface().getRevelsCupEventsList();
        revelsCupCall.enqueue(new Callback<RevelsCupEventsListModel>() {
            @Override
            public void onResponse(Call<RevelsCupEventsListModel> call, Response<RevelsCupEventsListModel> response) {
                if (response.isSuccess() && response.body() != null) {
                    eventScheduleList.clear();
                    eventScheduleList.addAll(response.body().getEvents());
                    Log.d(TAG,"RevelsCup Events updated in background");
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<RevelsCupEventsListModel> call, Throwable t) {
                Log.d(TAG, "onFailure: RevelsCup Events not updated");
            }
        });
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
                startActivity(new Intent((MainActivity)getActivity(), RegistrationsActivity.class));
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
