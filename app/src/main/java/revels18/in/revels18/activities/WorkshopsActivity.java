package revels18.in.revels18.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import revels18.in.revels18.R;
import revels18.in.revels18.adapters.EventsAdapter;
import revels18.in.revels18.adapters.WorkshopsAdapter;
import revels18.in.revels18.models.events.ScheduleModel;
import revels18.in.revels18.models.workshops.WorkshopModel;

public class WorkshopsActivity extends AppCompatActivity {
    Realm realm = Realm.getDefaultInstance();
    List<WorkshopModel> workshopList = new ArrayList<>();
    WorkshopsAdapter adapter = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workshops);
        RealmResults<WorkshopModel> realmResults = realm.where(WorkshopModel.class).findAll().sort("date");
        workshopList = realm.copyFromRealm(realmResults);
        WorkshopsAdapter.EventClickListener eventClickListener = new WorkshopsAdapter.EventClickListener() {
            @Override
            public void onItemClick(ScheduleModel event, View view) {

            }
        };
        adapter = new WorkshopsAdapter(this, workshopList, eventClickListener);
        RecyclerView rv = (RecyclerView) findViewById(R.id.workshop_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(layoutManager);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(adapter);
    }
}
