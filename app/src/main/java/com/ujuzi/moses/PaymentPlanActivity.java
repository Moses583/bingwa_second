package com.ujuzi.moses;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.ujuzi.moses.listeners.GetTariffsListener;
import com.ujuzi.moses.listeners.PaymentListener;
import com.ujuzi.moses.listeners.STKPushListener;
import com.ujuzi.moses.models.Payment;
import com.ujuzi.moses.models.STKPushPojo;
import com.ujuzi.moses.models.STKPushResponse;
import com.ujuzi.moses.models.TariffApiResponse;
import com.google.android.material.textfield.TextInputLayout;

public class PaymentPlanActivity extends AppCompatActivity implements View.OnClickListener {
    private Button button, paymentProceed;
    private RequestManager manager;
    private TextView txtPaymentPlan, txtExpiryDate,txtPlanOne,txtPlanTwo,txtPlanThree,txtCheckoutTill,tariffNameOne,tariffNameTwo,tariffNameThree;
    private CheckBox checkPayment1,checkPayment2,checkPayment3;
    private Button cancel,okay;
    private TextInputLayout enterNumber;
    private int amount1 = 0;
    private int amount2 = 0;
    private int amount3 = 0;
    private int amount = 0;
    private EditText editText;
    private String number,till;
    private DBHelper helper,helper2;
    public CountDownTimer countDownTimer;

    private Dialog dialog;
    private Dialog dialog2;
    private Dialog dialog3;
    ProgressBar progressBar,progressBar2;
    private TextView txtLoading,txtLoading2;

