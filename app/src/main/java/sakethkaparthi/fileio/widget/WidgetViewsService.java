package sakethkaparthi.fileio.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import org.apache.commons.io.FilenameUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import sakethkaparthi.fileio.R;
import sakethkaparthi.fileio.database.FilesContract;

/**
 * Created by saketh on 10/12/16.
 */

public class WidgetViewsService extends RemoteViewsService {
    static String TAG = WidgetViewsService.class.getCanonicalName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d(TAG, "onGetViewFactory: ");
        return new FileViewsFactory(this.getApplicationContext(), intent);
    }
}

class FileViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private Cursor mCursor;
    static String TAG = FileViewsFactory.class.getCanonicalName();

    FileViewsFactory(Context applicationContext, Intent intent) {
        Log.d(TAG, "Constructor: ");
        mContext = applicationContext;
    }

    @Override
    public void onCreate() {
        setup();
    }

    @Override
    public void onDataSetChanged() {
        setup();
    }

    private void setup() {
        Log.d(TAG, "setup: ");
        mCursor = mContext.getContentResolver().query(FilesContract.FileEntry.CONTENT_URI, null, null, null, FilesContract.FileEntry.COLUMN_UPLOAD_DATE + " desc, " + FilesContract.FileEntry.COLUMN_STATUS + " desc");
        if (mCursor != null) {
            mCursor.moveToFirst();
            Log.d(TAG, "setup: size = " + mCursor.getCount());
        }

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.list_item_widget);
        if (mCursor != null && !mCursor.isAfterLast()) {
            mCursor.moveToPosition(position);
            views.setTextViewText(R.id.file_name_text_view, mCursor.getString(1));
            views.setImageViewResource(R.id.file_icon, getIconFromExtension(mCursor.getString(1)));
            int uploadedDay = getDateFromEpoch(mCursor.getLong(3));
            int today = getDateFromEpoch(System.currentTimeMillis());
            int remaining = uploadedDay + 13 - today;
            if (remaining < 0) {
                views.setTextViewText(R.id.file_expiry_text_view, mContext.getResources().getString(R.string.file_expired));
                views.setTextColor(R.id.file_expiry_text_view, ContextCompat.getColor(mContext, R.color.colorAccent));
            } else {
                views.setTextViewText(R.id.file_expiry_text_view, "Expires in " + remaining + " days");
            }
        }
        Bundle extras = new Bundle();
        extras.putInt(FilesWidgetProvider.EXTRA_ITEM, position);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        views.setOnClickFillInIntent(R.id.widget_layout, fillInIntent);
        return views;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public RemoteViews getLoadingView() {
        return new RemoteViews(mContext.getPackageName(), R.layout.widget_loading_layout);
    }

    private int getDateFromEpoch(long milliseconds) {
        Date date = new Date(milliseconds);
        DateFormat formatter = new SimpleDateFormat("DDD", Locale.UK);
        String dateFormatted = formatter.format(date);
        return Integer.valueOf(dateFormatted);
    }

    private int getIconFromExtension(String filename) {
        String extension = FilenameUtils.getExtension(filename);
        if (extension.equalsIgnoreCase("docx") || extension.equalsIgnoreCase("doc"))
            return R.drawable.ic_doc;
        if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg"))
            return R.drawable.ic_jpg;
        if (extension.equalsIgnoreCase("avi"))
            return R.drawable.ic_avi;
        if (extension.equalsIgnoreCase("mp3"))
            return R.drawable.ic_mp3;
        if (extension.equalsIgnoreCase("png"))
            return R.drawable.ic_png;
        if (extension.equalsIgnoreCase("zip"))
            return R.drawable.ic_zip;
        if (extension.equalsIgnoreCase("mp4"))
            return R.drawable.ic_mp4;
        if (extension.equalsIgnoreCase("pdf"))
            return R.drawable.ic_pdf;
        if (extension.equalsIgnoreCase("ppt") || extension.equalsIgnoreCase("pptx"))
            return R.drawable.ic_ppt;
        if (extension.equalsIgnoreCase("exe"))
            return R.drawable.ic_exe;
        if (extension.equalsIgnoreCase("iso"))
            return R.drawable.ic_iso;
        if (extension.equalsIgnoreCase("txt"))
            return R.drawable.ic_txt;
        if (extension.equalsIgnoreCase("xml"))
            return R.drawable.ic_xml;
        if (extension.equalsIgnoreCase("xls"))
            return R.drawable.ic_xls;

        return R.drawable.ic_file;
    }
}
