package com.bingwa.bingwasokonibot;

import static androidx.core.content.ContextCompat.getDrawable;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private Dialog dialog,dialog2;
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
        startActivity(intent);
        finish();
    }

    private void showGoToLoginDialog() {
        dialog2 = new Dialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_go_to_login,null);
        btnGoToLogin = view.findViewById(R.id.btnGoToLogin);
        dialog2.setContentView(view);
        dialog2.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog2.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background));
        dialog2.setCancelable(false);
    }

    private void showDialog3() {
        dialog = new Dialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_progress_layout,null);
        progressBar = view.findViewById(R.id.myProgressBar);
        txtLoading = view.findViewById(R.id.txtProgress);
        dialog.setContentView(view);
        int widthInDp = 250;

        final float scale = getResources().getDisplayMetrics().density;
        int widthInPx = (int) (widthInDp * scale + 0.5f);

        dialog.getWindow().setLayout(widthInPx, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background));
        dialog.setCancelable(false);
        txtLoading.setText("Requesting token...");
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