

package yepeer.le.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gc.materialdesign.views.ButtonRectangle;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.github.ksoichiro.android.observablescrollview.Scrollable;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

import libaries.FlexibleSpaceWithImageBaseFragment;
import libaries.util.IabHelper;
import libaries.util.IabResult;
import libaries.util.Inventory;
import libaries.util.Purchase;
import yepeer.le.ChooseLangActivity;
import yepeer.le.Constants;
import yepeer.le.LEApplication;
import yepeer.le.MainActivity;
import yepeer.le.R;
import yepeer.le.objects.TargetForeign;
import yepeer.le.save.UserData;


/*
 * DuongNT : create inappp purchase ( include restore )
 */
public class SettingScrollViewFragment extends FlexibleSpaceWithImageBaseFragment<ObservableScrollView> {
    private ButtonRectangle btnChooseCountry;
    private ButtonRectangle btnRemoveAds;
    private ButtonRectangle btnRemoveAdsGive;
    private ButtonRectangle btnRateMe,btnLikeUs;
    private IabHelper mHelper;
    static final int RC_REQUEST = 10001;
    final String TAG = "TAG";

    private void reloadCountry() {
        String s = UserData.getInstance().getTargetForeign(getActivity());
        ArrayList<TargetForeign> lists = Constants.getTargetForeign();
        TargetForeign t = null;
        if (s != null) {
            for (TargetForeign f : lists) {
                if (f.getId().equals(s)) {
                    t = f;
                    break;
                }
            }
        }
        if (t == null) {
            btnChooseCountry.setText(getString(R.string.choose_ur_country) + " : N/A");
        } else
            btnChooseCountry.setText(getString(R.string.choose_ur_country) + " : " + t.getName());
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadCountry();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_scroll_view, container, false);
        btnChooseCountry = (ButtonRectangle) view.findViewById(R.id.btnChooseCountry);
        btnRemoveAds = (ButtonRectangle) view.findViewById(R.id.btnRemoveAds);
        btnRemoveAdsGive = (ButtonRectangle) view.findViewById(R.id.btnRemoveGive);
        btnRateMe = (ButtonRectangle)view.findViewById(R.id.btnRate);
        btnLikeUs = (ButtonRectangle)view.findViewById(R.id.btnLikeUs);
        reloadCountry();
        btnChooseCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ChooseLangActivity.class));
            }
        });
        final ObservableScrollView scrollView = (ObservableScrollView) view.findViewById(R.id.scroll);
        // TouchInterceptionViewGroup should be a parent view other than ViewPager.
        // This is a workaround for the issue #117:
        // https://github.com/ksoichiro/Android-ObservableScrollView/issues/117
        scrollView.setTouchInterceptionViewGroup((ViewGroup) view.findViewById(R.id.fragment_root));

        // Scroll to the specified offset after layout
        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_SCROLL_Y)) {
            final int scrollY = args.getInt(ARG_SCROLL_Y, 0);
            ScrollUtils.addOnGlobalLayoutListener(scrollView, new Runnable() {
                @Override
                public void run() {
                    scrollView.scrollTo(0, scrollY);
                }
            });
            updateFlexibleSpace(scrollY, view);
        } else {
            updateFlexibleSpace(0, view);
        }

        scrollView.setScrollViewCallbacks(this);


        mHelper = new IabHelper(getActivity(), Constants.BASE_64);
        mHelper.enableDebugLogging(true);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });

        btnRateMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String appPackageName = getActivity().getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });

        btnLikeUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/yepeer")));
            }
        });

        refreshUI();
        return view;
    }

    private void refreshUI(){
        if(UserData.getInstance().didRemoveADs(getActivity())){
            btnRemoveAds.setVisibility(View.GONE);
            btnRemoveAdsGive.setText(getString(R.string.did_remove_ads));
            btnRemoveAdsGive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onlyGive();
                }
            });
        }else{
            btnRemoveAds.setVisibility(View.VISIBLE);
            btnRemoveAds.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeAds();
                }
            });
            btnRemoveAds.setText(getString(R.string.remove_ads));
            btnRemoveAdsGive.setText(getString(R.string.remove_ads_and_give_cf));
            btnRemoveAdsGive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeAdsAndGive();
                }
            });
        }
    }
    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");
            if (mHelper == null) return;
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }
            Log.d(TAG, "Query inventory was successful.");
            Purchase removeAds = inventory.getPurchase(Constants.SKU_REMOVE_ADS);
            Purchase removeAdsGive = inventory.getPurchase(Constants.SKU_REMOVE_ADS_GIVE);
            if (removeAds != null || removeAdsGive != null) {
                Log.d(TAG, "User buyed remove ads");
                UserData.getInstance().setDidRemoveAds(getActivity());
            }
            refreshUI();
            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                complain("Error purchasing: " + result);
                return;
            }

            Log.d(TAG, "Purchase successful.");

            if (purchase.getSku().equals(Constants.SKU_REMOVE_ADS)) {
                // bought the premium upgrade!
                Log.d(TAG, "Purchase is premium upgrade. Congratulating user.");
                alert("Thank you for upgrading to premium!");
                UserData.getInstance().setDidRemoveAds(getActivity());
                Tracker t = ((LEApplication) getActivity().getApplication()).getTracker(
                        LEApplication.TrackerName.APP_TRACKER);
                t.send(new HitBuilders.EventBuilder().setCategory("UX").setAction("Remove Ads").build());
            }else if(purchase.getSku().equals(Constants.SKU_REMOVE_ADS_GIVE)){
                UserData.getInstance().setDidRemoveAds(getActivity());
                Log.d(TAG, "Purchase is premium upgrade. Congratulating user.");
                alert("Thank you for upgrading to premium!");
                Tracker t = ((LEApplication) getActivity().getApplication()).getTracker(
                        LEApplication.TrackerName.APP_TRACKER);
                t.send(new HitBuilders.EventBuilder().setCategory("UX").setAction("Remove ads and give coffee").build());
            }else if(purchase.getSku().equals(Constants.SKU_GIVE)){
                UserData.getInstance().setDidRemoveAds(getActivity());
                Log.d(TAG, "Purchase is premium upgrade. Congratulating user.");
                alert("Thank you for upgrading to premium!");
                Tracker t = ((LEApplication) getActivity().getApplication()).getTracker(
                        LEApplication.TrackerName.APP_TRACKER);
                t.send(new HitBuilders.EventBuilder().setCategory("UX").setAction("Only give").build());
            }
            refreshUI();
        }
    };

    private void removeAds() {
        if(UserData.getInstance().didRemoveADs(getActivity())) return;
        Tracker t = ((LEApplication) getActivity().getApplication()).getTracker(
                LEApplication.TrackerName.APP_TRACKER);
        t.send(new HitBuilders.EventBuilder().setCategory("UX").setAction("Remove ads").build());
        mHelper.launchPurchaseFlow(getActivity(), Constants.SKU_REMOVE_ADS, RC_REQUEST,mPurchaseFinishedListener);
    }

    private void removeAdsAndGive(){
        if(UserData.getInstance().didRemoveADs(getActivity())) return;
        Tracker t = ((LEApplication) getActivity().getApplication()).getTracker(
                LEApplication.TrackerName.APP_TRACKER);
        t.send(new HitBuilders.EventBuilder().setCategory("UX").setAction("Remove ads and give").build());
        mHelper.launchPurchaseFlow(getActivity(), Constants.SKU_REMOVE_ADS_GIVE, RC_REQUEST,mPurchaseFinishedListener);
    }

    private void onlyGive(){
        Tracker t = ((LEApplication) getActivity().getApplication()).getTracker(
                LEApplication.TrackerName.APP_TRACKER);
        t.send(new HitBuilders.EventBuilder().setCategory("UX").setAction("Only give").build());
        mHelper.launchPurchaseFlow(getActivity(), Constants.SKU_GIVE, RC_REQUEST,mPurchaseFinishedListener);
    }

    void complain(String message) {
        Log.e("TAG", "**** LE Error: " + message);
        alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(getActivity());
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d("TAG", "Showing alert dialog: " + message);
        bld.create().show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }
    }

    @Override
    public void updateFlexibleSpace(int scrollY) {
        // Sometimes scrollable.getCurrentScrollY() and the real scrollY has different values.
        // As a workaround, we should call scrollVerticallyTo() to make sure that they match.
        Scrollable s = getScrollable();
        s.scrollVerticallyTo(scrollY);

        // If scrollable.getCurrentScrollY() and the real scrollY has the same values,
        // calling scrollVerticallyTo() won't invoke scroll (or onScrollChanged()), so we call it here.
        // Calling this twice is not a problem as long as updateFlexibleSpace(int, View) has idempotence.
        updateFlexibleSpace(scrollY, getView());
    }

    @Override
    protected void updateFlexibleSpace(int scrollY, View view) {
        ObservableScrollView scrollView = (ObservableScrollView) view.findViewById(R.id.scroll);

        // Also pass this event to parent Activity
        MainActivity parentActivity =
                (MainActivity) getActivity();
        if (parentActivity != null) {
            parentActivity.onScrollChanged(scrollY, scrollView);
        }
    }
}
