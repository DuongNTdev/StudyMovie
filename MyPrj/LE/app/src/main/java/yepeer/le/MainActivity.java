package yepeer.le;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.CacheFragmentStatePagerAdapter;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.github.ksoichiro.android.observablescrollview.Scrollable;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import io.vov.vitamio.LibsChecker;
import libaries.BaseActivity;
import libaries.FlexibleSpaceWithImageBaseFragment;
import libaries.SlidingTabLayout;
import yepeer.le.fragments.FilmRecyclerViewFragment;
import yepeer.le.fragments.MyVideoFragment;
import yepeer.le.fragments.SettingScrollViewFragment;
import yepeer.le.save.UserData;

public class MainActivity extends BaseActivity {
    //save our header or result
//    private AccountHeader headerResult = null;
//    private Drawer result = null;
    protected static final float MAX_TEXT_SCALE_DELTA = 0.3f;
    private MyVideoFragment myVideoFragment;
    private ViewPager mPager;
    private NavigationAdapter mPagerAdapter;
    private SlidingTabLayout mSlidingTabLayout;
    private int mFlexibleSpaceHeight;
    private int mTabHeight;
    private View statusCheat;
    private ImageView image;

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void loadImage(){
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        if(timeOfDay >= 0 && timeOfDay < 12){
            Picasso.with(this).load(R.drawable.morning).into(image);
        }else if(timeOfDay >= 12 && timeOfDay < 16){
            Picasso.with(this).load(R.drawable.afternoon).into(image);
        }else if(timeOfDay >= 16 && timeOfDay < 21){
            Picasso.with(this).load(R.drawable.afternoon).into(image);
        }else if(timeOfDay >= 21 && timeOfDay < 24){
            Picasso.with(this).load(R.drawable.night).into(image);
        }
    }
//    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!LibsChecker.checkVitamioLibs(this)) {
            return;
        }
        if(!UserData.getInstance().isOpenedAppIntro(this)){
            startActivity(new Intent(this,IntroActivity.class));
        }
        setContentView(R.layout.activity_main);
        image = (ImageView)findViewById(R.id.image);
        loadImage();
        //Remove line to test RTL support
        //getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        // Handle Toolbar
//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        // Create a few sample profile
        // NOTE you have to define the loader logic too. See the CustomApplication for more details
//        final IProfile profile = new ProfileDrawerItem().withName("Mike Penz").withEmail("mikepenz@gmail.com").withIcon("https://avatars3.githubusercontent.com/u/1476232?v=3&s=460");
//        // Create the AccountHeader
//        headerResult = new AccountHeaderBuilder()
//                .withActivity(this)
//                .withHeaderBackground(R.drawable.header)
//                .withAccountSwitcherArrowVisible(false)
//                .withSavedInstance(savedInstanceState)
//                .build();
//
//        //Create the drawer
//        result = new DrawerBuilder()
//                .withActivity(this)
//                .withToolbar(toolbar)
//                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
//                .addDrawerItems(
//
//                ) // add the items we want to use with our Drawer
//                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
//                    @Override
//                    public boolean onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
//                        return false;
//                    }
//                })
//                .withSavedInstance(savedInstanceState)
//                .withShowDrawerOnFirstLaunch(true)
//                .build();
        mPagerAdapter = new NavigationAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setOffscreenPageLimit(2);
        statusCheat = (View)findViewById(R.id.statusCheat);
        mPager.setAdapter(mPagerAdapter);
        mFlexibleSpaceHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mTabHeight = getResources().getDimensionPixelSize(R.dimen.tab_height);
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) statusCheat.getLayoutParams();
            p.height = getStatusBarHeight();
            statusCheat.setLayoutParams(p);
            statusCheat.setVisibility(View.VISIBLE);
        }else statusCheat.setVisibility(View.GONE);
        TextView titleView = (TextView) findViewById(R.id.title);
        titleView.setText("Study Movie");

        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.accent));
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mPager);

        // Initialize the first Fragment's state when layout is completed.
        ScrollUtils.addOnGlobalLayoutListener(mSlidingTabLayout, new Runnable() {
            @Override
            public void run() {
                translateTab(0, false);
            }
        });

        //only set the active selection or active profile if we do not recreate the activity
