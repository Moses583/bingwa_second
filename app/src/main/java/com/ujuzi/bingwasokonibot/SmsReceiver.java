package com.ujuzi.bingwasokonibot;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.ujuzi.bingwasokonibot.listeners.CheckTransactionListener;
import com.ujuzi.bingwasokonibot.listeners.PostTransactionListener;
import com.ujuzi.bingwasokonibot.models.CheckTransactionApiResponse;
import com.ujuzi.bingwasokonibot.models.OfferPOJO;
import com.ujuzi.bingwasokonibot.models.Transaction;
import com.ujuzi.bingwasokonibot.models.TransactionApiResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsReceiver extends BroadcastReceiver {
    private static final String SENDER_ID = "MPESA";
    private DBHelper dbHelper,helper2;
    List<OfferPOJO> pojos = new ArrayList<>();
    TelephonyManager manager;
    private TelephonyManager.UssdResponseCallback callback;
    private Context mContext,zContext;
    Handler handler;
    String response1 = "";
    String messageBody = "";
    String matchedAmount = "";
    String ussdCode = "";
    String messageSender = "";

    String sub = "";
    String phoneNumber = "";
    String smsNumber = "";

    String transactionTimeStamp = "";
    List<OfferPOJO> pojoList = new ArrayList<>();

    String globalTimestamp = "";

    private RequestManager requestManager,requestManager2;


    @Override
    public void onReceive(Context context, Intent intent) {
        dbHelper = new DBHelper(context);

        mContext = context;
        zContext = context;
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            StringBuilder stringBuilder = new StringBuilder();
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                stringBuilder.append(smsMessage.getDisplayMessageBody());
                messageSender = smsMessage.getDisplayOriginatingAddress();
            }
            messageBody = stringBuilder.toString();
            long time = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String timeStamp = sdf.format(time);
            Bundle bundle = intent.getExtras();
            sub = String.valueOf(bundle.getInt("subscription", -1));
            if (messageSender.equals(SENDER_ID)){
                if (messageBody.contains("received Ksh")){
                    extract(context,messageBody);
                    checkTransaction(dbHelper,context, smsNumber);
                    insert(context,dbHelper,messageBody,timeStamp);
                }
                else if(messageBody.contains("AMKsh")){
                    extract2(context,messageBody);
                    checkTransaction(dbHelper,context, smsNumber);
                    insert(context,dbHelper,messageBody,timeStamp);
                }
                else if(messageBody.contains("PMKsh")){
                    extract3(context,messageBody);
                    insert(mContext,dbHelper,messageBody,globalTimestamp);
                    checkTransaction(dbHelper,context, smsNumber);
                    insert(context,dbHelper,messageBody,timeStamp);
                }
                else{
                    insert(context,dbHelper,messageBody,timeStamp);
                }
            }
        }

    }

    public void extract(Context context, String message){
        Pattern amountPattern = Pattern.compile("received Ksh([0-9,]+)\\.00");
        Matcher amountMatcher = amountPattern.matcher(message);
        if (amountMatcher.find()) {
            matchedAmount = amountMatcher.group(1).replace(",", "");
        }
        Pattern phonePattern = Pattern.compile("\\b0[0-9]{9}\\b");
        Matcher phoneMatcher = phonePattern.matcher(message);
        if (phoneMatcher.find()) {
            smsNumber = phoneMatcher.group();
        }
    }
    public void extract2(Context context, String message){
        Pattern amountPattern = Pattern.compile("AMKsh([0-9,]+)\\.00");
        Matcher amountMatcher = amountPattern.matcher(message);
        if (amountMatcher.find()) {
            matchedAmount = amountMatcher.group(1).replace(",", "");
        }

        // Pattern to match '254758567551'
        Pattern phonePattern = Pattern.compile("(254)(\\d{9})");
        Matcher phoneMatcher = phonePattern.matcher(message);
        if (phoneMatcher.find()) {
            smsNumber = "0" + phoneMatcher.group(2);
        }
    }
    public void extract3(Context context, String message){
        Pattern amountPattern = Pattern.compile("PMKsh([0-9,]+)\\.00");
        Matcher amountMatcher = amountPattern.matcher(message);
        if (amountMatcher.find()) {
            matchedAmount = amountMatcher.group(1).replace(",", "");
        }
        // Pattern to match '254758567551'
        Pattern phonePattern = Pattern.compile("(254)(\\d{9})");
        Matcher phoneMatcher = phonePattern.matcher(message);
        if (phoneMatcher.find()) {
            smsNumber = "0" + phoneMatcher.group(2);
        }

    }

    public void checkTransaction(DBHelper helper,Context context,String phoneNumber){
        requestManager2 = new RequestManager(context);
        requestManager2.checkTransactions(listener,phoneNumber,token());
    }

    private final CheckTransactionListener listener = new CheckTransactionListener() {
        @Override
        public void didFetch(CheckTransactionApiResponse response, String message) {
            if (response.status.contains("No transaction found")){
                phoneNumber = smsNumber;
            }
            else {
                phoneNumber = response.Phone;
            }
            getOffer(mContext,dbHelper,sub,matchedAmount);
            insert(mContext,dbHelper,messageBody,globalTimestamp);
        }

        @Override
        public void didError(String message) {
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        }
    };

    public void getOffer(Context context,DBHelper helper,String sub, String amount){
        String code = "";
        String newCode = "";
        int subscriptionId = 0;
        int till = 0;
        OfferPOJO pojo = null;
        Cursor cursor = helper.getSpecificOffer(amount,sub);
        if (cursor.getCount() == 0){
            Toast.makeText(context, "no offer found", Toast.LENGTH_SHORT).show();
        }
        else {
            while (cursor.moveToNext()){
                String name = cursor.getString(1);
                String amount1 = cursor.getString(2);
                String ussdCode = cursor.getString(3);
                String dialSim = cursor.getString(4);
                String dialSimId = cursor.getString(5);
                String paymentSim = cursor.getString(6);
                String paymentSimId = cursor.getString(7);
                String offerTill = cursor.getString(8);
                pojo = new OfferPOJO(name,amount1,ussdCode,dialSim,Build.ID,dialSimId,paymentSim,paymentSimId,offerTill);
            }
            code = pojo.getUssd();
            subscriptionId = Integer.parseInt(pojo.getSubscriptionId());
            till = Integer.parseInt(pojo.getOfferTill());
            if (code.contains("pppp")){
                newCode = code.replace("pppp",phoneNumber);
            }
            dialUssdCode(context,subscriptionId,newCode,till);
        }
    }

    private void dialUssdCode(Context context, int subscriptionId, String ussdCode, int till) {
        manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            callback = new TelephonyManager.UssdResponseCallback() {
                @Override
                public void onReceiveUssdResponse(TelephonyManager telephonyManager, String request, CharSequence response) {
                    super.onReceiveUssdResponse(telephonyManager, request, response);
                    response1 = response.toString();

                    long currentTimeMillis = System.currentTimeMillis();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    transactionTimeStamp = sdf.format(currentTimeMillis);

                    if (response1.contains("Kindly wait as we process")){
                        insertSuccess(context,dbHelper,response1,matchedAmount,transactionTimeStamp,phoneNumber,till,"1",subscriptionId,ussdCode,messageBody);
                    } else if (response1.contains("You have successfully purchased")) {
                        insertSuccess(context,dbHelper,response1,matchedAmount,transactionTimeStamp,phoneNumber,till,"1",subscriptionId,ussdCode,messageBody);
                    }else if (response1.contains("You have transferred")) {
                        insertSuccess(context,dbHelper,response1,matchedAmount,transactionTimeStamp,phoneNumber,till,"1",subscriptionId,ussdCode,messageBody);
                    }else if (response1.contains("Message has been sent successfully")) {
                        insertSuccess(context,dbHelper,response1,matchedAmount,transactionTimeStamp,phoneNumber,till,"1",subscriptionId,ussdCode,messageBody);
                    }else{
                        insertFailed(context,dbHelper,response1,matchedAmount,transactionTimeStamp,phoneNumber,till,"0",subscriptionId,ussdCode,messageBody);
                    }

                    Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onReceiveUssdResponseFailed(TelephonyManager telephonyManager, String request, int failureCode) {
                    super.onReceiveUssdResponseFailed(telephonyManager, request, failureCode);
                    response1 = String.valueOf(failureCode);

                    long currentTimeMillis = System.currentTimeMillis();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    transactionTimeStamp = sdf.format(currentTimeMillis);
                    insertFailed(context,dbHelper,response1,matchedAmount,transactionTimeStamp,phoneNumber,till,"0",subscriptionId,ussdCode,messageBody);
                    Log.d("TAG",String.valueOf(failureCode));
                }
            };
        }
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
            }
        };
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.CALL_PHONE},101);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createForSubscriptionId(subscriptionId).sendUssdRequest(ussdCode, callback, handler);
        }

    }
    public void insertSuccess(Context context,DBHelper helper,String ussdResponse, String amount, String transactionTimeStamp, String recipient,int till, String status,int subId,String ussd, String messageFull){
        boolean checkInsertData = helper.insertSuccess(ussdResponse,amount,transactionTimeStamp,recipient,status,subId,ussd,till,messageFull);
        if (checkInsertData){
            Toast.makeText(context, "Transaction recorded", Toast.LENGTH_SHORT).show();
            postTransaction(context,Double.parseDouble(matchedAmount),phoneNumber,till,messageBody);
        }
        else{
            Toast.makeText(context, "Transaction not recorded", Toast.LENGTH_SHORT).show();
        }

    }
    public void insertFailed(Context context,DBHelper helper,String ussdResponse, String amount, String transactionTimeStamp, String recipient,int till, String status,int subId,String ussd, String messageFull){
        boolean checkInsertData = helper.insertFailed(ussdResponse,amount,transactionTimeStamp,recipient,status,subId,ussd,till,messageFull);
        if (checkInsertData){
            Toast.makeText(context, "Transaction recorded", Toast.LENGTH_SHORT).show();
            postTransaction(context,Double.parseDouble(matchedAmount),phoneNumber,till,messageBody);
        }
        else{
            Toast.makeText(context, "Transaction not recorded", Toast.LENGTH_SHORT).show();
        }

    }
    public void postTransaction(Context context, double amount, String number,int till,String message){
        Transaction transaction = new Transaction(message,number,amount,till);
        requestManager = new RequestManager(context);
        final PostTransactionListener listener = new PostTransactionListener() {
            @Override
            public void didFetch(TransactionApiResponse response, String message) {
                Toast.makeText(context, "Transaction uploaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void didError(String message) {
                Toast.makeText(context, "Please connect to the internet", Toast.LENGTH_SHORT).show();
            }
        };

        requestManager.postTransaction(listener, transaction,token());
    }

    public void insert(Context context,DBHelper helper, String message, String time){
        boolean checkInsertData = helper.insertData(message, time, messageSender);
    }
    public String token(){
        helper2 = new DBHelper(zContext);
        Cursor cursor = helper2.getToken();
        String token = "";
        if (cursor.getCount() == 0){
            Toast.makeText(mContext, "token not found", Toast.LENGTH_SHORT).show();
        }else{
            while (cursor.moveToNext()){
                token = cursor.getString(0);
            }
        }
        cursor.close();
        return "Bearer "+token;
    }



}
