package yepeer.le.dialogs;

import android.app.Activity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gc.materialdesign.views.ButtonFlat;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import yepeer.le.LEApplication;
import yepeer.le.R;
import yepeer.le.save.UserData;

/**
 * Created by duongnt on 8/6/15.
 */
public class DialogTranslate {
    public static void show(Activity context,String subtitle){
        ViewHolder holder = new ViewHolder(R.layout.dialog_translate);
        int screenWidth = context.getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = context.getWindowManager().getDefaultDisplay().getHeight();
        final DialogPlus dialog = DialogPlus.newDialog(context)
                .setContentHolder(holder)
                .setExpanded(true,screenHeight / 2)
                .create();
        WebView w = (WebView)holder.getInflatedView().findViewById(R.id.webView);
        w.getSettings().setJavaScriptEnabled(true);
        w.setWebViewClient(new WebViewClient());
        String target = UserData.getInstance().getTargetForeign(context);
        w.loadUrl("https://translate.google.com.vn/#auto/" + target + "/" + subtitle);
        ButtonFlat btnClose = (ButtonFlat)holder.getInflatedView().findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

        Tracker t = ((LEApplication) context.getApplicationContext()).getTracker(
                LEApplication.TrackerName.APP_TRACKER);
        t.send(new HitBuilders.EventBuilder().setCategory("UX").setAction("Translate to "+target).build());
    }
}
