package com.bingwa.bingwasokonibot;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.widget.Toast;


import com.bingwa.bingwasokonibot.listeners.PaymentListener;
import com.bingwa.bingwasokonibot.models.Payment;
import com.bingwa.bingwasokonibot.services.MyService;
import com.bingwa.bingwasokonibot.services.RenewalsService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AlarmReceiver extends BroadcastReceiver {
    private DBHelper helper2;
    private DBHelper helper;
    private RequestManager manager;
    private Context context2;

    @Override
    public void onReceive(Context context, Intent intent) {
        context2 = context;
        serviceStart(context);
    }

//    private void checkStatus(String till,Context context){
//        manager = new RequestManager(context);
//        manager.getPaymentStatus(listener2,till,token(context));
//    }
//
//    private final PaymentListener listener2 = new PaymentListener() {
//        @Override
//        public void didFetch(Payment payment, String message) {
//            confirm2(payment);
//        }
//
//        @Override
//        public void didError(String message) {
//            stopServiceTwo(context2);
//            if (message.contains("Unable to resolve host")){
//                Toast.makeText(context2, "Please connect to the internet", Toast.LENGTH_SHORT).show();
//            }
//        }
//    };
//    private void confirm2(Payment payment) {
//        if (payment.status.equalsIgnoreCase("error")){
//            Toast.makeText(context2, "subscription expired", Toast.LENGTH_SHORT).show();
//        }else if (payment.status.equals("success")){
//            compareDates2(payment.data.timestamp);
//        }
//    }
//    private void compareDates2(String date) {
//        long currentTimeMillis = System.currentTimeMillis();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//        String time = sdf.format(currentTimeMillis);
//        Date current, expiry;
//        try {
//            current =  sdf.parse(time);
//            expiry = sdf.parse(date);
//            if (current.compareTo(expiry) > 0) {
//                Toast.makeText(context2, "subscription expired", Toast.LENGTH_SHORT).show();
//            } else if (current.compareTo(expiry) < 0) {
//                serviceStart(context2);
//            }
//        } catch (ParseException e) {
//            throw new RuntimeException(e);
//        }
//    }
    public void stopServiceTwo(Context context) {
        if (myService(context)){
            Intent intent = new Intent(context, RenewalsService.class);
            context.stopService(intent);
            Toast.makeText(context, "Renewal service stopped", Toast.LENGTH_SHORT).show();
        }
    }
    public void serviceStart(Context context) {
        if (myService(context)) {
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent intent = new Intent(context, RenewalsService.class);
                context.startForegroundService(intent);
                Toast.makeText(context, "Renewal service started", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public boolean myService(Context context){
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo info :
                manager.getRunningServices(Integer.MAX_VALUE)) {
            if (RenewalsService.class.getName().equalsIgnoreCase(info.service.getClassName())){
                return true;
            }
        }
        return false;
    }
    public String tillNumber(Context context){
        helper = new DBHelper(context);
        Cursor cursor = helper.getUser();
        String till = "";
        if (cursor.getCount() == 0){
            Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
        }else{
            while (cursor.moveToNext()){
                till = cursor.getString(0);
            }
        }
        cursor.close();
        return till;
    }
    public String token(Context context){
        helper2 = new DBHelper(context);
        Cursor cursor = helper2.getToken();
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
