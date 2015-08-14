package yepeer.le.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gc.materialdesign.views.Button;

import yepeer.le.R;

/**
 * Created by duongnt on 7/26/15.
 */
public class DialogEditFilmSource extends Dialog{
    private Button btnFilm, btnSub;
    private TextView edtFilmPath,edtSubPath;
    private RelativeLayout rlHelp;
    private Button btnAddEdit,btnCancel;

    public DialogEditFilmSource(final Context context, final DialogEditFilmSourceListener dialogEditFilmSourceListener) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_edit_film_source);
        btnFilm = Button.class.cast(findViewById(R.id.btnFilm));
        btnSub = Button.class.cast(findViewById(R.id.btnSub));
        btnAddEdit = Button.class.cast(findViewById(R.id.btnAddEdit));
        btnCancel = Button.class.cast(findViewById(R.id.btnCancel));
        edtFilmPath = TextView.class.cast(findViewById(R.id.edtFilmPath));
        edtSubPath = TextView.class.cast(findViewById(R.id.edtSubPath));
        rlHelp = RelativeLayout.class.cast(findViewById(R.id.rlHelp));
        rlHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.sub_link))));
            }
        });
        btnFilm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogEditFilmSourceListener.onFilmClick();
            }
        });

        btnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogEditFilmSourceListener.onSubtitleClick();
            }
        });

        btnAddEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogEditFilmSourceListener.onSubmitClick();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogEditFilmSourceListener.onCancelClick();
            }
        });
    }

    public TextView getEdtFilmPath() {
        return edtFilmPath;
    }

    public TextView getEdtSubPath() {
        return edtSubPath;
    }

    public static abstract class DialogEditFilmSourceListener{
        public abstract void onFilmClick();
        public abstract void onSubtitleClick();
        public abstract void onSubmitClick();
        public abstract void onCancelClick();
    }

}
