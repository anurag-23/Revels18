package mitrev.in.mitrev18.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import mitrev.in.mitrev18.R;
import mitrev.in.mitrev18.models.registration.EventRegResponse;
import mitrev.in.mitrev18.network.RegistrationClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterTeamActivity extends AppCompatActivity {
    private static final int ADD_TEAM_MEMBER = 0;
    private CardView userCard;
    private Button addTeamMate;
    private TextView teamID;
    private TextView maxTeamSize;
    private TextView curTeamSize;
    private int maxSize;
    private int curSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_team);
        setTitle(R.string.register_team);

        Intent intent = getIntent();

        int status = intent.getIntExtra("status", 0);
        maxSize = Integer.parseInt(intent.getStringExtra("maxTeamSize"));
        curSize = 0;

        String eventName = intent.getStringExtra("eventName");
        String tID = "";

        if (status == 3){
            curSize = Integer.parseInt(intent.getStringExtra("curTeamSize"));
            tID = intent.getStringExtra("teamID");
        }

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setSubtitle(eventName);
        }

        userCard = (CardView)findViewById(R.id.user_card);
        addTeamMate = (Button)findViewById(R.id.add_team_mate_button);
        teamID = (TextView)findViewById(R.id.team_id);
        maxTeamSize = (TextView)findViewById(R.id.max_team_size_text_view);
        curTeamSize = (TextView)findViewById(R.id.cur_team_size_text_view);

        if (curSize == 0)
            curSize = 1;

        maxTeamSize.setText(maxSize+"");
        curTeamSize.setText(curSize+"");

        addTeamMate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (curSize == maxSize){
                    new AlertDialog.Builder(RegisterTeamActivity.this).setMessage("Maximum team limit reached!")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            }).show();
                    return;
                }
                Intent intent = new Intent(RegisterTeamActivity.this, AddTeamMemberActivity.class);
                startActivityForResult(intent, ADD_TEAM_MEMBER);
            }
        });

        if (status != 3){
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Creating Team... please wait!");
            dialog.setCancelable(false);
            dialog.show();

            Call<EventRegResponse> call = RegistrationClient.getRegistrationInterface(this).createTeam();
            call.enqueue(new Callback<EventRegResponse>() {
                @Override
                public void onResponse(Call<EventRegResponse> call, Response<EventRegResponse> response) {
                    dialog.dismiss();
                    if (response != null && response.body() != null){
                        int status = response.body().getStatus();
                        String message = "";

                        switch(status){
                            case -1: message = "Session expired. Login again to continue!";
                                    break;
                            case 0: message = "Please scan the event QR!";
                                    break;
                            case 1: message = null;
                                    teamID.setText(response.body().getTeamID());
                                    break;
                            case 2:
                            case 3: message = response.body().getMessage();
                                    break;
                            case 4: message = null;
                                    teamID.setText(response.body().getTeamID());
                                    break;
                            case 5: message = "Contact the infodesk to register for sports events!";
                        }
                        showAlert(message, status);
                    }else{
                        noConnectionAlert();
                    }
                }

                @Override
                public void onFailure(Call<EventRegResponse> call, Throwable t) {
                    dialog.dismiss();
                    noConnectionAlert();
                }
            });
        }else{
            teamID.setText(tID);
            userCard.setVisibility(View.VISIBLE);
            addTeamMate.setVisibility(View.VISIBLE);
        }
    }

    public void showAlert(String message, final int status){
        if (message != null){
            new AlertDialog.Builder(RegisterTeamActivity.this).setTitle("Error").setMessage(message)
                    .setIcon(R.drawable.ic_error)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (status == -1){
                                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(RegisterTeamActivity.this).edit();
                                editor.remove("loggedIn");
                                editor.remove("COOKIE");
                                editor.apply();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }else{
                                finish();
                            }
                        }
                    }).setCancelable(false).show();
        }else{
            showSuccess(status);
            userCard.setVisibility(View.VISIBLE);
            addTeamMate.setVisibility(View.VISIBLE);
        }
    }

    private void showSuccess(final int status) {
        String message[] = {"Team created successfully!", "Team already created, proceed to add members!"};
        new AlertDialog.Builder(RegisterTeamActivity.this).setTitle("Success").setMessage(status == 4 ? message[0]:message[1])
                .setIcon(R.drawable.ic_success)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).setCancelable(false).show();
    }

    public void noConnectionAlert(){
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setIcon(R.drawable.ic_error)
                .setMessage("Could not connect to server! Please check your internet connect or try again later.")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == ADD_TEAM_MEMBER && data != null){
            if (data.getBooleanExtra("success", false)){
                try{
                    curSize++;
                    curTeamSize.setText(curSize+"");
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
