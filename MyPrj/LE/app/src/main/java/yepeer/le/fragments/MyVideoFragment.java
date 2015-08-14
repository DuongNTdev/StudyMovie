package yepeer.le.fragments;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.pedrovgs.DraggableListener;
import com.github.pedrovgs.DraggableView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nineoldandroids.animation.ObjectAnimator;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import io.vov.vitamio.MediaPlayer;
import libaries.subtitleFile.Caption;
import libaries.subtitleFile.FormatSRT;
import libaries.subtitleFile.TimedTextObject;
import yepeer.le.ChooseLangActivity;
import yepeer.le.LEApplication;
import yepeer.le.MainActivity;
import yepeer.le.R;
import yepeer.le.Utils;
import yepeer.le.adapters.SubtitleListViewAdapter;
import yepeer.le.objects.FilmModel;
import yepeer.le.objects.SubtitleModel;
import yepeer.le.save.UserData;

/*
 * DuongNT : Use Draggable view and hook to draggable touch
 * Customize draggable view libary
 */
public class MyVideoFragment extends Fragment implements Callback, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    private DraggableView draggableView;
    private FilmModel filmModel;
    public static final String BUNDLE_FILM_ID = "BUNDLE_FILM_ID";
    private SurfaceView splayer;
    private SurfaceHolder sholder;
    private MediaPlayer mediaPlayer;
    private boolean isShowedAds = false;
    private InterstitialAd interstitialAd;
    private ListView lstSubtitle;
    private ArrayList<SubtitleModel> subtitle = new ArrayList<SubtitleModel>();
    private SubtitleListViewAdapter adapter;
    private RelativeLayout overlay_black;
    private SeekBar skProgress;
    private Handler handlerHideOverlay = new Handler();
    private Handler handlerMediaController = new Handler();
    private TextView tv_start_time, tv_duration;
    private int drawable_play_state = R.drawable.ic_play_arrow_white_48dp;
    private int drawable_pause_state = R.drawable.ic_pause_white_48dp;
    private int drawable_cc_white = R.drawable.ic_closed_caption_white_36dp;
    private int drawable_cc_gray = R.drawable.ic_closed_caption_gray_36dp;
    private ImageView iv_state, iv_rewind, iv_forward, iv_aspect, iv_min, iv_cc, iv_setting;
    private boolean isEnableCC = true;
    private TextView tvSubtitle;
    private TimedTextObject srt;
    private boolean isEnded;

    public static MyVideoFragment newInstance(long filmId) {
        Bundle b = new Bundle();
        b.putLong(BUNDLE_FILM_ID, filmId);
        MyVideoFragment f = new MyVideoFragment();
        f.setArguments(b);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_video, null);
        long b = getArguments().getLong(BUNDLE_FILM_ID);
        filmModel = FilmModel.find(FilmModel.class, b);
        interstitialAd = new InterstitialAd(getActivity());
        interstitialAd.setAdUnitId(getString(R.string.admob_id));
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                interstitialAd.show();
                Tracker t = ((LEApplication) getActivity().getApplication()).getTracker(
                        LEApplication.TrackerName.APP_TRACKER);
                t.send(new HitBuilders.EventBuilder().setCategory("UX").setAction("ShowAds").build());
            }
        });
        overlay_black = RelativeLayout.class.cast(view.findViewById(R.id.overlay_black));
        draggableView = DraggableView.class.cast(view.findViewById(R.id.draggable_view));
        skProgress = SeekBar.class.cast(view.findViewById(R.id.skProgress));
        splayer = (SurfaceView) view.findViewById(R.id.surface);
        tvSubtitle = TextView.class.cast(view.findViewById(R.id.tvSubtitle));
        iv_state = ImageView.class.cast(view.findViewById(R.id.iv_state));
        lstSubtitle = ListView.class.cast(view.findViewById(R.id.lstSubtitle));
        tv_start_time = TextView.class.cast(view.findViewById(R.id.tv_start_time));
        tv_duration = TextView.class.cast(view.findViewById(R.id.tv_duration));
        iv_aspect = ImageView.class.cast(view.findViewById(R.id.iv_aspect));
        iv_rewind = ImageView.class.cast(view.findViewById(R.id.iv_rewind));
        iv_forward = ImageView.class.cast(view.findViewById(R.id.iv_forward));
        iv_min = ImageView.class.cast(view.findViewById(R.id.iv_min));
        iv_cc = ImageView.class.cast(view.findViewById(R.id.iv_cc));
        iv_setting = ImageView.class.cast(view.findViewById(R.id.iv_setting));
        draggableView.setClickToMaximizeEnabled(true);
        hookDraggablePanelListeners();

        skProgress.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        draggableView.setIsDisableDraggableView(true);
                        draggableView.setEnabled(false);
                        Log.d("TAG", "Disabled");
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        draggableView.setIsDisableDraggableView(false);
                        draggableView.setEnabled(true);
                        Log.d("TAG", "Enabled");
                        break;
                }
                waitHideOverlay();
                return false;
            }
        });
        draggableView.setOnTransfer(new DraggableView.OnTransferEventToView() {
            @Override
            public void onTransferToView(MotionEvent v) {
                int heightDraggableView = draggableView.getHeight();
                int widthDraggableView = draggableView.getWidth();
                int heightProgress = skProgress.getHeight();
                int widthProgress = skProgress.getWidth();

                float ratioWidth = widthDraggableView / widthProgress;
                float ratioHeight = heightDraggableView/heightProgress;

                float xDraggableView = v.getX() / ratioWidth;
                float yDraggableView = v.getY() /  ratioHeight;

                MotionEvent m  = MotionEvent.obtain(v.getDownTime(),v.getEventTime(),v.getAction(),xDraggableView,yDraggableView,v.getMetaState());
                skProgress.onTouchEvent(m);
            }
        });

        waitHideOverlay();

        Tracker t = ((LEApplication) getActivity().getApplication()).getTracker(
                LEApplication.TrackerName.APP_TRACKER);
        t.setScreenName("MyVideoFragment");
        t.send(new HitBuilders.AppViewBuilder().build());

        return view;
    }

    private void requestNewInterestial() {
        Tracker t = ((LEApplication) getActivity().getApplication()).getTracker(
                LEApplication.TrackerName.APP_TRACKER);
        t.send(new HitBuilders.EventBuilder().setCategory("UX").setAction("Request new Ads").build());
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        interstitialAd.loadAd(adRequest);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            draggableView.setTopViewHeight(size.y);
            draggableView.setIsDisableDraggableView(true);
            draggableView.setEnabled(false);
            resize();
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Resources r = getResources();
            int px = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 200, r.getDisplayMetrics()
            );
            draggableView.setTopViewHeight(px);
            draggableView.setIsDisableDraggableView(false);
            draggableView.setEnabled(true);
            resize();
        }
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new SubtitleListViewAdapter(getActivity(), subtitle);
        lstSubtitle.setItemsCanFocus(false);
        lstSubtitle.setAdapter(adapter);
        sholder = splayer.getHolder();
        sholder.setFormat(PixelFormat.RGBA_8888);
        sholder.addCallback(this);
        overlay_black.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (show_overlay) hideOverlay();
                else showOverlay();
            }
        });

        iv_aspect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    draggableView.setIsDisableDraggableView(true);
                    draggableView.setEnabled(false);
                } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    draggableView.setIsDisableDraggableView(false);
                    draggableView.setEnabled(true);
                }
                waitHideOverlay();
            }
        });

        iv_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {
                    if(isEnded){
                        seekTo(0);
                    }else {
                        if (mediaPlayer.isPlaying()) mediaPlayer.pause();
                        else mediaPlayer.start();
                    }
                    isEnded = false;
                }
                waitHideOverlay();
            }
        });

        iv_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forward();
                waitHideOverlay();
                Tracker t = ((LEApplication) getActivity().getApplication()).getTracker(
                        LEApplication.TrackerName.APP_TRACKER);
                t.send(new HitBuilders.EventBuilder().setCategory("UX").setAction("Forward Video").build());
            }
        });

        iv_rewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rewind();
                waitHideOverlay();
                Tracker t = ((LEApplication) getActivity().getApplication()).getTracker(
                        LEApplication.TrackerName.APP_TRACKER);
                t.send(new HitBuilders.EventBuilder().setCategory("UX").setAction("Rewind Video").build());
            }
        });
        iv_min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    draggableView.minimize();
                }
                waitHideOverlay();
            }
        });

        iv_cc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEnableCC) {
                    isEnableCC = false;
                    iv_cc.setImageResource(drawable_cc_gray);
                    tvSubtitle.setVisibility(View.GONE);
                    Tracker t = ((LEApplication) getActivity().getApplication()).getTracker(
                            LEApplication.TrackerName.APP_TRACKER);
                    t.send(new HitBuilders.EventBuilder().setCategory("UX").setAction("Disable CC").build());
                } else {
                    isEnableCC = true;
                    iv_cc.setImageResource(drawable_cc_white);
                    tvSubtitle.setVisibility(View.VISIBLE);
                    Tracker t = ((LEApplication) getActivity().getApplication()).getTracker(
                            LEApplication.TrackerName.APP_TRACKER);
                    t.send(new HitBuilders.EventBuilder().setCategory("UX").setAction("Enable CC").build());
                }
                waitHideOverlay();
            }
        });

        iv_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ChooseLangActivity.class));
            }
        });

        lstSubtitle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SubtitleModel s = (SubtitleModel) parent.getItemAtPosition(position);
                seekTo((int) s.getTime());
                Tracker t = ((LEApplication) getActivity().getApplication()).getTracker(
                        LEApplication.TrackerName.APP_TRACKER);
                t.send(new HitBuilders.EventBuilder().setCategory("UX").setAction("Seek subtitle").build());
            }
        });

    }

    private boolean show_overlay = true;
    private final long millsHideOverlay = 7000;

    private void hideOverlay() {
        ObjectAnimator.ofFloat(overlay_black, "alpha", 1, 0).start();
        enableControls(false);
        hideStatusbar();
        show_overlay = false;
        draggableView.setIsDisableDraggableView(false);
        draggableView.setEnabled(true);
    }

    private void enableControls(boolean isEnable) {
        iv_aspect.setVisibility(isEnable ? View.VISIBLE : View.GONE);
        iv_cc.setVisibility(isEnable ? View.VISIBLE : View.GONE);
        iv_forward.setVisibility(isEnable ? View.VISIBLE : View.GONE);
        iv_min.setVisibility(isEnable ? View.VISIBLE : View.GONE);
        iv_rewind.setVisibility(isEnable ? View.VISIBLE : View.GONE);
        iv_state.setVisibility(isEnable ? View.VISIBLE : View.GONE);
        tv_start_time.setVisibility(isEnable ? View.VISIBLE : View.GONE);
        tv_duration.setVisibility(isEnable ? View.VISIBLE : View.GONE);
        skProgress.setVisibility(isEnable ? View.VISIBLE : View.GONE);
        iv_setting.setVisibility(isEnable ? View.VISIBLE : View.GONE);
    }

    private final long millsRefreshMediaController = 100;
    private final int mills_show_ads = 600000;

    private void refreshMediaController() {
        handlerMediaController.post(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        tv_duration.setText(Utils.getTimeString(mediaPlayer.getDuration()));
                        tv_start_time.setText(Utils.getTimeString(mediaPlayer.getCurrentPosition()));
                        skProgress.setProgress((int) mediaPlayer.getCurrentPosition());
                        iv_state.setImageResource(drawable_pause_state);
                    } else {
                        iv_state.setImageResource(drawable_play_state);
                    }
                    if (!isShowedAds && mediaPlayer.getCurrentPosition() >= mills_show_ads) {
                        if (UserData.getInstance().didRemoveADs(getActivity())) {
                            isShowedAds = true;
                        } else {
                            requestNewInterestial();
                            isShowedAds = true;
                        }
                    }
                } else {
                    tv_duration.setText(Utils.getTimeString(0));
                    tv_start_time.setText(Utils.getTimeString(0));
                    skProgress.setProgress(0);
                    iv_state.setImageResource(drawable_play_state);
                }
                handlerMediaController.postDelayed(this, millsRefreshMediaController);
            }
        });
    }

    private void waitHideOverlay() {
        handlerHideOverlay.removeCallbacksAndMessages(null);
        handlerHideOverlay.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideOverlay();
            }
        }, millsHideOverlay);
    }

    private void showOverlay() {
        ObjectAnimator.ofFloat(overlay_black, "alpha", 0, 1).start();
        enableControls(true);
        show_overlay = true;
        waitHideOverlay();
    }

    /**
     * Hook the DraggableListener to DraggablePanel to pause or resume the video when the DragglabePanel is maximized
     * or closed.
     */
    private void hookDraggablePanelListeners() {
        draggableView.setDraggableListener(new DraggableListener() {
            @Override
            public void onMaximized() {
                hideStatusbar();
            }

            @Override
            public void onMinimized() {
                showStatusBar();
            }

            @Override
            public void onClosedToLeft() {
                relaMediaPlay();
                showStatusBar();
                getActivity().getSupportFragmentManager().beginTransaction().remove(MyVideoFragment.this).commit();
                ((MainActivity) getActivity()).setMyVideoFragment(null);
            }

            @Override
            public void onClosedToRight() {
                relaMediaPlay();
                showStatusBar();
                getActivity().getSupportFragmentManager().beginTransaction().remove(MyVideoFragment.this).commit();
                ((MainActivity) getActivity()).setMyVideoFragment(null);
            }

        });
    }

    public void maximize() {
        draggableView.maximize();
    }

    public FilmModel getFilmModel() {
        return filmModel;
    }

    private boolean isMediaPlayerLoaded;

    private void playVideo() {
        if(!new File(filmModel.getFilmPath()).exists()){
            new MaterialDialog.Builder(getActivity())
                    .title(getString(R.string.warning))
                    .content(getString(R.string.cant_play_video))
                    .positiveText(getString(R.string.argee))
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            relaMediaPlay();
                            showStatusBar();
                            getActivity().getSupportFragmentManager().beginTransaction().remove(MyVideoFragment.this).commit();
                            ((MainActivity) getActivity()).setMyVideoFragment(null);
                        }
                    })
                    .cancelable(false)
                    .show();
        }else {
            try {
                mediaPlayer = new MediaPlayer(getActivity());
                mediaPlayer.setDataSource(filmModel.getFilmPath());
                mediaPlayer.setDisplay(sholder);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(this);
                mediaPlayer.setOnCompletionListener(this);
                mediaPlayer.setOnErrorListener(this);
            } catch (Exception e) {
                e.printStackTrace();
                isMediaPlayerLoaded = false;
                new MaterialDialog.Builder(getActivity())
                        .title("Warning")
                        .content("Cannot play video, please re-check the path of your video")
                        .positiveText("Agree")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                relaMediaPlay();
                                showStatusBar();
                                getActivity().getSupportFragmentManager().beginTransaction().remove(MyVideoFragment.this).commit();
                                ((MainActivity) getActivity()).setMyVideoFragment(null);
                            }
                        })
                        .cancelable(false)
                        .show();
            }
        }
    }

    private void startVPback() {
        if(mediaPlayer!=null)
            mediaPlayer.start();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        playVideo();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    private void rewind() {
        if (mediaPlayer != null) {
            seekTo((int) (mediaPlayer.getCurrentPosition() - 10000));
        }
    }

    private void forward() {
        if (mediaPlayer != null) {
            seekTo((int) (mediaPlayer.getCurrentPosition() + 10000));
        }
    }

    private void seekTo(int position) {
        if (mediaPlayer != null) {
            tvSubtitle.setText("");
            ArrayList<SubtitleModel> clone = new ArrayList<SubtitleModel>();
            clone.addAll(subtitle);
            subtitle.clear();
            for (SubtitleModel s : clone) {
                if (s.getTime() <= position)
                    addSubtitle(s);
            }
            adapter.notifyDataSetChanged();
            mediaPlayer.pause();
            mediaPlayer.seekTo(position);
            mediaPlayer.start();
        }
    }

    private void addSubtitle(SubtitleModel s) {
        if (!subtitle.contains(s)) {
            subtitle.add(s);
            adapter.notifyDataSetChanged();
        }
    }

    private void sortSubtitle() {
        Collections.sort(subtitle, new Comparator<SubtitleModel>() {
            @Override
            public int compare(SubtitleModel lhs, SubtitleModel rhs) {
                return (int) (lhs.getTime() - rhs.getTime());
            }
        });
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        isMediaPlayerLoaded = true;


        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                FormatSRT formatSRT = new FormatSRT();
                try {
                    InputStream is = new FileInputStream(filmModel.getSubtitlePath());
                    srt = formatSRT.parseFile(filmModel.getSubtitlePath(), is);
                    return srt.captions.size() > 0;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean b) {

                if (!b) {
                    if(isAdded()) {
                        new MaterialDialog.Builder(getActivity())
                                .title(getString(R.string.warning))
                                .content(getString(R.string.cant_parse_subtitle))
                                .positiveText(getString(R.string.argee))
                                .show();
                    }
                } else {
                    subtitleDisplayHandler.post(subtitleProcessesor);
                }
                startVPback();
                seekTo((int) filmModel.getTime());
            }
        }.execute();

        skProgress.setMax((int) mediaPlayer.getDuration());
        skProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null)
                    mediaPlayer.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null)
                    mediaPlayer.start();
            }
        });
        resize();
        refreshMediaController();
    }

    private Handler subtitleDisplayHandler = new Handler();
    private Runnable subtitleProcessesor = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                long currentPos = mediaPlayer.getCurrentPosition();
                Object[] subtitles = srt.captions.values().toArray();
                for (Object o : subtitles) {
                    Caption caption = Caption.class.cast(o);
                    if (currentPos >= caption.start.getMseconds()
                            && currentPos <= caption.end.getMseconds()) {
                        onTimedText(caption);
                        break;
                    } else if (currentPos > caption.end.getMseconds()) {
                        onTimedText(null);
                    }
                }
            }
            subtitleDisplayHandler.postDelayed(this, 100);
        }
    };

    public void onTimedText(Caption text) {
        if (text != null && !TextUtils.isEmpty(text.content)) {
            if (isEnableCC) tvSubtitle.setVisibility(View.VISIBLE);
            SubtitleModel sub = new SubtitleModel();
            sub.setSubtitle(text.content);
            sub.setTime(text.start.getMseconds());
            addSubtitle(sub);
            tvSubtitle.setText(Html.fromHtml(text.content));
        } else {
            tvSubtitle.setVisibility(View.GONE);

        }
    }

    private void relaMediaPlay() {
        if (mediaPlayer != null) {
            filmModel.setTime(mediaPlayer.getCurrentPosition());
            filmModel.save();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        splayer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();
        splayer.setVisibility(View.GONE);
        relaMediaPlay();
        showStatusBar();
        subtitleDisplayHandler.removeCallbacksAndMessages(null);
        handlerMediaController.removeCallbacksAndMessages(null);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        splayer.setVisibility(View.GONE);
        relaMediaPlay();
        showStatusBar();
        subtitleDisplayHandler.removeCallbacksAndMessages(null);
        handlerMediaController.removeCallbacksAndMessages(null);
    }

    private void resize() {
        if (mediaPlayer != null && isMediaPlayerLoaded) {
            int videoWidth = mediaPlayer.getVideoWidth();
            int videoHeight = mediaPlayer.getVideoHeight();
            float videoProportion = (float) videoWidth / (float) videoHeight;

            int screenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
            int screenHeight = getActivity().getWindowManager().getDefaultDisplay().getHeight();
            float screenProportion = (float) screenWidth / (float) screenHeight;
            android.view.ViewGroup.LayoutParams lp = splayer.getLayoutParams();

            if (videoProportion > screenProportion) {
                lp.width = screenWidth;
                lp.height = (int) ((float) screenWidth / videoProportion);
            } else {
                lp.width = (int) (videoProportion * (float) screenHeight);
                lp.height = screenHeight;
            }
            splayer.setLayoutParams(lp);
        }
    }

    private void hideStatusbar() {
        try {
            //show status bar
            if (Build.VERSION.SDK_INT < 16) {
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else {
                View decorView = getActivity().getWindow().getDecorView();
                // show the status bar.
                int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showStatusBar() {
        try {
            if (Build.VERSION.SDK_INT < 16) {
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else {
                View decorView = getActivity().getWindow().getDecorView();
                // Hide the status bar.
                int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
                decorView.setSystemUiVisibility(uiOptions);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        new MaterialDialog.Builder(getActivity())
                .title(getString(R.string.warning))
                .content(getString(R.string.cant_play_video))
                .positiveText(getString(R.string.argee))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        relaMediaPlay();
                        showStatusBar();
                        getActivity().getSupportFragmentManager().beginTransaction().remove(MyVideoFragment.this).commit();
                        ((MainActivity) getActivity()).setMyVideoFragment(null);
                    }
                })
                .cancelable(false)
                .show();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        filmModel.setTime(0);
        filmModel.save();
        isEnded = true;
    }
}
