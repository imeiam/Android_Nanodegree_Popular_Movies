package gop.akiladeshwar.movies_1;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import gop.akiladeshwar.movies_1.data.MovieContract;
import gop.akiladeshwar.movies_1.data.MovieDBHelper;
import gop.akiladeshwar.movies_1.sync.MovieSensorSyncAdapter;

public class MainActivity extends AppCompatActivity implements MoviesDisplayFragment.CallBack {


    public static final String MOVIE_DISPLAY_FRAGMENT = "MOVIE_DISPLAY";
    public static final String NO_INTERNET_FRAGMENT = "NO_INTERNET";
    public static final String LOG_TAG = MainActivity.class.getSimpleName();


    String sortOrder = null;

    Typeface titleTypeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        titleTypeface =  Typeface
                .createFromAsset(this.getAssets(),"fonts/dead.TTF");
        TextView textView = (TextView) findViewById(R.id.toolbar_title);
        textView.setTypeface(titleTypeface);
        int filmCamera = 0x1F3A5;
        String filmCameraText = new String(Character.toChars(filmCamera));
        String title = filmCameraText+" "+"Movie Sensor";
        textView.setText(title);

        sortOrder = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getResources().getString(R.string.pref_sort_order_key),"popular");

        MovieSensorSyncAdapter.syncImmediately(this);

        MovieDBHelper dbHelper = new MovieDBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM "+ MovieContract.PopularEntry.TABLE_NAME,null);
        if(!cursor.moveToFirst())
        {
            if(sortOrder.equals("popular")) {
                MovieSensorSyncAdapter.syncImmediately(this);
                Log.d(LOG_TAG, "Table Empty - Starting Sync");
            }

        }
//        int i=0;
//        while(!cursor.isAfterLast()){
//            Movie movie = Utility.createMovieObjectFromCursor(cursor);
//            Log.d(LOG_TAG,i+" - "+movie.getName())
//            i++;
//            cursor.moveToNext();
//        }
        cursor = db.rawQuery("SELECT * FROM "+ MovieContract.TopRatedEntry.TABLE_NAME,null);
        if(!cursor.moveToFirst())
        {
            if(sortOrder.equals("topRated")) {
                MovieSensorSyncAdapter.syncImmediately(this);
                Log.d(LOG_TAG, "Table Empty - Starting Sync");
            }
        }

        String tableName;
        if(sortOrder.matches("popular")){
            tableName = MovieContract.PopularEntry.TABLE_NAME;
        }
        else{
           tableName = MovieContract.TopRatedEntry.TABLE_NAME;
        }
        String query = "SELECT * FROM "+ tableName;
        cursor = db.rawQuery(query, null);

        if(!cursor.moveToFirst() && !Utility.isNetworkAvailable(this)){

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame, new NoInternetFragment(), NO_INTERNET_FRAGMENT)
                    .commit();

        }

        else{

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame, new MoviesDisplayFragment(), MOVIE_DISPLAY_FRAGMENT)
                    .commit();

        }
        db.close();
    }

    @Override
    protected void onResume() {
        super.onResume();

        String preferredSortOrder = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getResources().getString(R.string.pref_sort_order_key),"popular");

        if(preferredSortOrder!=null && !preferredSortOrder.equals(sortOrder)){

            MoviesDisplayFragment displayFragment =
                    (MoviesDisplayFragment)getSupportFragmentManager().findFragmentByTag(MOVIE_DISPLAY_FRAGMENT);
            if(displayFragment != null)
                displayFragment.onSortOrderChanged();
            sortOrder = preferredSortOrder;
        }
    }


    @Override
    public void onItemSelected(Cursor cursor,View transitionView) {
        Movie movie = Utility.createMovieObjectFromCursor(cursor);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Movie.MOVIE_TAG, movie);
        Intent intent = new Intent(this,DetailsActivity.class);
        intent.putExtra(Movie.MOVIE_TAG, bundle);
        startActivity(intent);
    }
}
