package com.example.alessio.project101;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class InventoryDbHelper extends SQLiteOpenHelper {

    public final static String DB_NAME = "inventory.db";
    public final static int DB_VER = 1;

    public InventoryDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(StuffContract.StockEntry.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(StuffContract.StockEntry.COL_NAME, item.getProdName());
        values.put(StuffContract.StockEntry.COL_PRICE, item.getPrice());
        values.put(StuffContract.StockEntry.COL_QUANT, item.getQuant());
        values.put(StuffContract.StockEntry.COL_VENDOR_NAME, item.getVendorName());
        values.put(StuffContract.StockEntry.COL_VENDOR_PHONE, item.getVendorPhone());
        values.put(StuffContract.StockEntry.COL_VENDOR_EMAIL, item.getVendorEmail());
        values.put(StuffContract.StockEntry.COL_IMAGE, item.getImg());
        long id = db.insert(StuffContract.StockEntry.TAB_NAME, null, values);
    }

    public Cursor readStock() {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                StuffContract.StockEntry._ID,
                StuffContract.StockEntry.COL_NAME,
                StuffContract.StockEntry.COL_PRICE,
                StuffContract.StockEntry.COL_QUANT,
                StuffContract.StockEntry.COL_VENDOR_NAME,
                StuffContract.StockEntry.COL_VENDOR_PHONE,
                StuffContract.StockEntry.COL_VENDOR_EMAIL,
                StuffContract.StockEntry.COL_IMAGE
        };
        Cursor cursor = db.query(
                StuffContract.StockEntry.TAB_NAME,
                projection,
                null, null, null, null, null
        );
        return cursor;
    }

    public Cursor readItem(long itemId) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                StuffContract.StockEntry._ID,
                StuffContract.StockEntry.COL_NAME,
                StuffContract.StockEntry.COL_PRICE,
                StuffContract.StockEntry.COL_QUANT,
                StuffContract.StockEntry.COL_VENDOR_NAME,
                StuffContract.StockEntry.COL_VENDOR_PHONE,
                StuffContract.StockEntry.COL_VENDOR_EMAIL,
                StuffContract.StockEntry.COL_IMAGE
        };
        String selection = StuffContract.StockEntry._ID + "=?";
        String[] selectionArgs = new String[] { String.valueOf(itemId) };

        Cursor cursor = db.query(
                StuffContract.StockEntry.TAB_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        return cursor;
    }

    public void updateItem(long currentItemId, int quantity) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(StuffContract.StockEntry.COL_QUANT, quantity);
        String selection = StuffContract.StockEntry._ID + "=?";
        String[] selectionArgs = new String[] { String.valueOf(currentItemId) };
        db.update(StuffContract.StockEntry.TAB_NAME,
                values, selection, selectionArgs);
    }

    public void sellOneItem(long itemId, int quantity) {
        SQLiteDatabase db = getWritableDatabase();
        int newQuantity = 0;
        if (quantity > 0) {
            newQuantity = quantity -1;
        }
        ContentValues values = new ContentValues();
        values.put(StuffContract.StockEntry.COL_QUANT, newQuantity);
        String selection = StuffContract.StockEntry._ID + "=?";
        String[] selectionArgs = new String[] { String.valueOf(itemId) };
        db.update(StuffContract.StockEntry.TAB_NAME,
                values, selection, selectionArgs);
    }
}
