package revels18.in.revels18.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import revels18.in.revels18.R;

public class RegisterTeamActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_team);
        setTitle(R.string.register_team);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
