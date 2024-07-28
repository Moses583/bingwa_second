package com.ujuzi.bingwasokonibot;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
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

import com.ujuzi.bingwasokonibot.listeners.PostOfferListener;
import com.ujuzi.bingwasokonibot.models.OfferPOJO;
import com.ujuzi.bingwasokonibot.models.PostOfferApiResponse;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateOfferActivity extends AppCompatActivity {
    private String name,amount,ussdCode,dialId,paymentId,offerTill;
    private Button txtHelp,txtSave;
    private TextInputLayout enterName, enterAmount,enterUssdCode, enterTill;
    private Spinner spinner1,spinner2;
    RequestManager manager;
    DBHelper helper;
    private Map<Integer, Integer> simMap;
    private ArrayList<String> simNames;
    private ArrayList<Integer> slotIndex;
    private EditText one,two,three,four;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_offer);
        initViews();

        one = enterAmount.getEditText();
        two = enterUssdCode.getEditText();
        three = enterTill.getEditText();
        four = enterName.getEditText();

        simMap = new HashMap<>();
        simNames = new ArrayList<>();
        slotIndex = new ArrayList<>();

        manager = new RequestManager(this);
        helper = new DBHelper(this);

        three.setText(tillNumber());

        listSimInfo();


        txtHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateOfferActivity.this,HelpActivity.class));
            }
        });
        txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceed();
            }
        });
    }
    public void proceed(){
        if (fetchData() == null){
            Toast.makeText(CreateOfferActivity.this, "Unable to create offer", Toast.LENGTH_SHORT).show();
        }else{
            showAlertDialog();
        }
    }
    private void showAlertDialog() {
        dialog = new Dialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.offer_dialog_layout,null);
        TextView one = dialogView.findViewById(R.id.txtDialogAmount);
        TextView three = dialogView.findViewById(R.id.txtDialogUssdCode);
        TextView four = dialogView.findViewById(R.id.txtDialogDialSim);
        TextView two = dialogView.findViewById(R.id.txtDialogPaySim);
        TextView five = dialogView.findViewById(R.id.txtDialogDialogTill);
        TextView six = dialogView.findViewById(R.id.txtDialogName);
        Button edit = dialogView.findViewById(R.id.btnOfferEdit);
        Button okay = dialogView.findViewById(R.id.btnOfferOkay);
        one.setText(fetchData().getAmount());
        three.setText(fetchData().getUssd());
        four.setText(fetchData().getDialSim());
        two.setText(fetchData().getPaymentSim());
        five.setText(fetchData().getOfferTill());
        six.setText(fetchData().getName());
        dialog.setContentView(dialogView);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background));
        dialog.setCancelable(false);
        dialog.show();

        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checkInsertData = helper.insertOffer(
                        fetchData().getName(),
                        fetchData().getAmount(),
                        fetchData().getUssd(),
                        fetchData().getDialSim(),
                        fetchData().getSubscriptionId(),
                        fetchData().getPaymentSim(),
                        fetchData().getPaymentSimId(),
                        fetchData().getOfferTill());
                if (checkInsertData){
                    Toast.makeText(CreateOfferActivity.this, "Offer created successfully.", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(CreateOfferActivity.this, "data not inserted", Toast.LENGTH_SHORT).show();
                }
                startActivity(new Intent(CreateOfferActivity.this, HomeActivity.class));
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
    public OfferPOJO fetchData(){
        boolean complete = true;
        amount = one.getText().toString();
        ussdCode = two.getText().toString();
        offerTill = three.getText().toString();
        name = four.getText().toString();

        if (name.isEmpty()){
            four.setError("Field cannot be empty");
            complete = false;
        }

        if (amount.isEmpty()){
            one.setError("Field cannot be empty");
            complete = false;
        }

        if (ussdCode.isEmpty()){
            two.setError("Field cannot be empty");
            complete = false;
        }
        if (offerTill.isEmpty()){
            three.setError("Field cannot be empty");
            complete = false;
        }
        if (complete){
            String deviceId = Build.ID;
            return new OfferPOJO(name,amount,ussdCode,getDialSimCard(),deviceId,dialId,getPaymentSimCard(),paymentId,offerTill);
        }else{
            return null;
        }

    }
    private PostOfferListener listener = new PostOfferListener() {
        @Override
        public void didFetch(PostOfferApiResponse response, String message) {
            Toast.makeText(CreateOfferActivity.this, response.message, Toast.LENGTH_SHORT).show();

        }

        @Override
        public void didError(String message) {
            Toast.makeText(CreateOfferActivity.this, message+" from didError", Toast.LENGTH_SHORT).show();
        }
    };
    public void listSimInfo(){
        if (ActivityCompat.checkSelfPermission(CreateOfferActivity.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CreateOfferActivity.this,new String[]{android.Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE},101);
            return;
        }
        List<SubscriptionInfo> infoList = SubscriptionManager.from(CreateOfferActivity.this).getActiveSubscriptionInfoList();
        for (SubscriptionInfo info:
                infoList) {
            simMap.put(info.getSimSlotIndex(),info.getSubscriptionId());
            String slot = String.valueOf(info.getSimSlotIndex()+1);
            simNames.add(info.getCarrierName().toString()+" SIM: "+slot);
            slotIndex.add(info.getSimSlotIndex());
        }
        ArrayAdapter adapter1 = new ArrayAdapter(CreateOfferActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,simNames);
        spinner2.setAdapter(adapter1);
        spinner1.setAdapter(adapter1);
    }

    public String getPaymentSimCard(){
        String dialSim1;
        dialSim1 = spinner1.getSelectedItem().toString();
        if (dialSim1.contains("SIM: 1")){
            paymentId = String.valueOf(simMap.get(0));
        }else if(dialSim1.contains("SIM: 2")){
            paymentId = String.valueOf(simMap.get(1));
        }
        return dialSim1;
    }
    public String getDialSimCard(){
        String dialSim1;
        dialSim1 = spinner2.getSelectedItem().toString();
        if (dialSim1.contains("SIM: 1")){
            dialId = String.valueOf(simMap.get(0));
        }else if(dialSim1.contains("SIM: 2")){
            dialId = String.valueOf(simMap.get(1));
        }
        return dialSim1;
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
        txtHelp = findViewById(R.id.txtHelp);
        txtSave = findViewById(R.id.txtSave);
        enterAmount = findViewById(R.id.edtLayoutEnterAmount);
        enterUssdCode = findViewById(R.id.edtLayoutEnterCode);
        spinner1 = findViewById(R.id.spinner1);
        spinner2 = findViewById(R.id.spinner2);
        enterTill = findViewById(R.id.edtLayoutTillOffer);
        enterName = findViewById(R.id.edtLayoutEnterName);
    }
}