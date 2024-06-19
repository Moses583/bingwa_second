package com.example.meisterbot;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.meisterbot.listeners.PaymentListener;
import com.example.meisterbot.listeners.STKPushListener;
import com.example.meisterbot.models.Payment;
import com.example.meisterbot.models.STKPushPojo;
import com.example.meisterbot.models.STKPushResponse;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class PaymentPlanActivity extends AppCompatActivity implements View.OnClickListener {
    private Button button, paymentProceed;
    private RequestManager manager;
    private TextView txtPaymentPlan, txtExpiryDate,txtPlanOne,txtPlanTwo,txtPlanThree,txtCheckoutTill;
    private CheckBox checkPayment1,checkPayment2,checkPayment3;
    private List<STKPushPojo> pojos;
    private RelativeLayout cancel,okay;
    private TextInputLayout enterNumber;
    private int amount = 0;
    private EditText editText;
    private String number,till;
    private DBHelper helper;
    public CountDownTimer countDownTimer;

    private AlertDialog dialog;
    private AlertDialog dialog2;
    private AlertDialog dialog3;
    ProgressBar progressBar;
    private TextView txtLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_plan);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();

        manager = new RequestManager(this);

        checkPayment1.setOnClickListener(this);
        checkPayment2.setOnClickListener(this);
        checkPayment3.setOnClickListener(this);

        pojos = new ArrayList<>();

        helper = new DBHelper(this);
        till = tillNumber();

        showDialog3();


        countDownTimer = new CountDownTimer(20000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                dialog2.show();
            }

            @Override
            public void onFinish() {
                dialog2.dismiss();
                callPaymentApi(till);
            }
        };

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });

    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.checkSubPlan1){
            if (checkPayment1.isChecked()){
                checkPayment2.setChecked(false);
                checkPayment3.setChecked(false);
                txtPlanOne.setTextColor(getResources().getColor(R.color.green));
                txtPlanTwo.setTextColor(getResources().getColor(R.color.black));
                txtPlanThree.setTextColor(getResources().getColor(R.color.black));
                amount = 1;
            }
        } else if (v.getId() == R.id.checkSubPlan2) {
            if (checkPayment2.isChecked()){
                checkPayment1.setChecked(false);
                checkPayment3.setChecked(false);
                txtPlanTwo.setTextColor(getResources().getColor(R.color.green));
                txtPlanOne.setTextColor(getResources().getColor(R.color.black));
                txtPlanThree.setTextColor(getResources().getColor(R.color.black));
                amount = 95;
            }
        } else if (v.getId() == R.id.checkSubPlan3) {
            if (checkPayment3.isChecked()){
                checkPayment1.setChecked(false);
                checkPayment2.setChecked(false);
                txtPlanThree.setTextColor(getResources().getColor(R.color.green));
                txtPlanOne.setTextColor(getResources().getColor(R.color.black));
                txtPlanTwo.setTextColor(getResources().getColor(R.color.black));
                pojos.clear();
                amount = 360;
            }
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PaymentPlanActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.checkout_dialog_layout,null);
        enterNumber = dialogView.findViewById(R.id.enterSubNumber);
        txtCheckoutTill = dialogView.findViewById(R.id.txtCheckoutTill);
        cancel = dialogView.findViewById(R.id.planCancel);
        okay = dialogView.findViewById(R.id.planConfirm);
        editText = enterNumber.getEditText();
        txtCheckoutTill.setText(till);
        builder.setView(dialogView);
        dialog = builder.create();
        dialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                number = editText.getText().toString();
                STKPushPojo pojo = new STKPushPojo(number,amount,till);
                Toast.makeText(PaymentPlanActivity.this, pojo.getPhone()+ " " + pojo.getAmount()+" "+pojo.getTillNumber(), Toast.LENGTH_SHORT).show();
                manager.stkPush(listener,pojo);
                dialog.dismiss();
            }
        });


    }

    private final STKPushListener listener = new STKPushListener() {
        @Override
        public void didFetch(STKPushResponse response, String message) {
            Toast.makeText(PaymentPlanActivity.this, response.message, Toast.LENGTH_SHORT).show();
            countDownTimer.start();
        }

        @Override
        public void didError(String message) {
            Toast.makeText(PaymentPlanActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };

    private void showAlertDialog2(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_payment_proceed,null);
        paymentProceed = dialogView.findViewById(R.id.paymentProceed);
        builder.setView(dialogView);
        dialog3 = builder.create();
        dialog3.show();
        paymentProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog3.dismiss();
            }
        });
    }

    private void showDialog3(){
        AlertDialog.Builder builder = new AlertDialog.Builder(PaymentPlanActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_progress_layout,null);
        txtLoading = dialogView.findViewById(R.id.txtProgress);
        progressBar = dialogView.findViewById(R.id.myProgressBar);
        txtLoading.setText("Payment in progress, this may take a minute or so...");
        builder.setView(dialogView);
        dialog2 = builder.create();

    }

    private void callPaymentApi(String till) {
        manager.getPaymentStatus(paymentListener,till);
    }

    private final PaymentListener paymentListener = new PaymentListener() {
        @Override
        public void didFetch(Payment payment, String message) {
            Toast.makeText(PaymentPlanActivity.this, payment.data.timestamp+" "+payment.data.amount, Toast.LENGTH_SHORT).show();
            showAlertDialog2();
            setData(payment.data.amount,payment.data.timestamp);
        }

        @Override
        public void didError(String message) {
            Toast.makeText(PaymentPlanActivity.this, message, Toast.LENGTH_SHORT).show();

        }
    };

    private void setData(String amount, String timestamp) {
        txtPaymentPlan.setText(amount);
        txtExpiryDate.setText(timestamp);
    }

    public String tillNumber(){
        Intent intent = getIntent();
        Cursor cursor = helper.getUser();
        String till = "";
        if (cursor.getCount() == 0){
            till = intent.getStringExtra("till");
        }else{
            while (cursor.moveToNext()){
                till = cursor.getString(0);
            }
        }
        cursor.close();
        return till;
    }
    private void createAccount() {
        SharedPreferences sharedPreferences = getSharedPreferences("app_name", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("hasAccount", true);
        editor.apply();

        dialog.dismiss();
        // Navigate to the main screen
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void initViews() {
        button = findViewById(R.id.btnChoosePlan);
        txtPaymentPlan = findViewById(R.id.txtActivePaymentPlan);
        txtExpiryDate = findViewById(R.id.txtPlanExpiryDate);
        checkPayment1 = findViewById(R.id.checkSubPlan1);
        checkPayment2 = findViewById(R.id.checkSubPlan2);
        checkPayment3 = findViewById(R.id.checkSubPlan3);
        txtPlanOne = findViewById(R.id.txtSubPlanOne);
        txtPlanTwo = findViewById(R.id.txtSubPlanTwo);
        txtPlanThree = findViewById(R.id.txtSubPlanThree);
    }
}