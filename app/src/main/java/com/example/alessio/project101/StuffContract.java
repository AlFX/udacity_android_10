package com.example.alessio.project101;

import android.provider.BaseColumns;

public class StuffContract {

    public StuffContract() {
    }

    public static final class StockEntry implements BaseColumns {

        public static final String TAB_NAME = "stock";

        public static final String _ID = BaseColumns._ID;
        public static final String COL_NAME = "name";
        public static final String COL_PRICE = "price";
        public static final String COL_QUANT = "quantity";
        public static final String COL_VENDOR_NAME = "supplier_name";
        public static final String COL_VENDOR_PHONE = "supplier_phone";
        public static final String COL_VENDOR_EMAIL = "supplier_email";
        public static final String COL_IMAGE = "image";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                StuffContract.StockEntry.TAB_NAME + "(" +
                StuffContract.StockEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                StuffContract.StockEntry.COL_NAME + " TEXT NOT NULL," +
                StuffContract.StockEntry.COL_PRICE + " TEXT NOT NULL," +
                StuffContract.StockEntry.COL_QUANT + " INTEGER NOT NULL DEFAULT 0," +
                StuffContract.StockEntry.COL_VENDOR_NAME + " TEXT NOT NULL," +
                StuffContract.StockEntry.COL_VENDOR_PHONE + " TEXT NOT NULL," +
                StuffContract.StockEntry.COL_VENDOR_EMAIL + " TEXT NOT NULL," +
                StockEntry.COL_IMAGE + " TEXT NOT NULL" + ");";
    }
}
