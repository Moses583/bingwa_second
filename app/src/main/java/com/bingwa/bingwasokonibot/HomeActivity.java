package com.bingwa.bingwasokonibot;


import static androidx.core.content.ContextCompat.getDrawable;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.pm.PackageManager;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.bingwa.bingwasokonibot.Adapters.ViewPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Map;

public class HomeActivity extends AppCompatActivity{
    private BottomNavigationView bottomNavigationView;
    private ViewPager2 viewPager2;
    private Toolbar toolbar;


    private String till;
    private RequestManager manager;
    private DBHelper helper;
    private FloatingActionButton button;
    private ExtendedFloatingActionButton start,stop;
    public CountDownTimer countDownTimer;
    private Dialog permissionsDialog;
    private Button request;
    String[] permissions = new String[]{
            android.Manifest.permission.CALL_PHONE,
            android.Manifest.permission.SEND_SMS,
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.RECEIVE_SMS,
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.RECEIVE_SMS
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
        setSupportActionBar(toolbar);

        helper = new DBHelper(this);

        manager = new RequestManager(this);



//        myAlarmManager = new MyAlarmManager(this);
//        myAlarmManager.setAlarm(11,31);
        countDownTimer = new CountDownTimer(5000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Toast.makeText(HomeActivity.this, "Time remaining: " + millisUntilFinished / 1000, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
            }
        };

        SharedPreferences prefs = getSharedPreferences("app_name", Context.MODE_PRIVATE);
        boolean hasAccount = prefs.getBoolean("hasAccount", false);

        if (!hasAccount){
            startActivity(new Intent(this, CreateAccountActivity.class));
            finish();
        }

        viewPager2.setAdapter(new ViewPagerAdapter(this));
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                if (bottomNavigationView.getMenu().getItem(position).getTitle().equals("Home")){
                    toolbar.setTitle("Bingwa");
                }else{
                    toolbar.setTitle(bottomNavigationView.getMenu().getItem(position).getTitle());
                }
            }
        });
        bottomNavigationView.setOnItemSelectedListener(listener);
        if (hasPermissions()){
            Toast.makeText(this, "All permissions granted", Toast.LENGTH_SHORT).show();
        }else{
            if (      shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)
                    ||shouldShowRequestPermissionRationale(Manifest.permission.RECEIVE_SMS)
                    ||shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)
                    ||shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)
                    ||shouldShowRequestPermissionRationale(Manifest.permission.FOREGROUND_SERVICE)
                    ||shouldShowRequestPermissionRationale(Manifest.permission.FOREGROUND_SERVICE_SPECIAL_USE)){
                showRationaleDialog();
            }else {
                requestPermissionLauncher.launch(permissions);
            }
        }


    }

    private void showRationaleDialog() {
        permissionsDialog = new Dialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_request_permissions,null);
        request = view.findViewById(R.id.btnRequestPermissions);
        permissionsDialog.setContentView(view);
        permissionsDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        permissionsDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background));
        permissionsDialog.setCancelable(false);
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissionLauncher.launch(permissions);
                permissionsDialog.dismiss();
            }
        });
        permissionsDialog.show();
    }

    private ActivityResultLauncher<String[]> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
        @Override
        public void onActivityResult(Map<String, Boolean> o) {

        }
    });

    private boolean hasPermissions(){
        return
                 checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.FOREGROUND_SERVICE) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.FOREGROUND_SERVICE_SPECIAL_USE) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED;

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.idSettings){
            startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private final NavigationBarView.OnItemSelectedListener listener = new NavigationBarView.OnItemSelectedListener() {
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.idHome){
            viewPager2.setCurrentItem(0);
            toolbar.setTitle("Bingwa");
            return true;
        } else if (item.getItemId() == R.id.idTransactions){
            viewPager2.setCurrentItem(1);
            toolbar.setTitle("Transactions");
            return true;
        }
        else if (item.getItemId() == R.id.idCreateOffer){
            viewPager2.setCurrentItem(2);
            toolbar.setTitle("Offers");
            return true;
        } else if (item.getItemId() == R.id.idAutoRenewOffer){
            viewPager2.setCurrentItem(3);
            toolbar.setTitle("Auto renewals");
            return true;
        }
        else if (item.getItemId() == R.id.idInbox){
            viewPager2.setCurrentItem(4);
            toolbar.setTitle("Inbox");
            return true;
        }
        return false;
    }
};

    private void initViews(){
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        viewPager2 = findViewById(R.id.myViewPager);
        toolbar = findViewById(R.id.mainToolBar);
    }
}