package gop.akiladeshwar.movies_1;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

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



//Youttube API key - AIzaSyC3EjvFUQKzf8A3ZEhKj9Tlfl9FjCl1Nj8

/**
 * Created by AkilAdeshwar on 18-05-2016.
 */
public class Utility {


    public static final String LOG_TAG = Utility.class.getSimpleName();
    public static final String [] months = { "Jan" ,
            "Feb", "Mar", "Apr" , "May" , "Jun" , "Jul" , "Aug", "Sep",
            "Oct" , "Nov" , "Dec" };
    public static final String APP_HASHTAG = "#MovieSensor";


    public static String convertDateToDisplayFormat(String date){

        String month = months[Integer.parseInt(date.substring(5,7))-1];
        String year = date.substring(2,4);
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


    public static TextView getEmptyViewDisplay(Context context,String message){
        TextView textView = new TextView(context);
        textView.setText("OOPS! No "+message+" yet.");
        return textView;
    }




    public static Movie createMovieObjectFromCursor(Cursor cursor){
        Movie movie = new Movie();
        movie.setName(cursor.getString(MoviesDisplayFragment.COLUMN_NAME));
        movie.setOverview(cursor.getString(MoviesDisplayFragment.COLUMN_OVERVIEW));
        movie.setVote_average(cursor.getString(MoviesDisplayFragment.COLUMN_VOTE_AVERAGE));
        movie.setReleaseDate(cursor.getString(MoviesDisplayFragment.COLUMN_RELEASE_DATE));
        movie.setPosterPath(cursor.getString(MoviesDisplayFragment.COLUMN_POSTER_PATH));
        movie.setBackdropPath(cursor.getString(MoviesDisplayFragment.COLUMN_BACKDROP_PATH));
        movie.setId(cursor.getString(MoviesDisplayFragment.COLUMN_ID));
        movie.setVideosJson(cursor.getString(MoviesDisplayFragment.COLUMN_VIDEOS_JSON));
        movie.setReviewsJson(cursor.getString(MoviesDisplayFragment.COLUMN_REVIEWS_JSON));
        return movie;
    }





    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }



    public static ArrayList<Movie> getMovieData(Context context,String sortOrder){

        ArrayList<Movie> moviesList = null;
        try {
            moviesList= new ArrayList<>();
            String finalJSON = makeHTTPRequest("movie_major_details",sortOrder,null);
            moviesList = parseJSON(finalJSON);
            for(int i=0;i<moviesList.size();i++){
                fetchReviewData(moviesList.get(i));
                fetchVideoData(moviesList.get(i));
            }
        }
        catch (Exception e){
            Log.d(LOG_TAG,"Get Movie Data Error");
        }
        return moviesList;
    }


    public static void fetchReviewData(Movie movie) {
        String finalJSON = makeHTTPRequest("review", null, movie);
        if(finalJSON != null)
            movie.setReviewsJson(finalJSON);
    }

    public static void fetchVideoData(Movie movie){
        String finalJSON = makeHTTPRequest("video",null,movie);
        if(finalJSON != null)
            movie.setVideosJson(finalJSON);
    }


    public static String makeHTTPRequest(String task,String sortOrder,Movie movie){
        String finalJSON = null;
        while(true){
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                final String BASE_URL =
                        "http://api.themoviedb.org/3/movie";
                final String APPID_PARAM = "api_key";
                final String REVIEWS_PATH = "reviews";
                final String VIDEOS_PATH = "videos";



                final String app_id = "a48b2d314a0bc596313f609f4752ba47";

                Uri builtUri = null;

                if(task.matches("review")){
                    builtUri = Uri.parse(BASE_URL).buildUpon()
                            .appendPath(movie.getId())
                            .appendPath(REVIEWS_PATH)
                            .appendQueryParameter(APPID_PARAM, app_id)
                            .build();
                }else if(task.matches("video")) {

                    builtUri = Uri.parse(BASE_URL).buildUpon()
                            .appendPath(movie.getId())
                            .appendPath(VIDEOS_PATH)
                            .appendQueryParameter(APPID_PARAM, app_id)
                            .build();

                }else if (task.matches("movie_major_details")){

                    String topOrPopular = "popular";

                    if(sortOrder.matches("topRated")){
                        topOrPopular = "top_rated";
                    }
                    builtUri = Uri.parse(BASE_URL).buildUpon()
                            .appendPath(topOrPopular)
                            .appendQueryParameter(APPID_PARAM, app_id)
                            .build();
                }
                URL url = new URL(builtUri.toString());
                Log.i(LOG_TAG,"Making request: "+url);

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
                finalJSON =  buffer.toString();
                Log.i(LOG_TAG,"Got response: "+finalJSON);
            }catch (IOException e) {
                Log.i(LOG_TAG, e.getMessage(), e);
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.i(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            break;
        }
        return finalJSON;
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
            //DecimalFormat ratingFormat = new DecimalFormat("#.00");
//            movie.setVote_average(ratingFormat.format(result.getString("vote_average")));
            movie.setVote_average(result.getString("vote_average"));
            movie.setBackdropPath(result.getString("backdrop_path"));
            movie.setId(result.getString("id"));
            movie.setVideosJson("VideoJson");
            movie.setReviewsJson("ReviewJson");
            moviesList.add(movie);
        }
        return moviesList;
    }

    public static ArrayList<Review> parseReviewData(Movie movie){

        ArrayList<Review> reviewsList = new ArrayList<Review>();
        if (movie==null)
                return null;
        try {

            JSONObject responseJSON = new JSONObject(movie.getReviewsJson());
            JSONArray results = responseJSON.getJSONArray("results");

            if(results.length()==0)
                return null; //No reviews

            for(int i=0;i<results.length();i++){
                JSONObject result = (JSONObject) results.get(i);
                Review review = new Review();
                review.setAuthor(result.getString("author"));
                review.setContent(result.getString("content"));
                reviewsList.add(review);
            }
        }
        catch(Exception e){
            Log.i(LOG_TAG,"Error: Review Parsing");
        }

        return reviewsList;

    }

    public static ArrayList<Video> parseVideoData(Movie movie){

        ArrayList<Video> videosList = new ArrayList<>();
        if (movie==null)
            return null;
        try {

            JSONObject responseJSON = new JSONObject(movie.getVideosJson());
            JSONArray results = responseJSON.getJSONArray("results");

            if(results.length()==0)
                return null; //No reviews

            for(int i=0;i<results.length();i++){
                JSONObject result = (JSONObject) results.get(i);
                Video video = new Video();
                video.setKey(result.getString("key"));
                video.setSite(result.getString("site"));
                videosList.add(video);
            }
        }
        catch(Exception e){
            Log.i(LOG_TAG,"Error: Review Parsing");
        }
        return videosList;
    }


    public static void getListViewSize(ListView myListView) {
        ListAdapter myListAdapter = myListView.getAdapter();
        if (myListAdapter == null) {
            //do nothing return null
            return;
        }
        //set listAdapter in loop for getting final size
        int totalHeight = 0;
        for (int size = 0; size < myListAdapter.getCount(); size++) {
            View listItem = myListAdapter.getView(size, null, myListView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        //setting listview item in adapter
        ViewGroup.LayoutParams params = myListView.getLayoutParams();
        params.height = totalHeight + (myListView.getDividerHeight() * (myListAdapter.getCount() - 1));
        myListView.setLayoutParams(params);
        // print height of adapter on log
        Log.i("height of listItem:", String.valueOf(totalHeight));
    }


}
