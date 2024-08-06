package com.ujuzi.moses;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutAppActivity extends AppCompatActivity {
    private TextView txtEmail, txtPhoneNumber, privacyPolicy, terms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        initViews();
        txtEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, txtEmail.getText().toString());
                intent.putExtra(Intent.EXTRA_SUBJECT, "Hello, I have an issue!");
                startActivity(intent);
            }
        });
        txtPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + txtPhoneNumber.getText().toString()));
                startActivity(intent);
            }
        });
        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AboutAppActivity.this, PrivacyPolicyActivity.class);
                startActivity(intent);
            }
        });
        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AboutAppActivity.this, TermsAndConditionsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initViews() {
        txtEmail = findViewById(R.id.aboutTxtEmail);
        txtPhoneNumber = findViewById(R.id.aboutTxtNumber);
        privacyPolicy = findViewById(R.id.txtPrivacyPolicy);
        terms = findViewById(R.id.txtTermsAndConditions);
    }
}