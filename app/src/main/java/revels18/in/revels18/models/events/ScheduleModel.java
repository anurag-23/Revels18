package revels18.in.revels18.models.events;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class ScheduleModel extends RealmObject {

    @SerializedName("eid")
    @Expose
    private String eventID;
    @SerializedName("ename")
    @Expose
    private String eventName;
    @SerializedName("catid")
    @Expose
    private String catID;
    @SerializedName("catname")
    @Expose
    private String catName;
    @SerializedName("round")
    @Expose
    private String round;
    @SerializedName("venue")
    @Expose
    private String venue;
    @SerializedName("stime")
    @Expose
    private String startTime;
    @SerializedName("etime")
    @Expose
    private String endTime;
    @SerializedName("day")
    @Expose
    private String day;
    @SerializedName("date")
    @Expose
    private String date;

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


    public String getCatID() {
        return catID;
    }

    public void setCatID(String catID) {
        this.catID = catID;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }

    public String getVenue() {
        return venue.toUpperCase();
    }

    public void setVenue(String venue) {
        this.venue = venue.toUpperCase();
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}