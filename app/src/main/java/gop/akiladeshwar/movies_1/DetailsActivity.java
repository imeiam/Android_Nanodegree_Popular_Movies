package gop.akiladeshwar.movies_1;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

public class DetailsActivity extends AppCompatActivity {



    public static final String MOVIE_DETAIL_FRAGMENT_TAG = "movie_details";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            postponeEnterTransition();
        }
        if(savedInstanceState == null){

            Bundle bundle = getIntent().getBundleExtra(Movie.MOVIE_TAG);

            MovieDetailsFragment detailsFragment = new MovieDetailsFragment();
            detailsFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_detailx,detailsFragment,MOVIE_DETAIL_FRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(MOVIE_DETAIL_FRAGMENT_TAG);
        if(fragment!=null && MovieDetailsFragment.youtubeFullScreen && MovieDetailsFragment.player!=null){
            MovieDetailsFragment.player.setFullscreen(false);
            MovieDetailsFragment.youtubeFullScreen = false;
        }
        else {
            super.onBackPressed();
        }
    }
}
