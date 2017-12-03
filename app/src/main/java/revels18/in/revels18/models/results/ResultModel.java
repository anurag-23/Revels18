package revels18.in.revels18.models.results;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;


public class ResultModel extends RealmObject {

    @SerializedName("tid")
    @Expose
    private String teamID;
    @SerializedName("cat")
    @Expose
    private String catName;
    @SerializedName("eve")
    @Expose
    private String eventName;
    @SerializedName("round")
    @Expose
    private String round;
    @SerializedName("pos")
    @Expose
    private String position;

    public String getTeamID() {
        return teamID;
    }

    public void setTeamID(String teamID) {
        this.teamID = teamID;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
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

}
