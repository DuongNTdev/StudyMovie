package yepeer.le.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonRectangle;

import java.util.ArrayList;

import yepeer.le.R;
import yepeer.le.Utils;
import yepeer.le.dialogs.DialogTranslate;
import yepeer.le.objects.SubtitleModel;

/**
 * Created by duongnt on 7/28/15.
 */
public class SubtitleListViewAdapter extends ArrayAdapter<SubtitleModel> {
    public SubtitleListViewAdapter(Context context, ArrayList<SubtitleModel> items) {
        super(context, android.R.layout.simple_list_item_1, items);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ItemViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.item_subtitle, null);
            holder = new ItemViewHolder(convertView);
            convertView.setTag(holder);
        } else holder = ItemViewHolder.class.cast(convertView.getTag());
       final SubtitleModel s = getItem(position);
        holder.tvSubtitle.setText(Utils.getTimeString(s.getTime())+" - "+Html.fromHtml(s.getSubtitle()));
        holder.imgTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogTranslate.show((Activity)getContext(), Html.fromHtml(s.getSubtitle()).toString());
            }
        });
        return convertView;
    }

    static class ItemViewHolder {
        public ButtonRectangle imgTranslate;
        public TextView tvSubtitle;

        public ItemViewHolder(View view) {
            imgTranslate = ButtonRectangle.class.cast(view.findViewById(R.id.iv_translate));
            tvSubtitle = TextView.class.cast(view.findViewById(R.id.tvSubtitle));
        }
    }
}
