package revels18.in.revels18.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import revels18.in.revels18.R;
import revels18.in.revels18.models.registration.LoginResponse;
import revels18.in.revels18.models.registration.ProfileResponse;
import revels18.in.revels18.network.RegistrationClient;

public class RegistrationsActivity extends AppCompatActivity {
    private TextView name;
    private TextView delID;
    private TextView regNo;
    private TextView phone;
    private TextView email;
    private TextView college;
    private CardView profileCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrations);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
                toolbar.setElevation(0);
                setSupportActionBar(toolbar);
                getSupportActionBar().setTitle(R.string.registrations);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                AppBarLayout appBarLayout = (AppBarLayout)findViewById(R.id.app_bar);
                appBarLayout.setElevation(0);
                appBarLayout.setTargetElevation(0);
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
        profileCard = (CardView)findViewById(R.id.profile_card);

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading Profile... please wait!");
        dialog.show();

        Call<ProfileResponse> call = RegistrationClient.getLoginInterface(this).getProfileDetails();
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
                    }else{
                        new AlertDialog.Builder(RegistrationsActivity.this)
                                .setMessage("Session expired. Login again to continue!")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                });
                    }

                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Log.d("reg", "fail");
                dialog.dismiss();
            }
        });

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
