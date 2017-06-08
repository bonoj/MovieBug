package com.hk47.popularmoviesstageone.ui;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hk47.popularmoviesstageone.R;
import com.hk47.popularmoviesstageone.data.MyFavorite;
import com.hk47.popularmoviesstageone.data.MyMovie;
import com.hk47.popularmoviesstageone.utilities.PreferenceUtils;

public class MovieListActivity extends AppCompatActivity implements OnMovieSelectionChangeListener, OnFavoritesChangeListener {

    public static final String LIST_FRAGMENT_TAG = "com.hk47.popularmoviesstageone.ui.LIST_FRAGMENT_TAG";
    public static final String API_DETAILS_FRAGMENT_TAG = "com.hk47.popularmoviesstageone.ui.API_DETAILS_FRAGMENT_TAG";
    public static final String FAVORITE_DETAILS_FRAGMENT_TAG = "com.hk47.popularmoviesstageone.ui.FAVORITE_DETAILS_FRAGMENT_TAG";
    public static final String UNIQUE_FRAGMENT_BUNDLE_TAG = "com.hk47.popularmoviesstageone.ui.UNIQUE_INTENT_ID";

    private static final String STARTUP_KEY = "com.hk47.popularmoviesstageone.ui.STARTUP_KEY";

    private MovieListFragment mFragment;

    private boolean mIsDualPane;
    private boolean mIsTabletPortrait;
    private boolean mIsTabletPortraitBackPressed;
    private boolean mIsStartup;

    private TextView mWelcomeSplash;
    private LinearLayout.LayoutParams mWidthZero;
    private LinearLayout.LayoutParams mWidthMatchParent;
    private View mPortraitListView;
    private FrameLayout mPortraitDetailsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIsDualPane = getResources().getBoolean(R.bool.is_dual_pane);
        mIsTabletPortrait = getResources().getBoolean(R.bool.is_tablet_portrait);

        mIsDualPane = getResources().getBoolean(R.bool.is_dual_pane);
        mIsTabletPortrait = getResources().getBoolean(R.bool.is_tablet_portrait);

        // Tablet Logic
        if (mIsDualPane) {
            setContentView(R.layout.dual_pane);
            mWidthZero = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
            mWidthMatchParent = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            mPortraitListView = findViewById(R.id.dual_list);
            mPortraitDetailsView = (FrameLayout) findViewById(R.id.dual_details);
            mWelcomeSplash = (TextView) findViewById(R.id.dual_details_welcome);

            if (savedInstanceState != null) {
                mIsStartup = savedInstanceState.getBoolean(STARTUP_KEY);
            } else {
                mIsStartup = true;
                PreferenceUtils.setStartupCheck(this, true);
            }
        } else {
            setContentView(R.layout.activity_movie_list);
        }

        // Single pane, non tablet logic
        if (!mIsDualPane && !mIsTabletPortrait) {
            if (savedInstanceState != null) { // Saved instance state, fragment may exist
                // Look up the instance that already exists by tag
                mFragment = (MovieListFragment) getSupportFragmentManager().findFragmentByTag(LIST_FRAGMENT_TAG);
            } else if (mFragment == null) {
                // Only create fragment if it doesn't exist
                mFragment = new MovieListFragment();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        if (mIsDualPane) {
            outState.putBoolean(STARTUP_KEY, PreferenceUtils.getStartupCheck(this));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mIsDualPane) {
            mIsStartup = PreferenceUtils.getStartupCheck(this);
            mIsTabletPortraitBackPressed = PreferenceUtils.getTabletPortraitBackPress(this);

            // Displays the welcome splash if necessary
            if (mIsStartup) {
                mWelcomeSplash.setVisibility(View.VISIBLE);
                mPortraitDetailsView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            }

            if (mIsTabletPortraitBackPressed) {
                mPortraitDetailsView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            }

            if (mIsTabletPortrait) {
                mPortraitListView.setLayoutParams(mWidthMatchParent);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (mIsDualPane) {
            if (!mIsStartup && !mIsTabletPortrait) {
                mWelcomeSplash.setVisibility(View.GONE);
                mPortraitDetailsView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                finish();
            }
            if (mIsTabletPortrait) {
                if (mPortraitListView.getLayoutParams() == mWidthMatchParent) {
                    finish();
                } else {
                    PreferenceUtils.setTabletPortraitBackPress(this, true);
                    mPortraitListView.setLayoutParams(mWidthMatchParent);
                    mPortraitDetailsView.setLayoutParams(mWidthZero);
                }
            }
        }
    }

    private void handleDualPaneClick() {
        if (mIsStartup) {
            mIsStartup = false;
            PreferenceUtils.setStartupCheck(this, false);
            mPortraitDetailsView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            mWelcomeSplash.setVisibility(View.GONE);
        }
        if (mIsTabletPortrait) {
            mPortraitListView.setLayoutParams(mWidthZero);
            mPortraitDetailsView.setLayoutParams(mWidthMatchParent);
        }
        if (mIsTabletPortraitBackPressed) {
            PreferenceUtils.setTabletPortraitBackPress(this, false);
            mPortraitDetailsView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
    }

    @Override
    public void OnMovieSelectionChanged(MyMovie apiDetails) {

        if (mIsDualPane) {
            handleDualPaneClick();
        }

        getSupportFragmentManager().popBackStack();

        MovieDetailFragment newMovieDetailFragment = new MovieDetailFragment();
        Bundle args = new Bundle();

        args.putString(UNIQUE_FRAGMENT_BUNDLE_TAG, API_DETAILS_FRAGMENT_TAG);
        args.putParcelable(API_DETAILS_FRAGMENT_TAG, apiDetails);
        newMovieDetailFragment.setArguments(args);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.dual_details, newMovieDetailFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void OnMovieSelectionChanged(MyFavorite favoriteDetails) {

        if (mIsDualPane) {
            handleDualPaneClick();
        }

        getSupportFragmentManager().popBackStack();

        MovieDetailFragment newMovieDetailFragment = new MovieDetailFragment();
        Bundle args = new Bundle();

        args.putString(UNIQUE_FRAGMENT_BUNDLE_TAG, FAVORITE_DETAILS_FRAGMENT_TAG);
        args.putParcelable(FAVORITE_DETAILS_FRAGMENT_TAG, favoriteDetails);
        newMovieDetailFragment.setArguments(args);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.dual_details, newMovieDetailFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void OnFavoriteChanged() {

        MovieListFragment movieListFragment = (MovieListFragment) getSupportFragmentManager().findFragmentById(R.id.dual_list);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.detach(movieListFragment);
        fragmentTransaction.attach(movieListFragment);
        fragmentTransaction.commit();
    }
}