package me.bonoj.moviebug.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import me.bonoj.moviebug.R;
import me.bonoj.moviebug.data.MovieContract;
import me.bonoj.moviebug.data.MyFavorite;

public class FavoritesCursorAdapter extends RecyclerView.Adapter<FavoritesCursorAdapter.ViewHolder> {

    private CursorClickListener mCursorClickListener;
    private Cursor mCursor;
    private LayoutInflater mInflater;
    private Context mContext;

    public FavoritesCursorAdapter(Context context) {
        mContext = context;
        this.mInflater = LayoutInflater.from(context);
    }

    // Inflates the item layout and returns the holder
    @Override
    public FavoritesCursorAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.movie_grid_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        mContext = parent.getContext();
        return viewHolder;
    }

    // Binds the data to each item via the holder
    @Override
    public void onBindViewHolder(FavoritesCursorAdapter.ViewHolder holder, int position) {

        int idIndex = mCursor.getColumnIndex(MovieContract.MovieEntry._ID);
//        int movieIdIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        int movieTitleIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE);
        int posterUrlIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_URL);
//        int overviewIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW);
//        int releaseDateIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
//        int voteAverageIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);
//        int runtimeIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RUNTIME);
//        int trailersIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TRAILERS);
//        int reviewsIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_REVIEWS);

        mCursor.moveToPosition(position);

        final int id = mCursor.getInt(idIndex);
        String movieTitle = mCursor.getString(movieTitleIndex);
        String posterUrl = mCursor.getString(posterUrlIndex);

        holder.posterImageView.setContentDescription(movieTitle);
        Glide.with(mContext).load(posterUrl).into(holder.posterImageView);
    }

    // Returns the total number of items in the list
    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    // Retrieves the data at the clicked position
    public MyFavorite getParcelableCursor(int position) {
        mCursor.moveToPosition(position);
        int idIndex = mCursor.getColumnIndex(MovieContract.MovieEntry._ID);
        int movieIdIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        int movieTitleIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE);
        int posterUrlIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_URL);
        int overviewIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW);
        int releaseDateIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
        int voteAverageIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);
        int runtimeIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RUNTIME);
        int trailersIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TRAILERS);
        int reviewsIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_REVIEWS);

        String indexString = String.valueOf(mCursor.getInt(idIndex));
        String movieId = mCursor.getString(movieIdIndex);
        String movieTitle = mCursor.getString(movieTitleIndex);
        String posterUrl = mCursor.getString(posterUrlIndex);
        String overview = mCursor.getString(overviewIndex);
        String releaseDate = mCursor.getString(releaseDateIndex);
        String voteAverage = mCursor.getString(voteAverageIndex);
        String runtime = mCursor.getString(runtimeIndex);
        String trailers = mCursor.getString(trailersIndex);
        String reviews = mCursor.getString(reviewsIndex);

        return new MyFavorite(indexString, movieId, movieTitle, posterUrl, overview, releaseDate, voteAverage, runtime, trailers, reviews);
    }

    public Cursor swapCursor(Cursor newCursor) {
        // Checks if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == newCursor) {
            return null;
        }
        Cursor oldCursor = mCursor;
        this.mCursor = newCursor;

        // Checks if this is a valid cursor, then updates the cursor
        if (newCursor != null) {
            this.notifyDataSetChanged();
        }
        return oldCursor;
    }

    // Implemented in MovieListActivity to respond to click events
    public interface CursorClickListener {
        void onItemClick(View view, int position);
    }

    // Listens for click events
    public void setCursorClickListener(CursorClickListener cursorClickListener) {
        this.mCursorClickListener = cursorClickListener;
    }

    // Stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView posterImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            posterImageView = (ImageView) itemView.findViewById(R.id.grid_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mCursorClickListener != null) mCursorClickListener.onItemClick(view, getAdapterPosition());
        }
    }
}
