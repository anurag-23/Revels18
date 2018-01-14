package revels18.in.revels18.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
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

import java.util.List;

import io.realm.Realm;
import revels18.in.revels18.R;
import revels18.in.revels18.models.events.EventDetailsModel;
import revels18.in.revels18.models.events.ScheduleModel;
import revels18.in.revels18.models.favorites.FavouritesModel;

/**
 * Created by skvrahul on 9/12/17.
 */

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder>{
    String TAG = "EventsAdapter";
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
        //TODO: Add notification for favourite event
    }
    public void removeFavourite(ScheduleModel event){
        realm.beginTransaction();
        realm.where(FavouritesModel.class).equalTo("id",event.getEventID()).equalTo("day",event.getDay()).equalTo("round", event.getRound()).findAll().deleteAllFromRealm();
        realm.commitTransaction();
        //TODO: Remove Notification
    }

    private void displayEventDialog(final ScheduleModel event, Context context){
        final View view = View.inflate(context, R.layout.activity_event_dialogue, null);
        final Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(true);
        final String eventID = event.getEventID();
        EventDetailsModel schedule = realm.where(EventDetailsModel.class).equalTo("eventID",eventID).findFirst();
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
            eventRound.setText(event.getStartTime());
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

