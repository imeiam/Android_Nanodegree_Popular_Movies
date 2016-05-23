package gop.akiladeshwar.movies_1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

/**
 * Created by AkilAdeshwar on 20-05-2016.
 */
public class MovieDetailsFragment  extends Fragment {


    View rootView = null;
    Movie movie = null;
    Context context = null;

    public static final String BASE_URL = "http://image.tmdb.org/t/p/original/";
    public static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w500/";



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        rootView = inflater.inflate(R.layout.fragment_detail_final, container, false);
        final ImageView moviePosterView = (ImageView) rootView.findViewById(R.id.detail_movie_poster);
        final TextView nameView = (TextView) rootView.findViewById(R.id.detail_movie_name);
        final TextView releaseDateView = (TextView) rootView.findViewById(R.id.detail_release_Date);
        final TextView voteAverageView = (TextView) rootView.findViewById(R.id.detail_vote_average);
        final TextView overviewView = (TextView) rootView.findViewById(R.id.detail_overview);
        final TextView labelReleaseView  = (TextView) rootView.findViewById(R.id.detail_label_release_Date);
        final TextView labelVoteAverageView = (TextView) rootView.findViewById(R.id.detail_label_vote_average);
        final TextView labelOverviewView = (TextView) rootView.findViewById(R.id.detail_label_overview);
        final FloatingActionButton floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fab);

        Typeface robotoThinTypeface = Typeface
                .createFromAsset(getContext().getAssets(),"fonts/Roboto-Thin.ttf");


        Typeface robotoCondensedTypeface =  Typeface
                .createFromAsset(getContext().getAssets(),"fonts/RobotoCondensed-Regular.ttf");


        Typeface strictTypeface =  Typeface
                .createFromAsset(getContext().getAssets(),"fonts/strict.ttf");


        nameView.setTypeface(robotoThinTypeface);
        labelReleaseView.setTypeface(strictTypeface);
        labelVoteAverageView.setTypeface(strictTypeface);
        labelOverviewView.setTypeface(robotoCondensedTypeface);
        overviewView.setTypeface(robotoThinTypeface);


        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setHomeButtonEnabled(true);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        final CollapsingToolbarLayout collapsingToolbarLayout =
                (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("Movies");
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        collapsingToolbarLayout.setContentScrimColor(Color.BLUE);
        collapsingToolbarLayout.setStatusBarScrimColor(Color.GREEN);

        final ImageView backDropImageView = (ImageView) rootView.findViewById(R.id.detail_backdrop_image);
        context = getContext();

        Bundle bundle = getArguments();
        if (bundle != null) {
            movie = bundle.getParcelable(Movie.MOVIE_TAG);
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

            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(Utility.createShareIntent(movie));
                }
            });

        }
        return rootView;
    }

}
