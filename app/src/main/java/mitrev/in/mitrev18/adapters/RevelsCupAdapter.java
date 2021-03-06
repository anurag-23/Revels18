package mitrev.in.mitrev18.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import mitrev.in.mitrev18.R;
import mitrev.in.mitrev18.models.events.RevelsCupEventModel;

/**
 * Created by skvrahul on 14/2/18.
 */

public class RevelsCupAdapter extends RecyclerView.Adapter<RevelsCupAdapter.EventViewHolder> {
    private List<RevelsCupEventModel> eventScheduleList;
    public RevelsCupAdapter(List<RevelsCupEventModel> eventScheduleList){
        this.eventScheduleList=eventScheduleList;
    }
    @Override
    public RevelsCupAdapter.EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_revels_cup_event, parent, false);
        return new RevelsCupAdapter.EventViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        RevelsCupEventModel event = eventScheduleList.get(position);
        holder.onBind(event);
    }
    @Override
    public int getItemCount() {
        return eventScheduleList.size();
    }
    public class EventViewHolder extends RecyclerView.ViewHolder{
        public TextView eventName, eventVenue, eventTime, teamOne, teamTwo, eventDate;
        public View container;
        public EventViewHolder(View view){
            super(view);
            eventName = (TextView) view.findViewById(R.id.rc_name_text_view);
            eventVenue  = (TextView) view.findViewById(R.id.rc_venue_text_view);
            eventTime = (TextView)view.findViewById(R.id.rc_time_text_view);
            eventDate = (TextView)view.findViewById(R.id.rc_date_text_view);
            teamOne = (TextView)view.findViewById(R.id.rc_team_1);
            teamTwo = (TextView)view.findViewById(R.id.rc_team_2);

        }
        public void onBind(final RevelsCupEventModel event){
            eventName.setText(event.getSportName());
            eventVenue.setText(event.getVenue());
            eventTime.setText(event.getTime());
            eventDate.setText(event.getDate());
            teamOne.setText(event.getTeam1());
            teamTwo.setText(event.getTeam2());
        }
    }
}
