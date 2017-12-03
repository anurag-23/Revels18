package revels18.in.revels18.models.events;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class ScheduleListModel {

    @SerializedName("data")
    @Expose
    private List<ScheduleModel> data = new ArrayList<ScheduleModel>();

    public List<ScheduleModel> getData() {
        return data;
    }

    public void setData(List<ScheduleModel> data) {
        this.data = data;
    }

}