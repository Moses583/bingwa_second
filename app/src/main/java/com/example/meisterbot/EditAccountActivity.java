package com.example.meisterbot;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class EditAccountActivity extends AppCompatActivity {

    private TextInputLayout enterName,enterPhoneNumber,enterTillNumber,enterStoreName,enterWebUrl;
    private EditText one,two,three,four,five;
    private Button createPersona;
    RequestManager manager;

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
        createPersona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceed();
            }
        });
    }
    private void initEditTexts(){
        one = enterName.getEditText();
        two = enterPhoneNumber.getEditText();
        three = enterTillNumber.getEditText();
        four = enterStoreName.getEditText();
        five = enterWebUrl.getEditText();
    }

    public void proceed(){
        if (fetchDetails() == null){
            Toast.makeText(EditAccountActivity.this, "Unable to create account", Toast.LENGTH_SHORT).show();
        }else{
            showAlertDialog();
        }
    }
    public void showAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(EditAccountActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.persona_dialog_layout,null);
        TextView one = dialogView.findViewById(R.id.txtPersonaName);
        TextView two = dialogView.findViewById(R.id.personaNumber);
        TextView three = dialogView.findViewById(R.id.txtPersonaTill);
        TextView four = dialogView.findViewById(R.id.txtPersonaStoreName);
        TextView five = dialogView.findViewById(R.id.txtPersonaWeb);
        one.setText(fetchDetails().getName());
        two.setText(fetchDetails().getPhoneNumber());
        three.setText(fetchDetails().getTillNumber());
        four.setText(fetchDetails().getStoreName());
        five.setText(fetchDetails().getBingwaSite());
        builder.setView(dialogView);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                manager.postPersona(listener,fetchDetails());
//                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.myFragmentContainer, new MainContentFragment())
//                        .addToBackStack(null)
//                        .commit();
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
    private Persona fetchDetails(){
        boolean complete = true;
        String brand = Build.BRAND;
        String model = Build.MODEL;
        String product = Build.PRODUCT;
        String Device = Build.DEVICE;
        String manufacturer = Build.MANUFACTURER;
        String deviceId = brand+" "+model+" "+product+" "+Device+" "+manufacturer;
        String name = one.getText().toString();
        String number = two.getText().toString();
        String till = three.getText().toString();
        String storeName = four.getText().toString();
        String webUrl = five.getText().toString();

        if (name.isEmpty()){
            one.setError("Name cannot be empty");
            complete = false;
        }
        if (number.isEmpty()){
            two.setError("Name cannot be empty");
            complete = false;
        }
        if (till.isEmpty()){
            three.setError("Name cannot be empty");
            complete = false;
        }
        if (storeName.isEmpty()){
            four.setError("Name cannot be empty");
            complete = false;
        }
        if (webUrl.isEmpty()){
            five.setError("Name cannot be empty");
            complete = false;
        }
        if (complete){
            return new Persona(name,storeName,webUrl,deviceId,number,till,"null");
        }
        else {
            return null;
        }
    }
    private final PostPersonaListener listener = new PostPersonaListener() {
        @Override
        public void didFetch(PostPersonaApiResponse response, String message) {
            Toast.makeText(EditAccountActivity.this, response.message1, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void didError(String message) {
            Toast.makeText(EditAccountActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };

    private void initViews() {
        enterName = findViewById(R.id.enterName);
        enterPhoneNumber = findViewById(R.id.enterPhoneNumber);
        enterTillNumber = findViewById(R.id.enterTillNumber);
        enterStoreName = findViewById(R.id.enterStoreNumber);
        enterWebUrl = findViewById(R.id.enterWebsiteUrl);
        createPersona = findViewById(R.id.btnCreatePersona);
    }
}