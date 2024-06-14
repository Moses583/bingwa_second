package com.example.meisterbot;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.example.meisterbot.models.OfferPOJO;
import com.google.gson.Gson;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper( Context context) {
        super(context, "DatabasefIVE.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create Table InboxTable(id INTEGER PRIMARY KEY AUTOINCREMENT,message TEXT, timeStamp TEXT,sender TEXT)");
        db.execSQL("create table Offers(id INTEGER PRIMARY KEY AUTOINCREMENT,amount TEXT, ussdCode TEXT, dialSim TEXT, dialSimId TEXT,paymentSim TEXT, paymentSimId TEXT, offerTill TEXT)");
        db.execSQL("create Table Transactions(id INTEGER PRIMARY KEY AUTOINCREMENT,ussdResponse TEXT,amount TEXT,timeStamp TEXT,recipient TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop Table if exists InboxTable");
        db.execSQL("drop table if exists Offers");
        db.execSQL("drop table if exists Transactions");
    }
    public Boolean insertData( String message, String time, String sender){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("message",message);
        contentValues.put("timeStamp",time);
        contentValues.put("sender",sender);
        long result = database.insert("InboxTable",null,contentValues);
        return result != -1;

    }
    public Boolean insertTransaction(String ussdResponse, String amount,String timeStamp, String recipient){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ussdResponse",ussdResponse);
        contentValues.put("amount",amount);
        contentValues.put("timeStamp",timeStamp);
        contentValues.put("recipient",recipient);
        long result = database.insert("Transactions",null,contentValues);
        return result != -1;
    }
    public Boolean insertOffer(String amount, String ussdCode, String dialSim, String dialSimId,String paymentSim,String paymentSimId,String offerTill){
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("amount",amount);
        contentValues.put("ussdCode",ussdCode);
        contentValues.put("dialSim",dialSim);
        contentValues.put("dialSimId",dialSimId);
        contentValues.put("paymentSim",paymentSim);
        contentValues.put("paymentSimId",paymentSimId);
        contentValues.put("offerTill",offerTill);
        long result = database.insert("Offers",null,contentValues);
        return result != -1;
    }
    public Cursor getData(){
        SQLiteDatabase database = this.getWritableDatabase();
        return database.rawQuery("Select * from InboxTable ORDER BY id DESC",null);
    }
    public Cursor getOffers(){
        SQLiteDatabase database = this.getWritableDatabase();
        return database.rawQuery("Select * from Offers ORDER BY id DESC",null);
    }
    public Cursor getTransactions(){
        SQLiteDatabase database = this.getWritableDatabase();
        return database.rawQuery("Select * from Transactions ORDER BY id DESC",null);
    }

    public Cursor getSpecificOffer(String amount, String simId){
        SQLiteDatabase database = this.getReadableDatabase();
        return database.rawQuery("Select ussdCode from Offers where amount=? and paymentSimId =?", new String[]{amount,simId});
    }



    public Boolean deleteData (String ussdCode) {
        SQLiteDatabase DB = this.getWritableDatabase();
        long result = DB.delete("Offers", "ussdCode=?", new String[]{ussdCode});
        return result != -1;
    }
}
