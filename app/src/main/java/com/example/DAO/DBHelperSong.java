package com.example.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DBHelperSong extends SQLiteOpenHelper {

    private static final String dbname = "AppMusic.db";

    public DBHelperSong(@Nullable Context context) {
        super(context, dbname, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String qry = "create table Song (id integer primary key autoincrement, name text, artist text, path text)";
        db.execSQL(qry);
        System.out.println("Tạo cơ sở dữ liệu thành công");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Song");
        onCreate(db);
    }

    public String addRecord(String p1, String p2, String p3) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("name", p1);
        cv.put("artist", p2);
        cv.put("path", p3);

        long res = db.insert("Song", null, cv);

        if (res == -1)
            return "Thêm dữ liệu thất bại";
        else
            return "Thêm dữ liệu thành công";
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from Song",null);
        return cursor;
    }

}
