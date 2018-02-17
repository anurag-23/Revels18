package revels18.in.revels18.models.registration;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by anurag on 17/2/18.
 */
public class RegEvent {
    @Expose
    @SerializedName("event_name")
    private String eventName;

    @Expose
    @SerializedName("team_id")
    private String teamID;

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getTeamID() {
        return teamID;
    }

    public void setTeamID(String teamID) {
        this.teamID = teamID;
    }
}
