package gop.akiladeshwar.movies_1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import java.util.ArrayList;

import gop.akiladeshwar.movies_1.data.MovieContract;
import gop.akiladeshwar.movies_1.data.MovieDBHelper;

/**
 * Created by AkilAdeshwar on 20-05-2016.
 */
public class MovieDetailsFragment  extends Fragment implements YouTubePlayer.OnInitializedListener {


    View rootView = null;
    Movie movie = null;
    Context context = null;
    ArrayList<Review> reviewsList = null;
    ArrayList<Video> videosList = null;

    ListView reviewListView  = null;
    ArrayList<String> videoKeyList = null;



    public static final String BASE_URL = "http://image.tmdb.org/t/p/original/";
    public static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w500/";


    public static Boolean youtubeFullScreen=false;
    public static YouTubePlayer player;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {


        rootView = inflater.inflate(R.layout.fragment_detail_final, container, false);
        final ImageView moviePosterView = (ImageView) rootView.findViewById(R.id.detail_movie_poster);
        final TextView nameView = (TextView) rootView.findViewById(R.id.detail_movie_name);
        final TextView releaseDateView = (TextView) rootView.findViewById(R.id.detail_release_Date);
        final TextView voteAverageView = (TextView) rootView.findViewById(R.id.detail_vote_average);
        final TextView overviewView = (TextView) rootView.findViewById(R.id.detail_overview);
        final TextView labelReleaseView  = (TextView) rootView.findViewById(R.id.detail_label_release_Date);
        final TextView labelVoteAverageView = (TextView) rootView.findViewById(R.id.detail_label_vote_average);
        final TextView labelOverviewView = (TextView) rootView.findViewById(R.id.detail_label_overview);
        final TextView labelVideoView = (TextView) rootView.findViewById(R.id.detail_label_videos);
        final TextView labelReviewView = (TextView) rootView.findViewById(R.id.detail_label_reviews);
        final FloatingActionButton floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fab);
        //final ListView listView = (ListView) rootView.findViewById(R.id.detail_review_listview);

        Typeface robotoThinTypeface = Typeface
                .createFromAsset(getContext().getAssets(),"fonts/Roboto-Thin.ttf");


        Typeface robotoCondensedTypeface =  Typeface
                .createFromAsset(getContext().getAssets(),"fonts/RobotoCondensed-Regular.ttf");


        Typeface strictTypeface =  Typeface
                .createFromAsset(getContext().getAssets(),"fonts/strict.ttf");

        Typeface titleTypeface =  Typeface
                .createFromAsset(getContext().getAssets(),"fonts/dead.TTF");
        Typeface ralewayTypeface =  Typeface
                .createFromAsset(getContext().getAssets(), "fonts/raleway.ttf");


        nameView.setTypeface(robotoThinTypeface);

        labelReleaseView.setTypeface(strictTypeface);
        labelVoteAverageView.setTypeface(strictTypeface);
        labelOverviewView.setTypeface(robotoCondensedTypeface);
        labelReviewView.setTypeface(robotoCondensedTypeface);
        labelVideoView.setTypeface(robotoCondensedTypeface);

        overviewView.setTypeface(robotoThinTypeface);

        releaseDateView.setTypeface(ralewayTypeface);
        voteAverageView.setTypeface(titleTypeface);



        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        AppCompatActivity activity = ((AppCompatActivity) getActivity());



