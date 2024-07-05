package com.bingwa.bingwasokonibot;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
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


import com.bingwa.bingwasokonibot.listeners.GetTariffsListener;
import com.bingwa.bingwasokonibot.listeners.PaymentListener;
import com.bingwa.bingwasokonibot.listeners.STKPushListener;
import com.bingwa.bingwasokonibot.models.Payment;
import com.bingwa.bingwasokonibot.models.STKPushPojo;
import com.bingwa.bingwasokonibot.models.STKPushResponse;
import com.bingwa.bingwasokonibot.models.TariffApiResponse;
import com.google.android.material.textfield.TextInputLayout;

public class PaymentPlanActivity extends AppCompatActivity implements View.OnClickListener {
    private Button button, paymentProceed;
    private RequestManager manager;
    private TextView txtPaymentPlan, txtExpiryDate,txtPlanOne,txtPlanTwo,txtPlanThree,txtCheckoutTill,tariffNameOne,tariffNameTwo,tariffNameThree;
    private CheckBox checkPayment1,checkPayment2,checkPayment3;
    private RelativeLayout cancel,okay;
    private TextInputLayout enterNumber;
    private int amount1 = 0;
    private int amount2 = 0;
    private int amount3 = 0;
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

    private ProgressDialog progressDialog;



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

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Please wait");
        progressDialog.show();


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


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.checkSubPlan1){
            if (checkPayment1.isChecked()){
                checkPayment2.setChecked(false);
                checkPayment3.setChecked(false);
                txtPlanOne.setTextColor(getResources().getColor(R.color.green));
                txtPlanTwo.setTextColor(getResources().getColor(R.color.black));
                txtPlanThree.setTextColor(getResources().getColor(R.color.black));
                amount = amount1;

            }
        } else if (v.getId() == R.id.checkSubPlan2) {
            if (checkPayment2.isChecked()){
                checkPayment1.setChecked(false);
                checkPayment3.setChecked(false);
                txtPlanTwo.setTextColor(getResources().getColor(R.color.green));
                txtPlanOne.setTextColor(getResources().getColor(R.color.black));
                txtPlanThree.setTextColor(getResources().getColor(R.color.black));
                amount = amount2;
            }
        } else if (v.getId() == R.id.checkSubPlan3) {
            if (checkPayment3.isChecked()){
                checkPayment1.setChecked(false);
                checkPayment2.setChecked(false);
                txtPlanThree.setTextColor(getResources().getColor(R.color.green));
                txtPlanOne.setTextColor(getResources().getColor(R.color.black));
                txtPlanTwo.setTextColor(getResources().getColor(R.color.black));
                amount = amount3;
            }
        }
    }

    private void callTariffsApi() {
        manager.callTariffsApi(tariffsListener);
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
                if (amount == 0) {
                    STKPushPojo pojo = new STKPushPojo(number,1,till);
                    manager.stkPush(listener,pojo);
                    dialog.dismiss();
                    return;
                }
                STKPushPojo pojo = new STKPushPojo(number,amount,till);
                manager.stkPush(listener,pojo);
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
                startActivity(new Intent(PaymentPlanActivity.this, HomeActivity.class));
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
        manager.getPaymentStatus(paymentListener2,till);
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