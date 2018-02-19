package mitrev.in.mitrev18.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import mitrev.in.mitrev18.R;
import mitrev.in.mitrev18.fragments.ChangePwdDialogFragment;
import mitrev.in.mitrev18.models.registration.LoginResponse;
import mitrev.in.mitrev18.network.RegistrationClient;
import mitrev.in.mitrev18.utilities.NetworkUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText email = (EditText)findViewById(R.id.username_edit_text);
        final EditText password = (EditText)findViewById(R.id.password_edit_text);
        LinearLayout loginLayout = (LinearLayout)findViewById(R.id.login_child_linear_layout);

        Button loginButton = (Button)findViewById(R.id.login_button);
        Button guestLoginButton = (Button)findViewById(R.id.guest_login_button);
        Button signUpButton = (Button)findViewById(R.id.sign_up_button);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        if (sp.getBoolean("loggedIn", false)){
            Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
            startActivity(intent);
        }else{
            loginLayout.setVisibility(View.VISIBLE);
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (email.getText().toString().isEmpty() || password.getText().toString().isEmpty()){
                    showAlert("Please enter email and password!", 0);
                    return;
                }
                if (!NetworkUtils.isInternetConnected(LoginActivity.this)){
                    showAlert("Please connect to the internet and try again!", 1);
                    return;
                }
                final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
                dialog.setMessage("Logging in... please wait!");
                dialog.setCancelable(false);
                dialog.show();

                RequestBody body =  RequestBody.create(MediaType.parse("text/plain"), "email="+email.getText().toString()+"&password="+password.getText().toString());
                Call<LoginResponse> call = RegistrationClient.getRegistrationInterface(LoginActivity.this).attemptLogin(body);
                call.enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        String message = "";
                        int error = 0;
                        if (response != null && response.body() != null) {
                            switch(response.body().getPayload().getCode()){
                                case 1: message = "Email or password not specified!";
                                        error = 1;
                                        break;
                                case 2: message = "Could not connect to database!";
                                        error = 1;
                                        break;
                                case 3: message = "Login successful! However, we recommend setting a new password before you continue.";
                                        error = 3;
                                        break;
                                case 4: message = "Login successful!";
                                        error = 2;
                                        setLoggedIn();
                                        break;
                                case 5: message = "Incorrect email or password! Please try again.";
                                        error = 1;
                                        break;
                            }
                            dialog.dismiss();
                            if (error == 2){
                                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit();
                                editor.putBoolean("loggedIn", true);
                                editor.apply();
                            }
                            showAlert(message, error);
                        }else{
                            showAlert("Could not connect to server! Please check your internet connect or try again later.", 1);
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        dialog.dismiss();
                        showAlert("Could not connect to server! Please check your internet connect or try again later.", 1);
                    }
                });
            }
        });

        guestLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
                startActivity(intent);
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String URL = "https://mitportals.in/loginform.php";
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.setToolbarColor(ContextCompat.getColor(LoginActivity.this, R.color.colorPrimary));
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(LoginActivity.this, Uri.parse(URL));
            }
        });
    }

    private void setLoggedIn() {

    }

    public void showAlert(String message, final int error){
        String result[] = {"", "Error", "Success", "Success"};
        int icon[] = {0, R.drawable.ic_error, R.drawable.ic_success, R.drawable.ic_success};
        new AlertDialog.Builder(LoginActivity.this).setTitle(result[error]).setMessage(message)
                .setIcon(icon[error])
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (error == 2){
                            Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
                            startActivity(intent);
                        }else if (error == 3){
                            showChangePwdDialog();
                        }
                    }
                }).setCancelable(false).show();
    }

    private void showChangePwdDialog() {
        DialogFragment fragment = ChangePwdDialogFragment.createInstance(this);
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        fragment.show(getSupportFragmentManager(), "changePwd");
    }


}
