package com.ujuzi.bingwasokonibot;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ujuzi.bingwasokonibot.models.RenewalPOJO;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EditRenewalActivity extends AppCompatActivity {
    private DBHelper helper;
    private String frequency,period,ussdCode,oldUssdCode,dialId,money,dateCreation,dateExpiry;
    private Button btnSave;
    private TextInputLayout enterUssdCode, enterPeriod,enterMoney;
    private Spinner spinner;
    RequestManager manager;
    private ArrayList<String> frequencies;
    private EditText one,two,three;

    private Map<Integer, Integer> simMap;
    private ArrayList<String> simNames;
    private ArrayList<Integer> slotIndex;

    private MaterialTimePicker timePicker;
    private Calendar calendar;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_renewal);

        helper = new DBHelper(this);
        initViews();

        initArrays();

        listSimInfo();

        Intent intent = getIntent();
        oldUssdCode = intent.getStringExtra("ussdCode");
        String deuce = intent.getStringExtra("money");
        String tres = intent.getStringExtra("period");
        initEditTexts();
        one.setText(oldUssdCode);
        two.setText(deuce);
        three.setText(tres);


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceed();
            }
        });

    }

    private void listSimInfo() {
        if (ActivityCompat.checkSelfPermission(EditRenewalActivity.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(EditRenewalActivity.this,new String[]{android.Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE},101);
            return;
        }
        List<SubscriptionInfo> infoList = SubscriptionManager.from(EditRenewalActivity.this).getActiveSubscriptionInfoList();
        for (SubscriptionInfo info:
                infoList) {
            simMap.put(info.getSimSlotIndex(),info.getSubscriptionId());
            String slot = String.valueOf(info.getSimSlotIndex()+1);
            simNames.add(info.getCarrierName().toString()+" SIM: "+slot);
            slotIndex.add(info.getSimSlotIndex());
        }
        ArrayAdapter adapter1 = new ArrayAdapter(EditRenewalActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,simNames);
        spinner.setAdapter(adapter1);
    }

    private void initArrays() {
        simMap = new HashMap<>();
        simNames = new ArrayList<>();
        slotIndex = new ArrayList<>();
    }

    private void proceed() {
        if (fetchData() == null){
            Toast.makeText(EditRenewalActivity.this, "Unable to create renewal", Toast.LENGTH_SHORT).show();
        }else{
            showAlertDialog();
        }
    }

    private void showAlertDialog() {
        dialog = new Dialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.renewal_dialog_layout,null);
        TextView one = dialogView.findViewById(R.id.txtDialogRenewalFrequency);
        TextView two = dialogView.findViewById(R.id.txtDialogRenewalUssdCode);
        TextView three = dialogView.findViewById(R.id.txtDialogRenewalPeriod);
        TextView four = dialogView.findViewById(R.id.txtDialogRenewalTill);
        TextView five = dialogView.findViewById(R.id.txtDialogRenewalTime);
        TextView six = dialogView.findViewById(R.id.txtDialogRenewalMoney);
        TextView seven = dialogView.findViewById(R.id.txtDialogRenewalStartDate);
        TextView eight = dialogView.findViewById(R.id.txtDialogRenewalEndDate);
        Button edit = dialogView.findViewById(R.id.btnRenewalEdit);
        Button okay = dialogView.findViewById(R.id.btnRenewalOkay);
        one.setText(fetchData().getFrequency());
        three.setText(String.valueOf(fetchData().getPeriod()));
        four.setText(fetchData().getTill());
        two.setText(fetchData().getUssdCode());
        five.setText(fetchData().getDialSimCard());
        six.setText(fetchData().getMoney());
        seven.setText(fetchData().getDateCreation());
        eight.setText(fetchData().getDateExpiry());
        dialog.setContentView(dialogView);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background));
        dialog.setCancelable(false);
        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checkInsertData = helper.updateRenewals(
                        fetchData().getFrequency(),oldUssdCode, fetchData().getUssdCode(),
                        fetchData().getPeriod(), fetchData().getTill(),
                        fetchData().getSubId(), fetchData().getDialSimCard(), fetchData().getMoney(),
                        fetchData().getDateCreation(),fetchData().getDateExpiry()
                );
                if (checkInsertData){
                    Toast.makeText(EditRenewalActivity.this, "Renewal offer updated", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(EditRenewalActivity.this, "data not inserted", Toast.LENGTH_SHORT).show();
                }
                startActivity(new Intent(EditRenewalActivity.this, HomeActivity.class));
                finish();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void initEditTexts() {
        one = enterUssdCode.getEditText();
        two = enterPeriod.getEditText();
        three = enterMoney.getEditText();
    }



    public RenewalPOJO fetchData(){
        boolean complete = true;
        period = two.getText().toString();
        ussdCode = one.getText().toString();
        money = three.getText().toString();
        if (period.isEmpty()){
            two.setError("Field cannot be empty");
            complete = false;
        }
        if (ussdCode.isEmpty()){
            one.setError("Field cannot be empty");
            complete = false;
        }
        if (money.isEmpty()){
            three.setError("Field cannot be empty");
            complete = false;
        }

        long current = getDateCreation();
        long periodMillis = Long.parseLong(period) * 24 * 60 * 60 * 1000; // Convert period from days to milliseconds
        long future = current + periodMillis;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        dateCreation = sdf.format(current);
        dateExpiry = sdf.format(future);

        if (complete){
            int per = Integer.parseInt(period);
            return new RenewalPOJO("Daily",ussdCode,per,tillNumber(),dialId,getDialSimCard(),money,dateCreation,dateExpiry);
        }else{
            return null;
        }

    }
    private long getDateCreation(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        calendar.add(Calendar.DAY_OF_MONTH, 1);

        return calendar.getTimeInMillis();
    }


    public String getDialSimCard(){
        String dialSim1;
        dialSim1 = spinner.getSelectedItem().toString();
        if (dialSim1.contains("SIM: 1")){
            dialId = String.valueOf(simMap.get(0));
        }else if(dialSim1.contains("SIM: 2")){
            dialId = String.valueOf(simMap.get(1));
        }
        return dialSim1;
    }



    public String tillNumber(){
        Cursor cursor = helper.getUser();
        String till = "";
        if (cursor.getCount() == 0){
            Toast.makeText(this, "till", Toast.LENGTH_SHORT).show();
        }else{
            while (cursor.moveToNext()){
                till = cursor.getString(0);
            }
        }
        cursor.close();
        return till;
    }
    private void initViews() {
        btnSave = findViewById(R.id.btnSaveRenewalEdit);
        enterPeriod = findViewById(R.id.edtEnterPeriodEdit);
        enterUssdCode = findViewById(R.id.edtUssdRenewalEdit);
        enterMoney = findViewById(R.id.edtMoneyRenewalEdit);
        spinner = findViewById(R.id.frequencySpinnerEdit);
    }
}