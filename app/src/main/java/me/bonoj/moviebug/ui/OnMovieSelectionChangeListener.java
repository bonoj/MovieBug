package me.bonoj.moviebug.ui;

import me.bonoj.moviebug.data.MyFavorite;
import me.bonoj.moviebug.data.MyMovie;

public interface OnMovieSelectionChangeListener {
    public void OnMovieSelectionChanged(MyMovie apiDetails);

    public void OnMovieSelectionChanged(MyFavorite favoriteDetails);
}
