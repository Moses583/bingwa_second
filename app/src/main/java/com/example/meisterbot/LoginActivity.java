package com.example.meisterbot;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import com.example.meisterbot.listeners.PostLoginListener;
import com.example.meisterbot.models.LoginPojo;
import com.example.meisterbot.models.PostLoginApiResponse;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {
    private Button login;
    private TextInputLayout enterName,enterPassword;
    private EditText one, two;
    RequestManager manager;
    private Dialog dialog;
    ProgressBar progressBar;
    private TextView txtLoading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
        manager = new RequestManager(this);

        one = enterName.getEditText();
        two = enterPassword.getEditText();

        dialog = new Dialog(LoginActivity.this);
        dialog.setContentView(R.layout.dialog_progress_layout);


        progressBar = dialog.findViewById(R.id.myProgressBar);
        txtLoading = dialog.findViewById(R.id.txtProgress);




        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtLoading.setText("Logging you in...");
                dialog.show();
                fetchDetails();
            }
        });

    }

    private void fetchDetails(){
        String name = one.getText().toString();
        String password = two.getText().toString();
        if (validatePassword(name,password)){
            enterPassword.setHelperTextEnabled(true);
            enterPassword.setHelperText("Fields cannot be empty!!");
            dialog.dismiss();
        }
        else{
            enterPassword.setHelperTextEnabled(false);
            LoginPojo pojo = new LoginPojo();
            pojo.password = password;
            pojo.username = name;
            manager.postLogin(listener,pojo);
        }
    }

    private boolean validatePassword(String name, String password){
        return name.isEmpty() || password.isEmpty();
    }
    private final PostLoginListener listener = new PostLoginListener() {
        @Override
        public void didFetch(PostLoginApiResponse pojo, String message) {
            confirmCredentials(pojo,message);
        }

        @Override
        public void didError(String message) {
            dialog.dismiss();
            enterPassword.setHelperTextEnabled(true);
            enterPassword.setHelperText(message);
        }
    };

    private void confirmCredentials(PostLoginApiResponse pojo, String message) {
        if(pojo.message.equalsIgnoreCase("Login successful")){
            enterPassword.setHelperTextEnabled(false);
            dialog.dismiss();
            Toast.makeText(this, "login successful", Toast.LENGTH_SHORT).show();
            goToEnableServiceActivity();
        }
        else{
            dialog.dismiss();
            enterPassword.setHelperTextEnabled(true);
            enterPassword.setHelperText(pojo.message);
        }
    }
    private void goToEnableServiceActivity(){
        startActivity(new Intent(LoginActivity.this, EnableServiceActivity.class));
    }

    private void initViews() {
        login = findViewById(R.id.btnLogIn);
        enterName = findViewById(R.id.edtTxtLoginName);
        enterPassword = findViewById(R.id.edtTxtLoginPassword);
    }
}