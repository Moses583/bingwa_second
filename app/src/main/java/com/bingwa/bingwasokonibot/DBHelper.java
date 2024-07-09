package com.bingwa.bingwasokonibot;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper( Context context) {
        super(context, "RealDbEight.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create Table InboxTable(id INTEGER PRIMARY KEY AUTOINCREMENT,message TEXT, timeStamp TEXT,sender TEXT)");
        db.execSQL("create Table Offers(id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,amount TEXT, ussdCode TEXT, dialSim TEXT, dialSimId TEXT,paymentSim TEXT, paymentSimId TEXT, offerTill TEXT)");
        db.execSQL("create Table Transactions(id INTEGER PRIMARY KEY AUTOINCREMENT,ussdResponse TEXT,amount TEXT,timeStamp TEXT,recipient TEXT, status TEXT, subId INTEGER,ussd TEXT, till INTEGER, messageFull TEXT)");
        db.execSQL("create Table User(tillNumber TEXT)");
        db.execSQL("create Table Link(link TEXT)");
        db.execSQL("create Table AuthenticationToken(token TEXT)");
        db.execSQL("create Table Renewals(id INTEGER PRIMARY KEY AUTOINCREMENT,frequency TEXT, codeUssd TEXT,period TEXT,numberTill TEXT,theTime TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop Table if exists InboxTable");
        db.execSQL("drop table if exists Offers");
        db.execSQL("drop table if exists Transactions");
        db.execSQL("drop table if exists User");
        db.execSQL("drop table if exists Link");
        db.execSQL("drop table if exists AuthenticationToken");
        db.execSQL("drop table if exists Renewals");
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

    public Boolean insertRenewals( String frequency, String codeUssd, String period,String numberTill,String time){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("frequency",frequency);
        contentValues.put("codeUssd",codeUssd);
        contentValues.put("period",period);
        contentValues.put("numberTill",numberTill);
        contentValues.put("theTime",time);
        long result = database.insert("Renewals",null,contentValues);
        return result != -1;

    }

    public Boolean insertTransaction(String ussdResponse, String amount,String timeStamp, String recipient,String status, int subId, String ussd, int till, String messageFull){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ussdResponse",ussdResponse);
        contentValues.put("amount",amount);
        contentValues.put("timeStamp",timeStamp);
        contentValues.put("recipient",recipient);
        contentValues.put("status",status);
        contentValues.put("subId",subId);
        contentValues.put("ussd",ussd);
        contentValues.put("till",till);
        contentValues.put("messageFull",messageFull);
        long result = database.insert("Transactions",null,contentValues);
        return result != -1;
    }
    public Boolean insertOffer(String name, String amount, String ussdCode, String dialSim, String dialSimId,String paymentSim,String paymentSimId,String offerTill){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name",name);
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

    public Boolean insertUser(String tillNumber){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("tillNumber",tillNumber);
        long result = database.insert("User",null,contentValues);
        return result != -1;
    }
    public Boolean insertLink(String link){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("link",link);
        long result = database.insert("Link",null,contentValues);
        return result != -1;
    }
    public Boolean insertToken(String token){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("token",token);
        long result = database.insert("AuthenticationToken",null,contentValues);
        return result != -1;
    }

    public Cursor getToken(){
        SQLiteDatabase database = this.getWritableDatabase();
        return database.rawQuery("Select * from AuthenticationToken",null);
    }

    public Cursor getUser(){
        SQLiteDatabase database = this.getWritableDatabase();
        return database.rawQuery("Select * from User",null);
    }
    public Cursor getLink(){
        SQLiteDatabase database = this.getWritableDatabase();
        return database.rawQuery("Select * from Link",null);
    }
    public Cursor getData(){
        SQLiteDatabase database = this.getWritableDatabase();
        return database.rawQuery("Select * from InboxTable ORDER BY id DESC",null);
    }
    public Cursor getOffers(){
        SQLiteDatabase database = this.getWritableDatabase();
        return database.rawQuery("Select * from Offers ORDER BY id DESC",null);
    }
    public Cursor getRenewals(){
        SQLiteDatabase database = this.getWritableDatabase();
        return database.rawQuery("Select * from Renewals ORDER BY id DESC",null);
    }
    public Cursor getTransactions(){
        SQLiteDatabase database = this.getWritableDatabase();
        return database.rawQuery("Select * from Transactions ORDER BY id DESC",null);
    }
    public Cursor getSpecificOffer(String amount, String simId){
        SQLiteDatabase database = this.getReadableDatabase();
        return database.rawQuery("Select * from Offers where amount=? and paymentSimId =?", new String[]{amount,simId});
    }
    public Cursor getFailedResponses(){
        SQLiteDatabase database = this.getWritableDatabase();
        String pattern = "0";
        return database.rawQuery("SELECT * FROM Transactions WHERE status=?", new String[]{pattern});
    }
    public Cursor getYesTransactions(){
        SQLiteDatabase database = this.getWritableDatabase();
        String pattern = "1";
        return database.rawQuery("SELECT * FROM Transactions WHERE status=?", new String[]{pattern});
    }
    public Boolean deleteTransaction () {
        SQLiteDatabase DB = this.getWritableDatabase();
        String pattern = "0";
        long result = DB.delete("Transactions", "status=?", new String[]{pattern});
        return result != -1;
    }
    public Boolean deleteData (String ussdCode) {
        SQLiteDatabase DB = this.getWritableDatabase();
        long result = DB.delete("Offers", "ussdCode=?", new String[]{ussdCode});
        return result != -1;
    }
    public Boolean deleteRenewal (String ussdCode) {
        SQLiteDatabase DB = this.getWritableDatabase();
        long result = DB.delete("Renewals", "codeUssd=?", new String[]{ussdCode});
        return result != -1;
    }
    public void clearAllTables() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        List<String> tables = new ArrayList<>();

        // get all table names
        while (cursor.moveToNext()) {
            tables.add(cursor.getString(0));
        }

        // delete all records from each table
        for (String table : tables) {
            String deleteQuery = "DELETE FROM " + table;
            db.execSQL(deleteQuery);
        }
        cursor.close();
    }
}
