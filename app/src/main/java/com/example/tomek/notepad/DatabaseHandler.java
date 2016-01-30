package com.example.tomek.notepad;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.Html;
import android.text.Spannable;
import java.util.ArrayList;

/**
 * Created by tomek on 30.01.16.
 */
public class DatabaseHandler extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "notepadDatabase";
    private static final String TABLE_NOTES = "notes";
    private static final String KEY_ID = "id";
    private static final String KEY_SPANNABLE_NOTE = "serializedSpannableNote";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NOTES + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_SPANNABLE_NOTE + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }

    public void clearAllNotes() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABLE_NOTES);
    }

    public void createNote(Note note) {
        SQLiteDatabase db = getWritableDatabase();

        String spannableAsHtml = Html.toHtml(note.getSpannable());

        ContentValues values = new ContentValues();
        values.put(KEY_SPANNABLE_NOTE, spannableAsHtml);

        db.insert(TABLE_NOTES, null, values);

        db.close();
    }

    public Note getNote(int id) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_NOTES, new String[]{KEY_ID, KEY_SPANNABLE_NOTE}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        String spannableAsHtml = cursor.getString(1);
        Spannable spannable = (Spannable) Html.fromHtml(spannableAsHtml);

        db.close();
        cursor.close();
        return new Note(id, spannable);
    }

    public void deleteNote(Note note) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NOTES, KEY_ID + "=?", new String[] { String.valueOf(note.getId())});
        db.close();
    }

    public int getNoteCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTES, null);
        int result = cursor.getCount();

        cursor.close();
        db.close();
        return result;
    }

    public int updateNote(Note note) {
        SQLiteDatabase db = getWritableDatabase();

        String spannableAsHtml = Html.toHtml(note.getSpannable());


        ContentValues values = new ContentValues();
        values.put(KEY_SPANNABLE_NOTE, spannableAsHtml);

        return db.update(TABLE_NOTES, values, KEY_ID + "=?", new String[]{String.valueOf(note.getId())});
    }

    public ArrayList<Note> getAllNotes() {
        ArrayList<Note> notes = new ArrayList<>();

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTES, null);

        if (cursor.moveToFirst()) {
            do {
                Note note = new Note(Integer.parseInt(cursor.getString(0)),
                        ((Spannable) Html.fromHtml(cursor.getString(1))));
                notes.add(note);
            }
            while (cursor.moveToNext());
        }
        return notes;
    }
}
