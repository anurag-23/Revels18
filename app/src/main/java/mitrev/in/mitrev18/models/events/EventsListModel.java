package mitrev.in.mitrev18.models.events;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class EventsListModel {
    @SerializedName("data")
    @Expose
    private List<EventDetailsModel> events;

    public EventsListModel() {
    }

    public List<EventDetailsModel> getEvents() {
        return events;
    }

    public void setEvents(List<EventDetailsModel> events) {
        this.events = events;
    }
}

