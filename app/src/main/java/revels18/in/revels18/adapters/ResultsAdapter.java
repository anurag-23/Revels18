package revels18.in.revels18.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import revels18.in.revels18.R;
import revels18.in.revels18.utilities.IconCollection;
import revels18.in.revels18.models.results.EventResultModel;

/**
 * Created by Saptarshi on 12/6/2017.
 */

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ResultsViewHolder>{

    private List<EventResultModel> resultsList;
    private Context context;
    Activity activity;

    public ResultsAdapter(List<EventResultModel> resultsList, Context context, Activity activity){
        this.resultsList=resultsList;
        this.context=context;
        this.activity=activity;
    }

    @Override
    public ResultsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ResultsViewHolder(LayoutInflater.from(context).inflate(R.layout.item_results, parent, false));
    }

    @Override
    public void onBindViewHolder(ResultsViewHolder holder, int position) {
        EventResultModel result = resultsList.get(position);
        holder.eventName.setText(result.eventName);
        holder.eventRound.setText(result.eventRound);
        IconCollection icons = new IconCollection();
        holder.eventLogo.setImageResource(icons.getIconResource(activity, result.eventCategory));
    }

    @Override
    public int getItemCount() {
        return resultsList.size();
    }

    class ResultsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView eventLogo;
        TextView eventName;
        TextView eventRound;
        public ResultsViewHolder(View itemView) {
            super(itemView);

            eventName = (TextView) itemView.findViewById(R.id.res_event_name_text_view);
            eventRound= (TextView) itemView.findViewById(R.id.res_round_text_view);
            eventLogo = (ImageView) itemView.findViewById(R.id.result_cat_logo_image_view);

            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            displayInfoDialog(resultsList.get(getAdapterPosition()));
        }
        public void displayInfoDialog(EventResultModel result){
            View bottomSheetView = View.inflate(context, R.layout.dialog_results, null);
            final Dialog dialog = new Dialog(context);

            dialog.setContentView(bottomSheetView);

            TextView eventName = (TextView)bottomSheetView.findViewById(R.id.result_dialog_event_name_text_view);
            eventName.setText(result.eventName);

            TextView eventRound = (TextView)bottomSheetView.findViewById(R.id.result_dialog_round_text_view);
            eventRound.setText(result.eventRound);

            RecyclerView teamsRecyclerView = (RecyclerView)bottomSheetView.findViewById(R.id.result_dialog_teams_recycler_view);
            teamsRecyclerView.setAdapter(new QualifiedTeamsAdapter(result.eventResultsList, context));
            teamsRecyclerView.setLayoutManager(new GridLayoutManager(context, 2));

            dialog.show();
        }
    }
}