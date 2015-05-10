package bozic.bajo.weathercast.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by savo on 10.5.2015.
 */
public class SyncAdapterService extends Service {
    public SyncAdapterService() {
        super();
    }

    private final static Object mLockSyncAdapter = new Object();
    private static SyncAdapter mSyncAdapter =null;
    @Override
    public void onCreate() {
        super.onCreate();
        synchronized (mLockSyncAdapter) {
            if (mSyncAdapter == null) {
                mSyncAdapter = new SyncAdapter(this, true);
            }

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mSyncAdapter.getSyncAdapterBinder();
    }
}
