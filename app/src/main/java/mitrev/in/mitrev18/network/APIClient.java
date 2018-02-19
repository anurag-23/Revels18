package mitrev.in.mitrev18.network;

import mitrev.in.mitrev18.models.categories.CategoriesListModel;
import mitrev.in.mitrev18.models.events.EventsListModel;
import mitrev.in.mitrev18.models.events.RevelsCupEventsListModel;
import mitrev.in.mitrev18.models.events.ScheduleListModel;
import mitrev.in.mitrev18.models.results.ResultsListModel;
import mitrev.in.mitrev18.models.sports.SportsListModel;
import mitrev.in.mitrev18.models.workshops.WorkshopListModel;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class APIClient {

    private static Retrofit retrofit = null;
    private static Retrofit retrofitEgg = null;

    private static final String BASE_URL = "https://api.mitportals.in/";
    //private static final String EGGS_URL = "http://qnaxzrzrf.herokuapp.com/api/";

    public static APIInterface getAPIInterface(){

        if (retrofit == null){
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
            //retrofitEgg = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();

        }

        return retrofit.create(APIInterface.class);
    }

   /* public static APIInterface getEggAPIInterface(){

        if (retrofitEgg == null){
            //retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
            retrofitEgg = new Retrofit.Builder().baseUrl(EGGS_URL).addConverterFactory(GsonConverterFactory.create()).build();

        }

        return retrofitEgg.create(APIInterface.class);
    }*/

    public interface APIInterface{
        @GET("events")
        Call<EventsListModel> getEventsList();

        @GET("categories")
        Call<CategoriesListModel> getCategoriesList();

        @GET("results")
        Call<ResultsListModel> getResultsList();

        @GET("schedule")
        Call<ScheduleListModel> getScheduleList();

        @GET("revelscup")
        Call<RevelsCupEventsListModel> getRevelsCupEventsList();

        @GET("sports")
        Call<SportsListModel> getSportsResults();

        @GET("workshops")
        Call<WorkshopListModel> getWorkshopsList();
        //@GET("photos")
        //Call<EasterEggModel[]> getEggList();

    }
}