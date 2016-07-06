package gop.akiladeshwar.movies_1;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;


// Not Used in Stage 2 of the app.

public class SettingsActivity extends AppCompatActivity{


    Typeface titleTypeface;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        titleTypeface =  Typeface
                .createFromAsset(this.getAssets(), "fonts/dead.TTF");
        TextView textView = (TextView) findViewById(R.id.toolbar_title);
        textView.setTypeface(titleTypeface);
        String title = "Settings";
        textView.setText(title);




        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction()
                .replace(R.id.frame_settings,new SettingsFragment())
        .commit();

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public Intent getParentActivityIntent(){
        return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }
}