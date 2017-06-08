package com.hk47.popularmoviesstageone.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hk47.popularmoviesstageone.R;
import com.hk47.popularmoviesstageone.data.MovieContract;
import com.hk47.popularmoviesstageone.data.MyFavorite;
import com.hk47.popularmoviesstageone.data.MyMovie;
import com.hk47.popularmoviesstageone.utilities.MoviesJsonUtils;
import com.hk47.popularmoviesstageone.utilities.PreferenceUtils;
import com.hk47.popularmoviesstageone.utilities.QueryBuilderUtils;

import java.util.ArrayList;

public class MovieListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String DETAIL_DATA = "com.hk47.popularmoviesstageone.ui.DETAIL_DATA";
    public static final String FAVORITE_DATA = "com.hk47.popularmoviesstageone.ui.FAVORITE_DATA";
    public static final String UNIQUE_INTENT_ID = "com.hk47.popularmoviesstageone.ui.UNIQUE_INTENT_ID";
    public static final String SAVE_DATA_KEY = "com.hk47.popularmoviesstageone.ui.SAVE_DATA_KEY";
    public static final String SAVE_COUNTER_KEY = "com.hk47.popularmoviesstageone.ui.SAVE_COUNTER_KEY";

    private static final String FAVORITES_FLAG = "com.hk47.popularmoviesstageone.ui.FAVORITES_FLAG";
    public static final int FAVORITES_LOADER_ID = 0;

    private ProgressBar mProgressBar;
    private TextView mErrorTextView;
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;
    private FavoritesCursorAdapter mCursorAdapter;
    private GridLayoutManager mGridLayoutManager;
    private ScrollCounter mScrollCounter;
    private String mSortOrder;
    private Activity mActivity;
    private boolean mIsDualPane;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);
        mActivity = getActivity();
        mIsDualPane = getResources().getBoolean(R.bool.is_dual_pane);
        mSortOrder = PreferenceUtils.getSortOrder(mActivity);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.loading_indicator);
        mErrorTextView = (TextView) rootView.findViewById(R.id.error_text_view);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.movie_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mGridLayoutManager = new GridLayoutManager(mActivity, getResources().getInteger(R.integer.grid_columns));
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        if (!mSortOrder.equals(FAVORITES_FLAG)) {
            setApiView();
            if (savedInstanceState == null) {
                // Get the first page, sorting by popularity by default
                getNextPageFromApi(1);
            } else {
                mScrollCounter.setCounter(savedInstanceState.getInt(SAVE_COUNTER_KEY));
                ArrayList<MyMovie> data = savedInstanceState.getParcelableArrayList(SAVE_DATA_KEY);
                mAdapter.refillAdapter(data);
                hideLoadingIndicator();
            }
        } else if (mSortOrder.equals(FAVORITES_FLAG)) {
            setFavoritesView();
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mSortOrder.equals(FAVORITES_FLAG)) {
            getLoaderManager().restartLoader(FAVORITES_LOADER_ID, null, this);
        }
    }

    private void setFavoritesClickListener() {
        mCursorAdapter.setCursorClickListener(new FavoritesCursorAdapter.CursorClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if (!mIsDualPane) {
                    MyFavorite favoriteData = mCursorAdapter.getParcelableCursor(position);
                    Intent favoriteIntent = new Intent(mActivity, MovieDetailActivity.class);
                    favoriteIntent.putExtra(FAVORITE_DATA, favoriteData);
                    favoriteIntent.putExtra(UNIQUE_INTENT_ID, FAVORITE_DATA);
                    startActivity(favoriteIntent);
                } else {
                    OnMovieSelectionChangeListener listener = (OnMovieSelectionChangeListener) getActivity();
                    listener.OnMovieSelectionChanged(mCursorAdapter.getParcelableCursor(position));
                }
            }
        });
    }

    private void setApiClickListener() {
        mAdapter.setClickListener(new RecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if (!mIsDualPane) {
                    MyMovie detailData = mAdapter.getParcelableItem(position);
                    Intent detailIntent = new Intent(mActivity, MovieDetailActivity.class);
                    detailIntent.putExtra(DETAIL_DATA, detailData);
                    detailIntent.putExtra(UNIQUE_INTENT_ID, DETAIL_DATA);
                    startActivity(detailIntent);
                } else {
                    OnMovieSelectionChangeListener listener = (OnMovieSelectionChangeListener) getActivity();
                    listener.OnMovieSelectionChanged(mAdapter.getParcelableItem(position));
                }
            }
        });
    }

    private void setApiScrollListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mGridLayoutManager.findLastVisibleItemPosition() > mScrollCounter.getAdapterSize() - 10) {
                    getNextPageFromApi(mScrollCounter.getCounter());
                }
            }
        });
    }

    private void hideLoadingIndicator() {
        mProgressBar.setVisibility(View.GONE);
        mErrorTextView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void getNextPageFromApi(int pageNumber) {
        if (System.currentTimeMillis() - mScrollCounter.getTimeOfLastQuery() >= 1000) {
            mScrollCounter.setTimeOfLastQuery(System.currentTimeMillis());
            String url = QueryBuilderUtils.getPageQuery(mSortOrder, pageNumber);
            MoviesJsonUtils.getJsonPageDataFromApi(mActivity, url, new MoviesJsonUtils.OnMoviesReady() {
                @Override
                public void onMoviesReady(ArrayList<MyMovie> movies) {
                    mAdapter.setMovies(movies);
                    mScrollCounter.setAdapterSize(mAdapter.getItemCount());
                    mScrollCounter.incrementCounter();
                    if (mAdapter.getItemCount() == 20) {
                        mAdapter.notifyDataSetChanged();
                    }
                    if (mRecyclerView.getVisibility() == View.GONE) {
                        hideLoadingIndicator();
                    }
                }
            }, new MoviesJsonUtils.OnVolleyError() {
                @Override
                public void onVolleyError(String message) {
                    if (message != null) {

                       if (message.equals(MoviesJsonUtils.CONNECTION_ERROR_CASE)) {
                           message = getString(R.string.connection_error);
                       } else if (message.equals(MoviesJsonUtils.SERVER_ERROR_CASE)) {
                           message = getString(R.string.server_error);
                       } else {
                           message = getString(R.string.parsing_error);
                       }

                        mProgressBar.setVisibility(View.GONE);
                        if (mAdapter.getItemCount() == 0) {
                            mErrorTextView.setVisibility(View.VISIBLE);
                            mErrorTextView.setText(message);
                        } else {
                            Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        MenuInflater inflater = mActivity.getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_most_popular:
                Toast.makeText(mActivity, getString(R.string.most_popular_toast), Toast.LENGTH_SHORT).show();
                if (mSortOrder.equals(QueryBuilderUtils.SORT_POPULARITY)) {
                    return false;
                }
                mSortOrder = QueryBuilderUtils.SORT_POPULARITY;
                PreferenceUtils.setSortOrder(mActivity, mSortOrder);
                setApiView();
                getNextPageFromApi(1);
                return true;
            case R.id.menu_top_rated:
                Toast.makeText(mActivity, getString(R.string.top_rated_toast), Toast.LENGTH_SHORT).show();
                if (mSortOrder.equals(QueryBuilderUtils.SORT_VOTES)) {
                    return false;
                }
                mSortOrder = QueryBuilderUtils.SORT_VOTES;
                PreferenceUtils.setSortOrder(mActivity, mSortOrder);
                setApiView();
                getNextPageFromApi(1);
                return true;
            case R.id.menu_favorites:
                Toast.makeText(mActivity, getString(R.string.favorites_toast), Toast.LENGTH_SHORT).show();
                if (mSortOrder.equals(FAVORITES_FLAG)) {
                    return false;
                }
                mAdapter.resetAdapter();
                setFavoritesView();
                mSortOrder = FAVORITES_FLAG;
                PreferenceUtils.setSortOrder(mActivity, mSortOrder);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setApiView() {
        mAdapter = new RecyclerViewAdapter(mActivity);
        mRecyclerView.setAdapter(mAdapter);
        mScrollCounter = new ScrollCounter();
        setApiClickListener();
        setApiScrollListener();
    }

    private void setFavoritesView() {
        mCursorAdapter = new FavoritesCursorAdapter(mActivity);
        mRecyclerView.clearOnScrollListeners();
        mRecyclerView.setAdapter(mCursorAdapter);
        setFavoritesClickListener();

        getLoaderManager().restartLoader(FAVORITES_LOADER_ID, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (!mSortOrder.equals(FAVORITES_FLAG)) {
            outState.putParcelableArrayList(SAVE_DATA_KEY, mAdapter.getParcelableArrayList());
            outState.putInt(SAVE_COUNTER_KEY, mScrollCounter.getCounter());
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

                try {
                    return mActivity.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
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
        mCursorAdapter.swapCursor(data);
        if (data != null) {
            hideLoadingIndicator();
        }
        if (mCursorAdapter.getItemCount() == 0) {
            mRecyclerView.setVisibility(View.GONE);
            mErrorTextView.setVisibility(View.VISIBLE);
            mErrorTextView.setText(getString(R.string.no_favorites));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}