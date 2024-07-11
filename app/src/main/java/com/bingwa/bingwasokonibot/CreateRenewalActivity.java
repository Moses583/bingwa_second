package com.bingwa.bingwasokonibot;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bingwa.bingwasokonibot.models.RenewalPOJO;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CreateRenewalActivity extends AppCompatActivity {

    private DBHelper helper;
    private String frequency,period,ussdCode,dialId,money,dateCreation,dateExpiry;
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

        helper = new DBHelper(this);
        initViews();
        initEditTexts();
        initArrays();

        listSimInfo();


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceed();
            }
        });

    }

    private void listSimInfo() {
        if (ActivityCompat.checkSelfPermission(CreateRenewalActivity.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CreateRenewalActivity.this,new String[]{android.Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE},101);
            return;
        }
        List<SubscriptionInfo> infoList = SubscriptionManager.from(CreateRenewalActivity.this).getActiveSubscriptionInfoList();
        for (SubscriptionInfo info:
                infoList) {
            simMap.put(info.getSimSlotIndex(),info.getSubscriptionId());
            String slot = String.valueOf(info.getSimSlotIndex()+1);
            simNames.add(info.getCarrierName().toString()+" SIM: "+slot);
            slotIndex.add(info.getSimSlotIndex());
        }
        ArrayAdapter adapter1 = new ArrayAdapter(CreateRenewalActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,simNames);
        spinner.setAdapter(adapter1);
    }

    private void initArrays() {
        simMap = new HashMap<>();
        simNames = new ArrayList<>();
        slotIndex = new ArrayList<>();
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
        TextView two = dialogView.findViewById(R.id.txtDialogRenewalUssdCode);
        TextView three = dialogView.findViewById(R.id.txtDialogRenewalPeriod);
        TextView four = dialogView.findViewById(R.id.txtDialogRenewalTill);
        TextView five = dialogView.findViewById(R.id.txtDialogRenewalTime);
        TextView six = dialogView.findViewById(R.id.txtDialogRenewalMoney);
        TextView seven = dialogView.findViewById(R.id.txtDialogRenewalStartDate);
        TextView eight = dialogView.findViewById(R.id.txtDialogRenewalEndDate);
        one.setText(fetchData().getFrequency());
        three.setText(String.valueOf(fetchData().getPeriod()));
        four.setText(fetchData().getTill());
        two.setText(fetchData().getUssdCode());
        five.setText(fetchData().getDialSimCard());
        six.setText(fetchData().getMoney());
        seven.setText(fetchData().getDateCreation());
        eight.setText(fetchData().getDateExpiry());
        builder.setView(dialogView);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean checkInsertData = helper.insertRenewals(
                        fetchData().getFrequency(), fetchData().getUssdCode(),
                        fetchData().getPeriod(), fetchData().getTill(),
                        fetchData().getSubId(), fetchData().getDialSimCard(), fetchData().getMoney(),
                        fetchData().getDateCreation(),fetchData().getDateExpiry()
                );
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
        btnSave = findViewById(R.id.btnSaveRenewal);
        enterPeriod = findViewById(R.id.edtEnterPeriod);
        enterUssdCode = findViewById(R.id.edtUssdRenewal);
        enterMoney = findViewById(R.id.edtMoneyRenewal);
        spinner = findViewById(R.id.frequencySpinner);
    }
}