package com.ujuzi.moses;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ujuzi.moses.models.Persona;
import com.google.android.material.textfield.TextInputLayout;

public class CreateAccountActivity extends AppCompatActivity {

    private TextInputLayout enterPhoneNumber,enterTillNumber,enterStoreName;
    private EditText two,three,four;
    private Button createPersona;
    private TextView goToLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        initViews();
        initEditTexts();

        createPersona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceed();
            }
        });

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateAccountActivity.this, LoginActivity.class));
            }
        });


    }
    private void initEditTexts(){
        two = enterPhoneNumber.getEditText();
        three = enterTillNumber.getEditText();
        four = enterStoreName.getEditText();
    }

    public void proceed(){
        if (fetchDetails() == null){
            Toast.makeText(CreateAccountActivity.this, "Unable to continue", Toast.LENGTH_SHORT).show();
        }else{
            goToPasswordActivity(fetchDetails());
        }
    }

    private void goToPasswordActivity(Persona persona){
        String deviceId = persona.getDeviceId();
        String number = persona.getPhoneNumber();
        String till = persona.getTillNumber();
        String storeName = persona.getStoreName();
        Intent intent = new Intent(CreateAccountActivity.this, CreatePasswordActivity.class);
        intent.putExtra("name",storeName);
        intent.putExtra("number",number);
        intent.putExtra("storeName",storeName);
        intent.putExtra("till",till);
        intent.putExtra("deviceId",deviceId);
        startActivity(intent);

    }

    private Persona fetchDetails(){
        boolean complete = true;
        String brand = Build.BRAND;
        String model = Build.MODEL;
        String product = Build.PRODUCT;
        String Device = Build.DEVICE;
        String manufacturer = Build.MANUFACTURER;
        String deviceId = brand+" "+model+" "+product+" "+Device+" "+manufacturer;
        String number = two.getText().toString();
        String till = three.getText().toString();
        String storeName = four.getText().toString();

        if (number.isEmpty()){
            enterPhoneNumber.setError("Phone number cannot be empty");
            complete = false;
        }
        if (till.isEmpty()){
            enterTillNumber.setError("Till number cannot be empty");
            complete = false;
        }
        if (storeName.isEmpty()){
            enterStoreName.setError("Store name cannot be empty");
            complete = false;
        }
        if (complete){
            enterPhoneNumber.setErrorEnabled(false);
            enterTillNumber.setErrorEnabled(false);
            enterStoreName.setErrorEnabled(false);
            return new Persona(storeName,number,till,storeName,deviceId,"null");
        }
        else {
            return null;
        }
    }


    private void initViews() {
        enterPhoneNumber = findViewById(R.id.enterPhoneNumber1);
        enterTillNumber = findViewById(R.id.enterTillNumber1);
        enterStoreName = findViewById(R.id.enterStoreNumber1);
        createPersona = findViewById(R.id.btnCreatePersona1);
        goToLogin = findViewById(R.id.txtGoToLogin);
    }
}