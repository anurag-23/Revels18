package revels18.in.revels18.adapters;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import revels18.in.revels18.R;
import revels18.in.revels18.Receivers.NotificationReceiver;
import revels18.in.revels18.models.events.EventDetailsModel;
import revels18.in.revels18.models.events.ScheduleModel;
import revels18.in.revels18.models.favorites.FavouritesModel;

/**
 * Created by Saptarshi on 12/24/2017.
 */

public class HomeEventsAdapter extends RecyclerView.Adapter<HomeEventsAdapter.EventViewHolder> {

    private List<ScheduleModel> events;
    private final EventClickListener eventListener;
    private Context context;
    Activity activity;
    private Realm mDatabase = Realm.getDefaultInstance();
    private RealmResults<FavouritesModel> favouritesRealm = mDatabase.where(FavouritesModel.class).findAll();
    private List<FavouritesModel> favourites = mDatabase.copyFromRealm(favouritesRealm);
    private PendingIntent pendingIntent1 = null;
    private PendingIntent pendingIntent2 = null;
    private final int EVENT_DAY_ZERO = 03;
    private final int EVENT_MONTH = Calendar.OCTOBER;
    public interface EventClickListener {
        void onItemClick(ScheduleModel event);
    }
    public HomeEventsAdapter(List<ScheduleModel> events, EventClickListener eventListener, Activity activity) {
        this.events = events;
        this.activity = activity;
        this.eventListener = eventListener;
    }
    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favourite_event, parent, false);
        return new EventViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        ScheduleModel event = events.get(position);
        holder.onBind(event);
        //IconCollection icons = new IconCollection();
        //holder.eventLogo.setImageResource(icons.getIconResource(activity, event.getCatName()));
    }
    @Override
    public int getItemCount() {
        return events.size();
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {
        public ImageView eventLogo;
        public TextView eventRound;
        public TextView eventName;
        public TextView eventTime;
        public RelativeLayout eventItem;
        public EventViewHolder(View view) {
            super(view);
            eventLogo = (ImageView) view.findViewById(R.id.fav_event_logo_image_view);
            eventRound = (TextView) view.findViewById(R.id.fav_event_round_text_view);
            eventName = (TextView) view.findViewById(R.id.fav_event_name_text_view);
            eventTime = (TextView) view.findViewById(R.id.fav_event_time_text_view);
            eventItem = (RelativeLayout)view.findViewById(R.id.fav_event_item);

        }
        public void onBind(final ScheduleModel event) {
            eventName.setText(event.getEventName());
            eventRound.setText("R" + event.getRound());
            eventTime.setText(event.getStartTime()+" - "+ event.getEndTime());
            eventTime.setVisibility(View.GONE);
            eventItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(eventListener!=null){
                        eventListener.onItemClick(event);
                    }
                    displayBottomSheet(event);
                }
            });
        }
        private void displayBottomSheet(final ScheduleModel event){
            final View view = View.inflate(context, R.layout.activity_event_dialogue, null);
            final BottomSheetDialog dialog = new BottomSheetDialog(context);
            final String eventID = event.getEventID();

            EventDetailsModel schedule = mDatabase.where(EventDetailsModel.class).equalTo("eventID",eventID).findFirst();
            ImageView eventLogo1 = (ImageView) view.findViewById(R.id.event_logo_image_view);
            //IconCollection icons = new IconCollection();
            //eventLogo1.setImageResource(icons.getIconResource(activity, event.getCatName()));
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
                }
            });
            if(favouritesContainsEvent(event)){
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
            }
            TextView eventCategory = (TextView)view.findViewById(R.id.event_category);
            eventCategory.setText(event.getCatName());

            ImageView deleteIcon = (ImageView)view.findViewById(R.id.event_delete_icon);
            deleteIcon.setVisibility(View.GONE);
            dialog.setContentView(view);
            Snackbar.make(view.getRootView().getRootView(),"Swipe up for more", Snackbar.LENGTH_SHORT).show();
            dialog.show();
        }
        private void addFavourite(ScheduleModel eventSchedule){
            FavouritesModel favourite = new FavouritesModel();
            //Get Corresponding EventDetailsModel from Realm
            EventDetailsModel eventDetails = mDatabase.where(EventDetailsModel.class).equalTo("eventID",eventSchedule.getEventID()).findFirst();
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
            favourite.setParticipants(eventDetails.getMaxTeamSize());
            favourite.setContactName(eventDetails.getContactName());
            favourite.setContactNumber(eventDetails.getContactNo());
            favourite.setCatName(eventDetails.getCatName());
            favourite.setDescription(eventDetails.getDescription());
            //Commit to Realm
            mDatabase.beginTransaction();
            mDatabase.copyToRealm(favourite);
            mDatabase.commitTransaction();
            addNotification(eventSchedule);
            favourites.add(favourite);
        }
        private void removeFavourite(ScheduleModel eventSchedule){
            mDatabase.beginTransaction();
            mDatabase.where(FavouritesModel.class).equalTo("id",eventSchedule.getEventID()).equalTo("day",eventSchedule.getDay()).findAll().deleteAllFromRealm();
            mDatabase.commitTransaction();

            for(int i=0;i<favourites.size();i++){
                //Removing corresponding FavouritesModel from favourites
                FavouritesModel favourite = favourites.get(i);
                if((favourite.getId().equals(eventSchedule.getEventID()))&&(favourite.getDay().equals(eventSchedule.getDay()))){
                    favourites.remove(favourite);
                }
            }
            removeNotification(eventSchedule);
        }
        private boolean favouritesContainsEvent(ScheduleModel eventSchedule){
            for(FavouritesModel favourite : favourites){
                //Checking if Corresponding Event exists
                if((favourite.getId().equals(eventSchedule.getEventID()))&&(favourite.getDay().equals(eventSchedule.getDay()))){
                    return true;
                }
            }
            return false;
        }
        private void addNotification(ScheduleModel event){
            Intent intent = new Intent(activity, NotificationReceiver.class);
            intent.putExtra("eventName", event.getEventName());
            intent.putExtra("startTime", event.getStartTime());
            intent.putExtra("eventVenue", event.getVenue());
            intent.putExtra("eventID", event.getEventID());
            intent.putExtra("catName", event.getCatName());
            Log.i("HomeEventsAdapter", "addNotification: "+event.getStartTime());
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
    }
}