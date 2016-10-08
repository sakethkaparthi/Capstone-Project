package sakethkaparthi.fileio.activities;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import sakethkaparthi.fileio.R;
import sakethkaparthi.fileio.database.FilesContract;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int OPEN_FILE_CODE = 666;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getLoaderManager().initLoader(0, null, this);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("*/*");
                startActivityForResult(intent, OPEN_FILE_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_FILE_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                Log.i(TAG, "Uri: " + uri.toString());
                Cursor cursor = getContentResolver().query(uri, null, null, null, null, null);
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        String displayName = cursor
                                .getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        Log.i(TAG, "Display Name: " + displayName);
                        int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                        String size = null;
                        if (!cursor.isNull(sizeIndex)) {
                            size = cursor.getString(sizeIndex);
                        } else {
                            size = "Unknown";
                        }
                        Log.i(TAG, "Size: " + size);
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        OutputStream outputStream = openFileOutput(displayName, Context.MODE_PRIVATE);
                        IOUtils.copy(inputStream, outputStream);
                        File file = new File(getFilesDir(), displayName);
                        String serverUrlString = "https://file.io/";
                        new MultipartUploadRequest(this, serverUrlString)
                                .addFileToUpload(file.getAbsolutePath(), "file")
                                .setNotificationConfig(getNotificationConfig(displayName))
                                .setAutoDeleteFilesAfterSuccessfulUpload(true)
                                .setMaxRetries(3)
                                .setDelegate(new UploadStatusDelegate() {
                                    @Override
                                    public void onProgress(UploadInfo uploadInfo) {
                                        Log.d(TAG, "onProgress: " + uploadInfo.getProgressPercent());
                                    }

                                    @Override
                                    public void onError(UploadInfo uploadInfo, Exception exception) {
                                        exception.printStackTrace();
                                    }

                                    @Override
                                    public void onCompleted(UploadInfo uploadInfo, ServerResponse serverResponse) {
                                        Log.d(TAG, "onCompleted: File uploaded successfully");
                                        Log.d(TAG, "Server Response " + serverResponse.getBodyAsString());
                                    }

                                    @Override
                                    public void onCancelled(UploadInfo uploadInfo) {

                                    }
                                }).startUpload();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    cursor.close();
                }
            }
        }
    }

    private UploadNotificationConfig getNotificationConfig(String filename) {

        return new UploadNotificationConfig()
                .setIcon(R.drawable.ic_cloud_upload)
                .setCompletedIcon(android.R.drawable.stat_sys_upload_done)
                .setErrorIcon(android.R.drawable.stat_notify_error)
                .setTitle(filename)
                .setInProgressMessage(getString(R.string.uploading))
                .setCompletedMessage(getString(R.string.upload_success))
                .setErrorMessage(getString(R.string.upload_error))
                .setAutoClearOnSuccess(false)
                .setClickIntent(new Intent(this, MainActivity.class))
                .setClearOnAction(true)
                .setRingToneEnabled(true);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(MainActivity.this, FilesContract.FileEntry.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        while (cursor.moveToNext()) {
            Log.d(TAG, cursor.getString(1) + " " + cursor.getString(2) + " " + cursor.getLong(3));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
