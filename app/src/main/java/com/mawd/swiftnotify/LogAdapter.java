package com.mawd.swiftnotify;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mawd.swiftnotify.models.NotificationInfo;

import java.util.ArrayList;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {
    Context context;
    ArrayList<NotificationInfo> list;
    public LogAdapter(Context context, ArrayList<NotificationInfo> list) {
        this.context = context;
        this.list = list;
    }
    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.log_card, parent, false);
        return new LogViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        NotificationInfo student_log = list.get(position);
        String time_stamp = student_log.getTimeStamp();
        String student_log_gender = student_log.getUserGender();
        LinearLayout gender_card_preview = holder.gender_card_preview;

        if (time_stamp != null && student_log_gender != null) {
            holder.log_time_stamp_value.setText(time_stamp);

            int drawableResId;
            switch (student_log_gender.toLowerCase()) {
                case "male":
                    drawableResId = R.drawable.blue_left_side_card;
                    break;
                case "female":
                    drawableResId = R.drawable.red_left_side_card;
                    break;
                default:
                    drawableResId = R.drawable.default_left_side_card;
                    break;
            }
            Drawable drawable = ContextCompat.getDrawable(context, drawableResId);
            gender_card_preview.setBackground(drawable);
        }
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
    public static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView log_time_stamp_value;
        LinearLayout gender_card_preview;
        public LogViewHolder(@NonNull View itemView) {
            super(itemView);

            log_time_stamp_value = itemView.findViewById(R.id.log_time_stamp_value);
            gender_card_preview = itemView.findViewById(R.id.gender_card_preview);
        }
    }
}
