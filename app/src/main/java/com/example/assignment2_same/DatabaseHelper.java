package com.example.assignment2_same;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "items.db";
    private static final String TABLE_NAME = "items";
    private static final String COLUMN_PRODUCT = "productname";
    private static final String COLUMN_PRIORITY = "productpriority";
    private static final String COLUMN_QUANTITY = "productquantity";
    private static final String COLUMN_PRICE = "productprice";
    private static final String COLUMN_PURCHASED = "productpurchased";
    private static final String COLUMN_NOTPURCHASED = "productnotpurchased";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_PRODUCT + " TEXT PRIMARY KEY);";

    private static final String SQL_DELETE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME;


    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public boolean addItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT, item.getProduct());
        db.update(TABLE_NAME,values, COLUMN_PRODUCT + "= ?", new String[] {item.getProduct()});

        long result = db.insert(TABLE_NAME, null, values);

        if (result == -1) {
            return false;
        } else {
            db.close();
            return true;
        }
    }

    public Cursor loadItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public void deleteItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE 1";
        db.execSQL(query);
        db.close();
    }
}