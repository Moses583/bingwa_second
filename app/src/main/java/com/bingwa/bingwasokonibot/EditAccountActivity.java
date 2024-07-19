package com.bingwa.bingwasokonibot;

import android.app.Dialog;
import android.database.Cursor;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bingwa.bingwasokonibot.listeners.ResetPasswordListener;
import com.bingwa.bingwasokonibot.models.ResetPasswordApiResponse;
import com.bingwa.bingwasokonibot.models.ResetPasswordPojo;
import com.google.android.material.textfield.TextInputLayout;

public class EditAccountActivity extends AppCompatActivity {

    private TextInputLayout resetPassword, confirmPassword;
    TextView txtLoading;
    ProgressBar progressBar;
    private EditText one,two;
    private Button button;
    RequestManager manager;
    DBHelper helper,helper2;
    public String till;
    private Dialog dialog,dialog2;
    private Button btnResetExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        initEditTexts();
        manager = new RequestManager(this);
        helper = new DBHelper(this);

        till = tillNumber();

        showDialog3();
        showGoToLoginDialog();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchPassword();
                dialog.show();
            }
        });
        btnResetExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
                finish();
            }
        });
    }

    private void showGoToLoginDialog() {
        dialog2 = new Dialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_reset_password,null);
        btnResetExit = view.findViewById(R.id.btnExitReset);
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
        txtLoading.setText("Updating password...");
    }

    private void initEditTexts(){
        one = resetPassword.getEditText();
        two = confirmPassword.getEditText();
    }

    private void fetchPassword(){
        String passOne = one.getText().toString();
        String passTwo = two.getText().toString();
        if (checkEmpty(passOne,passTwo)){
            dialog.dismiss();
            confirmPassword.setHelperTextEnabled(true);
            confirmPassword.setHelperText("Fields cannot be empty!!");
        }
        else{
            if (validatePassword(passOne,passTwo)){
                confirmPassword.setHelperTextEnabled(false);
                confirmPassword.setHelperTextEnabled(false);
                callApi(till,passTwo);
            }
            else {
                dialog.dismiss();
                confirmPassword.setHelperTextEnabled(true);
                confirmPassword.setHelperText("Passwords do not match!!");
            }
        }
    }

    private void callApi(String till, String passTwo) {
        ResetPasswordPojo pojo = new ResetPasswordPojo(till,passTwo);
        manager.resetPassword(listener,pojo,token());
    }
    private final ResetPasswordListener listener = new ResetPasswordListener() {
        @Override
        public void didFetch(ResetPasswordApiResponse response, String message) {
            dialog.dismiss();
            Toast.makeText(EditAccountActivity.this, response.message, Toast.LENGTH_SHORT).show();
            showMessage(response);
        }

        @Override
        public void didError(String message) {
            dialog.dismiss();
            Toast.makeText(EditAccountActivity.this, "Please connect to the internet", Toast.LENGTH_SHORT).show();
        }
    };

    private void showMessage(ResetPasswordApiResponse response) {
        if (response.message.contains("Password updated")){
            dialog2.show();
        }
    }


    private boolean checkEmpty(String passOne, String passTwo){
        return passOne.isEmpty() || passTwo.isEmpty();
    }
    private boolean validatePassword(String passOne,String passTwo){
        return passOne.equals(passTwo);
    }

    public String tillNumber(){
        Cursor cursor = helper.getUser();
        String till = "";
        if (cursor.getCount() == 0){
            Log.d("TILL","Till number absent");
        }else{
            while (cursor.moveToNext()){
                till = cursor.getString(0);
            }
        }
        cursor.close();
        return till;
    }
    public String token(){
        helper2 = new DBHelper(this);
        Cursor cursor = helper2.getToken();
        String token = "";
        if (cursor.getCount() == 0){
            Toast.makeText(this, "token not found", Toast.LENGTH_SHORT).show();
        }else{
            while (cursor.moveToNext()){
                token = cursor.getString(0);
            }
        }
        cursor.close();
        return "Bearer "+token;
    }

    private void initViews() {
        resetPassword = findViewById(R.id.editPassword);
        confirmPassword = findViewById(R.id.editConfirmPassword);
        button = findViewById(R.id.btnResetPassword);
    }
}