package revels18.in.revels18.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import revels18.in.revels18.R;
import revels18.in.revels18.adapters.EventsAdapter;
import revels18.in.revels18.application.Revels;
import revels18.in.revels18.models.events.EventDetailsModel;
import revels18.in.revels18.models.events.ScheduleModel;
import revels18.in.revels18.models.favorites.FavouritesModel;
import revels18.in.revels18.views.SwipeScrollView;

public class EventsFragment extends Fragment {
    TabLayout tabs;
    TextView noEventsTV;
    View eventsLayout;
    RecyclerView eventsRV;
    Realm realm = Realm.getDefaultInstance();
    List<ScheduleModel> events;
    EventsAdapter adapter;
    SwipeScrollView swipeSV;
    GestureDetector swipeDetector;
    private MenuItem searchItem;
    private MenuItem filterItem;
    private final int NUM_DAYS = 4;
    private String TAG = "EventsFragment";
    public static EventsFragment newInstance() {
        EventsFragment fragment = new EventsFragment();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        final View view= inflater.inflate(R.layout.fragment_events, container, false);
        initViews(view);
        events = realm.where(ScheduleModel.class).findAll();

        if(events.size() ==0){
            noEventsTV.setVisibility(View.VISIBLE);
        }else{
            List<ScheduleModel> day1events = new ArrayList<>();
            for (int i = 0; i <events.size() ; i++) {
                if(events.get(i).getDay().contains(1+"")){
                    day1events.add(events.get(i));
                }
            }
            EventsAdapter.FavouriteClickListener favClickListener = new EventsAdapter.FavouriteClickListener() {
                @Override
                public void onItemClick(ScheduleModel event, boolean add) {
                    //Favourite Clicked
                    if(add){
                        Snackbar.make(view, event.getEventName()+" added to Favourites!",Snackbar.LENGTH_SHORT).show();
                    }else{
                        Snackbar.make(view, event.getEventName()+" removed from Favourites!",Snackbar.LENGTH_SHORT).show();
                    }

                }
            };
            adapter = new EventsAdapter(getActivity(),day1events, null, null,favClickListener);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
            eventsRV.setLayoutManager(layoutManager);
            eventsRV.setItemAnimator(new DefaultItemAnimator());
            eventsRV.setAdapter(adapter);
//            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(nulleventsRV.getContext(), DividerItemDecoration.VERTICAL);
//            eventsRV.addItemDecoration(dividerItemDecoration);

        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_hardware, menu);
        Log.d(TAG, "onCreateOptionsMenu: ");
        searchItem = menu.findItem(R.id.action_search);
        filterItem = menu.findItem(R.id.action_filter);
        final SearchView searchView = (SearchView)searchItem.getActionView();
        SearchManager searchManager = (SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setSubmitButtonEnabled(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {
                queryFilter(text);
                Revels.searchOpen = 1;
                return false;
            }
            @Override
            public boolean onQueryTextChange(String text) {
                queryFilter(text);
                Revels.searchOpen = 1;
                return false;
            }
        });
        searchView.setQueryHint("Search..");
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchView.clearFocus();
                Revels.searchOpen = 1;
                return false;
            }


        });

    }


    private void initViews(View view){
        swipeDetector = new GestureDetector(view.getContext(), new SwipeListener());
        swipeSV = (SwipeScrollView) view.findViewById(R.id.events_swipe_scroll_view);
        swipeSV.setGestureDetector(swipeDetector);
        tabs = (TabLayout) view.findViewById(R.id.tabs);
        eventsLayout = view.findViewById(R.id.events_linear_layout);
        eventsRV = (RecyclerView)view.findViewById(R.id.events_recycler_view);
        noEventsTV = (TextView)view.findViewById(R.id.events_no_events_text_view);
        for(int i=0;i<NUM_DAYS;i++){
            tabs.addTab(tabs.newTab().setText("Day "+(i+1)));
        }
        DayTabListener tabListener = new DayTabListener();
        tabs.addOnTabSelectedListener(tabListener);
        eventsRV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                swipeDetector.onTouchEvent(motionEvent);
                return true;
            }
        });
    }
    public void dayFilter(int day){
        List<ScheduleModel> temp = new ArrayList<>();
        for(int i=0;i<events.size();i++){
            if(events.get(i).getDay().contains(day+"")){
                temp.add(events.get(i));
            }
        }
        adapter.updateList(temp);
    }
    public void queryFilter(String query){
        query = query.toLowerCase();
        List<ScheduleModel> temp = new ArrayList<>();
        for(int i=0;i<events.size();i++){
            if(events.get(i).getEventName().toLowerCase().contains(query) || events.get(i).getCatName().toLowerCase().contains(query)){
                temp.add(events.get(i));
            }
        }
        adapter.updateList(temp);
    }
    class DayTabListener implements TabLayout.OnTabSelectedListener{
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            int day = tab.getPosition() + 1;
            Log.d(TAG, "onTabSelected: day = "+day);

            switch(day){
                case 1:
                    //Day 1 Tab Selected
                    dayFilter(day);
                    break;
                case 2:
                    //Day 2 Tab Selected
                    dayFilter(day);
                    break;
                case 3:
                    //Day 3 Tab Selected
                    dayFilter(day);
                    break;
                case 4:
                    //Day 4 Tab Selected
                    dayFilter(day);
                    break;
                default:
                    Log.d(TAG, "onTabSelected: Error in the tab index");
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            int day = tab.getPosition() + 1;
            Log.d(TAG, "onTabUnselected: day =  "+day);

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            int day = tab.getPosition() + 1;
            Log.d(TAG, "onTabReselected: day = "+day);

            switch(day){
                case 1:
                    //Day 1 Tab ReSelected
                    dayFilter(day);
                    break;
                case 2:
                    //Day 2 Tab ReSelected
                    dayFilter(day);
                    break;
                case 3:
                    //Day 3 Tab ReSelected
                    dayFilter(day);
                    break;
                default:
                    Log.d(TAG, "onTabReSelected: Error in the tab index");
            }

        }
    }
    class SwipeListener extends GestureDetector.SimpleOnGestureListener{

        private static final int SWIPE_MIN_DISTANCE = 100;
        private static final int SWIPE_MAX_VERTICAL = 300;
        private static final int SWIPE_THRESHOLD_VELOCITY = 300;
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d(TAG, "onFling: "+(Math.abs(e1.getY() - e2.getY())));

            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY && Math.abs(e1.getY() - e2.getY())<SWIPE_MAX_VERTICAL ) {
                // Right to left Swipe
                Log.d(TAG, "onFling: RtoL Fling");
                int tabIndex = tabs.getSelectedTabPosition();
                if(!(tabIndex==NUM_DAYS - 1)){
                    //Selecting the next tab
                    tabs.getTabAt(tabIndex+1).select();
                }
                return false;
            }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY &&Math.abs(e1.getY() - e2.getY())<SWIPE_MAX_VERTICAL ) {
                // Left to right Swipe
                Log.d(TAG, "onFling: LtoR Fling");
                int tabIndex = tabs.getSelectedTabPosition();
                if(!(tabIndex==0)){
                    //Selecting the previous tab
                    tabs.getTabAt(tabIndex-1).select();
                }
                return false;
            }

            if(e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                return false; // Bottom to top
            }  else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                return false; // Top to bottom
            }
            return false;
        }
    }
}

