package com.example.muizahmed.savecard.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.muizahmed.savecard.PhotoCard;
import com.example.muizahmed.savecard.model.Card;

import java.util.ArrayList;

public class DBhelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "saveCard_db";

    public DBhelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        TableAttributes object = new TableAttributes();
        String query = object.tableCreationQuery();

        try{
            db.execSQL(query);
            Log.i("Table Create", "Done");
        }catch (SQLException e){
            Log.e("SQL Error", e.toString());

        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertCard(Card card) {

        ContentValues values = new ContentValues();
        values.put(TableAttributes.CARD_NAME, card.getName());
        values.put(TableAttributes.CARD_PHONE_NO, card.getPhoneNo());

        SQLiteDatabase db = this.getWritableDatabase();
        try{
            db.insert(TableAttributes.TABLE_NAME, null, values);
            Log.i("Insert", "Done");
        }catch (SQLException e){
            Log.e("Error", e.toString());
        }
    }

    public ArrayList<Card> getAllCardsData(){
        ArrayList<Card> Cardlist = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TableAttributes.TABLE_NAME;

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        if(cursor.getCount() >0){
            while(!cursor.isAfterLast()){
                int id = cursor.getInt(cursor.getColumnIndex(TableAttributes.CARD_ID));
                String name = cursor.getString(cursor.getColumnIndex(TableAttributes.CARD_NAME));
                String phone = cursor.getString(cursor.getColumnIndex(TableAttributes.CARD_PHONE_NO));
                Card card = new Card(id, name, phone);

                Cardlist.add(card);
                cursor.moveToNext();
            }
        }

        return Cardlist;
    }

    public boolean deleteCard(int id) {

        boolean flag = false;
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            db.delete(TableAttributes.TABLE_NAME, TableAttributes.CARD_ID + "=" + id, null);
            flag = true;
            Log.i("Delete", "Done");
        }catch (SQLiteException e){
            Log.e("Delete Error",e.toString());
        }
        return flag;
    }
}
