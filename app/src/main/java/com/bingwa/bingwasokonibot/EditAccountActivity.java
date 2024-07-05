package com.bingwa.bingwasokonibot;

import android.database.Cursor;
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
    DBHelper helper;
    public String till;
    private AlertDialog dialog,dialog2;
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
            }
        });
    }

    private void showGoToLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_reset_password,null);
        btnResetExit = dialogView.findViewById(R.id.btnExitReset);
        builder.setView(dialogView);
        dialog2 = builder.create();
    }

    private void showDialog3() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_progress_layout,null);
        txtLoading = dialogView.findViewById(R.id.txtProgress);
        progressBar = dialogView.findViewById(R.id.myProgressBar);
        txtLoading.setText("Changing your password...");
        builder.setView(dialogView);
        dialog = builder.create();
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
        manager.resetPassword(listener,pojo);
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

    private void initViews() {
        resetPassword = findViewById(R.id.editPassword);
        confirmPassword = findViewById(R.id.editConfirmPassword);
        button = findViewById(R.id.btnResetPassword);
    }
}