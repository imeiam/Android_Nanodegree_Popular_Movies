package gop.akiladeshwar.movies_1.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Vector;

import gop.akiladeshwar.movies_1.Movie;
import gop.akiladeshwar.movies_1.R;
import gop.akiladeshwar.movies_1.Utility;
import gop.akiladeshwar.movies_1.data.MovieContract;

/**
 * Created by AkilAdeshwar on 20-05-2016.
 */
public class MovieSensorSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = MovieSensorSyncAdapter.class.getSimpleName();
    // Interval at which to sync with the api, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private static final int WEATHER_NOTIFICATION_ID = 3004;


    public MovieSensorSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        String sortOrder = PreferenceManager.getDefaultSharedPreferences(getContext())
                .getString(getContext().getResources().getString(R.string.pref_sort_order_key),"popular");
        ArrayList<Movie> moviesList = Utility.getMovieData(getContext(),sortOrder);
        if(moviesList.size() == Integer
                .parseInt(getContext().getResources().getString(R.string.no_of_results_from_api)))
            insertMoviesInToDB(moviesList,sortOrder);
        Log.d(LOG_TAG,"Size: "+moviesList.size());
        return;
    }


    public void insertMoviesInToDB(ArrayList<Movie> moviesList,String sortOrder){

        Vector<ContentValues> cVVector = new Vector<>(moviesList.size());

        for(Movie movie: moviesList){

            ContentValues movieRow = new ContentValues();
            movieRow.put(MovieContract.SuperTableEntry.COLUMN_NAME,movie.getName());
            movieRow.put(MovieContract.SuperTableEntry.COLUMN_OVERVIEW,movie.getOverview());
            movieRow.put(MovieContract.SuperTableEntry.COLUMN_POSTER_PATH,movie.getPosterPath());
            movieRow.put(MovieContract.SuperTableEntry.COLUMN_RELEASE_DATE,movie.getReleaseDate());
            movieRow.put(MovieContract.SuperTableEntry.COLUMN_VOTE_AVERAGE,movie.getVote_average());
            movieRow.put(MovieContract.SuperTableEntry.COLUMN_BACKDROP_PATH,movie.getBackdropPath());
            cVVector.add(movieRow);
        }

        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);

            Uri contentUri = null;

            // Insert in the correct table

            if(sortOrder.equals("popular")){
                contentUri = MovieContract.PopularEntry.CONTENT_URI;
            }
            else{
                contentUri = MovieContract.TopRatedEntry.CONTENT_URI;
            }
            // delete old data
            getContext().getContentResolver().delete(contentUri, null, null);
            Log.d(LOG_TAG, "Delete: "+contentUri+"Insert: Size - "+ moviesList.size());
            // then insert new data
            getContext().getContentResolver().bulkInsert(contentUri, cvArray);
        }
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }


    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    public static Account getSyncAccount(Context context) {

        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        Account newAccount = new Account(
                "Movie Sensor"/*context.getString(R.string.app_name)*/, context.getString(R.string.sync_account_type));
        if ( null == accountManager.getPassword(newAccount) ) {

            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        MovieSensorSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
            getSyncAccount(context);
    }
}
