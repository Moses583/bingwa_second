package com.example.meisterbot;


import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.meisterbot.Adapters.ViewPagerAdapter;
import com.example.meisterbot.fragments.InboxFragment;
import com.example.meisterbot.listeners.PaymentListener;
import com.example.meisterbot.models.Payment;
import com.example.meisterbot.services.MyService;
import com.example.meisterbot.services.RetryService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity{
    private BottomNavigationView bottomNavigationView;
    private ViewPager2 viewPager2;
    private Toolbar toolbar;
    String[] permissions = new String[]{
            android.Manifest.permission.CALL_PHONE,
            android.Manifest.permission.SEND_SMS,
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS
    };

    private String till;
    private RequestManager manager;
    private DBHelper helper;
    private FloatingActionButton button;

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
        till = tillNumber();

        manager = new RequestManager(this);
        Toast.makeText(this, till, Toast.LENGTH_SHORT).show();

        officialStarter();


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkOffers();
            }
        });

        SharedPreferences prefs = getSharedPreferences("app_name", Context.MODE_PRIVATE);
        boolean hasAccount = prefs.getBoolean("hasAccount", false);

        if (!hasAccount){
            startActivity(new Intent(this, CreateAccountActivity.class));
            finish();
        }


        if (!hasPermissions(this, permissions)) {
            // If permissions are not already granted, request them
            requestPermissions(permissions, 101);
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



    }



    private boolean hasPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    private final NavigationBarView.OnItemSelectedListener listener = new NavigationBarView.OnItemSelectedListener() {
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.idHome){
            viewPager2.setCurrentItem(0);
            toolbar.setTitle("Bingwa");
            return true;
        } else if (item.getItemId() == R.id.idCreateOffer){
            viewPager2.setCurrentItem(1);
            toolbar.setTitle("Offers");
            return true;
        } else if (item.getItemId() == R.id.idInbox){
            viewPager2.setCurrentItem(2);
            toolbar.setTitle("Inbox");
            return true;
        }
        return true;
    }
};

    private void callPaymentApi(String till) {
        manager.getPaymentStatus(paymentListener,till);
    }


    private final PaymentListener paymentListener = new PaymentListener() {
        @Override
        public void didFetch(Payment payment, String message) {
            Toast.makeText(HomeActivity.this, payment.status+" from official start", Toast.LENGTH_SHORT).show();
            compareDates(payment.data.timestamp);
        }

        @Override
        public void didError(String message) {
            Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };

    public void
    compareDates(String two){
        long currentTimeMillis = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String time = sdf.format(currentTimeMillis);
        Date current, expiry;
        try {
            current =  sdf.parse(time);
            expiry = sdf.parse(two);
            if (current.compareTo(expiry) > 0) {
                Toast.makeText(HomeActivity.this, "Your subscription has expired", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(HomeActivity.this, PaymentPlanActivity.class));
            } else if (current.compareTo(expiry) < 0) {
                Toast.makeText(HomeActivity.this, "You subscription is valid", Toast.LENGTH_SHORT).show();
                startService();
            } else {
                Toast.makeText(HomeActivity.this, "Your subscription will expire soon", Toast.LENGTH_SHORT).show();
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    public void startService(){
        if (myService()) {
            Toast.makeText(this, "service already running", Toast.LENGTH_SHORT).show();
        } else {
            // If the service is not running, start it
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent intent = new Intent(HomeActivity.this, MyService.class);startForegroundService(intent);
                Toast.makeText(HomeActivity.this, "You have successfully enabled the app, it will now receive messages and make transactions.", Toast.LENGTH_LONG).show();
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
            Toast.makeText(HomeActivity.this, "till number absent", Toast.LENGTH_SHORT).show();
        }else{
            while (cursor.moveToNext()){
                till = cursor.getString(0);
            }
        }
        cursor.close();
        return till;
    }

    private void checkOffers() {
        Cursor cursor = helper.getOffers();
        if(cursor.getCount() == 0){
            Toast.makeText(this, "you need to create an offer first", Toast.LENGTH_SHORT).show();
        }else{
            checkStatus(till);
        }
    }

    private void checkStatus(String till) {
        manager.getPaymentStatus(listener2,till);
    }
    private final PaymentListener listener2 = new PaymentListener() {
        @Override
        public void didFetch(Payment payment, String message) {
            Toast.makeText(HomeActivity.this, payment.data.timestamp+" from listener2", Toast.LENGTH_SHORT).show();
            String date = payment.data.timestamp;
            compareDates2(date);
        }

        @Override
        public void didError(String message) {
            
        }
    };

    private void compareDates2(String date) {
        long currentTimeMillis = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String time = sdf.format(currentTimeMillis);
        Date current, expiry;
        try {
            current =  sdf.parse(time);
            expiry = sdf.parse(date);
            if (current.compareTo(expiry) > 0) {
                Toast.makeText(HomeActivity.this, "Your subscription has expired", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(HomeActivity.this, PaymentPlanActivity.class));
            } else if (current.compareTo(expiry) < 0) {
                Toast.makeText(HomeActivity.this, "You subscription is valid", Toast.LENGTH_SHORT).show();
                serviceStart();
            } else {
                Toast.makeText(HomeActivity.this, "Your subscription will expire soon", Toast.LENGTH_SHORT).show();
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private void serviceStart() {
        if (myService()) {
            // If the service is running, stop it
            Intent intent = new Intent(this, MyService.class);
            Toast.makeText(this, "app paused", Toast.LENGTH_SHORT).show();
            stopService(intent);
        } else {
            // If the service is not running, start it
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent intent = new Intent(this, MyService.class);
                startForegroundService(intent);
                Toast.makeText(this, "app resumed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void officialStarter(){
        Cursor cursor = helper.getOffers();
        if(cursor.getCount() == 0){
            Toast.makeText(this, "Unable to start app, you need to create an offer first", Toast.LENGTH_SHORT).show();
        }else{
            callPaymentApi(till);
        }
    }

    private void initViews(){
        bottomNavigationView = findViewById(R.id
                .bottomNavigationView);
        viewPager2 = findViewById(R.id.myViewPager);
        toolbar = findViewById(R.id.mainToolBar);
        button = findViewById(R.id.btnStartService);
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }
}