package gop.akiladeshwar.movies_1;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import gop.akiladeshwar.movies_1.data.MovieContract;
import gop.akiladeshwar.movies_1.data.MovieDBHelper;

public class TestDB extends AndroidTestCase {


    public static final String TEST_NAME = TestDB.class.getSimpleName();

    public void setUp() {
        Log.i(TEST_NAME,"Starting Test");
    }



    public void testDBEntries(){


        SQLiteDatabase db = new MovieDBHelper(mContext).getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + MovieContract.PopularEntry.TABLE_NAME, null);


        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            Movie movie = Utility.createMovieObjectFromCursor(cursor);
            Log.d(TEST_NAME,movie.getName()+" - "+movie.getBackdropPath());
        }

        db.close();
    }




    public long insertLocation() {
        return -1L;
    }
}
