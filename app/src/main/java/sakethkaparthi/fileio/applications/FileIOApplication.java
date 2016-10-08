package sakethkaparthi.fileio.applications;

import android.app.Application;

import com.facebook.stetho.Stetho;

import net.gotev.uploadservice.UploadService;

import sakethkaparthi.fileio.BuildConfig;

/**
 * Created by saketh on 5/10/16.
 */

public class FileIOApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
    }
}
