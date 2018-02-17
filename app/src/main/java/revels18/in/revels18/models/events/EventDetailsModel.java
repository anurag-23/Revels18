package revels18.in.revels18.models.events;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;



public class EventDetailsModel extends RealmObject {

    @SerializedName("ename")
    @Expose
    private String eventName;
    @SerializedName("eid")
    @Expose
    private String eventID;
    @SerializedName("edesc")
    @Expose
    private String description;
    @SerializedName("emaxteamsize")
    @Expose
    private String maxTeamSize;
    @SerializedName("cid")
    @Expose
    private String catID;
    @SerializedName("cname")
    @Expose
    private String catName;
    @SerializedName("cntctname")
    @Expose
    private String contactName;
    @SerializedName("cntctno")
    @Expose
    private String contactNo;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("hash")
    @Expose
    private String hash;
    @SerializedName("day")
    @Expose
    private String day;

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMaxTeamSize() {
        return maxTeamSize;
    }

    public void setMaxTeamSize(String maxTeamSize) {
        this.maxTeamSize = maxTeamSize;
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

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

}

