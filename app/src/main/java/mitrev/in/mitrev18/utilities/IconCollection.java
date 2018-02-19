package mitrev.in.mitrev18.utilities;

import android.content.Context;
import android.util.Log;

import mitrev.in.mitrev18.R;

/**
 * Created by Saptarshi on 2/13/2018.
 */

public class IconCollection {


    private final int animania = R.drawable.animania;
    private final int anubhuti = R.drawable.anubhuti;
    private final int crescendo = R.drawable.crescendo;
    private final int dramebaaz = R.drawable.dramebaaz;
    private final int eq_iq = R.drawable.eq_iq;

    private final int ergo = R.drawable.ergo;
    private final int footloose = R.drawable.footloose;
    private final int haute_couture = R.drawable.haute_couture;
    private final int iridescent = R.drawable.iridescent;
    private final int kalakriti = R.drawable.kalakriti;
    private final int lensation = R.drawable.lensation;
    private final int paradigm_shift = R.drawable.paradigm_shift;
    private final int psychus = R.drawable.psychus;
    private final int gaming = R.drawable.gaming;
    private final int xventure = R.drawable.xventure;

    String TAG = "IconCollection";

    public IconCollection() {
    }

    public int getIconResource(Context context, String catName){
        switch(catName.toLowerCase()){
            case "crescendo": return crescendo;
            case "eq iq": return eq_iq;
            case "lensation": return lensation;
            case "dramebaaz": return dramebaaz;
            case "footloose": return footloose;
            case "iridescent": return iridescent;
            case "animania": return animania;
            case "anubhuti": return anubhuti;
            case "psychus": return psychus;
            case "haute couture": return haute_couture;
            case "xventure" : return xventure;
            case "kalakriti": return kalakriti;
            case "paradigm shift": return paradigm_shift;
            case "ergo": return ergo;
            case "gaming": return gaming;

            default: {
                Log.i(TAG,catName);
                return R.mipmap.ic_launcher;}
        }

    }


}