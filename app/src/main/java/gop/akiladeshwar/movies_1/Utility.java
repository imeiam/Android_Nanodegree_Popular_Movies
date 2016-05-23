package gop.akiladeshwar.movies_1;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by AkilAdeshwar on 18-05-2016.
 */
public class Utility {


    public static final String LOG_TAG = Utility.class.getSimpleName();
    public static final String [] months = { "Jan" ,
            "Feb", "Mar", "Apr" , "May" , "Jun" , "Jul" , "Aug", "Sep",
            "Oct" , "Nov" , "Dec" };
    public static final String APP_HASHTAG = "#MovieSensor";


    public static void printToast(Context context,String message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }

    public static String convertDateToDisplayFormat(String date){

        String month = months[Integer.parseInt(date.substring(5,7))-1];
        String year = Integer.parseInt(date.substring(2,4))+"";
        String finalStr = month+" '"+year; // Mar '16
        return finalStr;

    }

    public static Intent createShareIntent(Movie movie){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        String finalStr = movie.getName()+"\nRelease Date: "+
                Utility.convertDateToDisplayFormat(movie.getReleaseDate())+"\nRating: "+movie.getVote_average()+"\n"+APP_HASHTAG;
        shareIntent.putExtra(Intent.EXTRA_TEXT,finalStr);
        return shareIntent;
    }






    public static Movie createMovieObjectFromCursor(Cursor cursor){
        Movie movie = new Movie();
        movie.setName(cursor.getString(MoviesDisplayFragment.COLUMN_NAME));
        movie.setOverview(cursor.getString(MoviesDisplayFragment.COLUMN_OVERVIEW));
        movie.setVote_average(cursor.getString(MoviesDisplayFragment.COLUMN_VOTE_AVERAGE));
        movie.setReleaseDate(cursor.getString(MoviesDisplayFragment.COLUMN_RELEASE_DATE));
        movie.setPosterPath(cursor.getString(MoviesDisplayFragment.COLUMN_POSTER_PATH));
        movie.setBackdropPath(cursor.getString(MoviesDisplayFragment.COLUMN_BACKDROP_PATH));
        return movie;
    }





    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }



    public static ArrayList<Movie> getMovieData(Context context,String sortOrder){

        ArrayList<Movie> moviesList = new ArrayList<>();

        while(true){
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String finalJSON="";
            try {
                final String BASE_URL =
                        "http://api.themoviedb.org/3/movie";
                final String APPID_PARAM = "api_key";

                String topOrPopular = "popular";

                if(sortOrder.matches("topRated")){
                    topOrPopular = "top_rated";
                }

                final String app_id = "API-KEY-HERE";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(topOrPopular)
                        .appendQueryParameter(APPID_PARAM, app_id)
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do. Try again
                    continue;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Try again
                    continue;
                }
                finalJSON = buffer.toString();
                moviesList = parseJSON(finalJSON);
            }catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage(), e);

            }catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }
              finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            break;
        }
    return moviesList;
    }


    public static ArrayList<Movie> parseJSON(String finalJSON) throws JSONException{

        ArrayList<Movie> moviesList = new ArrayList<>();


        JSONObject responseJSON = new JSONObject(finalJSON);
        JSONArray results = responseJSON.getJSONArray("results");


        for(int i=0;i<results.length();i++){
            JSONObject result = (JSONObject) results.get(i);
            Movie movie = new Movie();
            movie.setName(result.getString("title"));
            movie.setOverview(result.getString("overview"));
            movie.setPosterPath(result.getString("poster_path"));
            movie.setReleaseDate(result.getString("release_date"));
            movie.setVote_average(result.getString("vote_average"));
            movie.setBackdropPath(result.getString("backdrop_path"));
            moviesList.add(movie);
        }
        return moviesList;
    }
}
