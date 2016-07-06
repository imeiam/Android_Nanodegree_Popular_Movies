package gop.akiladeshwar.movies_1;

/**
 * Created by AkilAdeshwar on 05-07-2016.
 */

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by AkilAdeshwar on 18-05-2016.
 */
public class NoFavFragment extends Fragment {

    Typeface typeface;

    public NoFavFragment(){
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_settings:
                startActivity(new Intent(getContext(),SettingsActivity.class));
                break;
        }
        return true;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.no_fav, container, false);
        typeface =  Typeface
                .createFromAsset(getContext().getAssets(),"fonts/dead.TTF");
        TextView textView = (TextView) rootView.findViewById(R.id.no_fav_text);
        textView.setTypeface(typeface);
        return rootView;
    }
}
