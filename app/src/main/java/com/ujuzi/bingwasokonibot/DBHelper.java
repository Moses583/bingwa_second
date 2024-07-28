package com.ujuzi.bingwasokonibot;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper( Context context) {
        super(context, "RealDbNineteen.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create Table InboxTable(id INTEGER PRIMARY KEY AUTOINCREMENT,message TEXT, timeStamp TEXT,sender TEXT)");
        db.execSQL("create Table Offers(id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,amount TEXT, ussdCode TEXT, dialSim TEXT, dialSimId TEXT,paymentSim TEXT, paymentSimId TEXT, offerTill TEXT)");
        db.execSQL("create Table SuccessfulTransactions(id INTEGER PRIMARY KEY AUTOINCREMENT,ussdResponse TEXT,amount TEXT,timeStamp TEXT,recipient TEXT, status TEXT, subId INTEGER,ussd TEXT, till INTEGER, messageFull TEXT)");
        db.execSQL("create Table FailedTransactions(id INTEGER PRIMARY KEY AUTOINCREMENT,ussdResponse TEXT,amount TEXT,timeStamp TEXT,recipient TEXT, status TEXT, subId INTEGER,ussd TEXT, till INTEGER, messageFull TEXT)");
        db.execSQL("create Table User(tillNumber TEXT)");
        db.execSQL("create Table Link(link TEXT)");
        db.execSQL("create Table StoreName(storeName TEXT)");
        db.execSQL("create Table AuthenticationToken(token TEXT)");
        db.execSQL("create Table Renewals(id INTEGER PRIMARY KEY AUTOINCREMENT,frequency TEXT, codeUssd TEXT,period INTEGER,numberTill TEXT,theTime TEXT,simDial TEXT,money TEXT, dateCreation TEXT, dateExpiry TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop Table if exists InboxTable");
        db.execSQL("drop table if exists Offers");
        db.execSQL("drop table if exists SuccessfulTransactions");
        db.execSQL("drop table if exists FailedTransactions");
        db.execSQL("drop table if exists User");
        db.execSQL("drop table if exists Link");
        db.execSQL("drop table if exists AuthenticationToken");
        db.execSQL("drop table if exists Renewals");
        db.execSQL("drop table if exists StoreName");
    }

    public boolean insertData( String message, String time, String sender){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("message",message);
        contentValues.put("timeStamp",time);
        contentValues.put("sender",sender);
        long result = database.insert("InboxTable",null,contentValues);
        return result != -1;
    }
    public boolean insertOffer(String name, String amount, String ussdCode, String dialSim, String dialSimId,String paymentSim,String paymentSimId,String offerTill){
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
    public boolean insertRenewals( String frequency, String codeUssd, int period, String numberTill, String time, String simDial,String money, String dateCreation, String dateExpiry){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("frequency",frequency);
        contentValues.put("codeUssd",codeUssd);
        contentValues.put("period",period);
        contentValues.put("numberTill",numberTill);
        contentValues.put("theTime",time);
        contentValues.put("simDial",simDial);
        contentValues.put("money",money);
        contentValues.put("dateCreation",dateCreation);
        contentValues.put("dateExpiry",dateExpiry);
        long result = database.insert("Renewals",null,contentValues);
        return result != -1;
    }
    public boolean updateOffer(String name, String amount, String oldUssdCode, String newUssdCode, String dialSim, String dialSimId, String paymentSim, String paymentSimId, String offerTill) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("name", name);
        contentValues.put("amount", amount);
        contentValues.put("ussdCode", newUssdCode);
        contentValues.put("dialSim", dialSim);
        contentValues.put("dialSimId", dialSimId);
        contentValues.put("paymentSim", paymentSim);
        contentValues.put("paymentSimId", paymentSimId);
        contentValues.put("offerTill", offerTill);

        // Assuming you are updating based on a unique identifier like 'id'
        String selection = " ussdCode = ?";
        String[] selectionArgs = { oldUssdCode };

        int result = database.update("Offers", contentValues, selection, selectionArgs);
        return result != -1;
    }
    public boolean updateRenewals( String frequency,String oldCodeUssd, String newCodeUssd, int period, String numberTill, String time, String simDial,String money, String dateCreation, String dateExpiry){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("frequency",frequency);
        contentValues.put("codeUssd",newCodeUssd);
        contentValues.put("period",period);
        contentValues.put("numberTill",numberTill);
        contentValues.put("theTime",time);
        contentValues.put("simDial",simDial);
        contentValues.put("money",money);
        contentValues.put("dateCreation",dateCreation);
        contentValues.put("dateExpiry",dateExpiry);

        String selection = "codeUssd = ?";
        String[] selectionArgs = { oldCodeUssd };

        int result = database.update("Renewals", contentValues, selection, selectionArgs);
        return result != -1;
    }

    public boolean insertSuccess(String ussdResponse, String amount,String timeStamp, String recipient,String status, int subId, String ussd, int till, String messageFull){
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
        long result = database.insert("SuccessfulTransactions",null,contentValues);
        return result != -1;
    }
    public boolean insertFailed(String ussdResponse, String amount,String timeStamp, String recipient,String status, int subId, String ussd, int till, String messageFull){
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
        long result = database.insert("FailedTransactions",null,contentValues);
        return result != -1;
    }

    public boolean insertUser(String tillNumber){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("tillNumber",tillNumber);
        long result = database.insert("User",null,contentValues);
        return result != -1;
    }
    public boolean insertLink(String link){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("link",link);
        long result = database.insert("Link",null,contentValues);
        return result != -1;
    }
    public boolean insertToken(String token){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("token",token);
        long result = database.insert("AuthenticationToken",null,contentValues);
        return result != -1;
    }
    public boolean insertStoreName(String storeName){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("storeName",storeName);
        long result = database.insert("StoreName",null,contentValues);
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
    public Cursor getStoreName(){
        SQLiteDatabase database = this.getWritableDatabase();
        return database.rawQuery("Select * from StoreName",null);
    }
    public Cursor getData(){
        SQLiteDatabase database = this.getWritableDatabase();
        return database.rawQuery("Select * from InboxTable ORDER BY id DESC",null);
    }
    public Cursor getOffers(){
        SQLiteDatabase database = this.getWritableDatabase();
        return database.rawQuery("Select * from Offers ORDER BY amount ASC",null);
    }
    public Cursor getRenewals(){
        SQLiteDatabase database = this.getWritableDatabase();
        return database.rawQuery("Select * from Renewals ORDER BY money ASC",null);
    }
    public Cursor getSuccessfulTransactions(){
        SQLiteDatabase database = this.getWritableDatabase();
        return database.rawQuery("Select * from SuccessfulTransactions ORDER BY id DESC",null);
    }
    public Cursor getFailedTransactions(){
        SQLiteDatabase database = this.getWritableDatabase();
        return database.rawQuery("Select * from FailedTransactions ORDER BY id DESC", null);
    }
    public Cursor getSpecificOffer(String amount, String simId){
        SQLiteDatabase database = this.getReadableDatabase();
        return database.rawQuery("Select * from Offers where amount=? and paymentSimId =?", new String[]{amount,simId});
    }
    public Boolean deleteTransaction () {
        SQLiteDatabase DB = this.getWritableDatabase();
        long result = DB.delete("FailedTransactions", null, null);
        return result != -1;
    }
    public Boolean deleteSuccessfulTransactions() {
        SQLiteDatabase DB = this.getWritableDatabase();
        long result = DB.delete("SuccessfulTransactions", null, null);
        return result != -1;
    }
    public Boolean deleteData (String ussdCode) {
        SQLiteDatabase DB = this.getWritableDatabase();
        long result = DB.delete("Offers", "ussdCode=?", new String[]{ussdCode});
        return result != -1;
    }
    public Boolean deleteSpecificTransaction (String ussdCode) {
        SQLiteDatabase DB = this.getWritableDatabase();
        long result = DB.delete("FailedTransactions", "ussd=?", new String[]{ussdCode});
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
        while (cursor.moveToNext()) {
            tables.add(cursor.getString(0));
        }
        for (String table : tables) {
            String deleteQuery = "DELETE FROM " + table;
            db.execSQL(deleteQuery);
        }
        cursor.close();
    }
    public void clearSpecificTables() {
        List<String> tablesToClear = new ArrayList<>();
        tablesToClear.add("User");
        tablesToClear.add("Link");
        tablesToClear.add("AuthenticationToken");
        tablesToClear.add("StoreName");

        SQLiteDatabase db = this.getWritableDatabase(); // Use getWritableDatabase for delete operations
        for (String table : tablesToClear) {
            String deleteQuery = "DELETE FROM " + table;
            db.execSQL(deleteQuery);
        }
    }

}
