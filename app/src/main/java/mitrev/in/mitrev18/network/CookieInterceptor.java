package mitrev.in.mitrev18.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by anurag on 14/2/18.
 */
public class CookieInterceptor implements Interceptor {
    private Context context;

    public CookieInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());

        if (!response.headers("Set-Cookie").isEmpty()){
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
            List<String> cookies = response.headers("Set-Cookie");
            for (String cookie: cookies){
                if (cookie.contains("PHPSESSID")) {
                    editor.remove("COOKIE");
                    editor.putString("COOKIE", cookie);
                    Log.d("Cookie", cookie);
                    break;
                }
            }
            editor.apply();
        }

        return response;
    }
}
