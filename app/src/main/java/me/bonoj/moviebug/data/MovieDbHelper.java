package me.bonoj.moviebug.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MovieDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movie.db";
    public static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_MOVIE_TABLE =

                "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +

                         MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +

                        MovieContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +

                        MovieContract.MovieEntry.COLUMN_POSTER_URL + " TEXT NOT NULL, " +

                        MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +

                        MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +

                        MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL, " +

                        MovieContract.MovieEntry.COLUMN_RUNTIME + " TEXT, " +

                        MovieContract.MovieEntry.COLUMN_TRAILERS + " TEXT, " +

                        MovieContract.MovieEntry.COLUMN_REVIEWS + " TEXT" + ");";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);

        onCreate(db);
    }
}
