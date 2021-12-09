package com.example.emirim_java_petdiary;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Data extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "test.db";
    // DBHelper 생성자
    public Data(Context context, int version) {
        super(context, DATABASE_NAME, null, version);
    }

    // Diary Table생성
    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Diary(title TEXT, note TEXT, feed INT, walk INT, play INT, date TEXT)");
    }

    // Diary Table Upgrade
    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Diary"); onCreate(db);
    }

    // Diary Table 데이터 입력
    public void insert(String title, String note, int feed, int walk, int play, String date) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO Diary VALUES('" + title + "', '" + note + "', " + feed + ", " + walk + ", " + play + ", '" + date + "')");
        db.close();
    }

    // Diary Table 데이터 수정
    public void Update(String title, String note, int feed, int walk, int play, String date) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE Diary SET title = '" + title + "', '" + note + "', " + feed + ", " + walk + ", " + play + ", '" + date + "')");
        db.close();
    }

    // Diary Table 조회
    public String getResult() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";
        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM Person", null);
        while (cursor.moveToNext()) {
            result += " 제목 : " + cursor.getString(0)
                    + ", 메모 : " + cursor.getString(1)
                    + ", 밥 : " + cursor.getInt(2)
                    + ", 산책 : " + cursor.getInt(3)
                    + ", 놀기 : " + cursor.getInt(4)
                    + ", 날짜 : " + cursor.getString(5)
                    + "\n";
        }
        return result;
    }
}