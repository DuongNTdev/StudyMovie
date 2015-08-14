package yepeer.le;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by duongnt on 7/28/15.
 */
public class Utils {
    public static String getTimeString(long millis) {
        StringBuffer buf = new StringBuffer();

        int hours = (int) (millis / (1000 * 60 * 60));
        int minutes = (int) ((millis % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) (((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);

        buf
                .append(String.format("%02d", hours))
                .append(":")
                .append(String.format("%02d", minutes))
                .append(":")
                .append(String.format("%02d", seconds));

        return buf.toString();
    }

    public  static String getNameOfLink(String link){
        int lastIndex = link.lastIndexOf("/") + 1;
        int lengthLink = link.length();
        if(lastIndex>0 && lastIndex < lengthLink){
            return link.substring(lastIndex,link.length());
        }
        return  link;
    }
    public  static int dp2px(int dp,Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

}
