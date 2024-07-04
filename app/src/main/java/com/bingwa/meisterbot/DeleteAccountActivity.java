package com.bingwa.meisterbot;

import android.app.ActivityManager;
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

import com.bingwa.meisterbot.listeners.DeleteAccountListener;
import com.bingwa.meisterbot.models.DeleteAccountApiResponse;
import com.bingwa.meisterbot.models.DeleteAccountPojo;
import com.bingwa.meisterbot.services.MyService;
import com.google.android.material.textfield.TextInputLayout;

public class DeleteAccountActivity extends AppCompatActivity {
    private Button btnDelete;
    private TextInputLayout deleteTillNumber,deletePassword;
    private EditText one, two;
    RequestManager manager;
    private AlertDialog dialog;
    ProgressBar progressBar;
    private TextView txtLoading;
    private DBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_delete_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
        manager = new RequestManager(this);
        helper = new DBHelper(this);

        one = deleteTillNumber.getEditText();
        two = deletePassword.getEditText();

        showDialog3();


        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                fetchDetails();
            }
        });
    }
    private void showDialog3() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_progress_layout,null);
        txtLoading = dialogView.findViewById(R.id.txtProgress);
        progressBar = dialogView.findViewById(R.id.myProgressBar);
        txtLoading.setText("Deleting your account...");
        builder.setView(dialogView);
        dialog = builder.create();
    }
    private void fetchDetails(){
        String till = one.getText().toString();
        String password = two.getText().toString();
        if (validatePassword(till,password)){
            deletePassword.setHelperTextEnabled(true);
            deletePassword.setHelperText("Fields cannot be empty!!");
            dialog.dismiss();
        }
        else{
            deletePassword.setHelperTextEnabled(false);
            DeleteAccountPojo pojo = new DeleteAccountPojo(till,password);
            manager.deleteAccount(listener,pojo);
        }
    }

    private boolean validatePassword(String name, String password){
        return name.isEmpty() || password.isEmpty();
    }
    private final DeleteAccountListener listener = new DeleteAccountListener() {
        @Override
        public void didFetch(DeleteAccountApiResponse pojo, String message) {
            confirmCredentials(pojo,message);
        }

        @Override
        public void didError(String message) {
            if (message.contains("Unable to resolve host")){
                dialog.dismiss();
                deletePassword.setHelperTextEnabled(true);
                deletePassword.setHelperText("Please connect to the internet");
            }
            else{
                dialog.dismiss();
                deletePassword.setHelperTextEnabled(true);
                deletePassword.setHelperText(message);
            }

        }
    };
    private void confirmCredentials(DeleteAccountApiResponse pojo, String message) {
        if(pojo.message.equalsIgnoreCase("User deleted successfully.")){
            deletePassword.setHelperTextEnabled(false);
            dialog.dismiss();
            Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
            logout();

        }
        else{
            dialog.dismiss();
            deletePassword.setHelperTextEnabled(true);
            deletePassword.setHelperText(pojo.message);
        }
    }
    private void logout() {
        // Clear user session data here
        // This depends on how you're managing user sessions
        // For example, if you're using SharedPreferences, you can do:
        if (myService()){
            Intent intent = new Intent(this, MyService.class);
            stopService(intent);
        }
        SharedPreferences preferences = getSharedPreferences("app_name", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        helper.clearAllTables();

        // Navigate back to login screen
        Intent intent = new Intent(DeleteAccountActivity.this, CreateAccountActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    public boolean myService(){
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo info :
                manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MyService.class.getName().equalsIgnoreCase(info.service.getClassName())){
                return true;
            }
        }
        return false;
    }

    private void initViews() {
        btnDelete = findViewById(R.id.btnDeleteAccount);
        deleteTillNumber = findViewById(R.id.edtDeleteTillNumber);
        deletePassword = findViewById(R.id.edtDeletePassword);

    }


}