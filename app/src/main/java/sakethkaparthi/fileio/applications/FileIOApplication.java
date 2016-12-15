package sakethkaparthi.fileio.applications;

import android.app.Application;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by saketh on 5/10/16.
 */

public class FileIOApplication extends Application {
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    synchronized public FirebaseAnalytics getAnalyticsInstance() {
        if (mFirebaseAnalytics == null)
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        return mFirebaseAnalytics;
    }
}
