package com.bingwa.bingwasokonibot;

import android.content.Context;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bingwa.bingwasokonibot.listeners.PostLoginListener;
import com.bingwa.bingwasokonibot.models.LoginPojo;
import com.bingwa.bingwasokonibot.models.PostLoginApiResponse;
import com.bingwa.bingwasokonibot.models.PostPersonaApiResponse;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {
    private Button login;
    private TextInputLayout enterName,enterPassword;
    private EditText one, two;
    RequestManager manager;
    private AlertDialog dialog;
    ProgressBar progressBar;
    private TextView txtLoading,txtForgotPassword;
    DBHelper helper;
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

        showDialog3();

        helper = new DBHelper(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                fetchDetails();
//                createAccount();
            }
        });
        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RequestTokenActivity.class));
                finish();
            }
        });

    }

    private void showDialog3() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_progress_layout,null);
        txtLoading = dialogView.findViewById(R.id.txtProgress);
        progressBar = dialogView.findViewById(R.id.myProgressBar);
        txtLoading.setText("Logging you in...");
        builder.setView(dialogView);
        dialog = builder.create();
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
            if (message.contains("Unable to resolve host")){
                dialog.dismiss();
                enterPassword.setHelperTextEnabled(true);
                enterPassword.setHelperText("Please connect to the internet");
            }
            else{
                dialog.dismiss();
                enterPassword.setHelperTextEnabled(true);
                enterPassword.setHelperText(message);
            }

        }
    };

    private void confirmCredentials(PostLoginApiResponse pojo, String message) {
        if(pojo.message.equalsIgnoreCase("Login successful")){
            enterPassword.setHelperTextEnabled(false);
            dialog.dismiss();
            Toast.makeText(this, "login successful", Toast.LENGTH_SHORT).show();
            String till = pojo.tillNumber;
            String link = pojo.bingwaSite;
            String token = pojo.token;
            insertLink(link);
            insertTill(till);
            insertToken(token);
        }
        else{
            dialog.dismiss();
            enterPassword.setHelperTextEnabled(true);
            enterPassword.setHelperText(pojo.message);
        }
    }

    private void insertTill(String till) {
        boolean insertTill = helper.insertUser(till);
        if (insertTill){
            createAccount();
        }
        else{
            Toast.makeText(this, "unable to insert till", Toast.LENGTH_SHORT).show();
        }

    }
    private void insertLink(String link) {
        boolean insertTill = helper.insertLink(link);
        if (insertTill){
            Toast.makeText(this, link, Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "link not saved", Toast.LENGTH_SHORT).show();
        }

    }
    public void insertToken(String token){
        boolean checkInsertToken = helper.insertToken(token);
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

        dialog.dismiss();
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
        login = findViewById(R.id.btnLogIn);
        enterName = findViewById(R.id.edtTxtLoginName);
        enterPassword = findViewById(R.id.edtTxtLoginPassword);
        txtForgotPassword = findViewById(R.id.txtForgotPassword);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        }
}