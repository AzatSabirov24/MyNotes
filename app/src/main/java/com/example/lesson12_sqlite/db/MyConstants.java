package com.example.lesson12_sqlite.db;

public class MyConstants {
    public static final String EDIT_STATE = "edit_state";
    public static final String LIST_ITEM_INTENT = "list_item_intent";
    public static final String TABLE_NAME = "my_table";
    public static final String _ID = "id";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "desc";
    public static final String URI = "uri";
    public static final String DB_NAME = "my_db.db";
    public static final int DB_VERSION = 2;

    public static final String TABLE_STRUCTURE = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY," + TITLE + " TEXT," + URI + " Text," + DESCRIPTION + " TEXT)";

    public static final String DROP_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;
}
