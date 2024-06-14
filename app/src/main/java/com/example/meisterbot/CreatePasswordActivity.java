package com.example.meisterbot;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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

import com.example.meisterbot.listeners.PostPersonaListener;
import com.example.meisterbot.models.PostPersonaApiResponse;
import com.example.meisterbot.models.Persona;
import com.google.android.material.textfield.TextInputLayout;

public class CreatePasswordActivity extends AppCompatActivity {
    private TextInputLayout edtTxtEnterPassword, edtTxtConfirmPassword;
    private Button btnContinue,btnBack;
    private EditText one,two;
    private Dialog dialog1;
    private TextView txtLoading;
    ProgressBar progressBar;

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

        dialog1 = new Dialog(CreatePasswordActivity.this);
        dialog1.setContentView(R.layout.dialog_progress_layout);


        progressBar = dialog1.findViewById(R.id.myProgressBar);
        txtLoading = dialog1.findViewById(R.id.txtProgress);

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
        String web = intent.getStringExtra("web");
        Persona persona = new Persona(name,storeName,web,deviceId,number,till,password);
        showAlertDialog(persona);
    }
    public void showAlertDialog(Persona persona){
        AlertDialog.Builder builder = new AlertDialog.Builder(CreatePasswordActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.persona_dialog_layout,null);
        TextView one = dialogView.findViewById(R.id.txtPersonaName);
        TextView two = dialogView.findViewById(R.id.personaNumber);
        TextView three = dialogView.findViewById(R.id.txtPersonaTill);
        TextView four = dialogView.findViewById(R.id.txtPersonaStoreName);
        TextView five = dialogView.findViewById(R.id.txtPersonaWeb);
        TextView six = dialogView.findViewById(R.id.PersonaPassword);
        one.setText(persona.getName());
        two.setText(persona.getPhoneNumber());
        three.setText(persona.getTillNumber());
        four.setText(persona.getStoreName());
        five.setText(persona.getBingwaSite());
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
            Toast.makeText(CreatePasswordActivity.this, response.message1, Toast.LENGTH_SHORT).show();
            edtTxtConfirmPassword.setHelperTextEnabled(false);
            completeLogin(response);
        }

        @Override
        public void didError(String message) {
            dialog1.dismiss();
            edtTxtConfirmPassword.setHelperTextEnabled(true);
            edtTxtConfirmPassword.setHelperText(message);
        }
    };

    private void completeLogin(PostPersonaApiResponse response) {
        if (response.message1.equals("Successful account creation")){
            dialog1.dismiss();
            edtTxtConfirmPassword.setHelperTextEnabled(false);
            goToEnableServiceActivity();
        }else{
            dialog1.dismiss();
            edtTxtConfirmPassword.setHelperTextEnabled(true);
            edtTxtConfirmPassword.setHelperText(response.message1);
        }
    }

    private void goToEnableServiceActivity(){
        startActivity(new Intent(CreatePasswordActivity.this, EnableServiceActivity.class));
    }

    private void initViews() {
        edtTxtEnterPassword = findViewById(R.id.edtTxtEnterPassword);
        edtTxtConfirmPassword = findViewById(R.id.edtTxtConfirmPassword);
        btnContinue = findViewById(R.id.btnPasswordContinue);
        btnBack = findViewById(R.id.btnPasswordBack);
    }
}