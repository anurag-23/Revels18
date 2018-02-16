package revels18.in.revels18.network;

import android.content.Context;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import revels18.in.revels18.models.registration.EventRegResponse;
import revels18.in.revels18.models.registration.LoginResponse;
import revels18.in.revels18.models.registration.ProfileResponse;

/**
 * Created by anurag on 13/2/18.
 */
public class RegistrationClient {
    private static Retrofit retrofit = null;
    private static final String BASE_URL = "https://mitportals.in/includes/";

    public static RegistrationInterface getRegistrationInterface(Context context){
        if (retrofit == null){
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new CookieInterceptor(context))
                    .addInterceptor(new ApplyCookieInterceptor(context))
                    .build();

            retrofit = new Retrofit.Builder().baseUrl(BASE_URL).client(client)
                    .addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit.create(RegistrationInterface.class);
    }

    public interface RegistrationInterface {
        @Headers({"Content-Type: application/x-www-form-urlencoded"})
        @POST("login.php")
        Call<LoginResponse> attemptLogin(@Body RequestBody body);

        @GET("getDetails.php")
        Call<ProfileResponse> getProfileDetails();

        @Headers({"Content-Type: application/x-www-form-urlencoded"})
        @POST("change_password.php")
        Call<LoginResponse> changePassword(@Body RequestBody body);

        @Headers({"Content-Type: application/x-www-form-urlencoded"})
        @POST("eventReg.php")
        Call<EventRegResponse> eventReg(@Body RequestBody body);
    }

}
