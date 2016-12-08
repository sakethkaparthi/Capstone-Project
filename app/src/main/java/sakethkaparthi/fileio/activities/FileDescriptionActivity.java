package sakethkaparthi.fileio.activities;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.io.FilenameUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import sakethkaparthi.fileio.R;
import sakethkaparthi.fileio.database.FilesContract;

public class FileDescriptionActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    @BindView(R.id.file_icon)
    ImageView fileIcon;
    @BindView(R.id.file_name_text_view)
    TextView fileNameTextView;
    @BindView(R.id.description_heading)
    TextView descriptionHeading;
    @BindView(R.id.upload_date_text_view)
    TextView uploadDateTextView;
    @BindView(R.id.file_link_text_view)
    TextView fileLinkTextView;
    @BindView(R.id.file_expiry_text_view)
    TextView fileExpiryTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_description);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(FileDescriptionActivity.this, FilesContract.FileEntry.CONTENT_URI, null, null, null, FilesContract.FileEntry.COLUMN_UPLOAD_DATE + " desc, " + FilesContract.FileEntry.COLUMN_STATUS + " desc");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursor.moveToPosition(getIntent().getExtras().getInt("item"));
        fileNameTextView.setText(cursor.getString(1));
        fileIcon.setImageResource(getIconFromExtension(cursor.getString(1)));
        fileLinkTextView.setText(cursor.getString(2));
        Date date = new Date(cursor.getLong(3));
        uploadDateTextView.setText("Uploaded on " + new SimpleDateFormat("dd/MM", Locale.UK).format(date));
        int uploadedDay = getDateFromEpoch(cursor.getLong(3));
        int today = getDateFromEpoch(System.currentTimeMillis());
        int remaining = uploadedDay + 13 - today;
        if (remaining < 0) {
            fileExpiryTextView.setText(R.string.file_expired);
            fileExpiryTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
        } else {
            fileExpiryTextView.setText("Expires in " + remaining + " days");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    int getDateFromEpoch(long milliseconds) {
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
