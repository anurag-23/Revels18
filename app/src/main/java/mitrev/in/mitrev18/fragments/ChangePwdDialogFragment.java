package mitrev.in.mitrev18.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import mitrev.in.mitrev18.R;
import mitrev.in.mitrev18.models.registration.LoginResponse;
import mitrev.in.mitrev18.network.RegistrationClient;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by anurag on 15/2/18.
 */
public class ChangePwdDialogFragment extends DialogFragment {
    public static Context context;

    public ChangePwdDialogFragment() {
    }

    public static ChangePwdDialogFragment createInstance(Context newContext){
        context = newContext;
        return new ChangePwdDialogFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_change_password, container, false);
        final TextInputEditText newPass = (TextInputEditText)view.findViewById(R.id.new_pass);
        final TextInputEditText repeatNewPass = (TextInputEditText)view.findViewById(R.id.repeat_new_pass);
        final TextView unmatch = (TextView)view.findViewById(R.id.unmatch_text_view);
        TextView cancel = (TextView)view.findViewById(R.id.cancel_text_view);
        TextView change = (TextView)view.findViewById(R.id.change_text_view);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!newPass.getText().toString().isEmpty() && !repeatNewPass.getText().toString().isEmpty()){
                    if (!newPass.getText().toString().equals(repeatNewPass.getText().toString()))
                        unmatch.setVisibility(View.VISIBLE);
                    else{
                        unmatch.setVisibility(View.GONE);
                        dismiss();
                        changePwd(newPass.getText().toString(), repeatNewPass.getText().toString());
                    }
                }
            }
        });

        return view;
    }

    private void changePwd(String newPass, String repeatNewPass) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Changing password... please wait!");
        dialog.setCancelable(false);
        dialog.show();

        RequestBody body =  RequestBody.create(MediaType.parse("text/plain"), "newpass="+newPass+"&repeatnewpass="+repeatNewPass);
        Call<LoginResponse> call = RegistrationClient.getRegistrationInterface(getActivity()).changePassword(body);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                dialog.dismiss();
                if (response != null && response.body() != null){
                    Log.d("Status", response.body().getPayload().getCode()+"");
                    Log.d("Message", response.body().getPayload().getMessage());
                    try{
                        new AlertDialog.Builder(context).setMessage(response.body().getPayload().getMessage())
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                }).show();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.d("Pwd", "fail");
                dialog.dismiss();
            }
        });
    }
}
