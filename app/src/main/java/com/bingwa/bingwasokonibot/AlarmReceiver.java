package com.bingwa.bingwasokonibot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.Toast;

import androidx.coordinatorlayout.widget.DirectedAcyclicGraph;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bingwa.bingwasokonibot.Adapters.RenewalsListAdapter;
import com.bingwa.bingwasokonibot.models.RenewalPOJO;

import java.util.ArrayList;
import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {

    private List<RenewalPOJO> pojos;
    private DBHelper dbHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        showData(context);
    }
    private void showData(Context context) {
        pojos = new ArrayList<>();
        dbHelper = new DBHelper(context);
        Cursor cursor = dbHelper.getRenewals();
        if (cursor.getCount() == 0){
            Toast.makeText(context, "there are no renewals", Toast.LENGTH_SHORT).show();
        }
        else {
            while (cursor.moveToNext()){
                String frequency = cursor.getString(1);
                String ussdCode = cursor.getString(2);
                String period = cursor.getString(3);
                String tillNumber = cursor.getString(4);
                String time = cursor.getString(5);
                String dialSim = cursor.getString(5);
                RenewalPOJO pojo = new RenewalPOJO(frequency,ussdCode,period,tillNumber,time,dialSim);
                pojos.add(pojo);
            }
        }
        cursor.close();
        for (int i = 0; i < pojos.size(); i++) {
            Toast.makeText(context, pojos.get(i).getUssdCode(), Toast.LENGTH_SHORT).show();
        }
    }
}