    private Dialog progressDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_plan);
        initViews();

        manager = new RequestManager(this);
        showLoadingDialog();


        helper = new DBHelper(this);
        till = tillNumber();

        checkPayment1.setOnClickListener(this);
        checkPayment2.setOnClickListener(this);
        checkPayment3.setOnClickListener(this);

        callPaymentApi2(till);
        callTariffsApi();

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
    private void showLoadingDialog(){
        progressDialog = new Dialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_progress_layout,null);
        progressBar2 = view.findViewById(R.id.myProgressBar);
        txtLoading2 = view.findViewById(R.id.txtProgress);
        progressDialog.setContentView(view);
        int widthInDp = 250;

        final float scale = getResources().getDisplayMetrics().density;
        int widthInPx = (int) (widthInDp * scale + 0.5f);

        progressDialog.getWindow().setLayout(widthInPx, ViewGroup.LayoutParams.WRAP_CONTENT);
        progressDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background));
        progressDialog.setCancelable(false);
        txtLoading2.setText("Loading...");
        progressDialog.show();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.checkSubPlan1){
            if (checkPayment1.isChecked()){
                checkPayment2.setChecked(false);
                checkPayment3.setChecked(false);
                txtPlanOne.setTextColor(getResources().getColor(R.color.green2));
                txtPlanTwo.setTextColor(getResources().getColor(R.color.black));
                txtPlanThree.setTextColor(getResources().getColor(R.color.black));
                amount = amount1;
            }
        } else if (v.getId() == R.id.checkSubPlan2) {
            if (checkPayment2.isChecked()){
                checkPayment1.setChecked(false);
                checkPayment3.setChecked(false);
                txtPlanTwo.setTextColor(getResources().getColor(R.color.green2));
                txtPlanOne.setTextColor(getResources().getColor(R.color.black));
                txtPlanThree.setTextColor(getResources().getColor(R.color.black));
                amount = amount2;
            }
        } else if (v.getId() == R.id.checkSubPlan3) {
            if (checkPayment3.isChecked()){
                checkPayment1.setChecked(false);
                checkPayment2.setChecked(false);
                txtPlanThree.setTextColor(getResources().getColor(R.color.green2));
                txtPlanOne.setTextColor(getResources().getColor(R.color.black));
                txtPlanTwo.setTextColor(getResources().getColor(R.color.black));
                amount = amount3;
            }
        }
    }

    private void callTariffsApi() {
        manager.callTariffsApi(tariffsListener,token());
    }

    private final GetTariffsListener tariffsListener = new GetTariffsListener() {
        @Override
        public void didFetch(TariffApiResponse response, String message) {
            progressDialog.cancel();
            showTariffs(response);

        }

        @Override
        public void didError(String message) {
            progressDialog.cancel();
            if (message.contains("Unable to resolve host")){
                Toast.makeText(PaymentPlanActivity.this, "Please connect to the internet", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void showTariffs(TariffApiResponse response) {
        amount1 = response.tariff_1.amount;
        amount2 = response.tariff_2.amount;
        amount3 = response.tariff_3.amount;
        tariffNameOne.setText(response.tariff_1.name);
        tariffNameTwo.setText(response.tariff_2.name);
        tariffNameThree.setText(response.tariff_3.name);

        txtPlanOne.setText("KES"+response.tariff_1.amount+" for "+response.tariff_1.days+" days");
        txtPlanTwo.setText("KES"+response.tariff_2.amount+" for "+response.tariff_2.days+" days");
        txtPlanThree.setText("KES"+response.tariff_3.amount+" for "+response.tariff_3.days+" days");
    }


    private void showAlertDialog() {
        dialog = new Dialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.checkout_dialog_layout,null);
        enterNumber = view.findViewById(R.id.enterSubNumber);
        txtCheckoutTill = view.findViewById(R.id.txtCheckoutTill);
        cancel = view.findViewById(R.id.planCancel);
        okay = view.findViewById(R.id.planConfirm);
        editText = enterNumber.getEditText();
        txtCheckoutTill.setText(till);
        dialog.setContentView(view);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background));
        dialog.setCancelable(false);
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
                if (amount == 0) {
                    STKPushPojo pojo = new STKPushPojo(number,1,till);
                    manager.stkPush(listener,pojo,token());
                    dialog.dismiss();
                    return;
                }
                STKPushPojo pojo = new STKPushPojo(number,amount,till);
                manager.stkPush(listener,pojo,token());
                dialog.dismiss();
            }
        });


    }

    private final STKPushListener listener = new STKPushListener() {
        @Override
        public void didFetch(STKPushResponse response, String message) {
            Log.d("TAG","stkpush initiated successfully");
            countDownTimer.start();
        }

        @Override
        public void didError(String message) {
            if (message.contains("Unable to resolve host")){
                Toast.makeText(PaymentPlanActivity.this, "Please connect to the internet", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void showAlertDialog2(){
        dialog3 = new Dialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_payment_proceed,null);
        paymentProceed = view.findViewById(R.id.paymentProceed);
        dialog3.setContentView(view);
        dialog3.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog3.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background));
        dialog3.setCancelable(false);
        dialog3.show();
        paymentProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PaymentPlanActivity.this, HomeActivity.class));
            }
        });
    }

    private void showDialog3(){
        dialog2 = new Dialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_progress_layout,null);
        progressBar = view.findViewById(R.id.myProgressBar);
        txtLoading = view.findViewById(R.id.txtProgress);
        dialog2.setContentView(view);
        int widthInDp = 250;

        final float scale = getResources().getDisplayMetrics().density;
        int widthInPx = (int) (widthInDp * scale + 0.5f);

        dialog2.getWindow().setLayout(widthInPx, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog2.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background));
        dialog2.setCancelable(false);
        txtLoading.setText("Payment in progress...");
    }

    private void callPaymentApi(String till) {
        manager.getPaymentStatus(paymentListener,till,token());
    }

    private final PaymentListener paymentListener = new PaymentListener() {
        @Override
        public void didFetch(Payment payment, String message) {
            showAlertDialog2();
        }

        @Override
        public void didError(String message) {
            if (message.contains("Unable to resolve host")){
                Toast.makeText(PaymentPlanActivity.this, "Please connect to the internet", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void callPaymentApi2(String till) {
        manager.getPaymentStatus(paymentListener2,till,token());
    }

    private final PaymentListener paymentListener2 = new PaymentListener() {
        @Override
        public void didFetch(Payment payment, String message) {
            if (payment.message.contains("Your subscription ended")){
                txtPaymentPlan.setText(payment.data.amount);
                txtExpiryDate.setText(payment.data.timestamp+" (expired)");
            }else if(payment.message.contains("You have never paid")){
                txtPaymentPlan.setText("N/A");
                txtExpiryDate.setText("N/A");
            }else{
                setData(payment.data.amount,payment.data.timestamp);
            }

        }

        @Override
        public void didError(String message) {
            if (message.contains("Unable to resolve host")){
                Toast.makeText(PaymentPlanActivity.this, "Please connect to the internet", Toast.LENGTH_SHORT).show();
            }

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
        button = findViewById(R.id.btnChoosePlan);
        txtPaymentPlan = findViewById(R.id.txtActivePaymentPlan);
        txtExpiryDate = findViewById(R.id.txtPlanExpiryDate);
        checkPayment1 = findViewById(R.id.checkSubPlan1);
        checkPayment2 = findViewById(R.id.checkSubPlan2);
        checkPayment3 = findViewById(R.id.checkSubPlan3);
        txtPlanOne = findViewById(R.id.txtSubPlanOne);
        txtPlanTwo = findViewById(R.id.txtSubPlanTwo);
        txtPlanThree = findViewById(R.id.txtSubPlanThree);
        tariffNameOne = findViewById(R.id.tariffNameOne);
        tariffNameTwo = findViewById(R.id.tariffNameTwo);
        tariffNameThree = findViewById(R.id.tariffNameThree);
    }

}