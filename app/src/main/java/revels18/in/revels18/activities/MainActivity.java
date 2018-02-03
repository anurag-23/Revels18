package revels18.in.revels18.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import revels18.in.revels18.R;
import revels18.in.revels18.Utilities.BottomNavigationViewHelper;
import revels18.in.revels18.application.Revels;
import revels18.in.revels18.fragments.CategoriesFragment;
import revels18.in.revels18.fragments.EventsFragment;
import revels18.in.revels18.fragments.HomeFragment;
import revels18.in.revels18.fragments.ResultsFragment;
import revels18.in.revels18.fragments.ResultsTabsFragment;
import revels18.in.revels18.fragments.RevelsCupFragment;
import revels18.in.revels18.models.categories.CategoriesListModel;
import revels18.in.revels18.models.categories.CategoryModel;
import revels18.in.revels18.models.events.EventDetailsModel;
import revels18.in.revels18.models.events.EventsListModel;
import revels18.in.revels18.models.events.ScheduleListModel;
import revels18.in.revels18.models.events.ScheduleModel;
import revels18.in.revels18.models.results.ResultModel;
import revels18.in.revels18.models.results.ResultsListModel;
import revels18.in.revels18.network.APIClient;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity  {
    private FragmentManager fm;
    Fragment selectedFragment;
    private Realm mDatabase;
    ///private NavigationView drawerView;
    private BottomNavigationView navigation;
    private AppBarLayout appBarLayout;
    String TAG = "MainActivity";
    Boolean isConnected;
    private Context context = this;
    //String CCT_LAUNCH_URL = "https://www.techtatva.in";
    //private FirebaseRemoteConfig firebaseRemoteConfig;

    /*@Override
    protected void onPostResume() {
        super.onPostResume();
        if(selectedFragment.getClass()==OnlineEventsFragment.class) {
            //drawerView.setCheckedItem(R.id.drawer_home);
            navigation = (BottomNavigationView) findViewById(R.id.bottom_nav);
            navigation.setVisibility(VISIBLE);
            appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
            appBarLayout.setVisibility(VISIBLE);
            navigation.setSelectedItemId(R.id.bottom_nav_home);
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabase = Realm.getDefaultInstance();

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(Color.parseColor("#0d0d0d"));
            getWindow().setNavigationBarColor(Color.parseColor("#0d0d0d"));

        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();*/

        /*drawerView = (NavigationView) findViewById(R.id.nav_view);
        drawerView.setNavigationItemSelectedListener(mOnDrawerItemSelectedListener);
        drawerView.setCheckedItem(R.id.drawer_home);
        drawerView.setSelected(true);*/

        navigation = (BottomNavigationView) findViewById(R.id.bottom_nav);
        BottomNavigationViewHelper.removeShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnBottomNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.bottom_nav_home);
        navigation.setSelected(true);

        fm = getSupportFragmentManager();
        /*firebaseRemoteConfig=FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .build();
        firebaseRemoteConfig.setConfigSettings(configSettings);*/
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if(isConnected){
            loadAllFromInternet();
            Log.i(TAG, "onCreate: Connected and background updated");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_pro_show: {
                //Launch CCT taking the user to the ProShow page
                //TODO: Change the URL Below
                String URL = "https://www.google.com/";
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(this, Uri.parse(URL));
                return true;
            }
            case R.id.menu_about_us: {
                startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                return true;
            }
            case R.id.menu_developers: {
                startActivity(new Intent(MainActivity.this, DevelopersActivity.class));
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnBottomNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            selectedFragment=null;
            switch (item.getItemId()) {
                case R.id.bottom_nav_home:
                    selectedFragment = HomeFragment.newInstance();
                    break;
                case R.id.bottom_nav_events:
                    selectedFragment = EventsFragment.newInstance();
                    break;
                case R.id.bottom_nav_categories:
                    selectedFragment = CategoriesFragment.newInstance();
                    break;
                case R.id.bottom_nav_revels_cup:
                    selectedFragment = RevelsCupFragment.newInstance();
                    break;
                case R.id.bottom_nav_results:
                    selectedFragment = ResultsTabsFragment.newInstance();
                    break;
            }

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_from_top,R.anim.blank).replace(R.id.main_frame_layout, selectedFragment);
            transaction.commit();
            return true;
        }

    };

    /*private NavigationView.OnNavigationItemSelectedListener mOnDrawerItemSelectedListener
            = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            selectedFragment=null;
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawerView = (NavigationView) findViewById(R.id.nav_view);
            int id = item.getItemId();

            if (id == R.id.drawer_home) {
                navigation = (BottomNavigationView) findViewById(R.id.bottom_nav);
                navigation.setVisibility(VISIBLE);
                appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
                appBarLayout.setVisibility(VISIBLE);
                navigation.setSelectedItemId(R.id.bottom_nav_home);
                selectedFragment = HomeFragment.newInstance();

            } else if (id == R.id.drawer_favourites) {
                appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
                appBarLayout.setVisibility(VISIBLE);
                navigation = (BottomNavigationView) findViewById(R.id.bottom_nav);
                navigation.setVisibility(GONE);
                selectedFragment = FavouritesFragment.newInstance();

            } else if (id == R.id.drawer_online_events) {
                selectedFragment = OnlineEventsFragment.newInstance();

            } else if (id == R.id.drawer_results) {
                appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
                appBarLayout.setVisibility(VISIBLE);
                navigation = (BottomNavigationView) findViewById(R.id.bottom_nav);
                navigation.setVisibility(GONE);
                selectedFragment = ResultsFragment.newInstance();

            } else if (id == R.id.drawer_developers) {
                appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
                appBarLayout.setVisibility(VISIBLE);
                navigation = (BottomNavigationView) findViewById(R.id.bottom_nav);
                navigation.setVisibility(GONE);

                selectedFragment = DevelopersFragment.newInstance();

            } else if (id == R.id.drawer_about_us) {
                appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
                appBarLayout.setVisibility(VISIBLE);
                navigation = (BottomNavigationView) findViewById(R.id.bottom_nav);
                navigation.setVisibility(GONE);

                selectedFragment = AboutUsFragment.newInstance();
            }

            if(selectedFragment.getClass()==OnlineEventsFragment.class){
                launchCCT();

            }else{
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_left).replace(R.id.main_frame_layout, selectedFragment);
                transaction.commit();
            }

            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
    };*/

    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed");
        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

            if ( navigation.getMenu().getItem(0).isChecked() ){
                finishAffinity();
            }
            else{
                if(Revels.searchOpen ==1 ){
                    fm.beginTransaction().replace(R.id.main_frame_layout, new CategoriesFragment()).commit();
                    Revels.searchOpen =0;
                }
                if(Revels.searchOpen ==2 ){
                    fm.beginTransaction().replace(R.id.main_frame_layout, new EventsFragment()).commit();
                    Revels.searchOpen =0;
                }
                else{
                    fm.beginTransaction().replace(R.id.main_frame_layout, new HomeFragment()).commit();
                    navigation.setSelectedItemId(R.id.bottom_nav_home);
                }
                if(navigation.getVisibility()==GONE)
                { navigation.setVisibility(VISIBLE);}

                if(appBarLayout!=null)
                {appBarLayout.setVisibility(VISIBLE);}
            }
        }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    private void launchCCT(){
        /*firebaseRemoteConfig.fetch()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            firebaseRemoteConfig.activateFetched();
                            CCT_LAUNCH_URL = firebaseRemoteConfig.getString("onlineEvents");
                        }

                        Log.d("URL", CCT_LAUNCH_URL);

                        Uri uri = Uri.parse(CCT_LAUNCH_URL);
                        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
                        intentBuilder.setToolbarColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark));
                        intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark));
                        intentBuilder.setStartAnimations(MainActivity.this, R.anim.slide_in_right, R.anim.slide_out_left);
                        intentBuilder.setExitAnimations(MainActivity.this, android.R.anim.slide_in_left,
                                android.R.anim.slide_out_right);
                        CustomTabsIntent customTabsIntent = intentBuilder.build();
                        Log.i("MainActivity:", "Launching Chrome Custom Tab.....");
                        customTabsIntent.launchUrl(MainActivity.this, uri);
                    }
                });*/
    }
    public void changeFragment(Fragment fragment){
        /*if(fragment.getClass() == FavouritesFragment.class){
            //drawerView.setCheckedItem(R.id.drawer_favourites);
            appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
            navigation = (BottomNavigationView) findViewById(R.id.bottom_nav);
            appBarLayout.setVisibility(VISIBLE);
            navigation.setVisibility(GONE);
        }else*/ if(fragment.getClass() == ResultsTabsFragment.class){
            //drawerView.setCheckedItem(R.id.drawer_results);
            navigation.setSelectedItemId(R.id.bottom_nav_results);
        }else if(fragment.getClass() ==  CategoriesFragment.class){
            navigation.setSelectedItemId(R.id.bottom_nav_categories);
        }else if(fragment.getClass() == EventsFragment.class){
            navigation.setSelectedItemId(R.id.bottom_nav_events);
        }else{
            Log.i(TAG, "changeFragment: Unexpected fragment passed!!");
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_left).replace(R.id.main_frame_layout, fragment);
        transaction.commit();
    }

    private void loadAllFromInternet(){
        loadResultsFromInternet();
        loadEventsFromInternet();
        loadSchedulesFromInternet();
        loadCategoriesFromInternet();
    }
    private void loadEventsFromInternet() {

        Call<EventsListModel> eventsCall = APIClient.getAPIInterface().getEventsList();
        eventsCall.enqueue(new Callback<EventsListModel>() {
            @Override
            public void onResponse(Call<EventsListModel> call, Response<EventsListModel> response) {
                if (response.isSuccess() && response.body() != null && mDatabase != null) {
                    Log.d(TAG, "onResponse: Loading events....");
                    mDatabase.beginTransaction();
                    mDatabase.where(EventDetailsModel.class).findAll().deleteAllFromRealm();
                    mDatabase.copyToRealm(response.body().getEvents());
                    mDatabase.commitTransaction();
                    Log.d(TAG,"Events updated in background");
                }
            }
            @Override
            public void onFailure(Call<EventsListModel> call, Throwable t) {
                Log.d(TAG, "onFailure: Events not updated ");
            }
        });
    }
    private void loadSchedulesFromInternet() {
        Call<ScheduleListModel> schedulesCall = APIClient.getAPIInterface().getScheduleList();
        schedulesCall.enqueue(new Callback<ScheduleListModel>() {
            @Override
            public void onResponse(Call<ScheduleListModel> call, Response<ScheduleListModel> response) {
                if (response.isSuccess() && response.body() != null && mDatabase != null) {
                    mDatabase.beginTransaction();
                    mDatabase.where(ScheduleModel.class).findAll().deleteAllFromRealm();
                    mDatabase.copyToRealm(response.body().getData());
                    mDatabase.commitTransaction();
                    Log.d(TAG,"Schedule updated in background");
                }
            }
            @Override
            public void onFailure(Call<ScheduleListModel> call, Throwable t) {
                Log.d(TAG, "onFailure: Schedules not updated ");
            }
        });
    }

    private void loadCategoriesFromInternet() {
        Call<CategoriesListModel> categoriesCall = APIClient.getAPIInterface().getCategoriesList();
        categoriesCall.enqueue(new Callback<CategoriesListModel>() {
            @Override
            public void onResponse(Call<CategoriesListModel> call, Response<CategoriesListModel> response) {
                if (response.isSuccess() && response.body() != null && mDatabase != null) {
                    mDatabase.beginTransaction();
                    mDatabase.where(CategoryModel.class).findAll().deleteAllFromRealm();
                    //mDatabase.copyToRealmOrUpdate(response.body().getCategoriesList());
                    mDatabase.copyToRealm(response.body().getCategoriesList());
                    //mDatabase.where(CategoryModel.class).equalTo("categoryName", "minimilitia").or().equalTo("categoryName", "Mini Militia").or().equalTo("categoryName", "Minimilitia").or().equalTo("categoryName", "MiniMilitia").or().equalTo("categoryName", "MINIMILITIA").or().equalTo("categoryName", "MINI MILITIA").findAll().deleteAllFromRealm();
                    mDatabase.commitTransaction();
                    Log.d(TAG,"Categories updated in background");
                }
            }
            @Override
            public void onFailure(Call<CategoriesListModel> call, Throwable t) {
                Log.d(TAG, "onFailure: Categories not updated");
            }
        });
    }
    private void loadResultsFromInternet(){
        Call<ResultsListModel> resultsCall = APIClient.getAPIInterface().getResultsList();
        resultsCall.enqueue(new Callback<ResultsListModel>() {
            @Override
            public void onResponse(Call<ResultsListModel> call, Response<ResultsListModel> response) {
                if (response.isSuccess() && response.body() != null && mDatabase != null) {
                    mDatabase.beginTransaction();
                    mDatabase.where(ResultModel.class).findAll().deleteAllFromRealm();
                    mDatabase.copyToRealm(response.body().getData());
                    mDatabase.commitTransaction();
                    Log.d(TAG, "Results updated in the background");
                }
            }
            @Override
            public void onFailure(Call<ResultsListModel> call, Throwable t) {
                Log.d(TAG, "onFailure: Results not updated");
            }
        });
    }
}
