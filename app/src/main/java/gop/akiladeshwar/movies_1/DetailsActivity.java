package gop.akiladeshwar.movies_1;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DetailsActivity extends AppCompatActivity {

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
                    .replace(R.id.frame_detailx,detailsFragment)
                    .commit();
        }

    }
}
