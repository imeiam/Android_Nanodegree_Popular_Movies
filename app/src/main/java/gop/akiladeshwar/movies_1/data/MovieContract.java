package gop.akiladeshwar.movies_1.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by AkilAdeshwar on 20-05-2016.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "akiladeshwar.moviesensor.data";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_POPULAR = "popular";

    public static final String PATH_TOP_RATED = "top-rated";

    public static abstract class SuperTableEntry implements BaseColumns{

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE =  "release_date";
        public static final String COLUMN_VOTE_AVERAGE =  "vote_average";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";

    }

    public static final class PopularEntry extends SuperTableEntry {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_POPULAR).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_POPULAR;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+ PATH_POPULAR;


        public static final String TABLE_NAME = "popular";

        public static Uri buildPopularMovieUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static class TopRatedEntry extends SuperTableEntry{



        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_TOP_RATED).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_TOP_RATED;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+ PATH_TOP_RATED;


        public static final String TABLE_NAME = "top_rated";


        public static Uri buildTopRatedMovieUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
