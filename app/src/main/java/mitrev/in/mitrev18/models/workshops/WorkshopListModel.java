package mitrev.in.mitrev18.models.workshops;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by skvrahul on 16/2/18.
 */

public class WorkshopListModel {
    @SerializedName("data")
    @Expose
    private List<WorkshopModel> data = new ArrayList<>();

    public List<WorkshopModel> getWorkshopsList() {
        return data;
    }

    public void setWorkshopsList(List<WorkshopModel> data) {
        this.data = data;
    }
}
