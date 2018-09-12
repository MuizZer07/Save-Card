package com.example.muizahmed.savecard.Database;

public class TableAttributes {

    public static final String TABLE_NAME = "card";
    public static final String CARD_ID = "id";
    public static final String CARD_NAME = "name";
    public static final String CARD_PHONE_NO = "PhoneNo";

    public String tableCreationQuery(){
        String query = "CREATE TABLE " + TABLE_NAME + "(" + CARD_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT," + CARD_NAME + " TEXT," + CARD_PHONE_NO + " TEXT)";

        return query;
    }
}
