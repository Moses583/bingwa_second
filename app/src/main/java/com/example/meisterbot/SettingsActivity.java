package com.example.meisterbot;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.meisterbot.listeners.PaymentListener;
import com.example.meisterbot.models.Payment;
import com.example.meisterbot.services.MyService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private ConstraintLayout settingPaymentPlan,settingToken,settingAboutApp,settingPauseApp,settingProfile;

    private TextView pauseApp;
    private RequestManager manager;
    private String till;
    private DBHelper helper;

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
        Toast.makeText(this, till, Toast.LENGTH_SHORT).show();


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
//                startActivity(new Intent(SettingsActivity.this, TokensActivity.class));
                Toast.makeText(SettingsActivity.this, "Feature coming soon", Toast.LENGTH_SHORT).show();
            }
        });
        settingAboutApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(SettingsActivity.this, AboutAppActivity.class));
                Toast.makeText(SettingsActivity.this, "Feature coming soon", Toast.LENGTH_SHORT).show();
            }
        });
        settingPauseApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        settingProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(SettingsActivity.this, EditAccountActivity.class));
                Toast.makeText(SettingsActivity.this, "Feature coming soon", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callPaymentApi(String till) {
        manager.getPaymentStatus(paymentListener,till);
    }

    private final PaymentListener paymentListener = new PaymentListener() {
        @Override
        public void didFetch(Payment payment, String message) {
            Toast.makeText(SettingsActivity.this, payment.status, Toast.LENGTH_SHORT).show();
            compareDates(payment.data.timestamp);
        }

        @Override
        public void didError(String message) {
            Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };

    public void compareDates(String two){
        long currentTimeMillis = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String time = sdf.format(currentTimeMillis);
        Date current, expiry;
        try {
            current =  sdf.parse(time);
            expiry = sdf.parse(two);
            if (current.compareTo(expiry) > 0) {
                Toast.makeText(SettingsActivity.this, "Your subscription has expired", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SettingsActivity.this, PaymentPlanActivity.class));
            } else if (current.compareTo(expiry) < 0) {
                Toast.makeText(SettingsActivity.this, "You subscription is valid", Toast.LENGTH_SHORT).show();
                startService();
            } else {
                Toast.makeText(SettingsActivity.this, "Your subscription will expire soon", Toast.LENGTH_SHORT).show();
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    public void startService(){
        if (myService()) {
            Toast.makeText(this, "Service already started", Toast.LENGTH_SHORT).show();
        } else {
            // If the service is not running, start it
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent intent = new Intent(SettingsActivity.this, MyService.class);
                startForegroundService(intent);
                Toast.makeText(SettingsActivity.this, "You have successfully enabled the app, it will now receive messages and make transactions.", Toast.LENGTH_LONG).show();
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
        settingPauseApp = findViewById(R.id.settingPauseApp);
        settingProfile = findViewById(R.id.settingEditProfile);
        pauseApp = findViewById(R.id.txtPauseApp);
    }
}