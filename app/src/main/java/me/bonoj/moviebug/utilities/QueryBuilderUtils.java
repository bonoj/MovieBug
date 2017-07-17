package me.bonoj.moviebug.utilities;

import android.content.Context;

import me.bonoj.moviebug.BuildConfig;

public class QueryBuilderUtils {

    public static final String BASE_URL_STRING = "https://api.themoviedb.org/3/";
    public static final String API_KEY = "?api_key=" + BuildConfig.API_KEY;
    public static final String LANGUAGE = "&language=en-US";

    // Example query for the movie with ID 295693
    // https://api.themoviedb.org/3/movie/157336?api_key=<YOUR KEY WITHOUT <> HERE>&language=en-US&append_to_response=videos,reviews
    public static final String MOVIE_PATH = "movie/";
    public static final String ADDITIONAL_MOVIE_PARAMS = "&append_to_response=videos,reviews";

    // Example query for the first page of movies sorted by popularity
    // https://api.themoviedb.org/3/movie/popular?api_key=<YOUR KEY WITHOUT <> HERE>&language=en-US&page=1
    public static final String DISCOVER_PATH = "discover/movie";
    public static final String SORT_POPULARITY = "&sort_by=popularity.desc";
    public static final String SORT_VOTES = "&sort_by=vote_count.desc";
    public static final String ADDITIONAL_DISCOVER_PARAMS = "&include_adult=false&include_video=false";
    public static final String PAGE = "&page=";

    public static final String BASE_YOUTUBE_URL = "https://www.youtube.com/watch?v=";

    // Example query for the Interstellar poster
    // http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg
    public static final String BASE_POSTER_URL_STRING = "http://image.tmdb.org/t/p/";

    public static String getMovieQuery(String id) {
        return BASE_URL_STRING +
                MOVIE_PATH +
                id +
                API_KEY +
                LANGUAGE +
                ADDITIONAL_MOVIE_PARAMS;
    }

    public static String getPageQuery(String sortOrder, int pageNumber) {
        return BASE_URL_STRING +
                DISCOVER_PATH +
                API_KEY +
                LANGUAGE +
                sortOrder +
                ADDITIONAL_DISCOVER_PARAMS +
                PAGE +
                String.valueOf(pageNumber);
    }

    public static String getPosterUrl(Context context, String posterPath) {
        return BASE_POSTER_URL_STRING +
                context.getString(me.bonoj.moviebug.R.string.poster_width) +
                posterPath;
    }
}

