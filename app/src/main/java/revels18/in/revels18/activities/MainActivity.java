package revels18.in.revels18.activities;

import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import revels18.in.revels18.R;
import revels18.in.revels18.application.Revels;
import revels18.in.revels18.fragments.AboutUsFragment;
import revels18.in.revels18.fragments.CategoriesFragment;
import revels18.in.revels18.fragments.DevelopersFragment;
import revels18.in.revels18.fragments.EventsFragment;
import revels18.in.revels18.fragments.FavouritesFragment;
import revels18.in.revels18.fragments.HomeFragment;
import revels18.in.revels18.fragments.OnlineEventsFragment;
import revels18.in.revels18.fragments.ResultsFragment;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity  {
    private FragmentManager fm;
    Fragment selectedFragment;
    private NavigationView drawerView;
    private BottomNavigationView navigation;
    private AppBarLayout appBarLayout;
    String TAG = "MainActivity";
    //String CCT_LAUNCH_URL = "https://www.techtatva.in";
    //private FirebaseRemoteConfig firebaseRemoteConfig;

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(selectedFragment.getClass()==OnlineEventsFragment.class) {
            drawerView.setCheckedItem(R.id.drawer_home);
            navigation = (BottomNavigationView) findViewById(R.id.bottom_nav);
            navigation.setVisibility(VISIBLE);
            appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
            appBarLayout.setVisibility(VISIBLE);
            navigation.setSelectedItemId(R.id.bottom_nav_home);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(Color.parseColor("#0d0d0d"));
            getWindow().setNavigationBarColor(Color.parseColor("#0d0d0d"));

        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        drawerView = (NavigationView) findViewById(R.id.nav_view);
        drawerView.setNavigationItemSelectedListener(mOnDrawerItemSelectedListener);
        drawerView.setCheckedItem(R.id.drawer_home);
        drawerView.setSelected(true);

        navigation = (BottomNavigationView) findViewById(R.id.bottom_nav);
        navigation.setOnNavigationItemSelectedListener(mOnBottomNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.bottom_nav_home);
        navigation.setSelected(true);

        fm = getSupportFragmentManager();

        /*firebaseRemoteConfig=FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .build();
        firebaseRemoteConfig.setConfigSettings(configSettings);*/
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
            }

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_from_top,R.anim.blank).replace(R.id.main_frame_layout, selectedFragment);
            transaction.commit();
            return true;
        }

    };

    private NavigationView.OnNavigationItemSelectedListener mOnDrawerItemSelectedListener
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
    };

    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            if (drawerView.getMenu().getItem(0).isChecked() &&  navigation.getMenu().getItem(0).isChecked() ){
                finishAffinity();
            }
            else{
                if(Revels.searchOpen ==1 && drawerView.getMenu().getItem(0).isChecked()){
                    fm.beginTransaction().replace(R.id.main_frame_layout, new CategoriesFragment()).commit();
                    Revels.searchOpen =0;
                }
                if(Revels.searchOpen ==2 && drawerView.getMenu().getItem(0).isChecked()){
                    fm.beginTransaction().replace(R.id.main_frame_layout, new EventsFragment()).commit();
                    Revels.searchOpen =0;
                }
                else{
                    fm.beginTransaction().replace(R.id.main_frame_layout, new HomeFragment()).commit();
                    drawerView.setCheckedItem(R.id.drawer_home);
                    navigation.setSelectedItemId(R.id.bottom_nav_home);
                }
                if(navigation.getVisibility()==GONE)
                { navigation.setVisibility(VISIBLE);}

                if(appBarLayout!=null)
                {appBarLayout.setVisibility(VISIBLE);}
            }
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
        if(fragment.getClass() == FavouritesFragment.class){
            drawerView.setCheckedItem(R.id.drawer_favourites);
            appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
            navigation = (BottomNavigationView) findViewById(R.id.bottom_nav);
            appBarLayout.setVisibility(VISIBLE);
            navigation.setVisibility(GONE);
        }else if(fragment.getClass() == ResultsFragment.class){
            drawerView.setCheckedItem(R.id.drawer_results);
            appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
            appBarLayout.setVisibility(VISIBLE);
            navigation = (BottomNavigationView) findViewById(R.id.bottom_nav);
            navigation.setVisibility(GONE);
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
}