//        if (savedInstanceState == null) {
//            // set the selection to the item with the identifier 11
//            result.setSelectionByIdentifier(11, false);
//
//            //set the active profile
//            headerResult.setActiveProfile(profile);
//        }
    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        //add the values which need to be saved from the drawer to the bundle
//        outState = result.saveInstanceState(outState);
//        //add the values which need to be saved from the accountHeader to the bundle
//        outState = headerResult.saveInstanceState(outState);
//        super.onSaveInstanceState(outState);
//    }

//    @Override
//    public void onBackPressed() {
//        //handle the back press :D close the drawer first and if the drawer is closed close the activity
//        if (result != null && result.isDrawerOpen()) {
//            result.closeDrawer();
//        } else {
//            super.onBackPressed();
//        }
//    }

    /**
     * Called by children Fragments when their scrollY are changed.
     * They all call this method even when they are inactive
     * but this Activity should listen only the active child,
     * so each Fragments will pass themselves for Activity to check if they are active.
     *
     * @param scrollY scroll position of Scrollable
     * @param s       caller Scrollable view
     */
    public void onScrollChanged(int scrollY, Scrollable s) {
        FlexibleSpaceWithImageBaseFragment fragment =
                (FlexibleSpaceWithImageBaseFragment) mPagerAdapter.getItemAt(mPager.getCurrentItem());
        if (fragment == null) {
            return;
        }
        View view = fragment.getView();
        if (view == null) {
            return;
        }
        Scrollable scrollable = (Scrollable) view.findViewById(R.id.scroll);
        if (scrollable == null) {
            return;
        }
        if (scrollable == s) {
            // This method is called by not only the current fragment but also other fragments
            // when their scrollY is changed.
            // So we need to check the caller(S) is the current fragment.
            int adjustedScrollY = Math.min(scrollY, mFlexibleSpaceHeight - mTabHeight);
            translateTab(adjustedScrollY, false);
            propagateScroll(adjustedScrollY);
        }
    }

    private void translateTab(int scrollY, boolean animated) {
        int flexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        int tabHeight = getResources().getDimensionPixelSize(R.dimen.tab_height);
        View imageView = findViewById(R.id.image);
        View overlayView = findViewById(R.id.overlay);
        TextView titleView = (TextView) findViewById(R.id.title);

        // Translate overlay and image
        float flexibleRange = flexibleSpaceImageHeight - getActionBarSize();
        int minOverlayTransitionY = tabHeight - overlayView.getHeight();
        ViewHelper.setTranslationY(overlayView, ScrollUtils.getFloat(-scrollY, minOverlayTransitionY, 0));
        ViewHelper.setTranslationY(imageView, ScrollUtils.getFloat(-scrollY / 2, minOverlayTransitionY, 0));

        // Change alpha of overlay
        ViewHelper.setAlpha(overlayView, ScrollUtils.getFloat((float) scrollY / flexibleRange, 0, 1));

        // Scale title text
        float scale = 1 + ScrollUtils.getFloat((flexibleRange - scrollY - tabHeight) / flexibleRange, 0, MAX_TEXT_SCALE_DELTA);
        setPivotXToTitle(titleView);
        ViewHelper.setPivotY(titleView, 0);
        ViewHelper.setScaleX(titleView, scale);
        ViewHelper.setScaleY(titleView, scale);

        // Translate title text
        int maxTitleTranslationY = flexibleSpaceImageHeight - tabHeight - getActionBarSize();
        int titleTranslationY = maxTitleTranslationY - scrollY;
        ViewHelper.setTranslationY(titleView, titleTranslationY);
//        if(titleTranslationY == (-getActionBarSize())){
//            toolbar.setTitle(titleView.getText().toString());
//        }else {
//            toolbar.setTitle("");
//        }

        // If tabs are moving, cancel it to start a new animation.
        ViewPropertyAnimator.animate(mSlidingTabLayout).cancel();
        // Tabs will move between the top of the screen to the bottom of the image.
        float translationY = ScrollUtils.getFloat(-scrollY + mFlexibleSpaceHeight - mTabHeight, 0, mFlexibleSpaceHeight - mTabHeight);
        if (animated) {
            // Animation will be invoked only when the current tab is changed.
            ViewPropertyAnimator.animate(mSlidingTabLayout)
                    .translationY(translationY)
                    .setDuration(200)
                    .start();
        } else {
            // When Fragments' scroll, translate tabs immediately (without animation).
            ViewHelper.setTranslationY(mSlidingTabLayout, translationY);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setPivotXToTitle(View view) {
        final TextView mTitleView = (TextView) view.findViewById(R.id.title);
        Configuration config = getResources().getConfiguration();
        if (Build.VERSION_CODES.JELLY_BEAN_MR1 <= Build.VERSION.SDK_INT
                && config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            ViewHelper.setPivotX(mTitleView, view.findViewById(android.R.id.content).getWidth());
        } else {
            ViewHelper.setPivotX(mTitleView, 0);
        }
    }

    private void propagateScroll(int scrollY) {
        // Set scrollY for the fragments that are not created yet
        mPagerAdapter.setScrollY(scrollY);

        // Set scrollY for the active fragments
        for (int i = 0; i < mPagerAdapter.getCount(); i++) {
            // Skip current item
            if (i == mPager.getCurrentItem()) {
                continue;
            }

            // Skip destroyed or not created item
            FlexibleSpaceWithImageBaseFragment f =
                    (FlexibleSpaceWithImageBaseFragment) mPagerAdapter.getItemAt(i);
            if (f == null) {
                continue;
            }

            View view = f.getView();
            if (view == null) {
                continue;
            }
            f.setScrollY(scrollY, mFlexibleSpaceHeight);
            f.updateFlexibleSpace(scrollY);
        }
    }

    /**
     * This adapter provides three types of fragments as an example.
     * {@linkplain #createItem(int)} should be modified if you use this example for your app.
     */
    private static class NavigationAdapter extends CacheFragmentStatePagerAdapter {

        private static final String[] TITLES = new String[]{"My Video","Setting"};

        private int mScrollY;

        public NavigationAdapter(FragmentManager fm) {
            super(fm);
        }

        public void setScrollY(int scrollY) {
            mScrollY = scrollY;
        }

        @Override
        protected Fragment createItem(int position) {
            if(position==0) {
                FilmRecyclerViewFragment f = new FilmRecyclerViewFragment();
                f.setArguments(mScrollY);
                return f;
            }else{
                SettingScrollViewFragment f = new SettingScrollViewFragment();
                f.setArguments(mScrollY);
                return f;
            }
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }
    }

//    public void hideToolbars() {
//        toolbar.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
//    }
//
//    public void showToolbars(){
//        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
//    }

//    public void setDrawerState(boolean isEnabled) {
//        if ( isEnabled ) {
//            result.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
//            result.getActionBarDrawerToggle().onDrawerStateChanged(DrawerLayout.STATE_IDLE);
//            result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
//            result.getActionBarDrawerToggle().syncState();
//        }
//        else {
//            result.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//            result.getActionBarDrawerToggle().onDrawerStateChanged(DrawerLayout.STATE_DRAGGING);
//            result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
//            result.getActionBarDrawerToggle().syncState();
//        }
//    }

    public MyVideoFragment getMyVideoFragment() {
        return myVideoFragment;
    }

    public void setMyVideoFragment(MyVideoFragment myVideoFragment) {
        this.myVideoFragment = myVideoFragment;
    }

}
