package com.hk47.popularmoviesstageone.ui;

import com.hk47.popularmoviesstageone.data.MyFavorite;
import com.hk47.popularmoviesstageone.data.MyMovie;

public interface OnMovieSelectionChangeListener {
    public void OnMovieSelectionChanged(MyMovie apiDetails);

    public void OnMovieSelectionChanged(MyFavorite favoriteDetails);
}
