package com.bingwa.bingwasokonibot;

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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bingwa.bingwasokonibot.listeners.PaymentListener;
import com.bingwa.bingwasokonibot.models.Payment;
import com.bingwa.bingwasokonibot.services.MyService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    private ConstraintLayout settingPaymentPlan,settingToken,settingAboutApp, settingLogout, settingChangePassword,settingDeleteAccount;
    private Switch pauseApp;

    private TextView txtPauseApp;
    private RequestManager manager;
    private String till;
    private DBHelper helper,helper2;
    private AlertDialog firstTimePayDialog;
    private AlertDialog renewPlanDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
        initViews();

        manager = new RequestManager(this);
        helper = new DBHelper(this);

        till = tillNumber();

        if (pauseApp.isChecked()){
            txtPauseApp.setText("App paused");
        }
        else{
            txtPauseApp.setText("App resumed");
        }

        pauseApp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkStatus(till);
                } else {
                    checkStatus(till);
                }
            }
        });
        settingPaymentPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, PaymentPlanActivity.class));
//                Toast.makeText(SettingsActivity.this, "Feature coming soon", Toast.LENGTH_SHORT).show();
            }
        });
        settingToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, BingwaLinkActivity.class));
            }
        });
        settingAboutApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, AboutAppActivity.class));
            }
        });
        settingLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        settingChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, EditAccountActivity.class));
            }
        });
        settingDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, DeleteAccountActivity.class));
            }
        });
    }

    private void checkStatus(String till) {
        manager.getPaymentStatus(listener2,till,token());
    }
    private final PaymentListener listener2 = new PaymentListener() {
        @Override
        public void didFetch(Payment payment, String message) {
            confirm2(payment);
        }

        @Override
        public void didError(String message) {
            stopServiceTwo();
            if (message.contains("Unable to resolve host")){
                Toast.makeText(SettingsActivity.this, "Please connect to the internet", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void confirm2(Payment payment) {
        if (payment.status.equalsIgnoreCase("error")){
            showFirstTimePayDialog();
        }else if (payment.status.equals("success")){
            compareDates2(payment.data.timestamp);
        }
    }

    private void showFirstTimePayDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_first_time_pay,null);
        Button btn = view.findViewById(R.id.btnCheckAvailablePlans);
        builder.setView(view);
        firstTimePayDialog = builder.create();
        firstTimePayDialog.show();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToPlansActivity();
            }
        });
    }

    private void navigateToPlansActivity() {
        startActivity(new Intent(SettingsActivity.this, PaymentPlanActivity.class));
        firstTimePayDialog.dismiss();
    }

    private void compareDates2(String date) {
        long currentTimeMillis = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String time = sdf.format(currentTimeMillis);
        Date current, expiry;
        try {
            current =  sdf.parse(time);
            expiry = sdf.parse(date);
            if (current.compareTo(expiry) > 0) {
                stopServiceTwo();
                showRenewPlanDialog();
            } else if (current.compareTo(expiry) < 0) {
                serviceStart();
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private void showRenewPlanDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_subscription_expired,null);
        Button btn = view.findViewById(R.id.btnRenewPlan);
        builder.setView(view);
        renewPlanDialog = builder.create();
        renewPlanDialog.show();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToPlansActivity2();
            }
        });
    }

    private void navigateToPlansActivity2() {
        startActivity(new Intent(SettingsActivity.this, PaymentPlanActivity.class));
        renewPlanDialog.dismiss();
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

    private void logout() {
        // Clear user session data here
        // This depends on how you're managing user sessions
        // For example, if you're using SharedPreferences, you can do:
        if (myService()){
            Intent intent = new Intent(this, MyService.class);
            stopService(intent);
        }
        SharedPreferences preferences = getSharedPreferences("app_name", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        helper.clearSpecificTables();

        // Navigate back to login screen
        Intent intent = new Intent(SettingsActivity.this, CreateAccountActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    public String token(){
        helper2 = new DBHelper(this);
        Cursor cursor = helper2.getToken();
        String token = "";
        if (cursor.getCount() == 0){
            Toast.makeText(this, "token not found", Toast.LENGTH_SHORT).show();
        }else{
            while (cursor.moveToNext()){
                token = cursor.getString(0);
            }
        }
        cursor.close();
        return "Bearer "+token;
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