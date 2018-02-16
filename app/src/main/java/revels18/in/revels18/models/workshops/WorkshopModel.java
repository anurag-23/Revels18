package revels18.in.revels18.models.workshops;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by skvrahul on 16/2/18.
 */






public class WorkshopModel extends RealmObject{
    @SerializedName("cnumb")
    @Expose
    private String catNo;

    @SerializedName("wname")
    @Expose
    private String name;

    @PrimaryKey
    @SerializedName("wid")
    @Expose
    private String ID;

    @SerializedName("wdesc")
    @Expose
    private String desc;

    @SerializedName("wshuru")
    @Expose
    private String startTime;

    @SerializedName("wcost")
    @Expose
    private String cost;

    @SerializedName("wdate")
    @Expose
    private String date;

    @SerializedName("wvenue")
    @Expose
    private String venue;

    @SerializedName("cname")
    @Expose
    private String catName;

    @SerializedName("wkhatam")
    @Expose
    private String endTime;

    public String getCatNo() {
        return catNo;
    }

    public void setCatNo(String catNo) {
        this.catNo = catNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}




