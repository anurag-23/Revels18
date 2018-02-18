package mitrev.in.mitrev18.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import io.realm.Realm;
import mitrev.in.mitrev18.models.categories.CategoriesListModel;
import mitrev.in.mitrev18.models.categories.CategoryModel;
import mitrev.in.mitrev18.models.events.EventDetailsModel;
import mitrev.in.mitrev18.models.events.EventsListModel;
import mitrev.in.mitrev18.models.events.ScheduleListModel;
import mitrev.in.mitrev18.models.events.ScheduleModel;
import mitrev.in.mitrev18.models.results.ResultModel;
import mitrev.in.mitrev18.models.results.ResultsListModel;
import mitrev.in.mitrev18.models.sports.SportsListModel;
import mitrev.in.mitrev18.models.sports.SportsModel;
import mitrev.in.mitrev18.network.APIClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import mitrev.in.mitrev18.R;

public class SplashActivity extends AppCompatActivity {

    boolean isWiFi;
    boolean isConnected;
    public Runnable test;
    private RelativeLayout rootLayout;
    private Realm mDatabase;
    boolean dataAvailableLocally;
    boolean animationEnded=false;
    boolean animationStarted=false;
    int i = 0;
    private int counter = 0;
    private int apiCallsRecieved = 0;
    private Context context = this;
    private Handler mHandler = new Handler();
    private boolean eventsDataAvailableLocally = false;
    private boolean schedulesDataAvailableLocally = false;
    private boolean categoriesDataAvailableLocally = false;
    private String TAG ="SplashActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mDatabase = Realm.getDefaultInstance();
        rootLayout = (RelativeLayout) findViewById(R.id.splash_root_layout);
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        dataAvailableLocally = sharedPref.getBoolean("dataAvailableLocally",false);
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (isConnected){
            isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
        }
        final ImageView iconLeft = (ImageView) findViewById(R.id.splash_left_revels_icon);
        final ImageView iconRight = (ImageView) findViewById(R.id.splash_right_revels_icon);
        final ImageView text = (ImageView) findViewById(R.id.splash_revels_text);
        final FrameLayout container = (FrameLayout)findViewById(R.id.frameLayout4);


