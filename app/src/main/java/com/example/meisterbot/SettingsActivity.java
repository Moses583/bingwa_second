package com.example.meisterbot;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
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

public class SettingsActivity extends AppCompatActivity {

    private ConstraintLayout settingPaymentPlan,settingToken,settingAboutApp,settingPauseApp,settingProfile;

    private TextView pauseApp;

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


        settingPaymentPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(SettingsActivity.this, PaymentPlanActivity.class));
                Toast.makeText(SettingsActivity.this, "Feature coming soon", Toast.LENGTH_SHORT).show();
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
                if (myService()) {
                    // If the service is running, stop it
                    Intent intent = new Intent(SettingsActivity.this, MyService.class);
                    stopService(intent);
                    Toast.makeText(SettingsActivity.this, "You have successfully disabled the app, it will no longer receive messages nor make transactions.", Toast.LENGTH_LONG).show();
                } else {
                    // If the service is not running, start it
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Intent intent = new Intent(SettingsActivity.this, MyService.class);
                        startForegroundService(intent);
                        Toast.makeText(SettingsActivity.this, "You have successfully enabled the app, it will now receive messages and make transactions.", Toast.LENGTH_LONG).show();
                    }
                }
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
    private void initViews() {
        settingPaymentPlan = findViewById(R.id.settingPaymentPlan);
        settingToken = findViewById(R.id.settingToken);
        settingAboutApp = findViewById(R.id.settingAboutApp);
        settingPauseApp = findViewById(R.id.settingPauseApp);
        settingProfile = findViewById(R.id.settingEditProfile);
        pauseApp = findViewById(R.id.txtPauseApp);
    }
}