package com.bingwa.bingwasokonibot.services;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.bingwa.bingwasokonibot.DBHelper;
import com.bingwa.bingwasokonibot.RequestManager;
import com.bingwa.bingwasokonibot.listeners.PostTransactionListener;
import com.bingwa.bingwasokonibot.models.RenewalPOJO;
import com.bingwa.bingwasokonibot.models.Transaction;
import com.bingwa.bingwasokonibot.models.TransactionApiResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;

public class RenewalsService extends Service {
    private Queue<RenewalPOJO> pojos;
    private DBHelper dbHelper,dbHelper2,dbHelper3;
    private TelephonyManager manager;
    private TelephonyManager.UssdResponseCallback callback;
    String response1 = "";
    String transactionTimeStamp = "";
    private Handler handler,handler2;
    private RequestManager requestManager;
    String matchedAmount = "";
    String phoneNumber = "";
    private boolean isRunning = true;
    Runnable runnableCode;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler2 = new Handler();
        pojos = new LinkedList<>();
        dbHelper = new DBHelper(this);
        dbHelper2 = new DBHelper(this);
        dbHelper3 = new DBHelper(this);
        requestManager = new RequestManager(this);

        manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        fetchRenewals(this);

        runnableCode = new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (pojos.isEmpty()){
                            isRunning = false;
                            Log.d("Renewals","renewals dialed finished");
                            stopSelf();
                        }
                        else{
                            RenewalPOJO pojo = pojos.poll();
                            int subId = Integer.parseInt(pojo.getSubId());
                            String ussd = pojo.getUssdCode();
                            int till = Integer.parseInt(pojo.getTill());
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                            Date current, expiry;
                            try {
                                current =  sdf.parse(pojo.getDateCreation());
                                expiry = sdf.parse(pojo.getDateExpiry());
                                if (current.compareTo(expiry) > 0) {
                                    Log.d("Renewals","don't dial ussd code");
                                } else if (current.compareTo(expiry) < 0) {
                                    dialUssdCode(getApplicationContext(),subId,"*100*4*1*1#",till);
                                }
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }

                        }

                    }
                }).start();
                if (isRunning){
                    handler2.postDelayed(this, 5000);
                }
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("ForegroundServiceChannel", "Foreground Service Channel", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, "ForegroundServiceChannel")
                .setContentTitle("Renewals Dial Service")
                .setContentText("Running...");

        startForeground(1, notification.build());

        handler2.post(runnableCode);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void fetchRenewals(Context context) {
        pojos = new LinkedList<>();
        Cursor cursor = dbHelper.getRenewals();
        if (cursor.getCount() == 0){
            Log.d("Renewals","There are no renewals present");
        }
        else {
            while (cursor.moveToNext()){
                String frequency = cursor.getString(1);
                String ussdCode = cursor.getString(2);
                int period = cursor.getInt(3);
                String tillNumber = cursor.getString(4);
                String time = cursor.getString(5);
                String dialSim = cursor.getString(6);
                String money = cursor.getString(7);
                String start = cursor.getString(8);
                String end = cursor.getString(9);
                RenewalPOJO pojo = new RenewalPOJO(frequency,ussdCode,period,tillNumber,time,dialSim,money,start,end);
                pojos.add(pojo);
            }
        }
        cursor.close();
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
                        insertTransaction(context,dbHelper2,response1,matchedAmount,transactionTimeStamp,phoneNumber,till,"1",subscriptionId,ussdCode,"messageBody");
                    } else if (response1.contains("You have successfully purchased")) {
                        insertTransaction(context,dbHelper2,response1,matchedAmount,transactionTimeStamp,phoneNumber,till,"1",subscriptionId,ussdCode,"messageBody");
                    }
                    else{
                        insertTransaction(context,dbHelper2,response1,matchedAmount,transactionTimeStamp,phoneNumber,till,"0",subscriptionId,ussdCode,"messageBody");
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
                    insertTransaction(context,dbHelper2,response1,matchedAmount,transactionTimeStamp,phoneNumber,till,"0",subscriptionId,ussdCode,"messageBody");
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
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.CALL_PHONE},101);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createForSubscriptionId(subscriptionId).sendUssdRequest(ussdCode, callback, handler);
        }

    }
    public void insertTransaction(Context context,DBHelper helper,String ussdResponse, String amount, String transactionTimeStamp, String recipient,int till, String status,int subId,String ussd, String messageFull){
        boolean checkInsertData = helper.insertTransaction(ussdResponse,amount,transactionTimeStamp,recipient,status,subId,ussd,till,messageFull);
        if (checkInsertData){
            Toast.makeText(context, "Transaction recorded", Toast.LENGTH_SHORT).show();
//            postTransaction(context,Double.parseDouble(matchedAmount),phoneNumber,till,"messageBody");
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

        requestManager.postTransaction(listener, transaction,token(context));
    }
    public String token(Context context){
        dbHelper3 = new DBHelper(context);
        Cursor cursor = dbHelper3.getToken();
        String token = "";
        if (cursor.getCount() == 0){
            Toast.makeText(context, "token not found", Toast.LENGTH_SHORT).show();
        }else{
            while (cursor.moveToNext()){
                token = cursor.getString(0);
            }
        }
        cursor.close();
        return "Bearer "+token;
    }
}