        final ImageView backDropImageView = (ImageView) rootView.findViewById(R.id.detail_backdrop_image);
        context = getContext();
        //final LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Bundle bundle = getArguments();
        if (bundle != null) {
            movie = bundle.getParcelable(Movie.MOVIE_TAG);


            if(!MainActivity.twoPane) {
                activity.setSupportActionBar(toolbar);
                activity.getSupportActionBar().setHomeButtonEnabled(true);
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }


            final CollapsingToolbarLayout collapsingToolbarLayout =
                    (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar);
            if(MainActivity.twoPane) {
                collapsingToolbarLayout.setTitle(movie.getName());
            }
            else {
                collapsingToolbarLayout.setTitle("Movies");
            }
            collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
            collapsingToolbarLayout.setContentScrimColor(Color.BLUE);
            collapsingToolbarLayout.setStatusBarScrimColor(Color.GREEN);



            nameView.setText(movie.getName());
            releaseDateView.setText(Utility.convertDateToDisplayFormat(movie.getReleaseDate()));
            String starSymbol = getContext().getResources().getString(R.string.star_symbol);
            voteAverageView.setText(movie.getVote_average() + " " + starSymbol);
            overviewView.setText(movie.getOverview());
            Uri backdropImageUri = Uri.parse(BASE_URL + movie.getBackdropPath());

            Bitmap bitmap = ((BitmapDrawable) backDropImageView.getDrawable()).getBitmap();
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    int primaryDark = getResources().getColor(R.color.colorPrimaryDark);
                    int primary = getResources().getColor(R.color.colorPrimary);
                    collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(primary));
                    collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkVibrantColor(primaryDark));
                }
            });


            Glide.with(getContext())
                    .load(backdropImageUri)
                    .asBitmap()
                    .into(new BitmapImageViewTarget(backDropImageView) {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                            super.onResourceReady(bitmap, anim);
                            Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(Palette palette) {

                                    int primaryDark = getResources().getColor(R.color.colorPrimaryDark);
                                    int primary = getResources().getColor(R.color.colorPrimary);
                                    collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(primary));
                                    collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkVibrantColor(primaryDark));

                                }
                            });
                        }
                    });

            Uri posterImageUri = Uri.parse(POSTER_BASE_URL + movie.getPosterPath());
            Glide.with(getContext())
                    .load(posterImageUri)
                    .crossFade()
                    .into(moviePosterView);

            Glide.with(getContext())
                    .load(posterImageUri)
                    .asBitmap()
                    .into(new BitmapImageViewTarget(moviePosterView) {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                            super.onResourceReady(bitmap, anim);
                            Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(Palette palette) {
                                    int primary = getResources().getColor(R.color.detail_movie_name_background);
                                    nameView.setBackgroundColor(palette.getMutedColor(primary));

                                }
                            });
                        }
                    });

            //Setup FAB.

            MovieDBHelper dbHelper = new MovieDBHelper(context);
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            String query = "SELECT * FROM "+
                    MovieContract.FavoriteEntry.TABLE_NAME+" WHERE "+
                    MovieContract.FavoriteEntry.COLUMN_ID+" = "+movie.getId();
            Cursor cursor = db.rawQuery(query,null);
            if(cursor.moveToFirst()){
                floatingActionButton.setImageResource(R.drawable.black);
            }
            else {
                floatingActionButton.setImageResource(R.drawable.white);
            }
            db.close();

            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MovieDBHelper dbHelper = new MovieDBHelper(context);
                    SQLiteDatabase db = dbHelper.getReadableDatabase();
                    String query = "SELECT * FROM "+
                            MovieContract.FavoriteEntry.TABLE_NAME+" WHERE "+
                            MovieContract.FavoriteEntry.COLUMN_ID+" = "+movie.getId();
                    Cursor cursor = db.rawQuery(query,null);
                    if(cursor.moveToFirst()){
                        db.close();
                        db = dbHelper.getWritableDatabase();
                        db.delete(MovieContract.FavoriteEntry.TABLE_NAME,
                                MovieContract.FavoriteEntry.COLUMN_ID+ " = ?",
                                new String[] { movie.getId() });
                        db.close();
                        floatingActionButton.setImageResource(R.drawable.white);
                        Toast.makeText(context,"Removed from Favorites",Toast.LENGTH_SHORT).show();
                        String sortOrder = PreferenceManager.getDefaultSharedPreferences(context)
                                .getString(getResources().getString(R.string.pref_sort_order_key),"popular");
                        if(MainActivity.twoPane && sortOrder.matches("favorite") ){
                            ((MainActivity)getActivity()).restartActivity();
                        }
                    }
                    else{
                        //Entry not found
                        db.close();
                        db = dbHelper.getWritableDatabase();
                        ContentValues movieRow = new ContentValues();
                        movieRow.put(MovieContract.SuperTableEntry.COLUMN_NAME,movie.getName());
                        movieRow.put(MovieContract.SuperTableEntry.COLUMN_OVERVIEW,movie.getOverview());
                        movieRow.put(MovieContract.SuperTableEntry.COLUMN_POSTER_PATH,movie.getPosterPath());
                        movieRow.put(MovieContract.SuperTableEntry.COLUMN_RELEASE_DATE,movie.getReleaseDate());
                        movieRow.put(MovieContract.SuperTableEntry.COLUMN_VOTE_AVERAGE,movie.getVote_average());
                        movieRow.put(MovieContract.SuperTableEntry.COLUMN_BACKDROP_PATH,movie.getBackdropPath());
                        movieRow.put(MovieContract.SuperTableEntry.COLUMN_ID,movie.getId());
                        movieRow.put(MovieContract.SuperTableEntry.COLUMN_VIDEOS_JSON,movie.getVideosJson());
                        movieRow.put(MovieContract.SuperTableEntry.COLUMN_REVIEWS_JSON,movie.getReviewsJson());

                        db.insert(MovieContract.FavoriteEntry.TABLE_NAME,null,movieRow);
                        db.close();
                        floatingActionButton.setImageResource(R.drawable.black);
                        Toast.makeText(context,"Added to Favorites",Toast.LENGTH_SHORT).show();
                    }


                        //(getActivity()).onResume();

                    //startActivity(Utility.createShareIntent(movie));
                }
            });

            //Video
            videosList = Utility.parseVideoData(movie);
            LinearLayout videoLayout = (LinearLayout) rootView.findViewById(R.id.detail_videolist_parent);
            if (videosList == null) {
                View view = LayoutInflater.from(context).inflate(R.layout.videoitem_error_message, null);
                if(view!=null){
                    //U+1F64F
                    TextView textView = (TextView)view.findViewById(R.id.detail_error_video);
                    textView.setTypeface(robotoThinTypeface);

                    int sorry_code = 0x1F64F ;
                    String sorry = new String(Character.toChars(sorry_code));
                    String finalStr = "OOPS! No videos yet "+sorry;
                    textView.setText(finalStr);
                    view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    videoLayout.addView(view);
                }
            } else {
                videoKeyList = new ArrayList<String>();

                //StringBuffer stringBuffer = new StringBuffer("");
                for (int i = 0; i < videosList.size(); i++) {
                    Video video = videosList.get(i);
                    Config.playVideoKey = video.getKey();
                    videoKeyList.add(video.getKey());
                }
                View view = LayoutInflater.from(context).inflate(R.layout.videoitem, null);
                if(view==null){
                    Toast.makeText(context,"Nope",Toast.LENGTH_SHORT).show();
                }
                else{
                    videoLayout.addView(view);

                    YouTubePlayerSupportFragment frag =
                            new YouTubePlayerSupportFragment();
                    getChildFragmentManager().beginTransaction()
                            .replace(R.id.youtube_fragment,frag)
                            .commit();

                    if(frag!=null)
                        frag.initialize(Config.DEVELOPER_KEY,this);
                    else
                        Toast.makeText(context,"Nopexx",Toast.LENGTH_SHORT).show();
                }

            }

            // Review
            reviewsList = Utility.parseReviewData(movie);

            LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.detail_review_listview);

            if(reviewsList!=null) {
                for (Review review : reviewsList) {
                    View view = LayoutInflater.from(context).inflate(R.layout.listitem_review, null);
                    if (view != null) {
                        TextView authorView = (TextView) view.findViewById(R.id.detail_review_author);
                        TextView contentView = (TextView) view.findViewById(R.id.detail_review_content);


                        if (authorView != null && contentView != null) {

                            int userCode = 0x1F464;
                            String user = new String(Character.toChars(userCode));
                            String authorText = user + " " + review.getAuthor();
                            authorView.setText(authorText);
                            contentView.setText(review.getContent());

                            authorView.setTypeface(robotoThinTypeface, Typeface.BOLD);
                            contentView.setTypeface(robotoCondensedTypeface);


                            view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT));
                            layout.addView(view);
                        } else
                            Toast.makeText(context, "Missed params", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Missed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else {

                View view = LayoutInflater.from(context).inflate(R.layout.listitem_error_message, null);
                if(view!=null){
                    //U+1F64F
                    TextView textView = (TextView)view.findViewById(R.id.detail_error_review);
                    textView.setTypeface(robotoThinTypeface);

                    int sorry_code = 0x1F64F ;
                    String sorry = new String(Character.toChars(sorry_code));
                    String finalStr = "OOPS! No reviews yet "+sorry;
                    textView.setText(finalStr);
                    view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    layout.addView(view);
                }
            }
        }
        return rootView;
    }


    //Youtube Interface
    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                        YouTubePlayer youTubePlayer, boolean b) {
        if(!b){
            player = youTubePlayer;
            if(videoKeyList!=null)
                youTubePlayer.cueVideos(videoKeyList);

            youTubePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                @Override
                public void onFullscreen(boolean b) {
                    youtubeFullScreen = b;
                }
             });
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {

        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(getActivity(),0).show();
        } else {
            String errorMessage = "Hello";
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
        }
    }


    public interface DetailsCallBack{
        public void restartActivity();
    }

}
