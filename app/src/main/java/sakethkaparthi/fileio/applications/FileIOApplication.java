package sakethkaparthi.fileio.applications;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by saketh on 5/10/16.
 */

public class FileIOApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
