package com.bingwa.bingwasokonibot;

import android.content.Intent;
import android.database.Cursor;
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

import com.bingwa.bingwasokonibot.listeners.RequestTokenListener;
import com.bingwa.bingwasokonibot.models.RequestTokenApiResponse;
import com.bingwa.bingwasokonibot.models.RequestTokenPojo;
import com.google.android.material.textfield.TextInputLayout;

public class RequestTokenActivity extends AppCompatActivity {

    private TextInputLayout enterPhoneNumber;
    private Button goToResetPassword,btnGoToLogin;
    private EditText editText;
    private RequestManager manager;
    private AlertDialog dialog,dialog2;
    ProgressBar progressBar;
    private TextView txtLoading;
    private DBHelper dbHelper,helper2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_request_token);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
        manager = new RequestManager(this);
        editText = enterPhoneNumber.getEditText();

        showDialog3();
        showGoToLoginDialog();

        goToResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchData();
                dialog.show();
            }
        });
        btnGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
                login();
            }
        });
    }
    private void login(){
        Intent intent = new Intent(RequestTokenActivity.this, LoginActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void showGoToLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_go_to_login,null);
        btnGoToLogin = dialogView.findViewById(R.id.btnGoToLogin);
        builder.setView(dialogView);
        dialog2 = builder.create();
    }

    private void showDialog3() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_progress_layout,null);
        txtLoading = dialogView.findViewById(R.id.txtProgress);
        progressBar = dialogView.findViewById(R.id.myProgressBar);
        txtLoading.setText("Requesting new token");
        builder.setView(dialogView);
        dialog = builder.create();
    }

    private void fetchData() {
       String phone = editText.getText().toString();
       RequestTokenPojo pojo = new RequestTokenPojo(phone);
       manager.requestToken(listener,pojo);
    }
    private final RequestTokenListener listener = new RequestTokenListener() {
        @Override
        public void didFetch(RequestTokenApiResponse response, String message) {
            Toast.makeText(RequestTokenActivity.this, response.message, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            showDialog2(response);
        }

        @Override
        public void didError(String message) {
            Toast.makeText(RequestTokenActivity.this, message, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }
    };

    private void showDialog2(RequestTokenApiResponse response) {
        if (response.message.contains("Token sent")){
            dialog2.show();
        }
    }

    private void initViews(){
        enterPhoneNumber = findViewById(R.id.edtRequestPhoneNumber);
        goToResetPassword = findViewById(R.id.btnGoToResetPassword);
    }
}