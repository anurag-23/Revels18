package revels18.in.revels18.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by anurag on 14/2/18.
 */
public class ApplyCookieInterceptor implements Interceptor {
    private Context context;

    public ApplyCookieInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        builder.addHeader("Cookie", sp.getString("COOKIE", ""));

        return chain.proceed(builder.build());
    }
}
