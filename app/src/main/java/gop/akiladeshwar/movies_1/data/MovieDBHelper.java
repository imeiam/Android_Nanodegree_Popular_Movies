package gop.akiladeshwar.movies_1.data;

/**
 * Created by AkilAdeshwar on 20-05-2016.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import gop.akiladeshwar.movies_1.data.MovieContract.PopularEntry;
import gop.akiladeshwar.movies_1.data.MovieContract.TopRatedEntry;
import gop.akiladeshwar.movies_1.data.MovieContract.FavoriteEntry;

public class MovieDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "moviesrr.db";

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_POPULAR_TABLE = "CREATE TABLE " + PopularEntry.TABLE_NAME + " (" +

                PopularEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PopularEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                PopularEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                PopularEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                PopularEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL," +
                PopularEntry.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL, " +
                PopularEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, " +
                PopularEntry.COLUMN_ID + " TEXT NOT NULL, " +
                PopularEntry.COLUMN_VIDEOS_JSON + " TEXT NOT NULL, " +
                PopularEntry.COLUMN_REVIEWS_JSON + " TEXT NOT NULL, " +
                " UNIQUE ("+PopularEntry.COLUMN_NAME+") ON CONFLICT REPLACE );";
        sqLiteDatabase.execSQL(SQL_CREATE_POPULAR_TABLE);


        final String SQL_CREATE_TOP_RATED_TABLE = "CREATE TABLE " + TopRatedEntry.TABLE_NAME + " (" +

                TopRatedEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TopRatedEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                TopRatedEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                TopRatedEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                TopRatedEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL," +
                TopRatedEntry.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL, " +
                TopRatedEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, " +
                TopRatedEntry.COLUMN_ID + " TEXT NOT NULL, " +
                TopRatedEntry.COLUMN_VIDEOS_JSON + " TEXT NOT NULL, " +
                TopRatedEntry.COLUMN_REVIEWS_JSON + " TEXT NOT NULL, " +
                " UNIQUE ("+TopRatedEntry.COLUMN_NAME+") ON CONFLICT REPLACE );";
        sqLiteDatabase.execSQL(SQL_CREATE_TOP_RATED_TABLE);


        final String SQL_CREATE_FAVORITE_TABLE = "CREATE TABLE " + FavoriteEntry.TABLE_NAME + " (" +

                FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FavoriteEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL," +
                FavoriteEntry.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_ID + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_VIDEOS_JSON + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_REVIEWS_JSON + " TEXT NOT NULL, " +
                " UNIQUE ("+FavoriteEntry.COLUMN_NAME+") ON CONFLICT REPLACE );";
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_TABLE);




    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PopularEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TopRatedEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}