package gop.akiladeshwar.movies_1;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import gop.akiladeshwar.movies_1.data.MovieContract;
import gop.akiladeshwar.movies_1.data.MovieDBHelper;
import gop.akiladeshwar.movies_1.sync.MovieSensorSyncAdapter;

public class MainActivity extends AppCompatActivity implements MoviesDisplayFragment.CallBack,
        NavigationView.OnNavigationItemSelectedListener,
        MovieDetailsFragment.DetailsCallBack{


    public static final String MOVIE_DISPLAY_FRAGMENT = "MOVIE_DISPLAY";
    public static final String NO_INTERNET_FRAGMENT = "NO_INTERNET";
    public static final String NO_FAV_FRAGMENT = "NO_FAV_FRAGMENT";
    public static final String MOVIE_DETAIL_FRAGMENT = "MOVIE_DETAIL_FRAGMENT";
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    public static FragmentManager fragmentManager;

    String sortOrder = null;

    Typeface titleTypeface;
    Typeface navItemTypeface;
    TextView titleTextView;

    Typeface menuItemTypeface;
    NavigationView navigationView;
    NoFavFragment noFavFragment;
    NoInternetFragment noInternetFragment;

    Toolbar toolbar;

    public static ProgressBar spinner;
    public static Boolean twoPane = false;


    LinearLayout twoPaneLayout;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(null);
        setContentView(R.layout.activity_main_nav);

        fragmentManager = getSupportFragmentManager();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        twoPaneLayout = (LinearLayout) findViewById(R.id.second_frame);
        if(twoPaneLayout==null){
            twoPane = false;
        }
        else {
            twoPane = true;
        }

        sortOrder = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getResources().getString(R.string.pref_sort_order_key),"popular");

        Typeface robotoThinTypeface = Typeface
                .createFromAsset(getAssets(),"fonts/Roboto-Thin.ttf");

        Typeface robotoCondensedTypeface =  Typeface
                .createFromAsset(getAssets(),"fonts/RobotoCondensed-Regular.ttf");

        Typeface strictTypeface =  Typeface
                .createFromAsset(getAssets(),"fonts/strict.ttf");

        Typeface titleTypeface =  Typeface
                .createFromAsset(getAssets(),"fonts/dead.TTF");
        Typeface ralewayTypeface =  Typeface
                .createFromAsset(getAssets(), "fonts/raleway.ttf");

        titleTextView = (TextView) findViewById(R.id.toolbar_title);
        titleTextView.setTypeface(titleTypeface);


        spinner = (ProgressBar) findViewById(R.id.loading_bar);
        spinner.getIndeterminateDrawable().setColorFilter(0xFFFFEB3B, android.graphics.PorterDuff.Mode.MULTIPLY);
        spinner.setVisibility(View.INVISIBLE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navItemTypeface = Typeface.createFromAsset(getAssets(),"fonts/dead.TTF");

        View headerView = navigationView.getHeaderView(0);
        TextView textView = (TextView) headerView.findViewById(R.id.nav_title);
        textView.setTypeface(titleTypeface);

        MovieSensorSyncAdapter.syncImmediately(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String preferredSortOrder = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getResources().getString(R.string.pref_sort_order_key),"popular");

        // Detect changes in sort order
        if(preferredSortOrder!=null && !preferredSortOrder.equals(sortOrder)){

            sortOrder = preferredSortOrder;
            MoviesDisplayFragment displayFragment =
                    (MoviesDisplayFragment)getSupportFragmentManager().findFragmentByTag(MOVIE_DISPLAY_FRAGMENT);
            if(displayFragment != null)
                displayFragment.onSortOrderChanged();
            sortOrder = preferredSortOrder;
        }

//        if(sortOrder.matches("favorites")){
//
//        }

        String title;
        if(sortOrder.matches("popular")) {
//            int filmCamera = 0x1F525;
//            String filmCameraText = new String(Character.toChars(filmCamera));
//            title = filmCameraText + " " + "Popular Movies";
            title = "Popular Movies";
            if(twoPaneLayout!=null)
                twoPaneLayout.setBackgroundResource(R.drawable.poster2);
            //toolbar.setBackgroundColor();
        }
        else if(sortOrder.matches("topRated")) {
//            int filmCamera = 0x1F31F;
//            String filmCameraText = new String(Character.toChars(filmCamera));
//            title = filmCameraText + " " + "Top Rated Movies";
            title = "Top Rated Movies";
            if(twoPaneLayout!=null)
                twoPaneLayout.setBackgroundResource(R.drawable.poster3);
        }
        else {
//            int filmCamera = 0x2764;
//            String filmCameraText = new String(Character.toChars(filmCamera));
//            title = filmCameraText + " " + "Favorite Movies";
            title = "Favorite Movies";
            if(twoPaneLayout!=null)
                twoPaneLayout.setBackgroundResource(R.drawable.poster);
        }
        titleTextView.setText(title);

        MovieDBHelper dbHelper = new MovieDBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor;
        String tableName = null;
        if(sortOrder.matches("popular")){
            tableName = MovieContract.PopularEntry.TABLE_NAME;
        }
        else if (sortOrder.matches("topRated")){
            tableName = MovieContract.TopRatedEntry.TABLE_NAME;
        }
        else{
            tableName = MovieContract.FavoriteEntry.TABLE_NAME;
        }
        // Table is empty - Fetch
        cursor = db.rawQuery("SELECT * FROM "+ tableName,null);

        removeFragmentByTag(NO_INTERNET_FRAGMENT);
        removeFragmentByTag(MOVIE_DISPLAY_FRAGMENT);
        removeFragmentByTag(MOVIE_DETAIL_FRAGMENT);
        removeFragmentByTag(NO_FAV_FRAGMENT);

        if(!cursor.moveToFirst())
        {
            if(sortOrder.matches("favorite")){
                // If fav is empty load NoFavFragment

                if(twoPane){
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.double_frame, new NoFavFragment(), NO_FAV_FRAGMENT)
                            .commit();
                    twoPaneLayout.setBackgroundColor(Color.WHITE);
                }
                else {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame, new NoFavFragment(), NO_FAV_FRAGMENT)
                            .commit();
                }
            }
            else {

                //Fetch and fill table
                spinner.setVisibility(View.VISIBLE);
                MovieSensorSyncAdapter.syncImmediately(this);
                Log.d(LOG_TAG, "Table Empty - Starting Sync");

                String query = "SELECT * FROM "+ tableName;
                cursor = db.rawQuery(query, null);

                if(!cursor.moveToFirst() && !Utility.isNetworkAvailable(this)){
                    MainActivity.setSpinnerOff();
                    if(twoPane){
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.double_frame,new NoInternetFragment(), NO_INTERNET_FRAGMENT)
                                .commit();
                    }
                    else {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frame, new NoInternetFragment(), NO_INTERNET_FRAGMENT)
                                .commit();
                    }
                }
                else{
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame, new MoviesDisplayFragment(), MOVIE_DISPLAY_FRAGMENT)
                            .commit();
                }
            }
        }
        else{
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame, new MoviesDisplayFragment(), MOVIE_DISPLAY_FRAGMENT)
                    .commit();
        }
        db.close();
    }


    @Override
    public void onItemSelected(Cursor cursor,View transitionView) {
        Movie movie = Utility.createMovieObjectFromCursor(cursor);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Movie.MOVIE_TAG, movie);

        if(twoPane) {

            MovieDetailsFragment movieDetailsFragment = new MovieDetailsFragment();
            movieDetailsFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.second_frame, movieDetailsFragment,MOVIE_DETAIL_FRAGMENT)
                    .commit();
        }
        else {
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra(Movie.MOVIE_TAG, bundle);
            startActivity(intent);
        }
    }





    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if( id == R.id.nav_popular){

            if(titleTextView!=null){
                String title = "Popular Movies";
                titleTextView.setText(title);
            }
            PreferenceManager.getDefaultSharedPreferences(this).edit()
                    .putString(getString(R.string.pref_sort_order_key),"popular")
                    .commit();

            onResume();
        } else  if(id == R.id.nav_top_rated) {

            if(titleTextView!=null){
                String title ="Top Rated Movies";
                titleTextView.setText(title);
            }

            PreferenceManager.getDefaultSharedPreferences(this).edit()
                    .putString(getString(R.string.pref_sort_order_key),"topRated")
                    .commit();
            onResume();
        } else if(id == R.id.nav_fav){

            if(titleTextView!=null){
                String title = "Favorite Movies";
                titleTextView.setText(title);
            }

            PreferenceManager.getDefaultSharedPreferences(this).edit()
                    .putString(getString(R.string.pref_sort_order_key),"favorite")
                    .commit();
            onResume();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(MOVIE_DETAIL_FRAGMENT);
            if(fragment!=null && MovieDetailsFragment.youtubeFullScreen && MovieDetailsFragment.player!=null){
                MovieDetailsFragment.player.setFullscreen(false);
                MovieDetailsFragment.youtubeFullScreen = false;
            }
            else {
                super.onBackPressed();
            }
        }
    }

    public static void setSpinnerOff(){
        if(MainActivity.spinner!=null && MainActivity.spinner.getVisibility()== View.VISIBLE)
            MainActivity.spinner.setVisibility(View.INVISIBLE);
    }

    public void removeFragmentByTag(String tag){
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if(fragment!=null)
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
    }

    @Override
    public void restartActivity() {
        onResume();
    }

}





//Comments


//        //Check Fav table
//
//        int i=0;
//        db = dbHelper.getReadableDatabase();
//        cursor = db.rawQuery("SELECT * FROM "+MovieContract.FavoriteEntry.TABLE_NAME,null);
//        cursor.moveToFirst();
//        while(!cursor.isAfterLast()){
//            Movie movie = Utility.createMovieObjectFromCursor(cursor);
//            Log.e(LOG_TAG,"Favorites: "+i+" - "+movie.getId()+" : "+movie.getName());
//            i++;
//            cursor.moveToNext();
//        }
//        db.close();


//        //        CheckDBEntries
//        int i=0;
//        while(!cursor.isAfterLast()){
//            Movie movie = Utility.createMovieObjectFromCursor(cursor);
//            Log.d(LOG_TAG,i+" - "+movie.getName());
//            i++;
//            cursor.moveToNext();
//        }