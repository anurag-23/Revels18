package revels18.in.revels18.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import revels18.in.revels18.R;
import revels18.in.revels18.activities.FavouritesActivity;
import revels18.in.revels18.activities.LoginActivity;
import revels18.in.revels18.activities.MainActivity;
import revels18.in.revels18.activities.ProfileActivity;
import revels18.in.revels18.adapters.HomeAdapter;
import revels18.in.revels18.adapters.HomeCategoriesAdapter;
import revels18.in.revels18.adapters.HomeEventsAdapter;
import revels18.in.revels18.adapters.HomeResultsAdapter;
import revels18.in.revels18.models.categories.CategoryModel;
import revels18.in.revels18.models.events.ScheduleModel;
import revels18.in.revels18.models.favorites.FavouritesModel;
import revels18.in.revels18.models.instagram.InstagramFeed;
import revels18.in.revels18.models.results.EventResultModel;
import revels18.in.revels18.models.results.ResultModel;
import revels18.in.revels18.models.results.ResultsListModel;
import revels18.in.revels18.network.APIClient;
import revels18.in.revels18.network.InstaFeedAPIClient;
import revels18.in.revels18.utilities.NetworkUtils;

public class HomeFragment extends Fragment {
    private InstagramFeed feed;
    SwipeRefreshLayout swipeRefreshLayout;
    private HomeAdapter instaAdapter;
    private HomeResultsAdapter resultsAdapter;
    private HomeCategoriesAdapter categoriesAdapter;
    private HomeEventsAdapter eventsAdapter;
    private RecyclerView homeRV;
    private RecyclerView resultsRV;
    private RecyclerView  categoriesRV;
    private RecyclerView eventsRV;
    private TextView resultsMore;
    private TextView categoriesMore;
    private TextView eventsMore;
    private TextView resultsNone;
    private CardView homeResultsItem;
    private ProgressBar progressBar;
    private BottomNavigationView navigation;
    private AppBarLayout appBarLayout;
    private TextView instaTextView;
    private boolean initialLoad = true;
    private boolean firstLoad=true;
    private int processes = 0;
    private SliderLayout imageSlider;
    String TAG = "HomeFragment";
    Realm mDatabase = Realm.getDefaultInstance();
    private List<EventResultModel> resultsList = new ArrayList<>();
    private List<CategoryModel> categoriesList = new ArrayList<>();
    private List<ScheduleModel> eventsList = new ArrayList<>();
    private FirebaseRemoteConfig firebaseRemoteConfig;
    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.fest_name);
        setHasOptionsMenu(true);

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = initViews(inflater, container);

        progressBar = (ProgressBar)view.findViewById(R.id.insta_progress);
        instaTextView = (TextView)view.findViewById(R.id.insta_text_view);
        displayInstaFeed();

        //Setting up Firebase
        try {
            firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
            FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                    .build();
            firebaseRemoteConfig.setConfigSettings(configSettings);
        }catch (Exception e){
            e.printStackTrace();
        }
        //Checking User's Network Status
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        //Updating the SliderLayout with images
        getImageURLSfromFirebase();

        //Animation type
        imageSlider.setPresetTransformer(SliderLayout.Transformer.Default);
        //Setting the Transition time and Interpolator for the Animation
        imageSlider.setSliderTransformDuration(200,new AccelerateDecelerateInterpolator());
        imageSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        imageSlider.setCustomAnimation(new DescriptionAnimation());
        //Setting the time after which it moves to the next image
        imageSlider.setDuration(400);
        imageSlider.setVisibility(View.GONE);

        resultsAdapter = new HomeResultsAdapter(resultsList,getActivity());
        resultsRV.setAdapter(resultsAdapter);
        resultsRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false));
        updateResultsList();
        resultsMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MORE Clicked - Take user to Results Fragment
                Log.i(TAG, "onClick: Results more");
                ((MainActivity)getActivity()).changeFragment(ResultsTabsFragment.newInstance());
            }
        });

        //Display Categories
        RealmResults<CategoryModel> categoriesRealmList = mDatabase.where(CategoryModel.class).findAllSorted("categoryName");
        categoriesList = mDatabase.copyFromRealm(categoriesRealmList);
        if(categoriesList.size()>10){
            categoriesList.subList(10,categoriesList.size()).clear();
        }
        categoriesAdapter = new HomeCategoriesAdapter(categoriesList,getActivity());
        categoriesRV.setAdapter(categoriesAdapter);
        categoriesRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false));
        categoriesAdapter.notifyDataSetChanged();
        categoriesMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MORE Clicked - Take user to Categories Fragment
                Log.i(TAG, "onClick: Categories More");
                ((MainActivity)getActivity()).changeFragment(CategoriesFragment.newInstance());

            }
        });
        if(categoriesList.size()==0){
            view.findViewById(R.id.home_categories_none_text_view).setVisibility(View.VISIBLE);
        }

        //Display Events of current day
        Calendar cal = Calendar.getInstance();
        Calendar day1 = new GregorianCalendar(2018, 3, 7);
        Calendar day2 = new GregorianCalendar(2018, 3, 8);
        Calendar day3 = new GregorianCalendar(2018, 3, 9);
        Calendar day4 = new GregorianCalendar(2018, 3, 10);
        Calendar curDay = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

        int dayOfEvent;

        if(curDay.getTimeInMillis() < day1.getTimeInMillis()){
            dayOfEvent =0;
        }else if (curDay.getTimeInMillis() < day2.getTimeInMillis()){
            dayOfEvent = 1;
        }else if (curDay.getTimeInMillis() < day3.getTimeInMillis()){
            dayOfEvent = 2;
        }else if (curDay.getTimeInMillis() < day4.getTimeInMillis()){
            dayOfEvent = 3;
        }else {
            dayOfEvent = 4;
        }

        String sortCriteria[] = {"day", "startTime", "eventName"};
        Sort sortOrder[] = {Sort.ASCENDING, Sort.ASCENDING, Sort.ASCENDING};

        //PreRevels events
        if(dayOfEvent == 0){
            List<ScheduleModel> eventsRealmResults = mDatabase.copyFromRealm((mDatabase.where(ScheduleModel.class).findAll()));
            for(int i=0;i<eventsRealmResults.size();i++){
                Log.d(TAG, "dayFilter Value: "+eventsRealmResults.get(i).getIsRevels());
                if(eventsRealmResults.get(i).getIsRevels().contains("0")){
                    eventsList.add(eventsRealmResults.get(i));
                }
            }
        }
        //Main Revels Events
        else {
            List<ScheduleModel> eventsRealmResults = mDatabase.copyFromRealm(mDatabase.where(ScheduleModel.class).equalTo("day", dayOfEvent + "").findAllSorted(sortCriteria, sortOrder));
            eventsList = mDatabase.copyFromRealm(eventsRealmResults);
            for (int i = 0; i < eventsList.size(); i++) {
                ScheduleModel event = eventsList.get(i);
                if (isFavourite(event)) {
                    //Move to top if the event is a Favourite
                    eventsList.remove(event);
                    eventsList.add(0, event);
                }
            }
        }
        if (eventsList.size() > 10) {
            eventsList.subList(10, eventsList.size()).clear();
        }
        eventsAdapter = new HomeEventsAdapter(eventsList, null,getActivity());
        Log.i(TAG, "onCreateView: eventsList size"+eventsList.size());
        eventsRV.setAdapter(eventsAdapter);
        eventsRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false));
        eventsAdapter.notifyDataSetChanged();
        eventsMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MORE Clicked - Take user to Events Fragment
                Log.i(TAG, "onClick: Events More");
                ((MainActivity)getActivity()).changeFragment(EventsFragment.newInstance());
            }
        });
        if(eventsList.size()==0){
            view.findViewById(R.id.home_events_none_text_view).setVisibility(View.VISIBLE);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                boolean isConnectedTemp = NetworkUtils.isInternetConnected(getContext());;
                if(isConnectedTemp){
                    displayInstaFeed();
                    fetchResults();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }, 5000);
                }
                else{
                    Snackbar.make(view, "Check connection!", Snackbar.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);}

            }
        });
        return view;
    }
    public void displayInstaFeed(){
        if (initialLoad) progressBar.setVisibility(View.VISIBLE);
        homeRV.setVisibility(View.GONE);
        instaTextView.setVisibility(View.GONE);
        Call<InstagramFeed> call = InstaFeedAPIClient.getInterface().getInstagramFeed();
        processes ++;
        call.enqueue(new Callback<InstagramFeed>() {
            @Override
            public void onResponse(Call<InstagramFeed> call, Response<InstagramFeed> response) {
                if (initialLoad) progressBar.setVisibility(View.GONE);
                if(response.isSuccess()){
                    feed = response.body();
                    instaAdapter =  new HomeAdapter(feed);
                    homeRV.setAdapter(instaAdapter);
                    homeRV.setLayoutManager(new LinearLayoutManager(getContext()));
                    ViewCompat.setNestedScrollingEnabled(homeRV, false);
                }
                homeRV.setVisibility(View.VISIBLE);
                initialLoad = false;

            }

            @Override
            public void onFailure(Call<InstagramFeed> call, Throwable t) {
                if (initialLoad) progressBar.setVisibility(View.GONE);
                instaTextView.setVisibility(View.VISIBLE);
                Log.i(TAG, "onFailure: Error Fetching insta feed ");
                initialLoad = false;
            }
        });
    }
    public void updateResultsList(){
        RealmResults<ResultModel> results = mDatabase.where(ResultModel.class).findAllSorted("eventName", Sort.ASCENDING, "position",Sort.ASCENDING );
        List<ResultModel> resultsArrayList=new ArrayList<>();
        resultsArrayList=mDatabase.copyFromRealm(results);
        if (!resultsArrayList.isEmpty()){
            resultsList.clear();
            List<String> eventNamesList = new ArrayList<>();
            for (ResultModel result : resultsArrayList){
                String eventName = result.getEventName()+" "+result.getRound();
                if (eventNamesList.contains(eventName)){
                    resultsList.get(eventNamesList.indexOf(eventName)).eventResultsList.add(result);
                }
                else{
                    EventResultModel eventResult = new EventResultModel();
                    eventResult.eventName = result.getEventName();
                    eventResult.eventRound = result.getRound();
                    eventResult.eventCategory = result.getCatName();
                    eventResult.eventResultsList.add(result);
                    resultsList.add(eventResult);
                    eventNamesList.add(eventName);
                }
            }
        }
        Log.i(TAG, "displayResults: resultsList size:"+resultsList.size());
        //Moving favourite results to top
        for(int i=0;i<resultsList.size();i++){
            EventResultModel result= resultsList.get(i);
            if(isFavourite(result)){
                resultsList.remove(result);
                resultsList.add(0, result);
            }
        }
        //Picking first 10 results to display
        if(resultsList.size()>10){
            resultsList.subList(10,resultsList.size()).clear();
        }
        resultsAdapter.notifyDataSetChanged();

        if(resultsList.size()==0){
            homeResultsItem.setVisibility(View.GONE);
        }
        else{
            homeResultsItem.setVisibility(View.VISIBLE);
        }
    }
    private void getImageURLSfromFirebase(){
        long cacheExpiration = 3600;
        firebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        List<String> imgURLs  = new ArrayList<>();
                        final List<String> linkURLs = new ArrayList<>();
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Successful");
                            firebaseRemoteConfig.activateFetched();
                            int  n_banners = Integer.parseInt(firebaseRemoteConfig.getString("num_banners"));
                            Log.d(TAG, "n banners: "+n_banners);
                            for(int i=1;i<=n_banners;i++){
                                String imgURL = firebaseRemoteConfig.getString("banner_img_"+i);
                                String linkURL = firebaseRemoteConfig.getString("banner_link_"+i);

                                imgURLs.add(imgURL);
                                linkURLs.add(linkURL);
                                Log.d(TAG, "onComplete: img:"+imgURL+" \nLink:"+linkURL);
                            }

                        }else{
                            //Unable to fetch Config Values from Firebase.
                            //TODO: Add default values here
                            Log.d(TAG, "onComplete: Default"+task.getException().toString());
                        }
                        BaseSliderView.ScaleType imgScaleType = BaseSliderView.ScaleType.CenterCrop;
                        if(imgURLs.size()!=linkURLs.size() || imgURLs.size() == 0 || linkURLs.size() == 0 ){
                            return;
                        }
                        for(int i=0;i<imgURLs.size();i++){
                            TextSliderView tsv = new TextSliderView(getContext());
                            final String hyperlink = linkURLs.get(i);
                            tsv.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse(hyperlink));
                                    startActivity(intent);
                                }
                            });
                            tsv.image(imgURLs.get(i));
                            tsv.setScaleType(imgScaleType);
                            imageSlider.addSlider(tsv);
                        }
                        imageSlider.setVisibility(View.VISIBLE);
                    }
                });

    }
    public void fetchResults(){
        processes++;
        Call<ResultsListModel> callResultsList = APIClient.getAPIInterface().getResultsList();
        callResultsList.enqueue(new Callback<ResultsListModel>() {
            List<ResultModel> results = new ArrayList<>();
            @Override
            public void onResponse(Call<ResultsListModel> call, Response<ResultsListModel> response) {
                if (response.isSuccess() && response.body() != null){
                    results = response.body().getData();
                    mDatabase.beginTransaction();
                    mDatabase.where(ResultModel.class).findAll().deleteAllFromRealm();
                    mDatabase.copyToRealm(results);
                    mDatabase.commitTransaction();
                    //homeResultsItem.setVisibility(View.VISIBLE);
                    updateResultsList();
                    resultsNone.setVisibility(View.GONE);
                    resultsNone.setText("");
                }
            }
            @Override
            public void onFailure(Call<ResultsListModel> call, Throwable t) {
                if(homeResultsItem.getVisibility()==View.VISIBLE)
                    //homeResultsItem.setVisibility(View.GONE);
                processes--;
            }
        });
    }
    public boolean isFavourite(ScheduleModel event){
        RealmResults<FavouritesModel> favouritesRealmList = mDatabase.where(FavouritesModel.class).equalTo("id",event.getEventID()).contains("day", event.getDay()).equalTo("round", event.getRound()).findAll();
        return (favouritesRealmList.size()!=0);
    }
    public boolean isFavourite(EventResultModel result){
        RealmResults<FavouritesModel> favouritesRealmList = mDatabase.where(FavouritesModel.class).equalTo("eventName",result.eventName).findAll();
        return (favouritesRealmList.size()!=0);
    }

    public View initViews(LayoutInflater inflater, ViewGroup container){

        appBarLayout = (AppBarLayout) container.findViewById(R.id.app_bar);
        navigation = (BottomNavigationView) container.findViewById(R.id.bottom_nav);
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        homeRV = (RecyclerView) view.findViewById(R.id.home_recycler_view);
        resultsRV = (RecyclerView) view.findViewById(R.id.home_results_recycler_view);
        categoriesRV = (RecyclerView) view.findViewById(R.id.home_categories_recycler_view);
        eventsRV = (RecyclerView) view.findViewById(R.id.home_events_recycler_view);
        resultsMore = (TextView) view.findViewById(R.id.home_results_more_text_view);
        categoriesMore = (TextView) view.findViewById(R.id.home_categories_more_text_view);
        eventsMore = (TextView) view.findViewById(R.id.home_events_more_text_view);
        resultsNone = (TextView) view.findViewById(R.id.home_results_none_text_view);
        homeResultsItem=(CardView) view.findViewById(R.id.home_results_item);
        instaTextView = (TextView) view.findViewById(R.id.instagram_textview);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.home_swipe_refresh_layout);
        imageSlider = (SliderLayout) view.findViewById(R.id.home_image_slider);
        if(imageSlider==null){
            Log.d(TAG, "initViews: Null imageSlider");
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
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
                startActivity(new Intent(getActivity(), FavouritesActivity.class));
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
