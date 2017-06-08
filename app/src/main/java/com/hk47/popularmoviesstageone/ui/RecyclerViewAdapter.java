package com.hk47.popularmoviesstageone.ui;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hk47.popularmoviesstageone.R;
import com.hk47.popularmoviesstageone.data.MyMovie;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<MyMovie> mMovies;
    private Context mContext;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // TODO Implement: tracking item clicks for dual pane
    private int mClickedPosition = -1;

    public RecyclerViewAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    // Inflates the item layout and returns the holder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.movie_grid_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        mContext = parent.getContext();
        return viewHolder;
    }

    // Binds the data to each item via the holder
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String[] movieArray = mMovies.get(position).getMyMovie();
        String movieTitle = movieArray[1];
        String posterUrl = movieArray[2];
        holder.posterImageView.setContentDescription(movieTitle);
        Glide.with(mContext).load(posterUrl).into(holder.posterImageView);

        // TODO Implement: tracking item clicks for dual pane
        if (mClickedPosition == position) {

        }
    }

    // Returns the total number of items in the list
    @Override
    public int getItemCount() {
        if (mMovies == null) {
            return 0;
        } else {
            return mMovies.size();
        }
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
            if (mClickListener != null) {
                mClickListener.onItemClick(view, getAdapterPosition());
            }

            // TODO Implement: Tracking item clicks for dual pane
            mClickedPosition = getAdapterPosition();
            notifyItemChanged(mClickedPosition);
        }
    }

    // Retrieves the movie object at the clicked position
    public MyMovie getParcelableItem(int position) {
        return mMovies.get(position);
    }

    // Retrieves the entire array list to preserve scroll position during orientation changes
    public ArrayList<MyMovie> getParcelableArrayList() {
        return mMovies;
    }

    // Listens for click events
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // Implemented in MovieListActivity to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    // Updates the array, allowing for uninterrupted scrolling
    public void setMovies(ArrayList<MyMovie> movies) {
        if (mMovies == null) {
            mMovies = new ArrayList<>();
            mMovies = movies;
        } else {
            mMovies.addAll(movies);
            notifyDataSetChanged();
        }
    }

    // Clears the array when the user changes the sorting type
    public void resetAdapter() {
        if (mMovies != null) {
            mMovies.clear();
            notifyDataSetChanged();
        }
    }

    // Restores the previous array on device orientation change
    public void refillAdapter(ArrayList<MyMovie> data) {
        if (data != null) {
            mMovies = new ArrayList<>();
            mMovies.addAll(data);
        }
    }
}