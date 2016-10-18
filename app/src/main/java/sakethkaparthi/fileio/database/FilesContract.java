package sakethkaparthi.fileio.database;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by saketh on 4/10/16.
 */

public class FilesContract {
    public static final String CONTENT_AUTHORITY = "sakethkaparthi.fileio";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_FILE = "file";

    public static final class FileEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FILE).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_URI + "/" + PATH_FILE;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_FILE;

        public static final String TABLE_NAME = "fileTable";
        public static final String COLUMN_NAME = "fileName";
        public static final String COLUMN_UPLOAD_DATE = "fileUploadDate";
        public static final String COLUMN_LINK = "fileLink";
        public static final String COLUMN_STATUS = "status";

        public static Uri buildFileUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }


}
