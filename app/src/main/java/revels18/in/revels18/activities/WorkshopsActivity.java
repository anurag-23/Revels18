package revels18.in.revels18.activities;

import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

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
    LinearLayout noData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workshops);
        setTitle(R.string.workshops);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        RealmResults<WorkshopModel> realmResults = realm.where(WorkshopModel.class).findAll().sort("date");
        workshopList = realm.copyFromRealm(realmResults);
        WorkshopsAdapter.EventClickListener eventClickListener = new WorkshopsAdapter.EventClickListener() {
            @Override
            public void onItemClick(ScheduleModel event, View view) {

            }
        };
        adapter = new WorkshopsAdapter(this, workshopList, eventClickListener);
        RecyclerView rv = (RecyclerView) findViewById(R.id.workshop_recycler_view);
        noData=(LinearLayout)findViewById(R.id.no_workshop_data_layout);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(layoutManager);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setAdapter(adapter);
        if(workshopList.isEmpty()){
            rv.setVisibility(View.INVISIBLE);
            noData.setVisibility(View.VISIBLE);
        }
        else{
            rv.setVisibility(View.VISIBLE);
            noData.setVisibility(View.INVISIBLE);
        }
        adapter.notifyDataSetChanged();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:{
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
