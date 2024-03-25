package com.mawd.swiftnotify;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mawd.swiftnotify.models.User;

import java.util.ArrayList;


public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.TeacherViewHolder> {
    private final SelectListener selectListener;
    Context context;
    ArrayList<User> list;

    public TeacherAdapter(Context context, ArrayList<User> list, SelectListener selectListener) {
        this.context = context;
        this.list = list;
        this.selectListener = selectListener;
    }

    @NonNull
    @Override
    public TeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.teacher_beep_card, parent, false);
        return new TeacherViewHolder(view, selectListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherViewHolder holder, int position) {
        User teacher = list.get(position);
        String userStatus = teacher.getUserStatus();

        if (userStatus != null && userStatus.equalsIgnoreCase("Teacher")){
            holder.teacherFullName.setText(teacher.getFullName());
            boolean isTeacherAvailable = teacher.isTeacherAvailable();
            if (isTeacherAvailable) {
                holder.isTeacherAvailable.setText(R.string.affirmative);
            } else {
                holder.isTeacherAvailable.setText(R.string.negative);
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class TeacherViewHolder extends RecyclerView.ViewHolder {
        TextView teacherFullName, isTeacherAvailable;

        public TeacherViewHolder(@NonNull View itemView, SelectListener selectListener) {
            super(itemView);

            teacherFullName = itemView.findViewById(R.id.teacherFullName);
            isTeacherAvailable = itemView.findViewById(R.id.teacherAvailabilityValue);

            itemView.setOnClickListener(v -> {
                if (selectListener != null){
                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION){
                        selectListener.onItemClick(position);
                    }
                }
            });
        }
    }
}