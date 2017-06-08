package com.hk47.popularmoviesstageone.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hk47.popularmoviesstageone.R;
import com.hk47.popularmoviesstageone.data.MovieContract;
import com.hk47.popularmoviesstageone.data.MyFavorite;
import com.hk47.popularmoviesstageone.data.MyMovie;
import com.hk47.popularmoviesstageone.data.MyMovieDetail;
import com.hk47.popularmoviesstageone.utilities.DatabaseUtils;
import com.hk47.popularmoviesstageone.utilities.MoviesJsonUtils;
import com.hk47.popularmoviesstageone.utilities.QueryBuilderUtils;

import java.util.ArrayList;

import static com.hk47.popularmoviesstageone.ui.MovieListFragment.DETAIL_DATA;
import static com.hk47.popularmoviesstageone.ui.MovieListFragment.FAVORITE_DATA;
import static com.hk47.popularmoviesstageone.ui.MovieListFragment.UNIQUE_INTENT_ID;

public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int INDIVIDUAL_FAVORITE_LOADER_ID = 7;

    private Activity mActivity;
    private boolean mIsDualPane;
    private boolean mIsTabletPortrait;

    private View mRootView;

    private ImageView mPosterImageView;
    private TextView mTitleTextView;
    private TextView mOverviewTextView;
    private TextView mReleaseDateTextView;
    private TextView mVoteAverageTextView;
    private TextView mRunTimeTextView;
    private ImageView mAddFavoriteButton;
    private LinearLayout mTrailersContainer;
    private LinearLayout mReviewsContainer;

    private String mMovieId;
    private String mMovieTitle;
    private String mPosterUrl;
    private String mOverview;
    private String mReleaseDate;
    private String mVoteAverage;
    private String mRuntime;
    private String mTrailerString;
    private String mReviewsString;

    private ArrayList<String[]> mTrailers;
    private ArrayList<String[]> mReviews;

    private boolean mIsFavorite;
    private String mUriStringId;

    private OnFavoritesChangeListener mOnFavoritesChangeListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        mActivity = getActivity();
        mIsDualPane = getResources().getBoolean(R.bool.is_dual_pane);
        mIsTabletPortrait = getResources().getBoolean(R.bool.is_tablet_portrait);

        getViewReferences();

        // Tablet logic
        if (mIsDualPane) {

            Bundle args = getArguments();
            if (args != null) {
                String uniqueFragmentBundleTag = args.getString(MovieListActivity.UNIQUE_FRAGMENT_BUNDLE_TAG);
                if (uniqueFragmentBundleTag.equals(MovieListActivity.FAVORITE_DETAILS_FRAGMENT_TAG)) {
                    MyFavorite favoriteDetails = args.getParcelable(MovieListActivity.FAVORITE_DETAILS_FRAGMENT_TAG);
                    updateDualPaneFavoriteDetails(favoriteDetails);
                }
                if (uniqueFragmentBundleTag.equals(MovieListActivity.API_DETAILS_FRAGMENT_TAG)) {
                    MyMovie apiDetails = args.getParcelable(MovieListActivity.API_DETAILS_FRAGMENT_TAG);
                    updateDualPaneApiDetails(apiDetails);
                }
            }
        }

        // Single pane, non tablet logic
        if (!mIsDualPane && !mIsTabletPortrait){
            Intent intent = mActivity.getIntent();

            if (intent != null) {
                String uniqueIntentId = intent.getStringExtra(UNIQUE_INTENT_ID);
                if (uniqueIntentId.equals(DETAIL_DATA)) {
                    MyMovie apiDetails = intent.getParcelableExtra(MovieListFragment.DETAIL_DATA);
                    updateApiDetails(apiDetails);
                }
                if (uniqueIntentId.equals(FAVORITE_DATA)) {
                    MyFavorite favoriteDetails = intent.getParcelableExtra(MovieListFragment.FAVORITE_DATA);
                    updateFavoriteDetails(favoriteDetails);
                }
            }
            setViewContents();
        }

        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mIsDualPane) {
            try {
                mOnFavoritesChangeListener = (OnFavoritesChangeListener) getActivity();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getViewReferences() {
        mPosterImageView = (ImageView) mRootView.findViewById(R.id.detail_poster);
        mTitleTextView = (TextView) mRootView.findViewById(R.id.detail_title);
        mOverviewTextView = (TextView) mRootView.findViewById(R.id.detail_overview);
        mReleaseDateTextView = (TextView) mRootView.findViewById(R.id.detail_release_date);
        mVoteAverageTextView = (TextView) mRootView.findViewById(R.id.detail_vote_average);
        mRunTimeTextView = (TextView) mRootView.findViewById(R.id.detail_runtime);
        mAddFavoriteButton = (ImageView) mRootView.findViewById(R.id.add_favorite_button);
        mTrailersContainer = (LinearLayout) mRootView.findViewById(R.id.detail_trailers_container);
        mReviewsContainer = (LinearLayout) mRootView.findViewById(R.id.detail_reviews_container);
    }

    private void setViewContents() {
        Glide.with(this).load(mPosterUrl).into(mPosterImageView);
        mPosterImageView.setContentDescription(mMovieTitle);
        mTitleTextView.setText(mMovieTitle);
        mReleaseDateTextView.setText(formatReleaseData(mReleaseDate));
        mVoteAverageTextView.setText(formatVoteAverage(mVoteAverage));
        mOverviewTextView.setText(mOverview);
    }

    private void updateApiDetails(MyMovie apiDetails) {
        String[] detailsArray = apiDetails.getMyMovie();
        mMovieId = detailsArray[0];
        mMovieTitle = detailsArray[1];
        mPosterUrl = detailsArray[2];
        mOverview = detailsArray[3];
        mReleaseDate = detailsArray[4];
        mVoteAverage = detailsArray[5];
        getAdditionalDetails(mMovieId);
    }

    private void updateFavoriteDetails(MyFavorite favoriteDetails) {
        String[] favoriteDetailsArray = favoriteDetails.getMyFavorite();
        mUriStringId = favoriteDetailsArray[0];
        mMovieId = favoriteDetailsArray[1];
        mMovieTitle = favoriteDetailsArray[2];
        mPosterUrl = favoriteDetailsArray[3];
        mOverview = favoriteDetailsArray[4];
        mReleaseDate = favoriteDetailsArray[5];
        mVoteAverage = favoriteDetailsArray[6];
        mRuntime = favoriteDetailsArray[7];
        mTrailerString = favoriteDetailsArray[8];
        mReviewsString = favoriteDetailsArray[9];

        mRunTimeTextView.setText(formatRuntime(mRuntime));
        if (mTrailerString != null && !mTrailerString.equals("")) {
            mTrailers = DatabaseUtils.unpackArrayList(mTrailerString);
            addTrailerList();
        }
        if (mReviewsString != null && !mReviewsString.equals("")) {
            mReviews = DatabaseUtils.unpackArrayList(mReviewsString);
            addReviewList();
        }
        checkFavorites();
    }

    private void resetTrailersAndReviewsDualPane() {
        mTrailers = null;
        mTrailersContainer.setVisibility(View.GONE);
        if (mTrailersContainer.getChildCount() > 2) {
            mTrailersContainer.removeViews(2, mTrailersContainer.getChildCount() - 2);
        }
        mReviews = null;
        mReviewsContainer.setVisibility(View.GONE);
        if (mReviewsContainer.getChildCount() > 2) {
            mReviewsContainer.removeViews(2, mReviewsContainer.getChildCount() - 2);
        }
    }

    public void updateDualPaneFavoriteDetails(MyFavorite favoriteDetails) {

        getViewReferences();

        resetTrailersAndReviewsDualPane();

        updateFavoriteDetails(favoriteDetails);

        setViewContents();
    }

    public void updateDualPaneApiDetails(MyMovie apiDetails) {

        getViewReferences();

        resetTrailersAndReviewsDualPane();

        updateApiDetails(apiDetails);

        setViewContents();
    }

    private void setFavoriteButton() {

        if (mIsFavorite) {
            mAddFavoriteButton.setImageResource(R.drawable.ic_star_black_48dp);
        } else {
            mAddFavoriteButton.setImageResource(R.drawable.ic_star_border_black_48dp);
        }

        mAddFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!mIsFavorite) {

                    mTrailerString = DatabaseUtils.packArrayList(mTrailers);
                    mReviewsString = DatabaseUtils.packArrayList(mReviews);

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, mMovieId);
                    contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, mMovieTitle);
                    contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_URL, mPosterUrl);
                    contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, mOverview);
                    contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, mReleaseDate);
                    contentValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, mVoteAverage);
                    contentValues.put(MovieContract.MovieEntry.COLUMN_RUNTIME, mRuntime);
                    contentValues.put(MovieContract.MovieEntry.COLUMN_TRAILERS, mTrailerString);
                    contentValues.put(MovieContract.MovieEntry.COLUMN_REVIEWS, mReviewsString);

                    Uri uri = mActivity.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);

                    if (uri != null) {
                        mUriStringId = uri.getPathSegments().get(1);
                        mIsFavorite = true;
                        mAddFavoriteButton.setImageResource(R.drawable.ic_star_black_48dp);
                        Toast.makeText(mActivity, mMovieTitle + " " + getString(R.string.favorite_added), Toast.LENGTH_SHORT).show();

                        if (mIsDualPane) {
                            mOnFavoritesChangeListener.OnFavoriteChanged();
                        }
                    }
                } else {
                    Uri uri = MovieContract.MovieEntry.CONTENT_URI;
                    uri = uri.buildUpon().appendPath(mUriStringId).build();
                    int movieDeleted = mActivity.getContentResolver().delete(uri, null, null);

                    if (movieDeleted > 0) {
                        mIsFavorite = false;
                        mAddFavoriteButton.setImageResource(R.drawable.ic_star_border_black_48dp);
                        Toast.makeText(mActivity, mMovieTitle + " " + getString(R.string.favorite_removed), Toast.LENGTH_SHORT).show();

                        if (mIsDualPane) {
                            mOnFavoritesChangeListener.OnFavoriteChanged();
                        }
                    }
                }

            }
        });
    }

    private void getAdditionalDetails(String movieId) {

        String url = QueryBuilderUtils.getMovieQuery(movieId);
        MoviesJsonUtils.getJsonDetailsDataFromApi(mActivity, url, new MoviesJsonUtils.OnDetailsReady() {
            @Override
            public void onDetailsReady(MyMovieDetail movieDetails) {

                if (getActivity() != null && isAdded()) {

                    mRuntime = movieDetails.getRuntime();
                    mTrailers = movieDetails.getTrailers();
                    mReviews = movieDetails.getReviews();

                    mRunTimeTextView.setText(formatRuntime(mRuntime));
                    addTrailerList();
                    addReviewList();

                    checkFavorites();
                }

            }
        }, new MoviesJsonUtils.OnVolleyError() {
            @Override
            public void onVolleyError(String message) {

                if (getActivity() != null && isAdded()) {

                    if (message != null) {
                        switch (message) {
                            case MoviesJsonUtils.CONNECTION_ERROR_CASE:
                                message = getString(R.string.connection_error);
                                break;
                            case MoviesJsonUtils.PARSING_ERROR_CASE:
                                message = getString(R.string.parsing_error);
                                break;
                            case MoviesJsonUtils.SERVER_ERROR_CASE:
                                message = getString(R.string.server_error);
                                break;
                            default:
                                message = getString(R.string.parsing_error);
                        }
                        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void addTrailerList() {

        mTrailersContainer = (LinearLayout) mRootView.findViewById(R.id.detail_trailers_container);

        if (mTrailers.size() == 0) {
            mTrailersContainer.setVisibility(View.GONE);
            return;
        } else {
            mTrailersContainer.setVisibility(View.VISIBLE);
        }

        String[] trailer;
        String trailerName;
        int trailerIdSeries = 30000;
        int trailerNameIdSeries = 300000;
        int trailerSeparatorIdSeries = 3000000;
        for (int i = 0; i < mTrailers.size(); i++) {
            trailer = mTrailers.get(i);
            final String trailerUrl = trailer[0];
            trailerName = trailer[1];

            LayoutInflater inflater = LayoutInflater.from(mActivity);
            View trailerItemView = inflater.inflate(R.layout.trailer_item, null, false);
            trailerItemView.setId(trailerIdSeries + i);
            trailerItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent openTrailerIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl));

                    if (openTrailerIntent.resolveActivity(mActivity.getPackageManager()) != null) {
                        startActivity(openTrailerIntent);
                    }
                }
            });
            mTrailersContainer.addView(trailerItemView);

            TextView trailerNameTextView = (TextView) mRootView.findViewById(R.id.trailer_name);
            trailerNameTextView.setId(trailerNameIdSeries + i);
            trailerNameTextView.setText(trailerName);

            View separatorView = mRootView.findViewById(R.id.trailer_separator);
            separatorView.setId(trailerSeparatorIdSeries + i);
            if (i == mTrailers.size() - 1) {
                separatorView.setVisibility(View.GONE);
            }
        }
    }

    private void addReviewList() {

        mReviewsContainer = (LinearLayout) mRootView.findViewById(R.id.detail_reviews_container);

        if (mReviews.size() == 0) {
            mReviewsContainer.setVisibility(View.GONE);
            return;
        } else {
            mReviewsContainer.setVisibility(View.VISIBLE);
        }

        String[] review;
        String reviewAuthor;
        String reviewText;
        int reviewIdSeries = 90000;
        int reviewNameIdSeries = 900000;
        int reviewSeparatorIdSeries = 9000000;
        for (int i = 0; i < mReviews.size(); i++) {
            review = mReviews.get(i);
            final String reviewUrl = review[0];
            reviewAuthor = review[1];
            reviewText = mActivity.getString(R.string.reviewed_by) + " " + reviewAuthor;

            LayoutInflater inflater = LayoutInflater.from(mActivity);
            View reviewItemView = inflater.inflate(R.layout.review_item, null, false);
            reviewItemView.setId(reviewIdSeries + i);
            reviewItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent openReviewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(reviewUrl));

                    if (openReviewIntent.resolveActivity(mActivity.getPackageManager()) != null) {
                        startActivity(openReviewIntent);
                    }
                }
            });
            mReviewsContainer.addView(reviewItemView);

            TextView reviewAuthorTextView = (TextView) mRootView.findViewById(R.id.review_author);
            reviewAuthorTextView.setId(reviewNameIdSeries + i);
            reviewAuthorTextView.setText(reviewText);

            View separatorView = mRootView.findViewById(R.id.review_separator);
            separatorView.setId(reviewSeparatorIdSeries + i);
            if (i == mReviews.size() - 1) {
                separatorView.setVisibility(View.GONE);
            }
        }
    }

    private String formatReleaseData(String releaseDate) {
        return releaseDate.substring(0, Math.min(releaseDate.length(), 4));
    }

    private String formatVoteAverage(String voteAverage) {
        return voteAverage + "/10";
    }

    private String formatRuntime(String runtime) {
        return runtime + getString(R.string.runtime_minutes);
    }

    private void checkFavorites() {
        if (!mIsDualPane) {
            getLoaderManager().initLoader(INDIVIDUAL_FAVORITE_LOADER_ID, null, this);
        } else {
            getLoaderManager().restartLoader(INDIVIDUAL_FAVORITE_LOADER_ID, null, this);
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new AsyncTaskLoader<Cursor>(mActivity) {

            Cursor mCursorData = null;

            @Override
            protected void onStartLoading() {
                if (mCursorData != null) {
                    deliverResult(mCursorData);
                } else {
                    forceLoad();
                }

            }

            @Override
            public Cursor loadInBackground() {
                String[] projection = new String[] {MovieContract.MovieEntry._ID, MovieContract.MovieEntry.COLUMN_MOVIE_ID};
                String selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + " =?";
                String[] selectionArgs = {mMovieId};

                try {
                    return mActivity.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                            projection,
                            selection,
                            selectionArgs,
                            MovieContract.MovieEntry._ID);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(Cursor data) {
                mCursorData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() == 0) {
            mIsFavorite = false;
        }
        if (data != null && data.getCount() != 0) {
            data.moveToFirst();
            int idIndex = data.getColumnIndex(MovieContract.MovieEntry._ID);
            int movieIdIndex = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
            String movieId = data.getString(movieIdIndex);

            if (mMovieId.equals(movieId)) {
                mIsFavorite = true;
                mUriStringId = String.valueOf(data.getInt(idIndex));
            }
        }
        setFavoriteButton();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onResume() {
        super.onResume();

        getLoaderManager().restartLoader(INDIVIDUAL_FAVORITE_LOADER_ID, null, this);
    }

}