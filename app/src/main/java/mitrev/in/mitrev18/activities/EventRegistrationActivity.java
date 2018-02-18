package mitrev.in.mitrev18.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import mitrev.in.mitrev18.models.registration.EventRegResponse;
import mitrev.in.mitrev18.network.RegistrationClient;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import mitrev.in.mitrev18.R;

public class EventRegistrationActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private static final int REQUEST_CAMERA = 316;
    private ZXingScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_registration);
        setTitle(R.string.event_registration);
        if (getSupportActionBar() != null) getSupportActionBar().setSubtitle(R.string.scan_qr_code);

        scannerView = (ZXingScannerView)findViewById(R.id.event_scanner);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        }

        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        scannerView.setResultHandler(this);
    }

    @Override
    public void handleResult(Result result) {
        if (result.getText().isEmpty()) return;

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading Event... please wait!");
        dialog.setCancelable(false);
        dialog.show();

        RequestBody body =  RequestBody.create(MediaType.parse("text/plain"), "qr="+result.getText());
        Call<EventRegResponse> call = RegistrationClient.getRegistrationInterface(this).eventReg(body);
        call.enqueue(new Callback<EventRegResponse>() {
            @Override
            public void onResponse(Call<EventRegResponse> call, Response<EventRegResponse> response) {
                dialog.dismiss();
                if (response != null && response.body() != null){
                    showAlert(response.body());
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

    }

    private void showAlert(final EventRegResponse response){
        String message[] = {"Session expired. Login again to continue!", "Invalid QR code!",
                "You can proceed to creating your team for "+response.getEventName()+"!", "Already registered for "+response.getEventName()+"! You can still add new team members.",
        "Contact the infodesk to register for sports events!"};
        final int status = response.getStatus();

        new AlertDialog.Builder(this).setTitle(status<=1? "Error":"Success").setMessage(message[status])
                .setIcon(status<=1 ? R.drawable.ic_error:R.drawable.ic_success)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (status == 0){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(EventRegistrationActivity.this).edit();
                            editor.remove("loggedIn");
                            editor.apply();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }else if (status == 1) {
                            scannerView.stopCamera();
                            scannerView.startCamera();
                            scannerView.setResultHandler(EventRegistrationActivity.this);
                        } else if (status == 5){
                            finish();
                        } else if (status > 1){
                            Intent intent = new Intent(EventRegistrationActivity.this, RegisterTeamActivity.class);
                            intent.putExtra("status", response.getStatus());
                            intent.putExtra("eventName", response.getEventName());
                            intent.putExtra("maxTeamSize", response.getMaxTeamSize());
                            intent.putExtra("delID", getIntent().getStringExtra("delID"));
                            if (response.getStatus() == 3){
                                intent.putExtra("curTeamSize", response.getCurTeamSize());
                                intent.putExtra("teamID", response.getTeamID());
                            }
                            startActivity(intent);
                            finish();
                        }
                    }
        }).setCancelable(false).show();
    }

    public void noConnectionAlert(){
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("Could not connect to server! Please check your internet connect or try again later.")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        scannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event_reg, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.event_reg_flash:
                scannerView.toggleFlash();
                break;
            case R.id.event_reg_reload:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                }
                scannerView.stopCamera();
                scannerView.startCamera();
                scannerView.setResultHandler(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
