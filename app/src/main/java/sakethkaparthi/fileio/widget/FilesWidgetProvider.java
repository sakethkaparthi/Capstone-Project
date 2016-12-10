package sakethkaparthi.fileio.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import sakethkaparthi.fileio.R;
import sakethkaparthi.fileio.activities.FileDescriptionActivity;

/**
 * Implementation of App Widget functionality.
 */
public class FilesWidgetProvider extends AppWidgetProvider {
    public static final String EXTRA_ITEM = "item_pos";
    public static final String CLICK_ACTION = "item_click";
    private static final String TAG = "widget";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int count = appWidgetIds.length;

        Log.i(TAG, "onUpdate: count: " + count);

        for (int i = 0; i < count; i++) {
            int widgetId = appWidgetIds[i];
            Log.i(TAG, "onUpdate: widgetId: " + widgetId);

            Intent svcIntent = new Intent(context, WidgetViewsService.class);
            svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.files_widget);
            remoteViews.setRemoteAdapter(R.id.files_list_view, svcIntent);
            remoteViews.setEmptyView(R.id.files_list_view, R.id.empty_view);

            /*// START
            Intent intent = new Intent(context, WidgetViewsService.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.widget_item, pendingIntent);
            // END
*/
            final Intent onItemClick = new Intent(context, WidgetViewsService.class);
            onItemClick.setAction(CLICK_ACTION);
            onItemClick.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            onItemClick.setData(Uri.parse(onItemClick
                    .toUri(Intent.URI_INTENT_SCHEME)));
            final PendingIntent onClickPendingIntent = PendingIntent
                    .getBroadcast(context, 1001, onItemClick,
                            PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setPendingIntentTemplate(R.id.files_list_view,
                    onClickPendingIntent);

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
            appWidgetManager.notifyAppWidgetViewDataChanged(widgetId, R.id.files_list_view);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: received something");
        if (intent.getAction().equals(CLICK_ACTION)) {
            int viewIndex = intent.getIntExtra(EXTRA_ITEM, 0);
            Toast.makeText(context, "Touched view " + viewIndex, Toast.LENGTH_SHORT).show();
            Intent intentToLaunch = new Intent(context, FileDescriptionActivity.class);
            intentToLaunch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentToLaunch);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

