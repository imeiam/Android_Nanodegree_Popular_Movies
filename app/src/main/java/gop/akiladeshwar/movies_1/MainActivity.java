package gop.akiladeshwar.movies_1;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import gop.akiladeshwar.movies_1.data.MovieContract;
import gop.akiladeshwar.movies_1.data.MovieDBHelper;
import gop.akiladeshwar.movies_1.sync.MovieSensorSyncAdapter;

public class MainActivity extends AppCompatActivity implements MoviesDisplayFragment.CallBack,
        GestureDetector.OnDoubleTapListener,GestureDetector.OnGestureListener{


    public static final String MOVIE_DISPLAY_FRAGMENT = "MOVIE_DISPLAY";
    public static final String NO_INTERNET_FRAGMENT = "NO_INTERNET";
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private GestureDetectorCompat mDetector;


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

        // GestureDectection in NoInternetFragment
        mDetector = new GestureDetectorCompat(this,this);
        mDetector.setOnDoubleTapListener(this);


        sortOrder = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getResources().getString(R.string.pref_sort_order_key),"popular");

        MovieSensorSyncAdapter.syncImmediately(this);



//        CheckDBEntries
//        int i=0;
//        while(!cursor.isAfterLast()){
//            Movie movie = Utility.createMovieObjectFromCursor(cursor);
//            Log.d(LOG_TAG,i+" - "+movie.getName())
//            i++;
//            cursor.moveToNext();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        String preferredSortOrder = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getResources().getString(R.string.pref_sort_order_key),"popular");

        if(preferredSortOrder!=null && !preferredSortOrder.equals(sortOrder)){

            sortOrder = preferredSortOrder;
            MoviesDisplayFragment displayFragment =
                    (MoviesDisplayFragment)getSupportFragmentManager().findFragmentByTag(MOVIE_DISPLAY_FRAGMENT);
            if(displayFragment != null)
                displayFragment.onSortOrderChanged();
            sortOrder = preferredSortOrder;
        }

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

        NoInternetFragment noInternetFragment = (NoInternetFragment) getSupportFragmentManager()
                .findFragmentByTag(NO_INTERNET_FRAGMENT);

        if(noInternetFragment !=null && Utility.isNetworkAvailable(this)){

            MovieSensorSyncAdapter.syncImmediately(this);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame, new MoviesDisplayFragment(), MOVIE_DISPLAY_FRAGMENT)
                    .commit();
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

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        Toast.makeText(this,"Connecting..",Toast.LENGTH_SHORT).show();
        onResume();
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
