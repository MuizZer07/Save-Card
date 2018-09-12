package com.example.muizahmed.savecard;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muizahmed.savecard.Database.DBhelper;
import com.example.muizahmed.savecard.model.Card;

public class PhotoCard extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    TextView detailsText;
    EditText nameText, phoneText, emailText, addressText, companyText;
    Button saveButton, btnSelect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_card);

        detailsText = (TextView) findViewById(R.id.detailsText);
        saveButton = (Button) findViewById(R.id.saveBtn);

        Intent intent = getIntent();
        String text = intent.getStringExtra("imageText");
        String email = intent.getStringExtra("Email");
        String phn = intent.getStringExtra("Phone");
        String name = intent.getStringExtra("Name");
        String add = intent.getStringExtra("Address");
        String comp = intent.getStringExtra("Company");;

        String t = "Name: " + name + "\nPhone Number: " + phn +
                "\nEmail: " + email + "\nCompany: " + comp +
                "\nAddress: " + add;

        detailsText.setText(t);
        detailsText.setMovementMethod(new ScrollingMovementMethod());
        nameText = (EditText) findViewById(R.id.nameEditText);
        phoneText = (EditText) findViewById(R.id.phoneEditText);
        emailText = (EditText) findViewById(R.id.emailEditText);
        addressText = (EditText) findViewById(R.id.addressEditText);
        companyText = (EditText) findViewById(R.id.companyEditText);

        nameText.setText(name);
        emailText.setText(email);
        phoneText.setText(phn);
        addressText.setText(add);
        companyText.setText(comp);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name, phone;
                name = nameText.getText().toString();
                phone = phoneText.getText().toString();

                Card card = new Card();
                card.setName(name);
                card.setPhoneNo(phone);

                DBhelper dBhelper = new DBhelper(PhotoCard.this);
                dBhelper.insertCard(card);

                Toast.makeText(PhotoCard.this,"Saved!",
                        Toast.LENGTH_SHORT).show();

                Intent i = new Intent(PhotoCard.this, MainActivity.class);
                startActivity(i);
            }
        });

    }
}
