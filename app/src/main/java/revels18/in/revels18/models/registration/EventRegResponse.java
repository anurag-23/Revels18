package revels18.in.revels18.models.registration;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by anurag on 16/2/18.
 */
public class EventRegResponse {
    @Expose
    @SerializedName("status")
    private Integer status;

    @Expose
    @SerializedName("message")
    private String message;

    @Expose
    @SerializedName("event_name")
    private String eventName;

    @Expose
    @SerializedName("event_max_team_number")
    private String maxTeamSize;

    @Expose
    @SerializedName("present_team_size")
    private String curTeamSize;

    @Expose
    @SerializedName("team_id")
    private String teamID;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getMaxTeamSize() {
        return maxTeamSize;
    }

    public void setMaxTeamSize(String maxTeamSize) {
        this.maxTeamSize = maxTeamSize;
    }

    public String getCurTeamSize() {
        return curTeamSize;
    }

    public void setCurTeamSize(String curTeamSize) {
        this.curTeamSize = curTeamSize;
    }

    public String getTeamID() {
        return teamID;
    }

    public void setTeamID(String teamID) {
        this.teamID = teamID;
    }
}
