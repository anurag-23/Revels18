package revels18.in.revels18.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import revels18.in.revels18.R;
import revels18.in.revels18.utilities.IconCollection;
import revels18.in.revels18.activities.CategoryActivity;
import revels18.in.revels18.models.categories.CategoryModel;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {

    private List<CategoryModel> categoriesList;
    private Activity activity;

    public CategoriesAdapter(List<CategoryModel> categoriesList, Activity activity) {
        this.categoriesList = categoriesList;
        this.activity = activity;
    }
    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_category, parent, false));
    }
    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        CategoryModel category = categoriesList.get(position);
        holder.catName.setText(category.getCategoryName());
        IconCollection icons = new IconCollection();
        holder.catLogo.setImageResource(icons.getIconResource(activity, category.getCategoryName()));
    }
    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView catLogo;
        TextView catName;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            catLogo = (ImageView) itemView.findViewById(R.id.cat_event_logo_image_view);
            catName = (TextView) itemView.findViewById(R.id.cat_event_name_text_view);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(activity, CategoryActivity.class);
                intent.putExtra("catName", categoriesList.get(getAdapterPosition()).getCategoryName());
                intent.putExtra("catID", categoriesList.get(getAdapterPosition()).getCategoryID());
                intent.putExtra("catDesc", categoriesList.get(getAdapterPosition()).getCategoryDescription());
                activity.startActivity(intent);
        }
    }
}