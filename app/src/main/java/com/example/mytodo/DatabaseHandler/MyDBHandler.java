package com.example.mytodo.DatabaseHandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.mytodo.ToDos;

import java.util.ArrayList;

public class MyDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "todos.db";
    private static final String TABLE_TODOS = "todos";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TODODESC = "tododesc";
    public static final String COLUMN_ISCOMPLETED = "iscompleted";

    public MyDBHandler(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "CREATE TABLE " + TABLE_TODOS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                COLUMN_TODODESC + " TEXT, " +
                COLUMN_ISCOMPLETED + " INTEGER" +
                ");";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS " + TABLE_TODOS + ";";
        db.execSQL(query);
        onCreate(db);
    }

    // Add a new ToDo in the database
    public void addToDo(ToDos toDo) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TODODESC, toDo.get_todoDesc());
        values.put(COLUMN_ISCOMPLETED, toDo.get_isCompleted());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_TODOS, null, values);
        db.close();
    }

    // Delete a ToDo from the database
    public void deleteToDo(int _id) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "DELETE FROM " + TABLE_TODOS + " WHERE " + COLUMN_ID + "= " + _id + ";";
        db.execSQL(query);
    }

    public void changeCompletedStatus(int _id, int status) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "UPDATE " + TABLE_TODOS + " SET " + COLUMN_ISCOMPLETED + " = " + status + " WHERE " + COLUMN_ID + " = " + _id + ";";
        db.execSQL(query);
    }

    public void updateTodoDesc(int _id, String desc) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "UPDATE " + TABLE_TODOS + " SET " + COLUMN_TODODESC + " = \"" + desc + "\" WHERE " + COLUMN_ID + " = " + _id + ";";
        db.execSQL(query);
    }

    public ArrayList<ToDos> getToDosFromDB() {
        ArrayList<ToDos> todoList = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_TODOS + " WHERE 1;";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex(COLUMN_TODODESC)) != null) {
                String todoDesc = c.getString(c.getColumnIndex(COLUMN_TODODESC));
                int isCompleted = c.getInt(c.getColumnIndex(COLUMN_ISCOMPLETED));
                int _id = c.getInt(c.getColumnIndex(COLUMN_ID));

                ToDos newToDo = new ToDos(_id, todoDesc, isCompleted);
                todoList.add(newToDo);
            }
            c.moveToNext();
        }
        db.close();

        return todoList;
    }

}
