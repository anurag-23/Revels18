package mitrev.in.mitrev18.models.events;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by skvrahul on 15/2/18.
 */

public class RevelsCupEventsListModel {
    @SerializedName("data")
    @Expose
    private List<RevelsCupEventModel> events;

    public RevelsCupEventsListModel() {
    }

    public List<RevelsCupEventModel> getEvents() {
        return events;
    }

    public void setEvents(List<RevelsCupEventModel> events) {
        this.events = events;
    }
}
