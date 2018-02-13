package revels18.in.revels18.activities;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import revels18.in.revels18.R;
import revels18.in.revels18.Receivers.NotificationReceiver;
import revels18.in.revels18.adapters.FavouritesEventsAdapter;
import revels18.in.revels18.models.events.EventDetailsModel;
import revels18.in.revels18.models.events.EventModel;
import revels18.in.revels18.models.favorites.FavouritesModel;

public class FavouritesActivity extends AppCompatActivity {
    String TAG = "FavouritesActivity";
    private Realm realm = Realm.getDefaultInstance();
    private List<FavouritesModel> favouritesDay1 =  new ArrayList<>();
    private List<FavouritesModel> favouritesDay2 =  new ArrayList<>();
    private List<FavouritesModel> favouritesDay3 =  new ArrayList<>();
    private List<FavouritesModel> favouritesDay4 =  new ArrayList<>();
    RecyclerView recyclerViewDay1;
    RecyclerView recyclerViewDay2;
    RecyclerView recyclerViewDay3;
    RecyclerView recyclerViewDay4;
    private TextView noEventsDay1;
    private TextView noEventsDay2;
    private TextView noEventsDay3;
    private TextView noEventsDay4;
    private TextView eventsClearDay1;
    private TextView eventsClearDay2;
    private TextView eventsClearDay3;
    private TextView eventsClearDay4;
    private FavouritesEventsAdapter adapterDay1;
    private FavouritesEventsAdapter adapterDay2;
    private FavouritesEventsAdapter adapterDay3;
    private FavouritesEventsAdapter adapterDay4;
    Context context;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.favourites);
        setContentView(R.layout.activity_favourites);
        context=this;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getSupportActionBar().setTitle(R.string.drawer_favourites);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                AppBarLayout appBarLayout = (AppBarLayout)findViewById(R.id.app_bar);
                appBarLayout.setElevation(0);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        favouritesDay1 = realm.copyFromRealm( realm.where(FavouritesModel.class).equalTo("day","1").findAll());
        favouritesDay2 = realm.copyFromRealm( realm.where(FavouritesModel.class).equalTo("day","2").findAll());
        favouritesDay3 = realm.copyFromRealm( realm.where(FavouritesModel.class).equalTo("day","3").findAll());
        favouritesDay4 = realm.copyFromRealm( realm.where(FavouritesModel.class).equalTo("day","4").findAll());
        displayEvents();
        eventsClearDay1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                clearFavouriteCard(1);
            }
        });
        eventsClearDay2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                clearFavouriteCard(2);
            }
        });
        eventsClearDay3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                clearFavouriteCard(3);
            }
        });
        eventsClearDay4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                clearFavouriteCard(4);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favourites_fragment, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){

            case R.id.action_delete_all:{
                new AlertDialog.Builder(context)
                        .setTitle("Delete Favourites")
                        .setMessage("Are you sure you want to delete all favourites?")
                        .setIcon(R.drawable.ic_delete_all_dialog)
                        .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int favSize1 = favouritesDay1.size();
                                int favSize2 = favouritesDay2.size();
                                int favSize3 = favouritesDay3.size();
                                int favSize4 = favouritesDay4.size();
                                removeNotifications(favouritesDay1);
                                removeNotifications(favouritesDay2);
                                removeNotifications(favouritesDay3);
                                removeNotifications(favouritesDay4);
                                favouritesDay1.clear();
                                favouritesDay2.clear();
                                favouritesDay3.clear();
                                favouritesDay4.clear();
                                if(adapterDay1!=null){
                                    adapterDay1.notifyItemRangeRemoved(0,favSize1);
                                }
                                if(adapterDay2!=null){
                                    adapterDay2.notifyItemRangeRemoved(0,favSize2);
                                }
                                if(adapterDay3!=null){
                                    adapterDay3.notifyItemRangeRemoved(0,favSize3);
                                }
                                if(adapterDay4!=null){
                                    adapterDay4.notifyItemRangeRemoved(0,favSize4);
                                }
                                displayEvents();
                                updateRealm();
                            }
                        })
                        .setNegativeButton(R.string.dialog_no,null).show();
                return true;
            }

            case android.R.id.home:{
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    public void displayEvents(){

        recyclerViewDay1 = (RecyclerView)findViewById(R.id.favourites_day_1_recycler_view);
        recyclerViewDay2 = (RecyclerView)findViewById(R.id.favourites_day_2_recycler_view);
        recyclerViewDay3 = (RecyclerView)findViewById(R.id.favourites_day_3_recycler_view);
        recyclerViewDay4 = (RecyclerView)findViewById(R.id.favourites_day_4_recycler_view);

        noEventsDay1 = (TextView)findViewById(R.id.fav_day_1_no_events);
        noEventsDay2 = (TextView)findViewById(R.id.fav_day_2_no_events);
        noEventsDay3 = (TextView)findViewById(R.id.fav_day_3_no_events);
        noEventsDay4 = (TextView)findViewById(R.id.fav_day_4_no_events);

        eventsClearDay1= (TextView)findViewById(R.id.favourites_events_clear_1);
        eventsClearDay2= (TextView)findViewById(R.id.favourites_events_clear_2);
        eventsClearDay3= (TextView)findViewById(R.id.favourites_events_clear_3);
        eventsClearDay4= (TextView)findViewById(R.id.favourites_events_clear_4);

        FavouritesEventsAdapter.EventClickListener eventListener = new  FavouritesEventsAdapter.EventClickListener(){
            @Override
            public void onItemClick(FavouritesModel event) {
                displayBottomSheet(event);
            }
        };
        if(favouritesDay1.isEmpty()){
            recyclerViewDay1.setVisibility(View.GONE);
            ((View)recyclerViewDay1.getParent()).setVisibility(View.GONE);
            noEventsDay1.setVisibility(View.VISIBLE);
            ((View)noEventsDay1.getParent()).setVisibility(View.VISIBLE);
        }else{
            adapterDay1 = new FavouritesEventsAdapter(favouritesDay1, eventListener,this);
            recyclerViewDay1.setAdapter(adapterDay1);
            recyclerViewDay1.setItemAnimator(new DefaultItemAnimator());
            recyclerViewDay1.setNestedScrollingEnabled(false);
            recyclerViewDay1.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        }
        if(favouritesDay2.isEmpty()){
            recyclerViewDay2.setVisibility(View.GONE);
            ((View)recyclerViewDay2.getParent()).setVisibility(View.GONE);
            noEventsDay2.setVisibility(View.VISIBLE);
            ((View)noEventsDay2.getParent()).setVisibility(View.VISIBLE);
        }else{
            adapterDay2 = new FavouritesEventsAdapter(favouritesDay2, eventListener,this);
            recyclerViewDay2.setAdapter(adapterDay2);
            recyclerViewDay2.setItemAnimator(new DefaultItemAnimator());
            recyclerViewDay2.setNestedScrollingEnabled(false);
            recyclerViewDay2.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        }
        if(favouritesDay3.isEmpty()){
            recyclerViewDay3.setVisibility(View.GONE);
            ((View)recyclerViewDay3.getParent()).setVisibility(View.GONE);
            noEventsDay3.setVisibility(View.VISIBLE);
            ((View)noEventsDay3.getParent()).setVisibility(View.VISIBLE);
        }else{
            adapterDay3 = new FavouritesEventsAdapter(favouritesDay3, eventListener,this);
            recyclerViewDay3.setAdapter(adapterDay3);
            recyclerViewDay3.setItemAnimator(new DefaultItemAnimator());
            recyclerViewDay3.setNestedScrollingEnabled(false);
            recyclerViewDay3.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        }
        if(favouritesDay4.isEmpty()){
            recyclerViewDay4.setVisibility(View.GONE);
            ((View)recyclerViewDay4.getParent()).setVisibility(View.GONE);
            noEventsDay4.setVisibility(View.VISIBLE);
            ((View)noEventsDay4.getParent()).setVisibility(View.VISIBLE);
        }else{
            adapterDay4 = new FavouritesEventsAdapter(favouritesDay4, eventListener,this);
            recyclerViewDay4.setAdapter(adapterDay4);
            recyclerViewDay4.setItemAnimator(new DefaultItemAnimator());
            recyclerViewDay4.setNestedScrollingEnabled(false);
            recyclerViewDay4.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        }
    }
    private void displayBottomSheet(final FavouritesModel event){
        final View view = View.inflate(this, R.layout.activity_event_dialogue, null);
        final Dialog dialog = new Dialog(this);
        Log.i("TT17", "displayBottomSheet: NEW!");
        final String eventID = event.getId();
        final EventDetailsModel schedule = realm.where(EventDetailsModel.class).equalTo("eventID",eventID).findFirst();

        ImageView eventLogo1 = (ImageView) view.findViewById(R.id.event_logo_image_view);
        ImageView favIcon = (ImageView) view.findViewById(R.id.event_fav_icon);

        favIcon.setVisibility(View.GONE);

        //IconCollection icons = new IconCollection();
        //eventLogo1.setImageResource(icons.getIconResource(getActivity(), event.getCatName()));

        final TextView eventName = (TextView)view.findViewById(R.id.event_name);
        eventName.setText(event.getEventName());

        TextView eventRound = (TextView)view.findViewById(R.id.event_round);
        eventRound.setText(event.getRound());

        TextView eventDate = (TextView)view.findViewById(R.id.event_date);
        eventDate.setText(event.getDate());

        TextView eventTime = (TextView)view.findViewById(R.id.event_time);
        if(!event.getEndTime().equals(""))
            eventTime.setText(event.getStartTime());
        else
            eventTime.setText(event.getStartTime());

        TextView eventVenue = (TextView)view.findViewById(R.id.event_venue);
        eventVenue.setText(event.getVenue());

        TextView eventTeamSize = (TextView)view.findViewById(R.id.event_team_size);
        eventTeamSize.setText(schedule.getMaxTeamSize());

        TextView eventCategory = (TextView)view.findViewById(R.id.event_category);
        eventCategory.setText(event.getCatName());

        TextView eventContactName = (TextView) view.findViewById(R.id.event_contact_name);
        eventContactName.setText(event.getContactName() + " : ");

        TextView eventContact = (TextView) view.findViewById(R.id.event_contact);
        eventContact.setText(  schedule.getContactNo());
        eventContact.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        eventContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + schedule.getContactNo()));
                startActivity(intent);
            }
        });

        TextView eventDescription = (TextView)view.findViewById(R.id.event_description);
        eventDescription.setText(schedule.getDescription());

        ImageView deleteIcon = (ImageView)view.findViewById(R.id.event_delete_icon);
        deleteIcon.setVisibility(View.VISIBLE);
        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int day = Integer.parseInt(event.getDay());
                switch (day){
                    case 1: int pos1 = favouritesDay1.indexOf(event);
                        favouritesDay1.remove(event);
                        adapterDay1.notifyItemRemoved(pos1);
                        displayEvents();
                        break;
                    case 2:  int pos2 = favouritesDay2.indexOf(event);
                        favouritesDay2.remove(event);
                        adapterDay2.notifyItemRemoved(pos2);
                        displayEvents();
                        break;
                    case 3: int pos3 = favouritesDay3.indexOf(event);
                        favouritesDay3.remove(event);
                        adapterDay3.notifyItemRemoved(pos3);
                        displayEvents();
                        break;
                    case 4:  int pos4 = favouritesDay4.indexOf(event);
                        favouritesDay4.remove(event);
                        adapterDay4.notifyItemRemoved(pos4);
                        displayEvents();
                        break;
                }
                Snackbar snackbar = Snackbar.make(view.getRootView().getRootView(),"Removed from Favourites:"+eventName,Snackbar.LENGTH_SHORT);
                snackbar.show();
                dialog.dismiss();
                updateRealm();
                removeNotification(event);
            }
        });
        dialog.setContentView(view);
        Snackbar.make(view.getRootView().getRootView(),"Swipe up for more", Snackbar.LENGTH_SHORT).show();
        dialog.show();
    }
    private void updateRealm(){
        realm.beginTransaction();
        realm.where(FavouritesModel.class).findAll().deleteAllFromRealm();
        realm.copyToRealm(favouritesDay1);
        realm.copyToRealm(favouritesDay2);
        realm.copyToRealm(favouritesDay3);
        realm.copyToRealm(favouritesDay4);
        realm.commitTransaction();
    }

    private void removeNotification(FavouritesModel event){
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("eventName", event.getEventName());
        intent.putExtra("startTime", event.getStartTime());
        intent.putExtra("eventVenue", event.getVenue());
        intent.putExtra("eventID", event.getId());

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        //Request Codes
        int RC1 = Integer.parseInt(event.getCatID()+event.getId()+"0");
        int RC2 = Integer.parseInt(event.getCatID()+event.getId()+"1");
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this, RC1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(this, RC2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent1);
        alarmManager.cancel(pendingIntent2);
    }
    private void removeNotifications(List<FavouritesModel> events){
        for(FavouritesModel event:events){
            removeNotification(event);
        }
    }
    private void clearFavouriteCard(final int id){
        new AlertDialog.Builder(context)
                .setTitle("Delete Favourites")
                .setMessage("Are you sure you want to delete all favourites from Day "+id+"?")
                .setIcon(R.drawable.ic_delete_all_dialog)
                .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(id){
                            case 1:
                                int favSize = favouritesDay1.size();
                                removeNotifications(favouritesDay1);
                                favouritesDay1.clear();
                                if(adapterDay1!=null){
                                    adapterDay1.notifyItemRangeRemoved(0,favSize);
                                }
                                displayEvents();
                                updateRealm();
                                break;
                            case 2:
                                favSize = favouritesDay2.size();
                                removeNotifications(favouritesDay2);
                                favouritesDay2.clear();
                                if(adapterDay2!=null){
                                    adapterDay2.notifyItemRangeRemoved(0,favSize);
                                }
                                displayEvents();
                                updateRealm();
                                break;
                            case 3:
                                favSize = favouritesDay3.size();
                                removeNotifications(favouritesDay3);
                                favouritesDay3.clear();
                                if(adapterDay3!=null){
                                    adapterDay3.notifyItemRangeRemoved(0,favSize);
                                }
                                displayEvents();
                                updateRealm();
                                break;
                            case 4:
                                favSize = favouritesDay4.size();
                                removeNotifications(favouritesDay4);
                                favouritesDay4.clear();
                                if(adapterDay4!=null){
                                    adapterDay4.notifyItemRangeRemoved(0,favSize);
                                }
                                displayEvents();
                                updateRealm();
                                break;
                        }
                    }
                })
                .setNegativeButton(R.string.dialog_no,null).show();
    }
}