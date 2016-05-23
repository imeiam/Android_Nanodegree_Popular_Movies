package gop.akiladeshwar.movies_1.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by AkilAdeshwar on 20-05-2016.
 */
public class MovieSensorAuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    private MovieSensorAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new MovieSensorAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}