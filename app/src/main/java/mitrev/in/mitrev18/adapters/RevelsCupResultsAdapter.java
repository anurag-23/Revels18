package mitrev.in.mitrev18.adapters;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import mitrev.in.mitrev18.models.sports.SportsResultModel;
import mitrev.in.mitrev18.utilities.IconCollection;
import mitrev.in.mitrev18.R;

/**
 * Created by Saptarshi on 2/15/2018.
 */

public class RevelsCupResultsAdapter extends RecyclerView.Adapter<RevelsCupResultsAdapter.ResultViewHolder> {

    private List<SportsResultModel> resultsList;
    private Context context;

    public RevelsCupResultsAdapter(List<SportsResultModel> resultsList, Context context) {
        this.resultsList = resultsList;
        this.context = context;
    }

    @Override
    public ResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ResultViewHolder(LayoutInflater.from(context).inflate(R.layout.item_revels_cup_result, parent, false));
    }

    @Override
    public void onBindViewHolder(ResultViewHolder holder, int position) {
        SportsResultModel result = resultsList.get(position);

        holder.eventName.setText(result.eventName);
        holder.eventRound.setText(result.eventRound);
        holder.catLogo.setImageResource(new IconCollection().getIconResource(context,"sports"));
    }

    @Override
    public int getItemCount() {
        return resultsList.size();
    }

    class ResultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView catLogo;
        TextView eventName;
        TextView eventRound;

        public ResultViewHolder(View itemView) {
            super(itemView);

            catLogo = (ImageView)itemView.findViewById(R.id.result_cat_logo_image_view);
            eventName = (TextView)itemView.findViewById(R.id.res_event_name_text_view);
            eventRound = (TextView)itemView.findViewById(R.id.res_round_text_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            View bottomSheetView = View.inflate(context, R.layout.dialog_results, null);
            final Dialog dialog = new Dialog(context);

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(bottomSheetView);

            TextView eventName = (TextView)bottomSheetView.findViewById(R.id.result_dialog_event_name_text_view);
            eventName.setText(resultsList.get(getAdapterPosition()).eventName);

            TextView eventRound = (TextView)bottomSheetView.findViewById(R.id.result_dialog_round_text_view);
            eventRound.setText(resultsList.get(getAdapterPosition()).eventRound);

            RecyclerView teamsRecyclerView = (RecyclerView)bottomSheetView.findViewById(R.id.result_dialog_teams_recycler_view);
            teamsRecyclerView.setAdapter(new SportsQualifiedTeamsAdapter(resultsList.get(getAdapterPosition()).eventResultsList, context));
            teamsRecyclerView.setLayoutManager(new GridLayoutManager(context, 2));

            dialog.show();
        }
    }

}
