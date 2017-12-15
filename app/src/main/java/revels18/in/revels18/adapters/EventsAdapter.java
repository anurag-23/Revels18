package revels18.in.revels18.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import revels18.in.revels18.R;
import revels18.in.revels18.models.events.EventModel;
import revels18.in.revels18.models.events.ScheduleModel;

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
        this.eventScheduleList = eventScheduleList;
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
    public class EventViewHolder extends RecyclerView.ViewHolder{
        public TextView eventName, eventVenue, eventTime, eventRound;
        public ImageView eventIcon, favIcon;
        public void onBind(final ScheduleModel event,final EventClickListener eventClickListener, final EventLongPressListener eventLongPressListener, final FavouriteClickListener favouriteListener){
            eventName.setText(event.getEventName());
            eventTime.setText(event.getStartTime() + " - " + event.getEndTime());
            eventVenue.setText(event.getVenue());
            eventRound.setText(event.getStartTime());
            //TODO: Check DB If event is a favourite and change favIcon accordingly
            favIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Favourites Clicked
                    String favTag = favIcon.getTag().toString();
                    if(favTag.equals("deselected")){
                        favIcon.setTag("selected");
                        favIcon.setImageResource(R.drawable.ic_fav_selected);
                        favouriteListener.onItemClick(event, true);
                    }else{
                        favIcon.setTag("deselected");
                        favIcon.setImageResource(R.drawable.ic_fav_deselected);
                        favouriteListener.onItemClick(event, false);
                    }
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

        }
    }
}
