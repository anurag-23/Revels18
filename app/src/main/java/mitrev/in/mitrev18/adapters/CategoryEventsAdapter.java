package mitrev.in.mitrev18.adapters;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import mitrev.in.mitrev18.R;
import mitrev.in.mitrev18.models.events.EventDetailsModel;
import mitrev.in.mitrev18.models.events.EventModel;
import mitrev.in.mitrev18.models.events.ScheduleModel;
import mitrev.in.mitrev18.models.favorites.FavouritesModel;
import mitrev.in.mitrev18.receivers.NotificationReceiver;
import mitrev.in.mitrev18.utilities.IconCollection;
import mitrev.in.mitrev18.views.TabbedDialog;


public class CategoryEventsAdapter extends RecyclerView.Adapter<CategoryEventsAdapter.CategoryEventsViewHolder> {
    private String TAG = "CategoryEventsAdapter";
    private List<EventModel> eventsList;
    private final int PRE_REVELS_DAY_ZERO = 18;
    private final int EVENT_DAY_ZERO = 6;
    private final int PRE_REVELS_EVENT_MONTH = Calendar.FEBRUARY;
    private final int EVENT_MONTH = Calendar.MARCH;
    private PendingIntent pendingIntent1 = null;
    private PendingIntent pendingIntent2 = null;
    boolean isRevels;
    private FragmentActivity activity;
    private Context context;
    EventModel event;

    private Realm realm = Realm.getDefaultInstance();
    private RealmResults<FavouritesModel> favouritesRealm = realm.where(FavouritesModel.class).findAll();
    private List<FavouritesModel> favourites = realm.copyFromRealm(favouritesRealm);


    public CategoryEventsAdapter(List<EventModel> eventsList, FragmentActivity activity, Context context, boolean isRevels) {
        this.eventsList = eventsList;
        this.activity = activity;
        this.context = context;
        this.isRevels=isRevels;
    }

