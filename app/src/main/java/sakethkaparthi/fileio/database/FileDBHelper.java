package sakethkaparthi.fileio.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by saketh on 4/10/16.
 */

public class FileDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "fileList.db";

    public FileDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createFilesTable(db);
    }

    private void createFilesTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + FilesContract.FileEntry.TABLE_NAME + " (" +
                        FilesContract.FileEntry._ID + " INTEGER PRIMARY KEY, " +
                        FilesContract.FileEntry.COLUMN_NAME + " TEXT NOT NULL," +
                        FilesContract.FileEntry.COLUMN_LINK + " TEXT NOT NULL, " +
                        FilesContract.FileEntry.COLUMN_UPLOAD_DATE + " INTEGER NOT NULL, " +
                        FilesContract.FileEntry.COLUMN_STATUS + " INTEGER NOT  NULL );"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FilesContract.FileEntry.TABLE_NAME);
        onCreate(db);
    }
}
