package com.mawd.swiftnotify;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mawd.swiftnotify.models.User;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "teachers.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_TEACHERS = "teachers";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_AGE = "age";
    private static final String COLUMN_FULL_NAME = "fullName";
    private static final String COLUMN_TEACHER_AVAILABLE = "teacherAvailable";
    private static final String COLUMN_USER_EMAIL = "userEmail";
    private static final String COLUMN_USER_GENDER = "userGender";
    private static final String COLUMN_SIM_NUMBER = "simNumber";

    private static final String CREATE_TABLE_TEACHERS = " CREATE TABLE " + TABLE_TEACHERS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY,"
            + COLUMN_AGE + " INTEGER,"
            + COLUMN_FULL_NAME + " TEXT,"
            + COLUMN_TEACHER_AVAILABLE + " INTEGER,"
            + COLUMN_USER_EMAIL + " TEXT,"
            + COLUMN_USER_GENDER + " TEXT,"
            + COLUMN_SIM_NUMBER + " TEXT"
            + ")";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_TEACHERS);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int OldVersion, int newVersion){
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TEACHERS);
        onCreate(sqLiteDatabase);
    }
    public void addTeacher(User teacher) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_AGE, teacher.getAge());
        values.put(COLUMN_FULL_NAME, teacher.getFullName());
        values.put(COLUMN_TEACHER_AVAILABLE, teacher.isTeacherAvailable());
        values.put(COLUMN_USER_EMAIL, teacher.getUserEmail());
        values.put(COLUMN_USER_GENDER, teacher.getUserGender());
        values.put(COLUMN_SIM_NUMBER, teacher.getSimNumber());

        db.insert(TABLE_TEACHERS, null, values);
        db.close();
    }
    @SuppressLint("Range")
    public ArrayList<User> getAllTeachers() {
        ArrayList<User> teachersList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_TEACHERS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                User teacher = new User();
                teacher.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                teacher.setAge(cursor.getInt(cursor.getColumnIndex(COLUMN_AGE)));
                teacher.setFullName(cursor.getString(cursor.getColumnIndex(COLUMN_FULL_NAME)));
                teacher.setIsTeacherAvailable(cursor.getInt(cursor.getColumnIndex(COLUMN_TEACHER_AVAILABLE)) > 0);
                teacher.setUserEmail(cursor.getString(cursor.getColumnIndex(COLUMN_USER_EMAIL)));
                teacher.setUserGender(cursor.getString(cursor.getColumnIndex(COLUMN_USER_GENDER)));
                teacher.setSimNumber(cursor.getString(cursor.getColumnIndex(COLUMN_SIM_NUMBER)));

                teachersList.add(teacher);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();

        return teachersList;
    }



}
