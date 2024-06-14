package com.example.meisterbot;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.meisterbot.models.Persona;
import com.google.android.material.textfield.TextInputLayout;

public class CreateAccountActivity extends AppCompatActivity {

    private TextInputLayout enterName,enterPhoneNumber,enterTillNumber,enterStoreName,enterWebUrl;
    private EditText one,two,three,four,five;
    private Button createPersona;
    private TextView goToLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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
//        one = enterName.getEditText();
        two = enterPhoneNumber.getEditText();
        three = enterTillNumber.getEditText();
        four = enterStoreName.getEditText();
        five = enterWebUrl.getEditText();
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
        String name = persona.getName();
        String number = persona.getPhoneNumber();
        String till = persona.getTillNumber();
        String web = persona.getBingwaSite();
        String password = persona.getPassWord();
        String storeName = persona.getStoreName();
        Intent intent = new Intent(CreateAccountActivity.this, CreatePasswordActivity.class);
        intent.putExtra("name",storeName);
        intent.putExtra("number",number);
        intent.putExtra("storeName",storeName);
        intent.putExtra("till",till);
        intent.putExtra("deviceId",deviceId);
        intent.putExtra("web",web);
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
//        String name = one.getText().toString();
        String number = two.getText().toString();
        String till = three.getText().toString();
        String storeName = four.getText().toString();
        String webUrl = five.getText().toString();

//        if (name.isEmpty()){
//            enterName.setError("Name cannot be empty");
//            complete = false;
//        }
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
        if (webUrl.isEmpty()){
            enterWebUrl.setError("Web url cannot be empty");
            complete = false;
        }
        if (complete){
//            enterName.setErrorEnabled(false);
            enterPhoneNumber.setErrorEnabled(false);
            enterTillNumber.setErrorEnabled(false);
            enterStoreName.setErrorEnabled(false);
            enterWebUrl.setErrorEnabled(false);
            return new Persona(storeName,storeName,webUrl,deviceId,number,till,"null");
        }
        else {
            return null;
        }
    }


    private void initViews() {
//        enterName = findViewById(R.id.enterName1);
        enterPhoneNumber = findViewById(R.id.enterPhoneNumber1);
        enterTillNumber = findViewById(R.id.enterTillNumber1);
        enterStoreName = findViewById(R.id.enterStoreNumber1);
        enterWebUrl = findViewById(R.id.enterWebsiteUrl1);
        createPersona = findViewById(R.id.btnCreatePersona1);
        goToLogin = findViewById(R.id.txtGoToLogin);
    }
}