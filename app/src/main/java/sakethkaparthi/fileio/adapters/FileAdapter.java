package sakethkaparthi.fileio.adapters;

import android.app.Activity;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import sakethkaparthi.fileio.R;

/**
 * Created by saketh on 19/10/16.
 */

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {
    private Cursor cursor;
    private Activity activity;

    public FileAdapter(Activity activity, Cursor cursor) {
        this.cursor = cursor;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.fileNameTextView.setText(cursor.getString(1));
        int uploadedDay = getDateFromEpoch(cursor.getLong(3));
        int today = getDateFromEpoch(System.currentTimeMillis());
        int remaining = uploadedDay + 13 - today;
        if (remaining < 0) {
            holder.fileExpiryTextView.setText(R.string.file_expired);
            holder.fileExpiryTextView.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent));
        } else {
            holder.fileExpiryTextView.setText("Expires in " + remaining + " days");
        }
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView fileNameTextView, fileExpiryTextView;
        ImageView iconImageView;

        ViewHolder(View v) {
            super(v);
            fileNameTextView = (TextView) v.findViewById(R.id.file_name_text_view);
            fileExpiryTextView = (TextView) v.findViewById(R.id.file_expiry_text_view);
            iconImageView = (ImageView) v.findViewById(R.id.file_icon);
        }
    }

    int getDateFromEpoch(long milliseconds) {
        Date date = new Date(milliseconds);
        DateFormat formatter = new SimpleDateFormat("DDD", Locale.UK);
        String dateFormatted = formatter.format(date);
        return Integer.valueOf(dateFormatted);
    }
}
