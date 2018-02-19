package mitrev.in.mitrev18.network;

import mitrev.in.mitrev18.models.instagram.InstagramFeed;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

/**
 * Created by Saptarshi on 12/25/2017.
 */

public class InstaFeedAPIClient {

    public static final String BASE_URL_INSTAGRAM="https://api.instagram.com/";
    private static Retrofit retrofit = null;

    public static APIInterface getInterface(){

        if (retrofit == null){
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL_INSTAGRAM).addConverterFactory(GsonConverterFactory.create()).build();
        }

        return retrofit.create(APIInterface.class);
    }

    public interface APIInterface {
        //Query TAG is set 'Revels18' for this year's app
        //TODO: Change search TAG accordingly
        @GET("v1/tags/revels18/media/recent?access_token=630237785.f53975e.8dcfa635acf14fcbb99681c60519d04c")
        Call<InstagramFeed> getInstagramFeed();

    }
}