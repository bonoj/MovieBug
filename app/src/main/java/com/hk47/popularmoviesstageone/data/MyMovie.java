package com.hk47.popularmoviesstageone.data;

import android.os.Parcel;
import android.os.Parcelable;

public class MyMovie implements Parcelable {
    private String id;
    private String title;
    private String posterUrl;
    private String overview;
    private String releaseDate;
    private String voteAverage;

    public MyMovie(String id, String title, String posterUrl, String overview, String releaseDate, String voteAverage) {
        this.id = id;
        this.title = title;
        this.posterUrl = posterUrl;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
    }

    public String[] getMyMovie() {
        return new String[]{id, title, posterUrl, overview, releaseDate, voteAverage};
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(id);
        out.writeString(title);
        out.writeString(posterUrl);
        out.writeString(overview);
        out.writeString(releaseDate);
        out.writeString(voteAverage);
    }

    private MyMovie(Parcel in) {
        id = in.readString();
        title = in.readString();
        posterUrl = in.readString();
        overview = in.readString();
        releaseDate = in.readString();
        voteAverage = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<MyMovie> CREATOR = new Parcelable.Creator<MyMovie>() {
        @Override
        public MyMovie createFromParcel(Parcel parcel) {
            return new MyMovie(parcel);
        }

        @Override
        public MyMovie[] newArray(int size) {
            return new MyMovie[size];
        }
    };
}