package gop.akiladeshwar.movies_1;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.ArrayList;

/**
 * Created by AkilAdeshwar on 17-05-2016.
 */
public class MovieGridAdapter extends CursorAdapter {

    final private Context context= null;
    ArrayList<Movie> moviesList;

    Typeface robotoThinTypeface;

    Typeface robotoCondensedTypeface;


    Typeface strictTypeface;

    public static final String BASE_URL = "http://image.tmdb.org/t/p/w500/";


    public static class ViewHolder {

        public final TextView nameView;
        public final TextView labelReleaseView;
        public final TextView labelVoteAverageView;
        public final ImageView imageView;
        public final View textDisplayView;
        public final TextView dateView;
        public final TextView ratingView;

        public ViewHolder(View view) {
            nameView = (TextView) view.findViewById(R.id.display_movie_name);
            labelReleaseView   = (TextView) view.findViewById(R.id.display_label_release_Date);
            labelVoteAverageView = (TextView) view.findViewById(R.id.display_label_vote_average);
            imageView = (ImageView) view.findViewById(R.id.imageMovie);
            textDisplayView = view.findViewById(R.id.display_portrait_background);
            dateView = (TextView) view.findViewById(R.id.display_movie_date);
            ratingView = (TextView) view.findViewById(R.id.display_movie_rating);
        }

    }

    public MovieGridAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutId = R.layout.movie_layout;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);


        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        robotoThinTypeface = Typeface
                .createFromAsset(context.getAssets(), "fonts/Roboto-Thin.ttf");

        robotoCondensedTypeface =  Typeface
                .createFromAsset(context.getAssets(),"fonts/RobotoCondensed-Regular.ttf");
        strictTypeface =  Typeface
                .createFromAsset(context.getAssets(), "fonts/strict.ttf");


        return view;
    }


    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        Movie movie = Utility.createMovieObjectFromCursor(cursor);
        String posterPath = movie.getPosterPath();
        String releaseDate = movie.getReleaseDate();
        String rating = movie.getVote_average();

        final ViewHolder viewHolder = (ViewHolder) view.getTag();

        Uri imageUri = Uri.parse(BASE_URL + posterPath);

        Glide.with(context)
                .load(imageUri)
                .asBitmap()
                .into(new BitmapImageViewTarget(viewHolder.imageView) {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                        super.onResourceReady(bitmap, anim);
                        Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                int primary = context.getResources().getColor(R.color.detail_movie_name_background);

                                if (viewHolder.nameView == null) {
                                    viewHolder.textDisplayView.setBackgroundColor(palette.getMutedColor(primary));
                                } else {
                                    viewHolder.nameView.setBackgroundColor(palette.getMutedColor(primary));
                                }
                            }
                        });
                    }
                });

        viewHolder.dateView.setText(Utility.convertDateToDisplayFormat(releaseDate));
        viewHolder.ratingView.setText(rating + " " + context.getResources().getString(R.string.star_symbol));
        viewHolder.dateView.setTypeface(robotoCondensedTypeface);
        viewHolder.ratingView.setTypeface(robotoCondensedTypeface);

        if(viewHolder.nameView !=null) {
            viewHolder.nameView.setTypeface(robotoThinTypeface);
            viewHolder.nameView.setText(movie.getName());
            viewHolder.labelReleaseView.setTypeface(strictTypeface);
            viewHolder.labelVoteAverageView.setTypeface(strictTypeface);
        }

    }

    @Override
    public int getViewTypeCount() {
        return 1; // If Adapter returns same type of views all elements
    }

}
