package mitrev.in.mitrev18.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Saptarshi on 1/25/2018.
 */

public class ResultPagerAdapter extends FragmentPagerAdapter {
    List<Fragment> fragmentList = new ArrayList<>();
    List<String> titleList = new ArrayList<>();
    public ResultPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void add (Fragment fragment, String title) {
        Bundle bundle = new Bundle();
        switch(title){
            case "Event Results": bundle.putInt("Event Results", 1); break;
            case "Revels Cup": bundle.putInt("Revels Cup", 2); break;
        }
        fragment.setArguments(bundle);
        fragmentList.add(fragment);
        titleList.add(title);
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
