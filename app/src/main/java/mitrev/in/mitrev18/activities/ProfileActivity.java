package mitrev.in.mitrev18.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.glxn.qrgen.android.QRCode;

import mitrev.in.mitrev18.R;
import mitrev.in.mitrev18.adapters.EventRegAdapter;
import mitrev.in.mitrev18.models.registration.ProfileResponse;
import mitrev.in.mitrev18.network.RegistrationClient;
import mitrev.in.mitrev18.utilities.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    private TextView name;
    private TextView delID;
    private TextView regNo;
    private TextView phone;
    private TextView email;
    private TextView college;
    private LinearLayout profileCard;
    private Button logoutButton;
    private Button eventRegButton;
    private ImageView qrCode;
    private RecyclerView eventRegRecyclerView;
    private TextView noEvents;
    private LinearLayout eventRegHeader;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                if (getSupportActionBar() != null){
                    getSupportActionBar().setTitle(R.string.profile);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        name = (TextView)findViewById(R.id.name_text_view);
        delID = (TextView)findViewById(R.id.del_id_text_view);
        regNo = (TextView)findViewById(R.id.reg_no_text_view);
        phone = (TextView)findViewById(R.id.phone_text_view);
        email = (TextView)findViewById(R.id.email_text_view);
        college = (TextView)findViewById(R.id.college_text_view);
        profileCard = (LinearLayout)findViewById(R.id.profile_layout);
        logoutButton = (Button)findViewById(R.id.logout_button);
        qrCode = (ImageView)findViewById(R.id.qr_image_view);
        eventRegButton = (Button)findViewById(R.id.event_reg_button);
        eventRegRecyclerView = (RecyclerView)findViewById(R.id.event_reg_recycler_view);
        noEvents = (TextView)findViewById(R.id.no_reg_events);
        eventRegHeader = (LinearLayout)findViewById(R.id.event_reg_header);

        loadProfile();

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(ProfileActivity.this)
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ProfileActivity.this).edit();
                                editor.remove("loggedIn");
                                editor.remove("COOKIE");
                                editor.apply();
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).show();
            }
        });

        eventRegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, EventRegistrationActivity.class);
                intent.putExtra("delID", delID.getText().toString());
                startActivity(intent);
            }
        });
    }

    private void loadProfile() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading Profile... please wait!");
        dialog.setCancelable(false);

        if (!NetworkUtils.isInternetConnected(this)){
            noConnectionAlert("Please connect to the internet and try again!");
        }else{
            dialog.show();

            Call<ProfileResponse> call = RegistrationClient.getRegistrationInterface(this).getProfileDetails();
            call.enqueue(new Callback<ProfileResponse>() {
                @Override
                public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                    dialog.dismiss();
                    if (response != null && response.body() != null){
                        ProfileResponse profileResponse = response.body();

                        if (profileResponse.getStatus() == 1){
                            profileCard.setVisibility(View.VISIBLE);
                            name.setText(profileResponse.getName());
                            delID.setText(profileResponse.getDelegateID());
                            regNo.setText(profileResponse.getRegNo());
                            phone.setText(profileResponse.getPhoneNo());
                            email.setText(profileResponse.getEmail());
                            college.setText(profileResponse.getCollege());

                            if (profileResponse.getEventData().isEmpty()) {
                                noEvents.setVisibility(View.VISIBLE);
                            }
                            else{
                                EventRegAdapter adapter = new EventRegAdapter(profileResponse.getEventData(), ProfileActivity.this);
                                eventRegRecyclerView.setAdapter(adapter);
                                eventRegRecyclerView.setLayoutManager(new LinearLayoutManager(ProfileActivity.this));
                                eventRegRecyclerView.setVisibility(View.VISIBLE);
                                eventRegHeader.setVisibility(View.VISIBLE);
                            }
                            Bitmap myBitmap = QRCode.from(profileResponse.getQr()).withSize(1000, 1000).bitmap();
                            qrCode.setImageBitmap(myBitmap);
                        }else{
                            new AlertDialog.Builder(ProfileActivity.this)
                                    .setMessage("Session expired. Login again to continue!")
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ProfileActivity.this).edit();
                                            editor.remove("loggedIn");
                                            editor.remove("COOKIE");
                                            editor.apply();
                                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                        }
                                    }).setCancelable(false).show();
                        }
                    }else{
                        noConnectionAlert("Could not connect to server! Please check your internet connect or try again later.");
                    }
                }

                @Override
                public void onFailure(Call<ProfileResponse> call, Throwable t) {
                    Log.d("reg", "fail");
                    dialog.dismiss();
                    noConnectionAlert("Could not connect to server! Please check your internet connect or try again later.");
                }
            });
        }
    }

    public void noConnectionAlert(String message){
        new AlertDialog.Builder(ProfileActivity.this)
                .setTitle("Error")
                .setIcon(R.drawable.ic_error)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).show();
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

    @Override
    protected void onResume() {
        super.onResume();
        if (dialog != null && !dialog.isShowing())
            loadProfile();
    }
}
