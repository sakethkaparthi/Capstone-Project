package sakethkaparthi.fileio.activities;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;

import sakethkaparthi.fileio.R;
import sakethkaparthi.fileio.adapters.FileAdapter;
import sakethkaparthi.fileio.applications.FileIOApplication;
import sakethkaparthi.fileio.database.FilesContract;
import sakethkaparthi.fileio.services.UploadService;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int OPEN_FILE_CODE = 666;
    private FileAdapter adapter;
    private RecyclerView mRecyclerView;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        final FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.setType("*/*");
                    startActivityForResult(intent, OPEN_FILE_CODE);
                } else {
                    Snackbar.make(findViewById(R.id.container), getResources().getString(R.string.no_internet), Snackbar.LENGTH_LONG).show();
                }
            }
        });
        MobileAds.initialize(getApplicationContext(), getResources().getString(R.string.banner_ad_unit_id));
        FileIOApplication application = (FileIOApplication) getApplication();
        mFirebaseAnalytics = application.getAnalyticsInstance();
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "screen");
        params.putString(FirebaseAnalytics.Param.ITEM_NAME, "Main Activity");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params);
        mRecyclerView = (RecyclerView) findViewById(R.id.files_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Animation scaleDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_down);
                Animation scaleUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_up);
                if (dy > 0) {
                    if (floatingActionButton.getVisibility() == VISIBLE) {
                        floatingActionButton.startAnimation(scaleDown);
                        floatingActionButton.setVisibility(GONE);
                    }

                } else {
                    if (floatingActionButton.getVisibility() == GONE) {
                        floatingActionButton.startAnimation(scaleUp);
                        floatingActionButton.setVisibility(VISIBLE);
                    }

                }
            }
        });
        getLoaderManager().initLoader(0, null, this);
        AdView mAdView = (AdView) findViewById(R.id.adView);
        if (isNetworkAvailable()) {
            AdRequest adRequest = new AdRequest.Builder().addTestDevice("79053C9FC58F84225101E72A5113F19B").build();
            mAdView.loadAd(adRequest);
        } else {
            mAdView.setVisibility(GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_FILE_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri;
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
                        Intent uploadIntent = new Intent(MainActivity.this, UploadService.class);
                        uploadIntent.putExtra("uri", uri.toString());
                        uploadIntent.putExtra("displayName", displayName);
                        startService(uploadIntent);
                    }
                } catch (Exception e) {
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
        return new CursorLoader(MainActivity.this, FilesContract.FileEntry.CONTENT_URI, null, null, null, FilesContract.FileEntry.COLUMN_UPLOAD_DATE + " desc, " + FilesContract.FileEntry.COLUMN_STATUS + " desc");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.getCount() == 0) {
            findViewById(R.id.empty_view).setVisibility(VISIBLE);
        } else {
            findViewById(R.id.empty_view).setVisibility(GONE);
            cursor.moveToFirst();
            adapter = new FileAdapter(MainActivity.this, cursor);
            mRecyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

}
