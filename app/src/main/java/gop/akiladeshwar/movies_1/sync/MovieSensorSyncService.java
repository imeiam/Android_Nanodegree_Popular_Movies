package gop.akiladeshwar.movies_1.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


/**
 * Created by AkilAdeshwar on 20-05-2016.
 */
public class MovieSensorSyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static MovieSensorSyncAdapter sMovieSensorSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sMovieSensorSyncAdapter == null) {
                sMovieSensorSyncAdapter = new MovieSensorSyncAdapter(getApplicationContext(), true);
            }
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return sMovieSensorSyncAdapter.getSyncAdapterBinder();
    }
}
