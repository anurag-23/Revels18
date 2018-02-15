package revels18.in.revels18.adapters;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import revels18.in.revels18.models.events.EventDetailsModel;
import revels18.in.revels18.models.events.ScheduleModel;
import revels18.in.revels18.models.favorites.FavouritesModel;
import revels18.in.revels18.models.workshops.WorkshopModel;
import revels18.in.revels18.receivers.NotificationReceiver;
import revels18.in.revels18.utilities.IconCollection;
import revels18.in.revels18.views.TabbedDialog;

/**
 * Created by skvrahul on 9/12/17.
 */

public class WorkshopsAdapter extends RecyclerView.Adapter<WorkshopsAdapter.EventViewHolder>{
    String TAG = "WorkshopsAdapter";
    //TODO: Change EVENT_DAY_ZERO and EVENT_MONTH
    private final int EVENT_DAY_ZERO = 03;
    private final int EVENT_MONTH = Calendar.OCTOBER;
    private PendingIntent pendingIntent1 = null;
    private PendingIntent pendingIntent2 = null;
    private Activity activity;
    private List<WorkshopModel> eventScheduleList;
    private final EventClickListener eventListener;
    Realm realm = Realm.getDefaultInstance();
    public interface EventClickListener {
        void onItemClick(ScheduleModel event, View view);
    }
    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(itemView);
    }
    public void updateList(List<WorkshopModel> workshopModelList){
        this.eventScheduleList.clear();
        this.eventScheduleList.addAll(eventScheduleList);
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        WorkshopModel event = eventScheduleList.get(position);
        holder.onBind(event,eventListener);
    }

    @Override
    public int getItemCount() {
        return eventScheduleList.size();
    }
    public WorkshopsAdapter(Activity activity, List<WorkshopModel> events, EventClickListener eventListener){
        this.eventScheduleList = events;
        this.eventListener = eventListener;
        this.activity = activity;
    }
    private void displayEventDialog(final WorkshopModel event, Context context){
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View workshop_view = inflater.inflate(R.layout.dialog_workshop, null, false);
        TextView name =(TextView) workshop_view.findViewById(R.id.workshop_name);
        TextView date = (TextView) workshop_view.findViewById(R.id.workshop_date);
        TextView time = (TextView) workshop_view.findViewById(R.id.workshop_time);
        TextView venue = (TextView) workshop_view.findViewById(R.id.workshop_venue);
        TextView contact_name = (TextView) workshop_view.findViewById(R.id.workshop_contact_name);
        TextView contact_number = (TextView) workshop_view.findViewById(R.id.workshop_contact);
        TextView description = (TextView) workshop_view.findViewById(R.id.workshop_description);
        name.setText(event.getName());
        contact_name.setText(event.getCatName());
        contact_number.setText(event.getCatNo());
        venue.setText(event.getVenue());
        time.setText(event.getStartTime()+" - "+event.getEndTime());
        date.setText(event.getDate());
        description.setText(event.getDesc());
        dialog.setContentView(workshop_view);
        contact_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(event.getCatNo()==null || event.getCatNo()=="")
                    return;
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + event.getCatNo()));
                activity.startActivity(intent);
            }
        });
        dialog.show();
    }
    public class EventViewHolder extends RecyclerView.ViewHolder{
        public TextView eventName, eventVenue, eventTime, eventRound;
        public ImageView eventIcon, favIcon;
        public View container;
        public void onBind(final WorkshopModel event,final EventClickListener eventClickListener){
            favIcon.setVisibility(View.GONE);
            eventName.setText(event.getName());
            eventTime.setText(event.getStartTime() + " - " + event.getEndTime());
            eventVenue.setText(event.getVenue());
            IconCollection icons = new IconCollection();
            eventRound.setText(event.getDate());
            if(event.getCatName()!=null)
                eventIcon.setImageResource(icons.getIconResource(activity, event.getCatName()));

            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "onClick: Event clicked"+event.getName());
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

