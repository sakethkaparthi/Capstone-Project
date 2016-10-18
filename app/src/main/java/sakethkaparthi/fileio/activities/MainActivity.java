package sakethkaparthi.fileio.activities;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
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

import com.google.gson.JsonObject;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sakethkaparthi.fileio.R;
import sakethkaparthi.fileio.database.FilesContract;
import sakethkaparthi.fileio.models.ProgressRequestBody;
import sakethkaparthi.fileio.networkclients.RetrofitClient;

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
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                Log.i(TAG, "Uri: " + uri.toString());
                Cursor cursor = getContentResolver().query(uri, null, null, null, null, null);
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        final String displayName = cursor
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
                        assert inputStream != null;
                        IOUtils.copy(inputStream, outputStream);
                        final File file = new File(getFilesDir(), displayName);
                        ProgressRequestBody fileBody = new ProgressRequestBody(file, new ProgressRequestBody.UploadCallbacks() {
                            @Override
                            public void onProgressUpdate(int percentage) {
                                Log.d(TAG, "onProgressUpdate: " + percentage);
                            }

                            @Override
                            public void onError() {

                            }

                            @Override
                            public void onFinish() {
                                Log.d(TAG, "File upload finished");
                            }
                        });
                        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), fileBody);
                        Call<JsonObject> request = RetrofitClient.getAPI().uploadImage(filePart);
                        request.enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                try {
                                    response.body().toString();
                                    if (response.isSuccessful()) {
                                        JSONObject object = new JSONObject(String.valueOf(response.body()));
                                        String link = object.getString("link");
                                        Log.d(TAG, "Success: " + link);
                                        ContentValues values = new ContentValues();
                                        values.put(FilesContract.FileEntry.COLUMN_NAME, displayName);
                                        values.put(FilesContract.FileEntry.COLUMN_LINK, link);
                                        values.put(FilesContract.FileEntry.COLUMN_UPLOAD_DATE, System.currentTimeMillis());
                                        values.put(FilesContract.FileEntry.COLUMN_STATUS, 1);
                                        getContentResolver().insert(FilesContract.FileEntry.CONTENT_URI, values);
                                    } else {
                                        throw new Exception("Error while uploading");
                                    }
                                } catch (Exception e) {
                                    Log.d(TAG, "onResponse: " + e);
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                Log.d(TAG, "onFailure: " + t.getMessage());
                            }
                        });
                        //request.execute();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        }
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
