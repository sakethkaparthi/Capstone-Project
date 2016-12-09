package sakethkaparthi.fileio.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import sakethkaparthi.fileio.services.UploadService;

/**
 * Created by saketh on 9/12/16.
 */

public class RetryReceiver extends BroadcastReceiver {
    public static String TAG = RetryReceiver.class.getCanonicalName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String uri = intent.getStringExtra("uri");
        String displayName = intent.getStringExtra("displayName");
        Intent uploadIntent = new Intent(context, UploadService.class);
        uploadIntent.putExtra("uri", uri);
        uploadIntent.putExtra("displayName", displayName);
        context.startService(uploadIntent);

    }
}