    @Override
    public CategoryEventsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CategoryEventsViewHolder(LayoutInflater.from(activity).inflate(R.layout.category_event, parent, false));
    }

    @Override
    public void onBindViewHolder(final CategoryEventsViewHolder holder, int position) {
        event = eventsList.get(position);

        holder.eventName.setText(event.getEventName());
        holder.eventTime.setText(event.getStartTime());
        IconCollection icons = new IconCollection();
        holder.eventLogo.setImageResource(icons.getIconResource(activity, event.getCatName()));
        holder.eventRound.setVisibility(View.VISIBLE);

        if (event.getRound() != null && !event.getRound().equals("-") && !event.getRound().equals("")) {
            if (event.getRound().toLowerCase().charAt(0) == 'r')
                holder.eventRound.setText(event.getRound().toUpperCase());
            else
                holder.eventRound.setText("R" + event.getRound().toUpperCase().charAt(0));
        } else {
            holder.eventRound.setVisibility(View.GONE);
        }
    }
    @Override
    public int getItemCount() {
        return eventsList.size();
    }
    public boolean isFavourite(EventModel event){
        FavouritesModel favourite = realm.where(FavouritesModel.class).equalTo("id", event.getEventId()).equalTo("day",event.getDay()).equalTo("round" ,event.getRound()).findFirst();
        if(favourite!=null){
            return true;
        }

        return false;
    }

    public class CategoryEventsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView eventLogo;
        TextView eventName;
        TextView eventTime;
        FrameLayout logoFrame;
        TextView eventRound;

        public CategoryEventsViewHolder(View itemView) {
            super(itemView);

            eventLogo = (ImageView) itemView.findViewById(R.id.cat_event_logo_image_view);
            eventName = (TextView) itemView.findViewById(R.id.cat_event_name_text_view);
            eventTime = (TextView) itemView.findViewById(R.id.cat_event_time_text_view);
            logoFrame = (FrameLayout) itemView.findViewById(R.id.fav_event_logo_frame);
            eventRound = (TextView) itemView.findViewById(R.id.cat_event_round_text_view);

            itemView.setOnClickListener(this);
            Log.d(TAG, "CategoryEventsViewHolder: set listener ");
        }

        @Override
        public void onClick(View view) {

            /*final EventModel event = eventsList.get(getLayoutPosition());
            final Context context = view.getContext();
            Log.d(TAG, "onClick: s");
            displayEventDialog(event);*/

            final EventModel event = eventsList.get(getLayoutPosition());
            final Context context = view.getContext();
            final Dialog dialog = new Dialog(context);
            TabbedDialog td = new TabbedDialog();
            final String eventID = event.getEventId();
            final String dayOfEvent= event.getDay();
            final EventDetailsModel schedule = realm.where(EventDetailsModel.class).equalTo("eventID",eventID).findFirst();
            Log.d(TAG, "onClick: Using schedule"+schedule.getDay());
                ScheduleModel eventSchedule = realm.where(ScheduleModel.class).equalTo("eventID", eventID).equalTo("day", dayOfEvent).findFirst();
                TabbedDialog.EventFragment.DialogFavouriteClickListener fcl = new TabbedDialog.EventFragment.DialogFavouriteClickListener() {
                    @Override
                    public void onItemClick(boolean add) {
                        //TODO: App crashes when snackbar is displayed(Currently commented out).Fix crash

                        if (add) {
                            addFavourite(event);
                            //Snackbar.make(view, event.getEventName()+" Added to Favourites", Snackbar.LENGTH_LONG).show();
                        } else {
                            removeFavourite(event);
                            //Snackbar.make(view, event.getEventName()+" removed from Favourites", Snackbar.LENGTH_LONG).show();
                        }
                        notifyDataSetChanged();
                    }
                };
                td.setValues(eventSchedule, fcl, isFavourite(event), schedule);
                td.show(activity.getSupportFragmentManager(), "tag");
        }
    }
    private void addFavourite(EventModel event){
        FavouritesModel favourite = new FavouritesModel();
        //Get Corresponding EventDetailsModel from Realm
        //EventDetailsModel eventDetails = realm.where(EventDetailsModel.class).equalTo("eventID",eventSchedule.getEventID()).findFirst();
        //Create and Set Values for FavouritesModel
        ScheduleModel sm = realm.where(ScheduleModel.class).equalTo("eventID", event.getEventId()).equalTo("day",event.getDay()).equalTo("date", event.getDate()).findFirst();
        favourite.setId(event.getEventId());
        favourite.setCatID(event.getCatId());
        favourite.setEventName(event.getEventName());
        favourite.setRound(event.getRound());
        favourite.setVenue(event.getVenue());
        favourite.setDate(event.getDate());
        favourite.setDay(event.getDay());
        favourite.setStartTime(event.getStartTime());
        favourite.setEndTime(event.getEndTime());
        favourite.setParticipants(event.getEventMaxTeamNumber());
        favourite.setContactName(event.getContactName());
        favourite.setContactNumber(event.getContactNumber());
        favourite.setCatName(event.getCatName());
        favourite.setDescription(event.getDescription());
        favourite.setIsRevels(sm.getIsRevels());
        //Commit to Realm
        realm.beginTransaction();
        realm.copyToRealm(favourite);
        realm.commitTransaction();
        addNotification(event,sm.getIsRevels());
        favourites.add(favourite);
    }
    private void removeFavourite(EventModel event){
        realm.beginTransaction();
        realm.where(FavouritesModel.class).equalTo("eventName", event.getEventName()).equalTo("day", event.getDay()).findAll().deleteAllFromRealm();
        realm.commitTransaction();
        removeNotification(event);
    }
    private boolean favouritesContainsEvent(ScheduleModel eventSchedule){
        FavouritesModel favourite = realm.where(FavouritesModel.class).equalTo("id", event.getEventId()).equalTo("day",event.getDay()).equalTo("round" ,event.getRound()).findFirst();
        if(favourite!=null){
            return true;
        }

        return false;
    }
    private void addNotification(EventModel event, String isRevelsSTR) {
        Intent intent = new Intent(activity, NotificationReceiver.class);
        intent.putExtra("eventName", event.getEventName());
        intent.putExtra("startTime", event.getStartTime());
        intent.putExtra("eventVenue", event.getVenue());
        intent.putExtra("eventID", event.getEventId());
        intent.putExtra("catName", event.getCatName());
        Log.i("CategoryEventsAdapter", "addNotification: " + event.getStartTime());
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        //Request Codes
        int RC1 = Integer.parseInt(event.getCatId() + event.getEventId() + "0");
        int RC2 = Integer.parseInt(event.getCatId() + event.getEventId() + "1");
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
        if (isRevelsSTR.contains("1")) {
            int eventDate = EVENT_DAY_ZERO + Integer.parseInt(event.getDay());   //event dates start from 07th March
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(d);
            calendar1.set(Calendar.MONTH, EVENT_MONTH);
            calendar1.set(Calendar.YEAR, 2018);
            calendar1.set(Calendar.DATE, eventDate);
            calendar1.set(Calendar.SECOND, 0);
            long eventTimeInMillis = calendar1.getTimeInMillis();
            calendar1.set(Calendar.HOUR_OF_DAY, calendar1.get(Calendar.HOUR_OF_DAY) - 1);

            Calendar calendar2 = Calendar.getInstance();
            Log.d("Calendar 1", calendar1.getTimeInMillis() + "");
            Log.d("Calendar 2", calendar2.getTimeInMillis() + "");

            if (calendar2.getTimeInMillis() <= eventTimeInMillis)
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(), pendingIntent1);

            Calendar calendar3 = Calendar.getInstance();
            calendar3.set(Calendar.SECOND, 0);
            calendar3.set(Calendar.MINUTE, 30);
            calendar3.set(Calendar.HOUR, 8);
            calendar3.set(Calendar.AM_PM, Calendar.AM);
            calendar3.set(Calendar.MONTH, Calendar.MARCH);
            calendar3.set(Calendar.YEAR, 2018);
            calendar3.set(Calendar.DATE, eventDate);
            Log.d("Calendar 3", calendar3.getTimeInMillis() + "");
            if (calendar2.getTimeInMillis() < calendar3.getTimeInMillis()) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar3.getTimeInMillis(), pendingIntent2);

                Log.d("Alarm", "set for " + calendar3.toString());
            }
        }
        else{
            Log.d(TAG, "addNotification: pre Revels");
            int eventDate = PRE_REVELS_DAY_ZERO + Integer.parseInt(event.getDay());   //event dates start from 19th February
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(d);
            calendar1.set(Calendar.MONTH, PRE_REVELS_EVENT_MONTH);
            calendar1.set(Calendar.YEAR, 2018);
            calendar1.set(Calendar.DATE, eventDate);
            calendar1.set(Calendar.SECOND, 0);
            long eventTimeInMillis = calendar1.getTimeInMillis();
            calendar1.set(Calendar.HOUR_OF_DAY, calendar1.get(Calendar.HOUR_OF_DAY) - 1);

            Calendar calendar2 = Calendar.getInstance();
            Log.d("Calendar 1", calendar1.getTimeInMillis() + "");
            Log.d("Calendar 2", calendar2.getTimeInMillis() + "");

            if (calendar2.getTimeInMillis() <= eventTimeInMillis)
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(), pendingIntent1);

            Calendar calendar3 = Calendar.getInstance();
            calendar3.set(Calendar.SECOND, 0);
            calendar3.set(Calendar.MINUTE, 30);
            calendar3.set(Calendar.HOUR, 8);
            calendar3.set(Calendar.AM_PM, Calendar.AM);
            calendar3.set(Calendar.MONTH, Calendar.FEBRUARY);
            calendar3.set(Calendar.YEAR, 2018);
            calendar3.set(Calendar.DATE, eventDate);
            Log.d("Calendar 3", calendar3.getTimeInMillis() + "");
            if (calendar2.getTimeInMillis() < calendar3.getTimeInMillis()) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar3.getTimeInMillis(), pendingIntent2);

                Log.d("Alarm", "set for " + calendar3.toString());
            }
        }
    }
    private void removeNotification(EventModel event){
        Intent intent = new Intent(activity, NotificationReceiver.class);
        intent.putExtra("eventName", event.getEventName());
        intent.putExtra("startTime", event.getStartTime());
        intent.putExtra("eventVenue", event.getVenue());
        intent.putExtra("eventID", event.getEventId());
        Log.i(TAG, "removeNotification: "+event.getStartTime());
        AlarmManager alarmManager = (AlarmManager)activity.getSystemService(Context.ALARM_SERVICE);
        //Request Codes
        int RC1 = Integer.parseInt(event.getCatId()+event.getEventId()+"0");
        int RC2 = Integer.parseInt(event.getCatId()+event.getEventId()+"1");
        pendingIntent1 = PendingIntent.getBroadcast(activity, RC1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        pendingIntent2 = PendingIntent.getBroadcast(activity, RC2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent1);
        alarmManager.cancel(pendingIntent2);
    }
    private ScheduleModel getScheduleFromEvent(EventModel event){
        ScheduleModel s = realm.where(ScheduleModel.class).equalTo("eventID", event.getEventId()).findFirst();
        return s;
    }
}
