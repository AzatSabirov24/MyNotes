package com.example.lesson12_sqlite.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lesson12_sqlite.adapter.ListItem;

import java.util.ArrayList;
import java.util.List;

public class MyDbManager {
    private Context context;
    private MyDbHelper myDbHelper;
    private SQLiteDatabase db;

    public MyDbManager(Context context) {
        this.context = context;
        myDbHelper = new MyDbHelper(context);
    }

    public void openDb() {
        db = myDbHelper.getWritableDatabase();
    }

    public void insertToDb(String title, String desc, String uri) {
        ContentValues values = new ContentValues();
        values.put(MyConstants.TITLE, title);
        values.put(MyConstants.DESCRIPTION, desc);
        values.put(MyConstants.URI, uri);
        db.insert(MyConstants.TABLE_NAME, null, values);
    }

      public void updateItem(String title, String desc, String uri, int id) {
         ContentValues values = new ContentValues();
        values.put(MyConstants.TITLE, title);
        values.put(MyConstants.DESCRIPTION, desc);
        values.put(MyConstants.URI, uri);
        String selection = MyConstants._ID + "=" + id;
        db.update(MyConstants.TABLE_NAME,values, selection, null);
    }

    public void delete(int id){
        String selection = MyConstants._ID + "=" + id;
        db.delete(MyConstants.TABLE_NAME, selection, null);
    }
    public List<ListItem> getFromDb(String searchText) {
        List<ListItem> tempList = new ArrayList<>();
        String selection = MyConstants.TITLE + " like ?";
        Cursor cursor = db.query(MyConstants.TABLE_NAME, null, selection, new String[]{"%" + searchText + "%"},
                null, null, null);

        while (cursor.moveToNext()) {
            ListItem item = new ListItem();
            String title = cursor.getString(cursor.getColumnIndex(MyConstants.TITLE));
            String desc = cursor.getString(cursor.getColumnIndex(MyConstants.DESCRIPTION));
            String uri = cursor.getString(cursor.getColumnIndex(MyConstants.URI));
            int _id = cursor.getInt(cursor.getColumnIndex(MyConstants._ID));
            item.setTitle(title);
            item.setDesc(desc);
            item.setUri(uri);
            item.setId(_id);
            tempList.add(item);
        }
        cursor.close();
        return tempList;
    }

    public void closeDb(){
        myDbHelper.close();
    }
}
