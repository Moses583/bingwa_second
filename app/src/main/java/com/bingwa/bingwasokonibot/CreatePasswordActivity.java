package com.bingwa.bingwasokonibot;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bingwa.bingwasokonibot.listeners.PostPersonaListener;
import com.bingwa.bingwasokonibot.models.PostPersonaApiResponse;
import com.bingwa.bingwasokonibot.models.Persona;
import com.google.android.material.textfield.TextInputLayout;

public class CreatePasswordActivity extends AppCompatActivity {
    private TextInputLayout edtTxtEnterPassword, edtTxtConfirmPassword;
    private Button btnContinue,btnBack;
    private EditText one,two;
    private AlertDialog dialog1;
    private TextView txtLoading;
    ProgressBar progressBar;
    DBHelper dbHelper;

    RequestManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_progress_layout,null);
        txtLoading = dialogView.findViewById(R.id.txtProgress);
        progressBar = dialogView.findViewById(R.id.myProgressBar);
        txtLoading.setText("Creating your account...");
        builder.setView(dialogView);
        dialog1 = builder.create();
    }


    private void fetchPassword(){
        String passOne = one.getText().toString();
        String passTwo = two.getText().toString();
        if (checkEmpty(passOne,passTwo)){
            dialog1.dismiss();
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
                dialog1.dismiss();
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
        Persona persona = new Persona(name,number,till,storeName,deviceId,password);
        showAlertDialog(persona);
    }
    public void showAlertDialog(Persona persona){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.persona_dialog_layout,null);
        TextView two = dialogView.findViewById(R.id.personaNumber);
        TextView three = dialogView.findViewById(R.id.txtPersonaTill);
        TextView four = dialogView.findViewById(R.id.txtPersonaStoreName);
        TextView six = dialogView.findViewById(R.id.PersonaPassword);
        two.setText(persona.getPhoneNumber());
        three.setText(persona.getTillNumber());
        four.setText(persona.getStoreName());
        six.setText(persona.getPassWord());
        builder.setView(dialogView);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                txtLoading.setText("Creating your account...");
                dialog1.show();
                manager.postPersona(listener,persona);
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

    private final PostPersonaListener listener = new PostPersonaListener() {
        @Override
        public void didFetch(PostPersonaApiResponse response, String message) {
            edtTxtConfirmPassword.setHelperTextEnabled(false);
            completeLogin(response);
        }

        @Override
        public void didError(String message) {
            if (message.contains("Unable to resolve host")){
                dialog1.dismiss();
                edtTxtConfirmPassword.setHelperTextEnabled(true);
                edtTxtConfirmPassword.setHelperText("Please connect to the internet");
            }
            else{
                dialog1.dismiss();
                edtTxtConfirmPassword.setHelperTextEnabled(true);
                edtTxtConfirmPassword.setHelperText("Please connect to the internet");
            }

        }
    };

    private void completeLogin(PostPersonaApiResponse response) {
        if (response.message1.equals("Successful account creation")){
            dialog1.dismiss();
            edtTxtConfirmPassword.setHelperTextEnabled(false);
            insertTill(response);
            insertToken(response);
        }else{
            dialog1.dismiss();
            edtTxtConfirmPassword.setHelperTextEnabled(true);
            edtTxtConfirmPassword.setHelperText(response.message1);
        }
    }
    public void insertTill(PostPersonaApiResponse response){
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