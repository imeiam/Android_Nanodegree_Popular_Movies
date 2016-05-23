package gop.akiladeshwar.movies_1;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import gop.akiladeshwar.movies_1.data.MovieContract;
import gop.akiladeshwar.movies_1.sync.MovieSensorSyncAdapter;

/**
 * Created by AkilAdeshwar on 18-05-2016.
 */
public class MoviesDisplayFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    GridView gridView;
    ListView listView;
    View rootView;
    View emptyView;
    MovieGridAdapter movieAdapter = null;
    private static final int MOVIE_LOADER = 0;

    //_ID is 0 - FirstColumn as table inherits BaseColumns
    public static final int COLUMN_NAME = 1;
    public static final int COLUMN_POSTER_PATH = 2;
    public static final int COLUMN_OVERVIEW = 3;
    public static final int COLUMN_RELEASE_DATE = 4;
    public static final int COLUMN_VOTE_AVERAGE =  5;
    public static final int COLUMN_BACKDROP_PATH =  6;

    public MoviesDisplayFragment(){
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.action_settings:
                startActivity(new Intent(getContext(),SettingsActivity.class));
                break;
        }
        return true;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    public void onSortOrderChanged(){
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }

    @Override
    public void onStart(){
        super.onStart();
        updateMovies(); // Use this for service.
    }

    public void updateMovies(){
        MovieSensorSyncAdapter.syncImmediately(getActivity());
    }

    public interface CallBack{
        void onItemSelected(Cursor cursor,View view);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_movie_display,container,false);
        emptyView = inflater.inflate(R.layout.fragment_no_internet,container,false);
        gridView = (GridView) rootView.findViewById(R.id.gridview);
        movieAdapter = new MovieGridAdapter(getContext(),null,0);
        if(gridView == null){

            listView = (ListView) rootView.findViewById(R.id.listView);
            listView.setAdapter(movieAdapter);
            listView.setEmptyView(emptyView);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                    ImageView imageView = (ImageView)view.findViewById(R.id.imageMovie);
                    ((CallBack)getActivity()).onItemSelected(cursor, imageView);
                }
            });
        }
        else{
            gridView.setAdapter(movieAdapter);
            gridView.setEmptyView(emptyView);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                    ImageView imageView = (ImageView)view.findViewById(R.id.imageMovie);
                    ((CallBack)getActivity()).onItemSelected(cursor, imageView);
                }
            });
        }
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String sortOrder = PreferenceManager.getDefaultSharedPreferences(getContext())
                .getString(getContext().getResources().getString(R.string.pref_sort_order_key),"popular");

        Uri contentUri;

        if(sortOrder.matches("popular")){
            contentUri = MovieContract.PopularEntry.CONTENT_URI;
        }
        else{
            contentUri = MovieContract.TopRatedEntry.CONTENT_URI;
        }

        return new CursorLoader(getActivity(),
                contentUri,
                null,
                null,
                null,
                null);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        movieAdapter.swapCursor(data);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieAdapter.swapCursor(null);
    }

}