        iconLeft.startAnimation(AnimationUtils.loadAnimation(SplashActivity.this,R.anim.fade_in_first));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                iconRight.setVisibility(View.VISIBLE);
                iconRight.startAnimation(AnimationUtils.loadAnimation(SplashActivity.this,R.anim.fade_in_first));
            }
        }, 500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                text.setVisibility(View.VISIBLE);
                text.startAnimation(AnimationUtils.loadAnimation(SplashActivity.this,R.anim.fade_in_first));
            }
        }, 1000);

        Animation animation = AnimationUtils.loadAnimation(SplashActivity.this,R.anim.fade_in_first);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Log.d(TAG, "Splash:  onAnimationEnd: Splash animation Started");
                animationStarted=true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d(TAG, "Splash:  onAnimationEnd: Splash animation Ended");
                animationEnded=true;
            if (dataAvailableLocally){
                Log.d(TAG,"Data avail local");

                if(isConnected){
                    Log.d(TAG,"Is connected");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Snackbar.make(rootLayout, "Updating data", Snackbar.LENGTH_SHORT).show();

                            //loadAllFromInternet();
                            moveForward();
                        }
                    }, 1500);
                }

                else{Log.d(TAG,"not connected");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            moveForward();
                        }
                    }, 1500);
                }

            }
            else{
                Log.d("Splash","Data not avail local");
                if (!isConnected){
                    Log.d(TAG,"not connected");
                    final LinearLayout noConnectionLayout = (LinearLayout)findViewById(R.id.splash_no_connection_layout);
                    Button retry = (Button)noConnectionLayout.findViewById(R.id.retry);
                    noConnectionLayout.setVisibility(View.VISIBLE);
                    iconLeft.setVisibility(View.GONE);
                    iconRight.setVisibility(View.GONE);
                    text.setVisibility(View.GONE);
                    container.setVisibility(View.GONE);
                    retry.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ConnectivityManager cmTemp = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo activeNetworkTemp = cmTemp.getActiveNetworkInfo();
                            boolean isConnectedTemp = activeNetworkTemp != null && activeNetworkTemp.isConnectedOrConnecting();

                            if (isConnectedTemp){
                                noConnectionLayout.setVisibility(View.GONE);
                                iconLeft.setVisibility(View.VISIBLE);
                                iconRight.setVisibility(View.VISIBLE);
                                text.setVisibility(View.VISIBLE);
                                container.setVisibility(View.VISIBLE);
                                Snackbar.make(rootLayout, "Loading data... takes a couple of seconds.", Snackbar.LENGTH_SHORT).show();
                                loadAllFromInternet();
                            }
                            else{
                                Snackbar.make(rootLayout, "Check connection!", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{Log.d(TAG," connected");
                    Snackbar.make(rootLayout, "Loading data... takes a couple of seconds.", Snackbar.LENGTH_SHORT).show();
                    loadAllFromInternet();
                }
            }
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        text.startAnimation(animation);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResumeSplash:  Called");
        if(animationStarted && !animationEnded )
        {
            Log.d(TAG, "onResumeSplash: freeze Helper called");
            freezeSplashHelper();
        }
    }

    private void freezeSplashHelper(){
        final ImageView iconLeft = (ImageView) findViewById(R.id.splash_left_revels_icon);
        final ImageView iconRight = (ImageView) findViewById(R.id.splash_right_revels_icon);
        final ImageView text = (ImageView) findViewById(R.id.splash_revels_text);
        final FrameLayout container = (FrameLayout)findViewById(R.id.frameLayout4);
        if (dataAvailableLocally){
            Log.d(TAG,"Data avail local");

            if(isConnected){
                Log.d(TAG,"Is connected");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Snackbar.make(rootLayout, "Updating data", Snackbar.LENGTH_SHORT).show();

                        //loadAllFromInternet();
                        moveForward();
                    }
                }, 1500);
            }

            else{Log.d(TAG,"not connected");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        moveForward();
                    }
                }, 1500);
            }

        }
        else{
            Log.d("Splash","Data not avail local");
            if (!isConnected){
                Log.d(TAG,"not connected");
                final LinearLayout noConnectionLayout = (LinearLayout)findViewById(R.id.splash_no_connection_layout);
                Button retry = (Button)noConnectionLayout.findViewById(R.id.retry);
                noConnectionLayout.setVisibility(View.VISIBLE);
                iconLeft.setVisibility(View.GONE);
                iconRight.setVisibility(View.GONE);
                text.setVisibility(View.GONE);
                container.setVisibility(View.GONE);
                retry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ConnectivityManager cmTemp = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo activeNetworkTemp = cmTemp.getActiveNetworkInfo();
                        boolean isConnectedTemp = activeNetworkTemp != null && activeNetworkTemp.isConnectedOrConnecting();

                        if (isConnectedTemp){
                            noConnectionLayout.setVisibility(View.GONE);
                            iconLeft.setVisibility(View.VISIBLE);
                            iconRight.setVisibility(View.VISIBLE);
                            text.setVisibility(View.VISIBLE);
                            container.setVisibility(View.VISIBLE);
                            Snackbar.make(rootLayout, "Loading data... takes a couple of seconds.", Snackbar.LENGTH_SHORT).show();
                            loadAllFromInternet();
                        }
                        else{
                            Snackbar.make(rootLayout, "Check connection!", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else{Log.d(TAG," connected");
                Snackbar.make(rootLayout, "Loading data... takes a couple of seconds.", Snackbar.LENGTH_SHORT).show();
                loadAllFromInternet();
            }
        }
    }
    private void loadAllFromInternet(){
        loadEventsFromInternet();
        loadSchedulesFromInternet();
        loadCategoriesFromInternet();
        loadResultsFromInternet();
        loadRevelsCupResultsFromInternet();

        test = new Runnable() {
            @Override
            public void run() {
                if (eventsDataAvailableLocally && schedulesDataAvailableLocally && categoriesDataAvailableLocally){
                    SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("dataAvailableLocally",true);
                    editor.apply();
                    if(!dataAvailableLocally){
                        moveForward();
                    }
                }
                if (!(eventsDataAvailableLocally && schedulesDataAvailableLocally && categoriesDataAvailableLocally)){
                    counter++;
                    if(apiCallsRecieved == 3){
                        if(i==0){
                            i = counter;}
                        Snackbar.make(rootLayout, "Error in retriving data. Some data may be outdated", Snackbar.LENGTH_SHORT).show();
                        if(counter-i == 1){
                            moveForward();
                        }
                    }
                    if (counter == 10 && !dataAvailableLocally){
                        if(eventsDataAvailableLocally || schedulesDataAvailableLocally || categoriesDataAvailableLocally){
                            Snackbar.make(rootLayout, "Possible slow internet connection", Snackbar.LENGTH_SHORT).show();
                        }
                        else{
                            Snackbar.make(rootLayout, "Server may be down. Please try again later", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                    mHandler.postDelayed(test,1500);
                }
            }
        };
        mHandler.postDelayed(test,1500);
    }

    private void moveForward(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    private void loadEventsFromInternet() {

        Call<EventsListModel> eventsCall = APIClient.getAPIInterface().getEventsList();
        eventsCall.enqueue(new Callback<EventsListModel>() {
            @Override
            public void onResponse(Call<EventsListModel> call, Response<EventsListModel> response) {
                if (response.isSuccess() && response.body() != null && mDatabase != null) {
                    apiCallsRecieved++;
                    Log.d(TAG, "onResponse: Loading events....");
                    mDatabase.beginTransaction();
                    mDatabase.where(EventDetailsModel.class).findAll().deleteAllFromRealm();
                    mDatabase.copyToRealm(response.body().getEvents());
                    mDatabase.commitTransaction();
                    eventsDataAvailableLocally=true;
                    Log.d(TAG,"Events");
                }
            }
            @Override
            public void onFailure(Call<EventsListModel> call, Throwable t) {
                apiCallsRecieved++;
            }
        });
    }
    private void loadSchedulesFromInternet() {
        Call<ScheduleListModel> schedulesCall = APIClient.getAPIInterface().getScheduleList();
        schedulesCall.enqueue(new Callback<ScheduleListModel>() {
            @Override
            public void onResponse(Call<ScheduleListModel> call, Response<ScheduleListModel> response) {
                if (response.isSuccess() && response.body() != null && mDatabase != null) {
                    apiCallsRecieved++;
                    mDatabase.beginTransaction();
                    mDatabase.where(ScheduleModel.class).findAll().deleteAllFromRealm();
                    mDatabase.copyToRealm(response.body().getData());
                    mDatabase.commitTransaction();
                    schedulesDataAvailableLocally=true;
                    Log.d(TAG,"Schedules");
                }
            }
            @Override
            public void onFailure(Call<ScheduleListModel> call, Throwable t) {
                apiCallsRecieved++;
            }
        });
    }

    private void loadCategoriesFromInternet() {
        Call<CategoriesListModel> categoriesCall = APIClient.getAPIInterface().getCategoriesList();
        categoriesCall.enqueue(new Callback<CategoriesListModel>() {
            @Override
            public void onResponse(Call<CategoriesListModel> call, Response<CategoriesListModel> response) {
                if (response.isSuccess() && response.body() != null && mDatabase != null) {
                    apiCallsRecieved++;
                    mDatabase.beginTransaction();
                    mDatabase.where(CategoryModel.class).findAll().deleteAllFromRealm();
                    mDatabase.copyToRealm(response.body().getCategoriesList());
                    mDatabase.commitTransaction();
                    categoriesDataAvailableLocally=true;
                    Log.d(TAG,"Categories");
                }
            }
            @Override
            public void onFailure(Call<CategoriesListModel> call, Throwable t) {
                apiCallsRecieved++;
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
                    Log.d(TAG,"Results");
                }
            }
            @Override
            public void onFailure(Call<ResultsListModel> call, Throwable t) {
            }
        });
    }
    private void loadRevelsCupResultsFromInternet() {
        Call<SportsListModel> call = APIClient.getAPIInterface().getSportsResults();
        call.enqueue(new Callback<SportsListModel>() {
            @Override
            public void onResponse(Call<SportsListModel> call, Response<SportsListModel> response) {
                if (response.body() != null && mDatabase != null){
                    mDatabase.beginTransaction();
                    mDatabase.where(SportsModel.class).findAll().deleteAllFromRealm();
                    mDatabase.copyToRealm(response.body().getData());
                    mDatabase.commitTransaction();
                }
            }

            @Override
            public void onFailure(Call<SportsListModel> call, Throwable t) {
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}