package revels18.in.revels18.adapters;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
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
import revels18.in.revels18.R;
import revels18.in.revels18.Receivers.NotificationReceiver;
import revels18.in.revels18.models.events.EventDetailsModel;
import revels18.in.revels18.models.events.EventModel;
import revels18.in.revels18.models.events.ScheduleModel;
import revels18.in.revels18.models.favorites.FavouritesModel;


public class CategoryEventsAdapter extends RecyclerView.Adapter<CategoryEventsAdapter.CategoryEventsViewHolder> {

    private List<EventModel> eventsList;
    private final int EVENT_DAY_ZERO = 03;
    private final int EVENT_MONTH = Calendar.OCTOBER;
    private PendingIntent pendingIntent1 = null;
    private PendingIntent pendingIntent2 = null;
    private Activity activity;
    private Context context;
    EventModel event;
    private Realm realm = Realm.getDefaultInstance();
    private RealmResults<FavouritesModel> favouritesRealm = realm.where(FavouritesModel.class).findAll();
    private List<FavouritesModel> favourites = realm.copyFromRealm(favouritesRealm);


    public CategoryEventsAdapter(List<EventModel> eventsList, Activity activity, Context context) {
        this.eventsList = eventsList;
        this.activity = activity;
        this.context = context;
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
        //IconCollection icons = new IconCollection();
        //holder.eventLogo.setImageResource(icons.getIconResource(activity, event.getCatName()));
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
        }

        @Override
        public void onClick(View view) {

            final EventModel event = eventsList.get(getLayoutPosition());

            final Context context = view.getContext();
            view = View.inflate(context, R.layout.activity_event_dialogue, null);

            final BottomSheetDialog dialog = new BottomSheetDialog(context);

            ImageView eventLogo1 = (ImageView) view.findViewById(R.id.event_logo_image_view);

            //IconCollection icons = new IconCollection();
            //eventLogo1.setImageResource(icons.getIconResource(activity, event.getCatName()));

            TextView eventName = (TextView) view.findViewById(R.id.event_name);
            eventName.setText(event.getEventName());

            TextView eventRound = (TextView) view.findViewById(R.id.event_round);
            eventRound.setText(event.getRound());

            TextView eventDate = (TextView) view.findViewById(R.id.event_date);
            eventDate.setText(event.getDate());

            TextView eventTime = (TextView) view.findViewById(R.id.event_time);
            eventTime.setText(event.getStartTime() + " - " + event.getEndTime());

            TextView eventVenue = (TextView) view.findViewById(R.id.event_venue);
            eventVenue.setText(event.getVenue());

            TextView eventTeamSize = (TextView) view.findViewById(R.id.event_team_size);
            eventTeamSize.setText(event.getEventMaxTeamNumber());

            TextView eventCategory = (TextView) view.findViewById(R.id.event_category);
            eventCategory.setText(event.getCatName());

            TextView eventContactName = (TextView) view.findViewById(R.id.event_contact_name);
            eventContactName.setText(event.getContactName() + " : ");

            TextView eventContact = (TextView) view.findViewById(R.id.event_contact);
            eventContact.setText(event.getContactNumber());
            eventContact.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

            eventContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + event.getContactNumber()));
                    activity.startActivity(intent);
                }
            });
            TextView eventDescription = (TextView) view.findViewById(R.id.event_description);
            eventDescription.setText(event.getDescription());

            ImageView deleteIcon = (ImageView) view.findViewById(R.id.event_delete_icon);
            deleteIcon.setVisibility(View.GONE);

            final ImageView favIcon = (ImageView)view.findViewById(R.id.event_fav_icon);
            favIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
            if(favouritesContainsEvent(getScheduleFromEvent(event))){
                favIcon.setImageResource(R.drawable.ic_fav_selected);
                favIcon.setTag("selected");
            }else{
                favIcon.setImageResource(R.drawable.ic_fav_deselected);
                favIcon.setTag("deselected");
            }
            dialog.setContentView(view);
            Snackbar.make(view.getRootView().getRootView(),"Swipe up for more", Snackbar.LENGTH_SHORT).show();
            dialog.show();
        }
    }
    private void addFavourite(EventModel event){
        FavouritesModel favourite = new FavouritesModel();
        //Get Corresponding EventDetailsModel from Realm
        //EventDetailsModel eventDetails = realm.where(EventDetailsModel.class).equalTo("eventID",eventSchedule.getEventID()).findFirst();
        //Create and Set Values for FavouritesModel
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
        //Commit to Realm
        realm.beginTransaction();
        realm.copyToRealm(favourite);
        realm.commitTransaction();
        addNotification(event);
        favourites.add(favourite);
    }
    private void removeFavourite(EventModel event){
        realm.beginTransaction();
        realm.where(FavouritesModel.class).equalTo("eventName", event.getEventName()).equalTo("day", event.getDay()).findAll().deleteAllFromRealm();
        realm.commitTransaction();
        removeNotification(event);
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
    private void addNotification(EventModel event){
        Intent intent = new Intent(activity, NotificationReceiver.class);
        intent.putExtra("eventName", event.getEventName());
        intent.putExtra("startTime", event.getStartTime());
        intent.putExtra("eventVenue", event.getVenue());
        intent.putExtra("eventID", event.getEventId());
        intent.putExtra("catName", event.getCatName());
        Log.i("CategoryEventsAdapter", "addNotification: "+event.getStartTime());
        AlarmManager alarmManager = (AlarmManager)activity.getSystemService(Context.ALARM_SERVICE);
        //Request Codes
        int RC1 = Integer.parseInt(event.getCatId()+event.getEventId()+"0");
        int RC2 = Integer.parseInt(event.getCatId()+event.getEventId()+"1");
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
    private void removeNotification(EventModel event){
        Intent intent = new Intent(activity, NotificationReceiver.class);
        intent.putExtra("eventName", event.getEventName());
        intent.putExtra("startTime", event.getStartTime());
        intent.putExtra("eventVenue", event.getVenue());
        intent.putExtra("eventID", event.getEventId());

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
