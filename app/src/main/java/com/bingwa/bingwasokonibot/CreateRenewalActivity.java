package com.bingwa.bingwasokonibot;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bingwa.bingwasokonibot.models.OfferPOJO;
import com.bingwa.bingwasokonibot.models.RenewalPOJO;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.ArrayList;
import java.util.Map;

public class CreateRenewalActivity extends AppCompatActivity {

    private DBHelper helper;
    private String frequency,period,ussdCode;
    private Button btnSave,btnSelectTime;
    private TextInputLayout enterUssdCode, enterPeriod;
    private Spinner spinner;
    RequestManager manager;
    private ArrayList<String> frequencies;
    private EditText one,two;
    private TextView txtTime;

    private MaterialTimePicker timePicker;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_renewal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
        loadFrequencies();
        initEditTexts();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceed();
            }
        });
        btnSelectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRenewalTime();
            }
        });
    }

    private void getRenewalTime() {
        timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Set auto renewal")
                .build();
        timePicker.show(getSupportFragmentManager(), "Alarm Manager");
        timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timePicker.getHour()>12){
                    txtTime.setText(String.format("%02d",(timePicker.getHour()-12)) +":"+ String.format("%02d", timePicker.getMinute())+"PM");
                }
                else{
                    txtTime.setText(timePicker.getHour()+":" + timePicker.getMinute()+ "AM");
                }
                calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                calendar.set(Calendar.MINUTE, timePicker.getMinute());
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
            }
        });
    }

    private void proceed() {
        if (fetchData() == null){
            Toast.makeText(CreateRenewalActivity.this, "Unable to create renewal", Toast.LENGTH_SHORT).show();
        }else{
            showAlertDialog();
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateRenewalActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.renewal_dialog_layout,null);
        TextView one = dialogView.findViewById(R.id.txtDialogRenewalFrequency);
        TextView two = dialogView.findViewById(R.id.txtDialogRenewalPeriod);
        TextView three = dialogView.findViewById(R.id.txtDialogRenewalUssdCode);
        TextView four = dialogView.findViewById(R.id.txtDialogRenewalTill);
        TextView five = dialogView.findViewById(R.id.txtDialogRenewalTime);
        one.setText(fetchData().getFrequency());
        three.setText(fetchData().getUssdCode());
        four.setText(fetchData().getTill());
        two.setText(fetchData().getPeriod());
        five.setText(fetchData().getTime());
        builder.setView(dialogView);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean checkInsertData = helper.insertRenewals(fetchData().getFrequency(),fetchData().getUssdCode(),fetchData().getPeriod(), fetchData().getTill(), fetchData().getTime());
                if (checkInsertData){
                    Toast.makeText(CreateRenewalActivity.this, "data inserted", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(CreateRenewalActivity.this, "data not inserted", Toast.LENGTH_SHORT).show();
                }
                startActivity(new Intent(CreateRenewalActivity.this, HomeActivity.class));
                finish();
            }
        });
        builder.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void initEditTexts() {
        one = enterPeriod.getEditText();
        two = enterUssdCode.getEditText();
    }

    private void loadFrequencies(){
        helper = new DBHelper(this);
        frequencies = new ArrayList<>();
        frequencies.add("Daily");
        frequencies.add("Weekly");
        frequencies.add("Monthly");
        ArrayAdapter adapter1 = new ArrayAdapter(CreateRenewalActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,frequencies);
        spinner.setAdapter(adapter1);
    }

    public RenewalPOJO fetchData(){
        boolean complete = true;
        period = one.getText().toString();
        ussdCode = two.getText().toString();
        if (period.isEmpty()){
            one.setError("Field cannot be empty");
            complete = false;
        }
        if (ussdCode.isEmpty()){
            two.setError("Field cannot be empty");
            complete = false;
        }
        if (complete){
            return new RenewalPOJO(getFrequency(),period,ussdCode,tillNumber(),getTheTime());
        }else{
            return null;
        }

    }

    private String getFrequency() {
        frequency = spinner.getSelectedItem().toString();
        return frequency;
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
    private String getTheTime(){
       return txtTime.getText().toString();
    }
    private void initViews() {
        btnSave = findViewById(R.id.btnSaveRenewal);
        btnSelectTime = findViewById(R.id.btnSelectTime);
        txtTime = findViewById(R.id.txtRenewalTime);
        enterPeriod = findViewById(R.id.edtEnterPeriod);
        enterUssdCode = findViewById(R.id.edtUssdRenewal);
        spinner = findViewById(R.id.frequencySpinner);
    }
}