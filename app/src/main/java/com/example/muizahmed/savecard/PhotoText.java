package com.example.muizahmed.savecard;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

public class PhotoText extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView imgView;
    Button btn, btnSelect;
    TextView txt;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_text);

        imgView = (ImageView) findViewById(R.id.imageView);
        btn = (Button) findViewById(R.id.btnProcess);
        btnSelect = (Button) findViewById(R.id.btnSelectPhoto);
        txt = (TextView) findViewById(R.id.textDetails);

//         bitmap = BitmapFactory.decodeResource(
//                getApplicationContext().getResources(),
//                R.drawable.image
//        );

        imgView.setImageBitmap(bitmap);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextRecognizer tr = new TextRecognizer.Builder(getApplicationContext()).build();
                if(!tr.isOperational())
                    Log.e("ERROR", "Detector dependencies are not yet available");
                else{
                    Frame frame =  new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = tr.detect(frame);
                    StringBuilder sb = new StringBuilder();

                    for(int i=0; i<items.size(); i++){
                        TextBlock item = items.valueAt(i);
                        sb.append(item.getValue());
                        sb.append("\n");
                    }
                    txt.setText(sb.toString());
                }
            }
        });


        btnSelect.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 final CharSequence[] items = {"Camera", "Choose from library", "Cancel"};

                 AlertDialog.Builder builder = new AlertDialog.Builder(PhotoText.this);
                 builder.setTitle("Add Photo");
                 builder.setItems(items, new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         //boolean res = Utility.checkPermission(PhotoText.this);

                         if(items[which].equals("Camera")){
                             Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                             if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                 startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                             }

                             onActivityResult(1,1,takePictureIntent);
                         }
                         else if(items[which].equals("Choose from library")){

                         }
                         else if(items[which].equals("Cancel")){
                            dialog.dismiss();
                         }
                     }
                 });
                 builder.show();

             }
         }
        );

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
            imgView.setImageBitmap(bitmap);
        }
    }
}
