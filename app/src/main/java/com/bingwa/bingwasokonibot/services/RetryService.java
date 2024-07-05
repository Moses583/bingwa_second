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
import com.bingwa.bingwasokonibot.listeners.CheckTransactionListener;
import com.bingwa.bingwasokonibot.listeners.PostTransactionListener;
import com.bingwa.bingwasokonibot.models.CheckTransactionApiResponse;
import com.bingwa.bingwasokonibot.models.Transaction;
import com.bingwa.bingwasokonibot.models.TransactionApiResponse;
import com.bingwa.bingwasokonibot.models.TransactionPOJO;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;

public class RetryService extends Service {

    private Handler handler;
    private Runnable runnableCode;
    private Queue<TransactionPOJO> queue;
    private Queue<String> queue1;
    private DBHelper dbHelper;

    TelephonyManager manager;
    TelephonyManager.UssdResponseCallback callback;
    Handler handler2;
    private static final String TAG = "Failed";
    String s = "";
    private String response1;
    private String transactionTimeStamp;
    private RequestManager requestManager,requestManager2;

    private boolean isRunning = true;
    private boolean isTransaction = true;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        handler = new Handler();
        queue = new LinkedList<>();
        queue1 = new LinkedList<>();
        dbHelper = new DBHelper(this);
        requestManager = new RequestManager(this);
        requestManager2 = new RequestManager(this);

        manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        queue = getFailedTransactions();
        queue1 = getStrings();
        runnableCode = new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (queue.isEmpty()){
                            isRunning = false;
                            Log.d(TAG,"queue is empty");
                            stopSelf();
                            deleteData();
                        }
                        else{
                            TransactionPOJO pojo = queue.poll();
                            String response = pojo.getUssdResponse();
                            String amount = pojo.getTransactionAmount();
                            String time = pojo.getTimeStamp();
                            String number = pojo.getRecipient();
                            String status = pojo.getStatus();
                            int subId = pojo.getSubId();
                            String ussd = pojo.getUssd();
                            int till = pojo.getTill();
                            String message = pojo.getMessageFull();
//                            checkTransaction(RetryService.this,number);
                            dialUssdCode(getApplicationContext(),subId,ussd,till,amount,number,message);
                            Log.d(TAG,"Executing...");
                        }

                    }
                }).start();
                if (isRunning){
                    handler.postDelayed(this, 5000);
                }
            }
        };


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("ForegroundServiceChannel", "Foreground Service Channel", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, "ForegroundServiceChannel")
                .setContentTitle("Database Check Service")
                .setContentText("Running...");

        startForeground(1, notification.build());

        handler.post(runnableCode);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    private Queue<String> getStrings(){
        Queue<String> queue = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            queue.add("This was added");
        }
        return queue;
    }
    public void checkTransaction(Context context,String phoneNumber){
        requestManager2 = new RequestManager(context);
        requestManager2.checkTransactions(listener,phoneNumber);
    }

    private final CheckTransactionListener listener = new CheckTransactionListener() {
        @Override
        public void didFetch(CheckTransactionApiResponse response, String message) {
            if (response.status.contains("Transaction already made")){
                Toast.makeText(RetryService.this, response.status, Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(RetryService.this, "A similar transaction already took place", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void didError(String message) {
            Toast.makeText(RetryService.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }
    };

    private Queue<TransactionPOJO> getFailedTransactions(){
        Cursor cursor = dbHelper.getFailedResponses();
        Queue<TransactionPOJO> queue1 = new LinkedList<>();
        if (cursor.getCount() == 0){
            Log.d(TAG,"There are no failed transactions");
            return queue1;
        }else{
            while (cursor.moveToNext()){
                String ussdResponse = cursor.getString(1);
                String amount = cursor.getString(2);
                String timeStamp = cursor.getString(3);
                String recipient = cursor.getString(4);
                String status = cursor.getString(5);
                int subId = cursor.getInt(6);
                String ussd = cursor.getString(7);
                int till = cursor.getInt(8);
                String messageFull = cursor.getString(9);
                queue1.add(new TransactionPOJO(ussdResponse,amount,timeStamp,recipient,status,subId,ussd,till,messageFull));
            }
        }
        return queue1;
    }

    private void deleteData(){
        boolean deleteData = dbHelper.deleteTransaction();
        if (deleteData){
            Log.d(TAG,"Data deleted");
        }
        else{
            Log.d(TAG, "Data not deleted");
        }
    }

    private void dialUssdCode(Context context, int subscriptionId, String ussdCode, int till, String matchedAmount,String phoneNumber,String messageFull) {
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
                        insertTransaction(context,dbHelper,response1,matchedAmount,transactionTimeStamp,phoneNumber,till,"1",subscriptionId,ussdCode,messageFull);
                    } else if (response1.contains("You have successfully purchased")) {
                        insertTransaction(context,dbHelper,response1,matchedAmount,transactionTimeStamp,phoneNumber,till,"1",subscriptionId,ussdCode,messageFull);
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
                    Toast.makeText(context, String.valueOf(failureCode), Toast.LENGTH_SHORT).show();
                }
            };
        }
        handler2 = new Handler(Looper.getMainLooper()) {
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
            manager.createForSubscriptionId(subscriptionId).sendUssdRequest(ussdCode, callback, handler2);
        }

    }
    public void insertTransaction(Context context,DBHelper helper,String ussdResponse, String amount, String transactionTimeStamp, String recipient,int till, String status,int subId,String ussd,String messageFull){
        boolean checkInsertData = helper.insertTransaction(ussdResponse,amount,transactionTimeStamp,recipient,status,subId,ussd,till,messageFull);
        if (checkInsertData){
            Toast.makeText(context, "Transaction recorded", Toast.LENGTH_SHORT).show();
            postTransaction(context,Double.parseDouble(amount),recipient,till,messageFull);
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
                Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void didError(String message) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        };

        requestManager.postTransaction(listener, transaction);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
