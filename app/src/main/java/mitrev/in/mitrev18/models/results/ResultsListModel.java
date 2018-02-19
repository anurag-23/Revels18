package mitrev.in.mitrev18.models.results;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class ResultsListModel {
    @SerializedName("data")
    @Expose
    private List<ResultModel> data = new ArrayList<ResultModel>();

    public List<ResultModel> getData() {
        return data;
    }

    public void setData(List<ResultModel> data) {
        this.data = data;
    }

}
