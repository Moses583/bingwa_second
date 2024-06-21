package com.example.meisterbot;


import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaDrm;
import android.os.Build;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
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
    private AlertDialog offerCreationDialog,firstTimePayDialog,renewPlanDialog;
    private ExtendedFloatingActionButton start,stop;
    public CountDownTimer countDownTimer;

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

        checkOffersOne();
        countDownTimer = new CountDownTimer(5000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Toast.makeText(HomeActivity.this, "Time remaining: " + millisUntilFinished / 1000, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                stopServiceOne();
            }
        };

        SharedPreferences prefs = getSharedPreferences("app_name", Context.MODE_PRIVATE);
        boolean hasAccount = prefs.getBoolean("hasAccount", false);

        if (!hasAccount){
            startActivity(new Intent(this, CreateAccountActivity.class));
            finish();
        }


        if (!hasPermissions(this, permissions)) {
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
    public void checkOffersOne(){
        Cursor cursor = helper.getOffers();
        if(cursor.getCount() == 0){
            showOfferCreationDialog();
        }else{
            callPaymentApi(till);
        }
    }

    private void showOfferCreationDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_create_offer,null);
        Button btn = view.findViewById(R.id.btnNavigateToCreateOffer);
        builder.setView(view);
        offerCreationDialog = builder.create();
        offerCreationDialog.show();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToCreateOfferActivity();
            }
        });
    }

    private void navigateToCreateOfferActivity() {
        startActivity(new Intent(HomeActivity.this, CreateOfferActivity.class));
        offerCreationDialog.dismiss();
    }

    private void callPaymentApi(String till) {
        manager.getPaymentStatus(paymentListener,till);
    }


    private final PaymentListener paymentListener = new PaymentListener() {
        @Override
        public void didFetch(Payment payment, String message) {
            confirm1(payment);
        }

        @Override
        public void didError(String message) {
            stopServiceOne();
            if (message.contains("Unable to resolve host")){
                Toast.makeText(HomeActivity.this, "Please connect to the internet", Toast.LENGTH_SHORT).show();
            }

        }
    };

    public void confirm1(Payment payment){
        if (payment.status.equalsIgnoreCase("error")){
            showFirstTimePayDialog();
        }else if (payment.status.equals("success")){
            compareDates(payment.data.timestamp);
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
        startActivity(new Intent(HomeActivity.this, PaymentPlanActivity.class));
        firstTimePayDialog.dismiss();
    }

    public void compareDates(String two){
        long currentTimeMillis = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String time = sdf.format(currentTimeMillis);
        Date current, expiry;
        try {
            current =  sdf.parse(time);
            expiry = sdf.parse(two);
            if (current.compareTo(expiry) > 0) {
                stopServiceOne();
                showRenewPlanDialog();
            } else if (current.compareTo(expiry) < 0) {
                startService();
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
        startActivity(new Intent(HomeActivity.this, PaymentPlanActivity.class));
        renewPlanDialog.dismiss();
    }

    public void stopServiceOne() {
        if (myService()){
            Intent intent = new Intent(this, MyService.class);
            stopService(intent);
            Toast.makeText(this, "App paused, you need to check your subscription", Toast.LENGTH_LONG).show();
        }
    }

    public void startService(){
        if (myService()) {
            Log.d("MAIN_SERVICE","Service already running");
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent intent = new Intent(this, MyService.class);
                startForegroundService(intent);
                Toast.makeText(this, "App services are now available", Toast.LENGTH_SHORT).show();
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
            Log.d("TILL","Till number absent");
        }else{
            while (cursor.moveToNext()){
                till = cursor.getString(0);
            }
        }
        cursor.close();
        return till;
    }

    private void initViews(){
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        viewPager2 = findViewById(R.id.myViewPager);
        toolbar = findViewById(R.id.mainToolBar);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }
}