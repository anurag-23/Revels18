package revels18.in.revels18.fragments;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import revels18.in.revels18.R;
import revels18.in.revels18.activities.FavouritesActivity;
import revels18.in.revels18.activities.MainActivity;
import revels18.in.revels18.activities.RegistrationsActivity;
import revels18.in.revels18.adapters.CategoriesAdapter;
import revels18.in.revels18.application.Revels;
import revels18.in.revels18.models.categories.CategoryModel;

public class CategoriesFragment extends Fragment {

    private List<CategoryModel> categoriesList = new ArrayList<>();
    private Realm mDatabase;
    private CategoriesAdapter adapter;
    private ProgressDialog dialog;
    private static final int LOAD_CATEGORIES = 0;
    private static final int UPDATE_CATEGORIES = 1;
    private MenuItem searchItem;
    private String TAG = "CategoriesFragment";
    public CategoriesFragment() {
    }

    public static CategoriesFragment newInstance() {
        CategoriesFragment fragment = new CategoriesFragment();
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.categories);
        mDatabase = Realm.getDefaultInstance();
        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getActivity().findViewById(R.id.toolbar).setElevation((4 * getResources().getDisplayMetrics().density + 0.5f));
                getActivity().findViewById(R.id.app_bar).setElevation((4 * getResources().getDisplayMetrics().density + 0.5f));
            }
        }catch(NullPointerException e){
            e.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_categories, container, false);
        RecyclerView categoriesRecyclerView = (RecyclerView)view.findViewById(R.id.categories_recycler_view);
        adapter = new CategoriesAdapter(categoriesList, getActivity());
        categoriesRecyclerView.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 4);
        categoriesRecyclerView.setLayoutManager(gridLayoutManager);
        //categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(categoriesRecyclerView.getContext(),DividerItemDecoration.VERTICAL);
        //categoriesRecyclerView.addItemDecoration(dividerItemDecoration);

        if (mDatabase.where(CategoryModel.class).findAll().size() != 0){
            displayData();
        }else{
            Log.i(TAG, "onCreateView: No categories in realm");
        }
        return view;
    }
    private void displayData(){
        if (mDatabase != null){
            categoriesList.clear();
            List<CategoryModel> categoryResults = mDatabase.copyFromRealm(mDatabase.where(CategoryModel.class).findAllSorted("categoryName"));
            if (!categoryResults.isEmpty()){
                categoriesList.clear();
                categoriesList.addAll(categoryResults);
                adapter.notifyDataSetChanged();
            }
        }
    }
    private void displaySearchData(String text){
        /*if(text.equals("Memelord")){
            Intent intent = new Intent(getContext(),EasterEggActivity.class);
            startActivity(intent);
        }*/
        if (mDatabase != null){
            RealmResults<CategoryModel> categoryResults = mDatabase.where(CategoryModel.class).contains("categoryName",text, Case.INSENSITIVE ).findAllSorted("categoryName");
            if (!categoryResults.isEmpty()){
                categoriesList.clear();
                categoriesList.addAll(mDatabase.copyFromRealm(categoryResults));
                //categoriesList.addAll(categoryResults);
                adapter.notifyDataSetChanged();
            }
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mDatabase.close();
        mDatabase = null;
        if (dialog != null && dialog.isShowing())
            dialog.hide();
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_categories, menu);
        searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView)searchItem.getActionView();
        SearchManager searchManager = (SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setSubmitButtonEnabled(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {
                displaySearchData(text);
                Revels.searchOpen = 1;
                return false;
            }
            @Override
            public boolean onQueryTextChange(String text) {
                displaySearchData(text);
                Revels.searchOpen = 1;
                return false;
            }
        });
        searchView.setQueryHint("Search Categories");
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                displayData();
                searchView.clearFocus();
                Revels.searchOpen = 1;
                return false;
            }


        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_registrations:{
                startActivity(new Intent((MainActivity)getActivity(), RegistrationsActivity.class));
                return true;
            }
            case R.id.menu_favourites: {
                startActivity(new Intent((MainActivity)getActivity(), FavouritesActivity.class));
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        setHasOptionsMenu(false);
        setMenuVisibility(false);
    }
    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}