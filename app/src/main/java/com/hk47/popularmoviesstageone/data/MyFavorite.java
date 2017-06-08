package com.hk47.popularmoviesstageone.data;

import android.os.Parcel;
import android.os.Parcelable;

public class MyFavorite implements Parcelable {
    private String indexString;
    private String id;
    private String title;
    private String posterUrl;
    private String overview;
    private String releaseDate;
    private String voteAverage;
    private String runTime;
    private String trailersString;
    private String reviewsString;

    public MyFavorite(String indexString, String id, String title, String posterUrl, String overview, String releaseDate, String voteAverage, String runtime, String trailersString, String reviewsString) {
        this.indexString = indexString;
        this.id = id;
        this.title = title;
        this.posterUrl = posterUrl;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.runTime = runtime;
        this.trailersString = trailersString;
        this.reviewsString = reviewsString;
    }

    public String[] getMyFavorite() {
        return new String[]{indexString, id, title, posterUrl, overview, releaseDate, voteAverage, runTime, trailersString, reviewsString};
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(indexString);
        out.writeString(id);
        out.writeString(title);
        out.writeString(posterUrl);
        out.writeString(overview);
        out.writeString(releaseDate);
        out.writeString(voteAverage);
        out.writeString(runTime);
        out.writeString(trailersString);
        out.writeString(reviewsString);
    }

    private MyFavorite(Parcel in) {
        indexString = in.readString();
        id = in.readString();
        title = in.readString();
        posterUrl = in.readString();
        overview = in.readString();
        releaseDate = in.readString();
        voteAverage = in.readString();
        runTime = in.readString();
        trailersString = in.readString();
        reviewsString = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<MyFavorite> CREATOR = new Parcelable.Creator<MyFavorite>() {
        @Override
        public MyFavorite createFromParcel(Parcel parcel) {
            return new MyFavorite(parcel);
        }

        @Override
        public MyFavorite[] newArray(int size) {
            return new MyFavorite[size];
        }
    };
}