package yepeer.le.save;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by duongnt on 8/7/15.
 */
public class UserData {
    private static UserData _instance;

    public static UserData getInstance() {
        if (_instance == null) _instance = new UserData();
        return _instance;
    }

    public void setDidRemoveAds(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences("DuongNT", Context.MODE_PRIVATE).edit();
        editor.putBoolean("rmv", true);
        editor.commit();
    }

    public boolean didRemoveADs(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("DuongNT", Context.MODE_PRIVATE);
        return prefs.getBoolean("rmv", false);
    }


    public void setOpenedAppIntro(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences("DuongNT", Context.MODE_PRIVATE).edit();
        editor.putBoolean("intro", true);
        editor.commit();
    }

    public boolean isOpenedAppIntro(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("DuongNT", Context.MODE_PRIVATE);
        return prefs.getBoolean("intro", false);
    }

    public void saveTargetForeign(Context context, String target) {
        SharedPreferences.Editor editor = context.getSharedPreferences("DuongNT", Context.MODE_PRIVATE).edit();
        editor.putString("target", target);
        editor.commit();
    }

    public String getTargetForeign(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("DuongNT", Context.MODE_PRIVATE);
        String restoredText = prefs.getString("target", null);
        return restoredText;
    }
}
