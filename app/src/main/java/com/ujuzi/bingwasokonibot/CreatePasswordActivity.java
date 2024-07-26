package com.ujuzi.bingwasokonibot;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ujuzi.bingwasokonibot.listeners.PostPersonaListener;
import com.ujuzi.bingwasokonibot.models.PostPersonaApiResponse;
import com.ujuzi.bingwasokonibot.models.Persona;
import com.google.android.material.textfield.TextInputLayout;

public class CreatePasswordActivity extends AppCompatActivity {
    private TextInputLayout edtTxtEnterPassword, edtTxtConfirmPassword;
    private Button btnContinue,btnBack;
    private EditText one,two;
    private TextView txtLoading;
    ProgressBar progressBar;
    DBHelper dbHelper;

    RequestManager manager;
    private Dialog dialog,dialog2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_password);
        initViews();

        manager = new RequestManager(this);

        one = edtTxtEnterPassword.getEditText();
        two = edtTxtConfirmPassword.getEditText();

        showDialog3();

        dbHelper = new DBHelper(this);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchPassword();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreatePasswordActivity.super.onBackPressed();
            }
        });
    }

    private void showDialog3() {
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
    }


    private void fetchPassword(){
        String passOne = one.getText().toString();
        String passTwo = two.getText().toString();
        if (checkEmpty(passOne,passTwo)){
            dialog2.dismiss();
            edtTxtConfirmPassword.setHelperTextEnabled(true);
            edtTxtConfirmPassword.setHelperText("Fields cannot be empty!!");
        }
        else{
            if (validatePassword(passOne,passTwo)){
                edtTxtEnterPassword.setHelperTextEnabled(false);
                edtTxtConfirmPassword.setHelperTextEnabled(false);
                recreatePersona(passTwo);
            }
            else {
                dialog2.dismiss();
                edtTxtConfirmPassword.setHelperTextEnabled(true);
                edtTxtConfirmPassword.setHelperText("Passwords do not match!!");
            }
        }
    }
    private boolean checkEmpty(String passOne, String passTwo){
        return passOne.isEmpty() || passTwo.isEmpty();
    }
    private boolean validatePassword(String passOne,String passTwo){
        return passOne.equals(passTwo);
    }
    private void recreatePersona(String password) {
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String till = intent.getStringExtra("till");
        String storeName = intent.getStringExtra("storeName");
        String number = intent.getStringExtra("number");
        String deviceId = intent.getStringExtra("deviceId");
        boolean insert = dbHelper.insertUser(till);
        if (insert){
            Log.d("TAG","user added successfully");
        }else{
            Log.d("TAG", "user not added successfully");
        }
        boolean insertStoreName = dbHelper.insertStoreName(storeName);
        if (insertStoreName){
            Log.d("TAG","Store added successfully");
        }else{
            Log.d("TAG", "Store not added successfully");
        }
        Persona persona = new Persona(name,number,till,storeName,deviceId,password);
        showAlertDialog(persona);
    }
    public void showAlertDialog(Persona persona){
        dialog = new Dialog(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.persona_dialog_layout,null);
        TextView two = dialogView.findViewById(R.id.personaNumber);
        TextView three = dialogView.findViewById(R.id.txtPersonaTill);
        TextView four = dialogView.findViewById(R.id.txtPersonaStoreName);
        TextView six = dialogView.findViewById(R.id.PersonaPassword);
        Button edit = dialogView.findViewById(R.id.btnPersonaEdit);
        Button okay = dialogView.findViewById(R.id.btnPersonaOkay);
        two.setText(persona.getPhoneNumber());
        three.setText(persona.getTillNumber());
        four.setText(persona.getStoreName());
        six.setText(persona.getPassWord());
        dialog.setContentView(dialogView);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background));
        dialog.setCancelable(false);
        dialog.show();

        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtLoading.setText("Creating your account...");
                dialog.dismiss();
                dialog2.show();
                manager.postPersona(listener,persona);
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private final PostPersonaListener listener = new PostPersonaListener() {
        @Override
        public void didFetch(PostPersonaApiResponse response, String message) {
            edtTxtConfirmPassword.setHelperTextEnabled(false);
            completeLogin(response);
        }

        @Override
        public void didError(String message) {
            if (message.contains("Unable to resolve host")){
                dialog2.dismiss();
                edtTxtConfirmPassword.setHelperTextEnabled(true);
                edtTxtConfirmPassword.setHelperText("Please connect to the internet");
            }
            else{
                dialog2.dismiss();
                edtTxtConfirmPassword.setHelperTextEnabled(true);
                edtTxtConfirmPassword.setHelperText(message);
            }

        }
    };

    private void completeLogin(PostPersonaApiResponse response) {
        if (response.message1.equals("Successful account creation")){
            dialog2.dismiss();
            edtTxtConfirmPassword.setHelperTextEnabled(false);
            insertLink(response);
            insertToken(response);
        }else{
            dialog2.dismiss();
            edtTxtConfirmPassword.setHelperTextEnabled(true);
            edtTxtConfirmPassword.setHelperText(response.message1);
        }
    }
    public void insertLink(PostPersonaApiResponse response){
        boolean checkInsertLink = dbHelper.insertLink(response.url);
        if (checkInsertLink){
            Toast.makeText(this, "link saved", Toast.LENGTH_SHORT).show();
            createAccount();
        }else{
            Toast.makeText(this, "link not saved", Toast.LENGTH_SHORT).show();
        }
    }
    public void insertToken(PostPersonaApiResponse response){
        boolean checkInsertToken = dbHelper.insertToken(response.token);
        if (checkInsertToken){
            Toast.makeText(this, "Token saved", Toast.LENGTH_SHORT).show();
            createAccount();
        }else{
            Toast.makeText(this, "Token not saved", Toast.LENGTH_SHORT).show();
        }
    }
    private void createAccount() {
        SharedPreferences sharedPreferences = getSharedPreferences("app_name", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("hasAccount", true);
        editor.apply();
        login1();

    }
    private void login1(){
        // Navigate to the main screen
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }



    private void initViews() {
        edtTxtEnterPassword = findViewById(R.id.edtTxtEnterPassword);
        edtTxtConfirmPassword = findViewById(R.id.edtTxtConfirmPassword);
        btnContinue = findViewById(R.id.btnPasswordContinue);
        btnBack = findViewById(R.id.btnPasswordBack);
    }
}