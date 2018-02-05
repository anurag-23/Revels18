package revels18.in.revels18.adapters;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import revels18.in.revels18.R;
import revels18.in.revels18.Receivers.NotificationReceiver;
import revels18.in.revels18.models.events.EventDetailsModel;
import revels18.in.revels18.models.events.ScheduleModel;
import revels18.in.revels18.models.favorites.FavouritesModel;

/**
 * Created by skvrahul on 9/12/17.
 */

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder>{
    String TAG = "EventsAdapter";
    //TODO: Change EVENT_DAY_ZERO and EVENT_MONTH
    private final int EVENT_DAY_ZERO = 03;
    private final int EVENT_MONTH = Calendar.OCTOBER;
    private PendingIntent pendingIntent1 = null;
    private PendingIntent pendingIntent2 = null;
    private Activity activity;
    private List<ScheduleModel> eventScheduleList;
    private final EventClickListener eventListener;
    private final FavouriteClickListener favouriteListener;
    private final EventLongPressListener eventLongPressListener;
    Realm realm = Realm.getDefaultInstance();
    public interface EventClickListener {
        void onItemClick(ScheduleModel event, View view);
    }
    public interface FavouriteClickListener {
        void onItemClick(ScheduleModel event, boolean add);
    }

    public interface EventLongPressListener{
        void onItemLongPress(ScheduleModel event);
    }
    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(itemView);
    }
    public void updateList(List<ScheduleModel> eventScheduleList){
        this.eventScheduleList.clear();
        this.eventScheduleList.addAll(eventScheduleList);
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        ScheduleModel event = eventScheduleList.get(position);
        holder.onBind(event,eventListener, eventLongPressListener,favouriteListener);
    }

    @Override
    public int getItemCount() {
        return eventScheduleList.size();
    }
    public EventsAdapter(Activity activity, List<ScheduleModel> events, EventClickListener eventListener, EventLongPressListener eventLongPressListener, FavouriteClickListener favouriteListener){
        this.eventScheduleList = events;
        this.eventListener = eventListener;
        this.favouriteListener = favouriteListener;
        this.eventLongPressListener=eventLongPressListener;
        this.activity = activity;
    }
    public boolean isFavourite(ScheduleModel event){
        FavouritesModel favourite = realm.where(FavouritesModel.class).equalTo("id", event.getEventID()).equalTo("day",event.getDay()).equalTo("round" ,event.getRound()).findFirst();
        if(favourite!=null){
            return true;
        }

        return false;
    }
    private void addFavourite(ScheduleModel eventSchedule){
        FavouritesModel favourite = new FavouritesModel();
        Log.i(TAG, "addFavourite: "+eventSchedule.getEventID());
        //Get Corresponding EventDetailsModel from Realm
        EventDetailsModel eventDetails = realm.where(EventDetailsModel.class).equalTo("eventID",eventSchedule.getEventID()).equalTo("day", eventSchedule.getDay()).findFirst();
        //Create and Set Values for FavouritesModel
        favourite.setId(eventSchedule.getEventID());
        favourite.setCatID(eventSchedule.getCatID());
        favourite.setEventName(eventSchedule.getEventName());
        favourite.setRound(eventSchedule.getRound());
        favourite.setVenue(eventSchedule.getVenue());
        favourite.setDate(eventSchedule.getDate());
        favourite.setDay(eventSchedule.getDay());
        favourite.setStartTime(eventSchedule.getStartTime());
        favourite.setEndTime(eventSchedule.getEndTime());
        if(eventDetails!=null) {
            favourite.setParticipants(eventDetails.getMaxTeamSize());
            favourite.setContactName(eventDetails.getContactName());
            favourite.setContactNumber(eventDetails.getContactNo());
            favourite.setCatName(eventDetails.getCatName());
            favourite.setDescription(eventDetails.getDescription());
        }
        //Commit to Realm
        if(realm!=null){
            realm.beginTransaction();
            realm.copyToRealm(favourite);
            realm.commitTransaction();
        }
        addNotification(eventSchedule);

    }
    public void removeFavourite(ScheduleModel event){
        realm.beginTransaction();
        realm.where(FavouritesModel.class).equalTo("id",event.getEventID()).equalTo("day",event.getDay()).equalTo("round", event.getRound()).findAll().deleteAllFromRealm();
        realm.commitTransaction();
        removeNotification(event);
    }
    private void addNotification(ScheduleModel event){
        Intent intent = new Intent(activity, NotificationReceiver.class);
        intent.putExtra("eventName", event.getEventName());
        intent.putExtra("startTime", event.getStartTime());
        intent.putExtra("eventVenue", event.getVenue());
        intent.putExtra("eventID", event.getEventID());
        intent.putExtra("catName", event.getCatName());
        Log.i(TAG, "addNotification: "+event.getStartTime());
        AlarmManager alarmManager = (AlarmManager)activity.getSystemService(Context.ALARM_SERVICE);
        //Request Codes
        int RC1 = Integer.parseInt(event.getCatID()+event.getEventID()+"0");
        int RC2 = Integer.parseInt(event.getCatID()+event.getEventID()+"1");
        pendingIntent1 = PendingIntent.getBroadcast(activity, RC1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        pendingIntent2 = PendingIntent.getBroadcast(activity, RC2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa", Locale.US);
        Date d = null;
        try {
            d = sdf.parse(event.getStartTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }
        int eventDate = EVENT_DAY_ZERO + Integer.parseInt(event.getDay());   //event dates start from 04th October
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(d);
        calendar1.set(Calendar.MONTH,EVENT_MONTH);
        calendar1.set(Calendar.YEAR, 2017);
        calendar1.set(Calendar.DATE, eventDate);
        calendar1.set(Calendar.SECOND, 0);
        long eventTimeInMillis = calendar1.getTimeInMillis();
        calendar1.set(Calendar.HOUR_OF_DAY, calendar1.get(Calendar.HOUR_OF_DAY)-1);

        Calendar calendar2 = Calendar.getInstance();
        Log.d("Calendar 1", calendar1.getTimeInMillis()+"");
        Log.d("Calendar 2", calendar2.getTimeInMillis()+"");

        if(calendar2.getTimeInMillis() <= eventTimeInMillis)
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(), pendingIntent1);

        Calendar calendar3 = Calendar.getInstance();
        calendar3.set(Calendar.SECOND, 0);
        calendar3.set(Calendar.MINUTE, 30);
        calendar3.set(Calendar.HOUR, 8);
        calendar3.set(Calendar.AM_PM, Calendar.AM);
        calendar3.set(Calendar.MONTH, Calendar.SEPTEMBER);
        calendar3.set(Calendar.YEAR, 2017);
        calendar3.set(Calendar.DATE, eventDate);
        Log.d("Calendar 3", calendar3.getTimeInMillis()+"");
        if (calendar2.getTimeInMillis() < calendar3.getTimeInMillis()){
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar3.getTimeInMillis(), pendingIntent2);

            Log.d("Alarm", "set for "+calendar3.toString());
        }
    }
    private void removeNotification(ScheduleModel event){
        Intent intent = new Intent(activity, NotificationReceiver.class);
        intent.putExtra("eventName", event.getEventName());
        intent.putExtra("startTime", event.getStartTime());
        intent.putExtra("eventVenue", event.getVenue());
        intent.putExtra("eventID", event.getEventID());

        AlarmManager alarmManager = (AlarmManager)activity.getSystemService(Context.ALARM_SERVICE);
        //Request Codes
        int RC1 = Integer.parseInt(event.getCatID()+event.getEventID()+"0");
        int RC2 = Integer.parseInt(event.getCatID()+event.getEventID()+"1");
        pendingIntent1 = PendingIntent.getBroadcast(activity, RC1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        pendingIntent2 = PendingIntent.getBroadcast(activity, RC2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent1);
        alarmManager.cancel(pendingIntent2);
    }

    private void displayEventDialog(final ScheduleModel event, Context context){
        final View view = View.inflate(context, R.layout.activity_event_dialogue, null);
        final Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(true);
        final String eventID = event.getEventID();
        final EventDetailsModel schedule = realm.where(EventDetailsModel.class).equalTo("eventID",eventID).findFirst();
        ImageView eventLogo1 = (ImageView) view.findViewById(R.id.event_logo_image_view);
        //TODO: Add Icons for the event logo
//        IconCollection icons = new IconCollection();
//        eventLogo1.setImageResource(icons.getIconResource(activity, event.getCatName()));
        final ImageView favIcon = (ImageView) view.findViewById(R.id.event_fav_icon);
        favIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //FavIcon Clicked
                if(favIcon.getTag().equals("deselected")) {
                    favIcon.setImageResource(R.drawable.ic_fav_selected);
                    favIcon.setTag("selected");
                    addFavourite(event);
                    Snackbar.make(v.getRootView(), event.getEventName()+" Added to Favourites", Snackbar.LENGTH_LONG).show();
                }else{
                    favIcon.setImageResource(R.drawable.ic_fav_deselected);
                    favIcon.setTag("deselected");
                    removeFavourite(event);
                    Snackbar.make(v.getRootView(), event.getEventName()+" removed from Favourites", Snackbar.LENGTH_LONG).show();
                }
                notifyDataSetChanged();
            }
        });
        if(isFavourite(event)){
            favIcon.setImageResource(R.drawable.ic_fav_selected);
            favIcon.setTag("selected");
        }else{
            favIcon.setImageResource(R.drawable.ic_fav_deselected);
            favIcon.setTag("deselected");
        }
        final TextView eventName = (TextView)view.findViewById(R.id.event_name);
        eventName.setText(event.getEventName());

        TextView eventRound = (TextView)view.findViewById(R.id.event_round);
        eventRound.setText(event.getRound());

        TextView eventDate = (TextView)view.findViewById(R.id.event_date);
        eventDate.setText(event.getDate());

        TextView eventTime = (TextView)view.findViewById(R.id.event_time);
        eventTime.setText(event.getStartTime() + " - " + event.getEndTime());

        TextView eventVenue = (TextView)view.findViewById(R.id.event_venue);
        eventVenue.setText(event.getVenue());
        if(schedule !=null){
            TextView eventTeamSize = (TextView)view.findViewById(R.id.event_team_size);
            eventTeamSize.setText(schedule.getMaxTeamSize());

            TextView eventDescription = (TextView)view.findViewById(R.id.event_description);
            eventDescription.setText(schedule.getDescription());

            TextView eventContactName = (TextView) view.findViewById(R.id.event_contact_name);
            eventContactName.setText(schedule.getContactName() + " : ");

            TextView eventContact = (TextView) view.findViewById(R.id.event_contact);
            eventContact.setText(  schedule.getContactNo());
            eventContact.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            eventContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + schedule.getContactNo()));
                    activity.startActivity(intent);
                }
            });
        }
        TextView eventCategory = (TextView)view.findViewById(R.id.event_category);
        eventCategory.setText(event.getCatName());

        ImageView deleteIcon = (ImageView)view.findViewById(R.id.event_delete_icon);
        deleteIcon.setVisibility(View.GONE);
        dialog.setContentView(view);
        //Snackbar.make(view.getRootView().getRootView(),"Swipe up for more", Snackbar.LENGTH_SHORT).show();
        dialog.show();
    }
    public class EventViewHolder extends RecyclerView.ViewHolder{
        public TextView eventName, eventVenue, eventTime, eventRound;
        public ImageView eventIcon, favIcon;
        public View container;
        public void onBind(final ScheduleModel event,final EventClickListener eventClickListener, final EventLongPressListener eventLongPressListener, final FavouriteClickListener favouriteListener){
            eventName.setText(event.getEventName());
            eventTime.setText(event.getStartTime() + " - " + event.getEndTime());
            eventVenue.setText(event.getVenue());
            eventRound.setText("R".concat(event.getRound()));
            if(isFavourite(event)){
                favIcon.setImageResource(R.drawable.ic_fav_selected);
                favIcon.setTag("selected");
            }else{
                favIcon.setImageResource(R.drawable.ic_fav_deselected);
                favIcon.setTag("deselected");
            }
            favIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Favourites Clicked
                    String favTag = favIcon.getTag().toString();
                    if(favTag.equals("deselected")){
                        favIcon.setTag("selected");
                        favIcon.setImageResource(R.drawable.ic_fav_selected);
                        addFavourite(event);
                        favouriteListener.onItemClick(event, true);
                    }else{
                        favIcon.setTag("deselected");
                        favIcon.setImageResource(R.drawable.ic_fav_deselected);
                        removeFavourite(event);
                        favouriteListener.onItemClick(event, false);
                    }
                }
            });
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "onClick: Event clicked"+event.getEventName());
                    displayEventDialog(event, view.getContext());

                }
            });
        }
        public EventViewHolder(View view){
            super(view);
            eventIcon = (ImageView)view.findViewById(R.id.event_logo_image_view);
            favIcon = (ImageView)view.findViewById(R.id.event_fav_ico);
            eventName = (TextView)view.findViewById(R.id.event_name_text_view);
            eventVenue = (TextView)view.findViewById(R.id.event_venue_text_view);
            eventTime = (TextView)view.findViewById(R.id.event_time_text_view);
            eventRound = (TextView)view.findViewById(R.id.event_round_text_view);
            container = view.findViewById(R.id.event_item_relative_layout);
        }
    }

}

