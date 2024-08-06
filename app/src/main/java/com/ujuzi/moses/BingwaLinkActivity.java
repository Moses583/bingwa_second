package com.ujuzi.moses;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BingwaLinkActivity extends AppCompatActivity {
    String url;
    private DBHelper helper;
    private TextView txtBingwaLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tokens);
        initViews();
        Toast.makeText(this, "Click on your link to share it", Toast.LENGTH_SHORT).show();
        helper = new DBHelper(this);
        url = link();
        txtBingwaLink.setText(url);

        txtBingwaLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, txtBingwaLink.getText().toString());
                sendIntent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
        });
    }
    public String link(){
        Cursor cursor = helper.getLink();
        String link = "";
        if (cursor.getCount() == 0){
            Toast.makeText(BingwaLinkActivity.this, "", Toast.LENGTH_SHORT).show();
        }else{
            while (cursor.moveToNext()){
                link = cursor.getString(0);
            }
        }
        cursor.close();
        return link;
    }

    private void initViews() {
        txtBingwaLink = findViewById(R.id.txtBingwaLink);
    }
}