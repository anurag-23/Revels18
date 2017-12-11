package revels18.in.revels18.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import revels18.in.revels18.R;
import revels18.in.revels18.adapters.EventsAdapter;
import revels18.in.revels18.models.events.ScheduleModel;
import revels18.in.revels18.views.SwipeScrollView;

public class EventsFragment extends Fragment {
    TabLayout tabs;
    TextView testTV, noEventsTV;
    View eventsLayout;
    RecyclerView eventsRV;
    Realm realm = Realm.getDefaultInstance();
    List<ScheduleModel> events;
    EventsAdapter adapter;
    SwipeScrollView swipeSV;
    GestureDetector swipeDetector;
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
        View view= inflater.inflate(R.layout.fragment_events, container, false);
        initViews(view);
        events = realm.where(ScheduleModel.class).findAll();
        if(events.size() ==0){
            noEventsTV.setVisibility(View.VISIBLE);
        }else{
            adapter = new EventsAdapter(getActivity(),events, null, null,null);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
            eventsRV.setLayoutManager(layoutManager);
            eventsRV.setItemAnimator(new DefaultItemAnimator());
            eventsRV.setAdapter(adapter);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(eventsRV.getContext(), DividerItemDecoration.VERTICAL);
            eventsRV.addItemDecoration(dividerItemDecoration);
        }
        return view;
    }
    private void initViews(View view){
        swipeDetector = new GestureDetector(view.getContext(), new SwipeListener());
        swipeSV = (SwipeScrollView) view.findViewById(R.id.events_swipe_scroll_view);
        swipeSV.setGestureDetector(swipeDetector);
        tabs = (TabLayout) view.findViewById(R.id.tabs);
        testTV = (TextView)view.findViewById(R.id.testTV);
        eventsLayout = view.findViewById(R.id.events_linear_layout);
        eventsRV = (RecyclerView)view.findViewById(R.id.events_recycler_view);
        noEventsTV = (TextView)view.findViewById(R.id.events_no_events_text_view);
        for(int i=0;i<NUM_DAYS;i++){
            tabs.addTab(tabs.newTab().setText("Day "+(i+1)));
        }
        tabs.addOnTabSelectedListener(new DayTabListener());

        eventsRV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                swipeDetector.onTouchEvent(motionEvent);
                return true;
            }
        });
    }
    class DayTabListener implements TabLayout.OnTabSelectedListener{
        //Update switch case if there are more than 3 days
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            int day = tab.getPosition() + 1;
            Log.d(TAG, "onTabSelected: day = "+day);

            switch(day){
                case 1:
                    //Day 1 Tab Selected
                    testTV.setText("Day 1!");
                    break;
                case 2:
                    //Day 2 Tab Selected
                    testTV.setText("Day 2!");
                    break;
                case 3:
                    //Day 3 Tab Selected
                    testTV.setText("Day 3!");
                    break;
                case 4:
                    //Day 4 Tab Selected
                    testTV.setText("Day 4!");
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
                    testTV.setText("Day 1!");
                    break;
                case 2:
                    //Day 2 Tab ReSelected
                    testTV.setText("Day 2!");
                    break;
                case 3:
                    //Day 3 Tab ReSelected
                    testTV.setText("Day 3!");
                    break;
                default:
                    Log.d(TAG, "onTabReSelected: Error in the tab index");
            }

        }
    }
    class SwipeListener extends GestureDetector.SimpleOnGestureListener{

        private static final int SWIPE_MIN_DISTANCE = 30;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d(TAG, "onFling: ");
            if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                // Right to left Swipe
                Log.d(TAG, "onFling: RtoL Fling");
                int tabIndex = tabs.getSelectedTabPosition();
                if(!(tabIndex==NUM_DAYS - 1)){
                    //Selecting the next tab
                    tabs.getTabAt(tabIndex+1).select();
                }
                return false;
            }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
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

