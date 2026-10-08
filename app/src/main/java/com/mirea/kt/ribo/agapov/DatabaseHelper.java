package com.mirea.kt.ribo.agapov;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String DB_NAME = "dictionary.db";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Создание таблицы terms");
        db.execSQL("CREATE TABLE terms (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT, definition TEXT, favorite INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS terms");
        onCreate(db);
    }

    public void addTerm(String name, String definition) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("definition", definition);
        values.put("favorite", 0);
        SQLiteDatabase db = getWritableDatabase();
        db.insert("terms", null, values);
        Log.d(TAG, "Добавлен термин: " + name);
    }

    public void deleteTerm(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("terms", "_id = ?", new String[]{String.valueOf(id)});
        Log.d(TAG, "Удалён термин, id = " + id);
    }

    public void setFavorite(long id, boolean favorite) {
        ContentValues values = new ContentValues();
        if (favorite) {
            values.put("favorite", 1);
        } else {
            values.put("favorite", 0);
        }
        SQLiteDatabase db = getWritableDatabase();
        db.update("terms", values, "_id = ?", new String[]{String.valueOf(id)});
        Log.d(TAG, "Избранное изменено, id = " + id);
    }

    public Term getTerm(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("terms", null, "_id = ?", new String[]{String.valueOf(id)},
                null, null, null);
        Term term = null;
        if (cursor.moveToFirst()) {
            term = readTerm(cursor);
        }
        cursor.close();
        return term;
    }

    public ArrayList<Term> getAllTerms() {
        ArrayList<Term> terms = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("terms", null, null, null, null, null, "name COLLATE NOCASE");
        while (cursor.moveToNext()) {
            terms.add(readTerm(cursor));
        }
        cursor.close();
        return terms;
    }

    private Term readTerm(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
        String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        String definition = cursor.getString(cursor.getColumnIndexOrThrow("definition"));
        boolean favorite = cursor.getInt(cursor.getColumnIndexOrThrow("favorite")) == 1;
        return new Term(id, name, definition, favorite);
    }
}
