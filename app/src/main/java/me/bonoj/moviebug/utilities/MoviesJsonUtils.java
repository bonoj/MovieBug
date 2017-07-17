package me.bonoj.moviebug.utilities;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import me.bonoj.moviebug.R;
import me.bonoj.moviebug.data.MyMovie;
import me.bonoj.moviebug.data.MyMovieDetail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MoviesJsonUtils {

    public static final String CONNECTION_ERROR_CASE = "connection-error-case";
    public static final String PARSING_ERROR_CASE = "parsing-error-case";
    public static final String SERVER_ERROR_CASE = "server-error-case";

    private static MoviesJsonUtils mInstance;
    private RequestQueue mRequestQueue;
    private static Context mContext;

    private MoviesJsonUtils(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized MoviesJsonUtils getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MoviesJsonUtils(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    // For main thread notification of volley request completion
    public interface OnMoviesReady {
        void onMoviesReady(ArrayList<MyMovie> movies);
    }

    public interface OnVolleyError {
        void onVolleyError(String message);
    }

    public static void getJsonPageDataFromApi(Context context, String url, final OnMoviesReady moviesListener, final OnVolleyError moviesErrorListener) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        ArrayList<MyMovie> movies = extractGridDataFromJson(response);
                        // Notify the main thread that the list of movies is ready
                        moviesListener.onMoviesReady(movies);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        String message = null;

                        if (volleyError instanceof NetworkError) {
                            message = CONNECTION_ERROR_CASE;
                        } else if (volleyError instanceof ServerError) {
                            message = SERVER_ERROR_CASE;
                        } else if (volleyError instanceof AuthFailureError) {
                            message = CONNECTION_ERROR_CASE;
                        } else if (volleyError instanceof ParseError) {
                            message = PARSING_ERROR_CASE;
                        } else if (volleyError instanceof TimeoutError) {
                            message = CONNECTION_ERROR_CASE;
                        }
                        moviesErrorListener.onVolleyError(message);
                    }
                });
        MoviesJsonUtils.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    private static ArrayList<MyMovie> extractGridDataFromJson(JSONObject response) {
        try {
            JSONArray jsonArray = response.getJSONArray("results");

            ArrayList<MyMovie> movies = new ArrayList<>();

            String id;
            String title;
            String posterPath;
            String posterUrl;
            String overview;
            String releaseDate;
            String voteAverage;

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject movie = jsonArray.getJSONObject(i);
                id = movie.getString("id");
                title = movie.getString("original_title");
                posterPath = movie.getString("poster_path");
                posterUrl = QueryBuilderUtils.getPosterUrl(mContext, posterPath);
                overview = movie.getString("overview");
                releaseDate = movie.getString("release_date");
                voteAverage = movie.getString("vote_average");

                // Store the movie data in a MyMovie object and append it to the movies ArrayList
                movies.add(new MyMovie(id, title, posterUrl, overview, releaseDate, voteAverage));
            }
            return movies;
        } catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public interface OnDetailsReady {
        void onDetailsReady(MyMovieDetail movieDetails);
    }

    public static void getJsonDetailsDataFromApi(Context context, String url, final OnDetailsReady detailsListener, final OnVolleyError detailsErrorListener) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        MyMovieDetail movieDetails = extractDetailDataFromJson(response);
                        // Notify the main thread that the details are ready
                        detailsListener.onDetailsReady(movieDetails);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        String message = null;
                        if (volleyError instanceof NetworkError) {
                            message = mContext.getString(R.string.connection_error);
                        } else if (volleyError instanceof ServerError) {
                            message = mContext.getString(R.string.server_error);
                        } else if (volleyError instanceof AuthFailureError) {
                            message = mContext.getString(R.string.connection_error);
                        } else if (volleyError instanceof ParseError) {
                            message = mContext.getString(R.string.parsing_error);
                        } else if (volleyError instanceof TimeoutError) {
                            message = mContext.getString(R.string.connection_error);
                        }
                        detailsErrorListener.onVolleyError(message);
                    }
                });
        MoviesJsonUtils.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    private static MyMovieDetail extractDetailDataFromJson(JSONObject response) {
        try {
            // Get runtime
            String runtime = response.getString("runtime");

            // Get trailers
            String trailerKey;
            String trailerUrl;
            String trailerName;
            String trailerType;
            JSONObject trailersObject = response.getJSONObject("videos");
            JSONArray trailersArray = trailersObject.getJSONArray("results");
            ArrayList<String[]> trailersArrayList = new ArrayList<>();
            for (int i = 0; i < trailersArray.length(); i++) {
                JSONObject trailer = trailersArray.getJSONObject(i);
                trailerKey = trailer.getString("key");
                trailerUrl = QueryBuilderUtils.BASE_YOUTUBE_URL + trailerKey;
                trailerName = trailer.getString("name");
                trailerType = trailer.getString("type");
                if (trailerType.equals("Trailer")) {
                    trailersArrayList.add(new String[]{trailerUrl, trailerName});
                }
            }

            // Get reviews
            String reviewUrl;
            String reviewAuthor;
            JSONObject reviewsObject = response.getJSONObject("reviews");
            JSONArray reviewsArray = reviewsObject.getJSONArray("results");
            ArrayList<String[]> reviewsArrayList = new ArrayList<>();
            for (int i = 0; i < reviewsArray.length(); i++) {
                JSONObject review = reviewsArray.getJSONObject(i);
                reviewUrl = review.getString("url");
                reviewAuthor = review.getString("author");
                reviewsArrayList.add(new String[]{reviewUrl, reviewAuthor});
            }

            return new MyMovieDetail(runtime, trailersArrayList, reviewsArrayList);
        } catch (JSONException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}


