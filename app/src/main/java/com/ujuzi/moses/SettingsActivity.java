package com.ujuzi.moses;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.ujuzi.moses.listeners.PaymentListener;
import com.ujuzi.moses.models.Payment;
import com.ujuzi.moses.services.MyService;
import com.ujuzi.moses.utilities.Constants;
import com.ujuzi.moses.utilities.PreferenceManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    private ConstraintLayout settingPaymentPlan,settingToken,settingAboutApp, settingLogout, settingChangePassword,settingDeleteAccount;
    private MaterialSwitch pauseApp;

    private TextView txtPauseApp;
    private DBHelper helper;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initViews();
        preferenceManager = new PreferenceManager(this);

        helper = new DBHelper(this);

        boolean isAppPaused = preferenceManager.getBoolean(Constants.KEY_IS_APP_PAUSED);

        if (isAppPaused){
            pauseApp.setChecked(true);
            txtPauseApp.setText("App paused");
        }
        else{
            pauseApp.setChecked(false);
            txtPauseApp.setText("App resumed");
        }

        pauseApp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    stopServiceTwo();
                    preferenceManager.putBoolean(Constants.KEY_IS_APP_PAUSED,true);
                } else {
                    serviceStart();
                    preferenceManager.putBoolean(Constants.KEY_IS_APP_PAUSED,false);
                }
            }
        });
    }

    private void stopServiceTwo() {
        if (myService()){
            Intent intent = new Intent(this, MyService.class);
            stopService(intent);
            Toast.makeText(this, "app paused", Toast.LENGTH_SHORT).show();
            txtPauseApp.setText("App paused");
        }
    }

    private void serviceStart() {
        if (myService()) {
            stopServiceTwo();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent intent = new Intent(this, MyService.class);
                startForegroundService(intent);
                Toast.makeText(this, "app resumed", Toast.LENGTH_SHORT).show();
                txtPauseApp.setText("App resumed");
            }
        }
    }



    public boolean myService(){
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo info :
                manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MyService.class.getName().equalsIgnoreCase(info.service.getClassName())){
                return true;
            }
        }
        return false;
    }
    public String tillNumber(){
        Cursor cursor = helper.getUser();
        String till = "";
        if (cursor.getCount() == 0){
            Toast.makeText(SettingsActivity.this, "", Toast.LENGTH_SHORT).show();
        }else{
            while (cursor.moveToNext()){
                till = cursor.getString(0);
            }
        }
        cursor.close();
        return till;
    }

    private void initViews() {
        settingPaymentPlan = findViewById(R.id.settingPaymentPlan);
        settingToken = findViewById(R.id.settingToken);
        settingAboutApp = findViewById(R.id.settingAboutApp);
        settingLogout = findViewById(R.id.settingLogout);
        pauseApp = findViewById(R.id.switchOnOffApp);
        txtPauseApp = findViewById(R.id.txtPauseApp);
        settingChangePassword = findViewById(R.id.settingResetPassword);
        settingDeleteAccount = findViewById(R.id.settingDeleteAccount);
    }
}