package me.bonoj.moviebug.data;

import java.util.ArrayList;

public class MyMovieDetail {
    private String runtime;
    private ArrayList<String[]> trailers;
    private ArrayList<String[]> reviews;

    public MyMovieDetail (String runtime, ArrayList<String[]> trailers, ArrayList<String[]> reviews) {
        this.runtime = runtime;
        this.trailers = trailers;
        this.reviews = reviews;
    }

    public String getRuntime() {
        return runtime;
    }

    public ArrayList<String[]> getTrailers() {
        return trailers;
    }

    public ArrayList<String[]> getReviews() {
        return reviews;
    }
}
