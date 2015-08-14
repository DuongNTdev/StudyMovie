

package yepeer.le.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nineoldandroids.view.ViewHelper;
import com.orleonsoft.android.simplefilechooser.Constants;
import com.orleonsoft.android.simplefilechooser.ui.FileChooserActivity;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import libaries.FlexibleSpaceWithImageBaseFragment;
import yepeer.le.LEApplication;
import yepeer.le.MainActivity;
import yepeer.le.R;
import yepeer.le.Utils;
import yepeer.le.adapters.FilmHeaderRecyclerAdapter;
import yepeer.le.dialogs.DialogEditFilmSource;
import yepeer.le.objects.FilmModel;

public class FilmRecyclerViewFragment extends FlexibleSpaceWithImageBaseFragment<ObservableRecyclerView> {
    private ArrayList<FilmModel> films = new ArrayList<FilmModel>();
    private FilmHeaderRecyclerAdapter adapter;
    private ImageView imgAdd;
    private final int FILM_CHOOSER = 1;
    private final int SUB_CHOOSER = 2;
    private String subtitlePath="";
    private String filmPath ="";
    private DialogEditFilmSource dialogEditFilmSource;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_film_recycler_view, container, false);
        imgAdd = ImageView.class.cast(view.findViewById(R.id.imgAdd));
        final ObservableRecyclerView recyclerView = (ObservableRecyclerView) view.findViewById(R.id.scroll);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(false);
        final View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.recycler_header, null);
        final int flexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        headerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, flexibleSpaceImageHeight));
        adapter = new FilmHeaderRecyclerAdapter(this, films, headerView, new FilmHeaderRecyclerAdapter.EditAtPositionCallback() {
            @Override
            public void onWillEdit(FilmModel f) {
                showDialogEdit(f);
            }
        });
        recyclerView.setAdapter(adapter);
        loadFilmData();
        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogEdit();
            }
        });
        // TouchInterceptionViewGroup should be a parent view other than ViewPager.
        // This is a workaround for the issue #117:
        // https://github.com/ksoichiro/Android-ObservableScrollView/issues/117
        recyclerView.setTouchInterceptionViewGroup((ViewGroup) view.findViewById(R.id.fragment_root));

        // Scroll to the specified offset after layout
        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_SCROLL_Y)) {
            final int scrollY = args.getInt(ARG_SCROLL_Y, 0);
            ScrollUtils.addOnGlobalLayoutListener(recyclerView, new Runnable() {
                @Override
                public void run() {
                    int offset = scrollY % flexibleSpaceImageHeight;
                    RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
                    if (lm != null && lm instanceof LinearLayoutManager) {
                        ((LinearLayoutManager) lm).scrollToPositionWithOffset(0, -offset);
                    }
                }
            });
            updateFlexibleSpace(scrollY, view);
        } else {
            updateFlexibleSpace(0, view);
        }

        recyclerView.setScrollViewCallbacks(this);

        return view;
    }
    private void animateViewWithTada(View view){
        YoYo.with(Techniques.Tada)
                .duration(700).playOn(view);
    }
    private void loadFilmData() {
        films.clear();
        films.addAll(DataSupport.findAll(FilmModel.class));
        Collections.sort(films, new Comparator<FilmModel>() {
            @Override
            public int compare(FilmModel lhs, FilmModel rhs) {
                return (int)(rhs.getId() - lhs.getId());
            }
        });
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setScrollY(int scrollY, int threshold) {
        View view = getView();
        if (view == null) {
            return;
        }
        ObservableRecyclerView recyclerView = (ObservableRecyclerView) view.findViewById(R.id.scroll);
        if (recyclerView == null) {
            return;
        }
        View firstVisibleChild = recyclerView.getChildAt(0);
        if (firstVisibleChild != null) {
            int offset = scrollY;
            int position = 0;
            if (threshold < scrollY) {
                int baseHeight = firstVisibleChild.getHeight();
                position = scrollY / baseHeight;
                offset = scrollY % baseHeight;
            }
            RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
            if (lm != null && lm instanceof LinearLayoutManager) {
                ((LinearLayoutManager) lm).scrollToPositionWithOffset(position, -offset);
            }
        }
    }

    @Override
    protected void updateFlexibleSpace(int scrollY, View view) {
        int flexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);

        View recyclerViewBackground = view.findViewById(R.id.list_background);

        // Translate list background
        ViewHelper.setTranslationY(recyclerViewBackground, Math.max(0, -scrollY + flexibleSpaceImageHeight));

        // Also pass this event to parent Activity
        MainActivity parentActivity =
                (MainActivity) getActivity();
        if (parentActivity != null) {
            parentActivity.onScrollChanged(scrollY, (ObservableRecyclerView) view.findViewById(R.id.scroll));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == FILM_CHOOSER || requestCode == SUB_CHOOSER) && (resultCode == Activity.RESULT_OK)) {
            String fileSelected = data.getStringExtra(Constants.KEY_FILE_SELECTED);
            if(dialogEditFilmSource!=null){
                if(requestCode == FILM_CHOOSER) {
                    dialogEditFilmSource.getEdtFilmPath().setText(Utils.getNameOfLink(fileSelected));
                    filmPath = fileSelected;
                }
                else if(requestCode == SUB_CHOOSER) {
                    dialogEditFilmSource.getEdtSubPath().setText(Utils.getNameOfLink(fileSelected));
                    subtitlePath = fileSelected;
                }
            }
        }
    }
    private void showDialogEdit(){
        showDialogEdit(null);
    }
    private void showDialogEdit(FilmModel f){
        if(dialogEditFilmSource!=null){
            dialogEditFilmSource.dismiss();
        }
        dialogEditFilmSource = new DialogEditFilmSource(getActivity(), new DialogEditFilmSource.DialogEditFilmSourceListener() {
            @Override
            public void onFilmClick() {
                Intent i = new Intent(getActivity(), FileChooserActivity.class);
                Bundle bundle = new Bundle();
                ArrayList<String> ext = new ArrayList<String>();
                ext.add(".mp4");ext.add(".3gp");ext.add(".mov");
                ext.add(".avi");ext.add(".m4v");ext.add(".mkv");
                ext.add(".rmvb");ext.add(".flv");ext.add(".wmv");
                bundle.putStringArrayList(Constants.KEY_FILTER_FILES_EXTENSIONS,ext);
                i.putExtras(bundle);
                startActivityForResult(i, FILM_CHOOSER);
            }

            @Override
            public void onSubtitleClick() {
                Intent i = new Intent(getActivity(), FileChooserActivity.class);
                Bundle bundle = new Bundle();
                ArrayList<String> ext = new ArrayList<String>();
                ext.add(".srt");ext.add(".ass");ext.add(".scc");
                ext.add(".stl");ext.add(".ttml");
                bundle.putStringArrayList(Constants.KEY_FILTER_FILES_EXTENSIONS,ext);
                i.putExtras(bundle);
                startActivityForResult(i, SUB_CHOOSER);
            }

            @Override
            public void onSubmitClick() {
                if(TextUtils.isEmpty(subtitlePath) && TextUtils.isEmpty(filmPath)){
                    animateViewWithTada(dialogEditFilmSource.getEdtFilmPath());
                    animateViewWithTada(dialogEditFilmSource.getEdtSubPath());
                    Toast.makeText(getActivity(),getResources().getString(R.string.subtitle_and_film_path_is_empty),Toast.LENGTH_LONG).show();
                }else if(TextUtils.isEmpty(subtitlePath)){
                    animateViewWithTada(dialogEditFilmSource.getEdtSubPath());
                    Toast.makeText(getActivity(),getResources().getString(R.string.subtitle_and_film_path_is_empty),Toast.LENGTH_LONG).show();
                }else if(TextUtils.isEmpty(filmPath)){
                    animateViewWithTada(dialogEditFilmSource.getEdtFilmPath());
                    Toast.makeText(getActivity(),getString(R.string.film_path_is_empty),Toast.LENGTH_LONG).show();
                }
                else {
                    List<FilmModel> films = DataSupport.where("filmPath = ?", filmPath).find(FilmModel.class);
                    if (films.size() > 0) {
                        //update subtitle
                        FilmModel f = films.get(0);
                        f.setSubtitlePath(subtitlePath);
                        f.update(f.getId());
                        loadFilmData();

                        Tracker t = ((LEApplication) getActivity().getApplication()).getTracker(
                                LEApplication.TrackerName.APP_TRACKER);
                        t.send(new HitBuilders.EventBuilder().setCategory("UX").setAction("Update subtitle").build());
                    } else {
                        //insert
                        FilmModel f = new FilmModel();
                        f.setSubtitlePath(subtitlePath);
                        f.setFilmPath(filmPath);
                        f.save();
                        loadFilmData();

                        Tracker t = ((LEApplication) getActivity().getApplication()).getTracker(
                                LEApplication.TrackerName.APP_TRACKER);
                        t.send(new HitBuilders.EventBuilder().setCategory("UX").setAction("Add new movie").build());
                    }
                    dialogEditFilmSource.dismiss();
                }
            }

            @Override
            public void onCancelClick() {
                dialogEditFilmSource.dismiss();
            }
        });
        if(f!=null){
            dialogEditFilmSource.getEdtSubPath().setText(Utils.getNameOfLink(f.getSubtitlePath()));
            dialogEditFilmSource.getEdtFilmPath().setText(Utils.getNameOfLink(f.getFilmPath()));
            filmPath = f.getFilmPath();
            subtitlePath = f.getSubtitlePath();
        }
        dialogEditFilmSource.show();
    }
}
