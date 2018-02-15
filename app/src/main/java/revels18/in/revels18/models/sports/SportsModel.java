package revels18.in.revels18.models.sports;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by Saptarshi on 2/15/2018.
 */

public class SportsModel extends RealmObject {

    @SerializedName("teamid")
    @Expose
    private String teamID;
    @SerializedName("catid")
    @Expose
    private String catID;
    @SerializedName("eveid")
    @Expose
    private String eventID;
    @SerializedName("evename")
    @Expose
    private String eventName;
    @SerializedName("roundno")
    @Expose
    private String round;
    @SerializedName("position")
    @Expose
    private String position;
    @SerializedName("teamsize")
    @Expose
    private String teamSize;
    @SerializedName("day")
    @Expose
    private String day;

    public String getTeamID() {
        return teamID;
    }

    public void setTeamID(String teamID) {
        this.teamID = teamID;
    }

    public String getCatID() {
        return catID;
    }

    public void setCatID(String catID) {
        this.catID = catID;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getTeamSize() {
        return teamSize;
    }

    public void setTeamSize(String teamSize) {
        this.teamSize = teamSize;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
