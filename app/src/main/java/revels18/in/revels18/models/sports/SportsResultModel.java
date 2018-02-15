package revels18.in.revels18.models.sports;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;

/**
 * Created by Saptarshi on 2/15/2018.
 */

public class SportsResultModel {
    public String eventName;
    public String eventRound;
    public String eventCatID;
    public List<SportsModel> eventResultsList = new ArrayList<>();
}
