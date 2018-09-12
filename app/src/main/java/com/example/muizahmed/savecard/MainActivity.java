package com.example.muizahmed.savecard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v7.widget.SearchView;
import android.widget.Toast;
import android.content.Context;

import com.example.muizahmed.savecard.Database.DBhelper;
import com.example.muizahmed.savecard.model.Card;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ListView listView;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int SELECT_FILE = 2;
    Bitmap bitmap;
    ArrayList<Card> CardList;
    ArrayAdapter<Card> adapter;
    DBhelper db;
    SearchView searchView;
    private Uri picUri;
    String mCurrentPhotoPath;
    final int PIC_CROP = 2;
    private Context context;
    String[] names;
    String name;
    String[] addresses;
    String address;
    String company;
    String[] otherss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new DBhelper(MainActivity.this);
        listView = (ListView) findViewById(R.id.list_item);
        CardList = db.getAllCardsData();
        adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, CardList);
        listView.setAdapter(adapter);
        searchView = (SearchView) findViewById(R.id.search);
        context = this;

        String Allname = readFileFromRawDirectory(R.raw.content);
        String Alladdress = readFileFromRawDirectory(R.raw.address);
        String Allother = readFileFromRawDirectory(R.raw.others);

        names = Allname.split(" ");
        addresses = Alladdress.split(" ");
        otherss = Allother.split(" ");
        name = "";
        address = "";
        company = "";
        //Log.i("FILE", name);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("Delete");
                dialog.setMessage("Are you sure?");
                dialog.setCancelable(false);


                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialogInterface, int which){
                      boolean flag = db.deleteCard(CardList.get(position).getId());
                      if(flag){
                          CardList.clear();
                          CardList.addAll(db.getAllCardsData());
                          adapter.notifyDataSetChanged();
                          Toast.makeText(MainActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                      }else{
                          Toast.makeText(MainActivity.this, "Delete Unsuccessful", Toast.LENGTH_SHORT).show();
                      }

                    }
                });
                dialog.setNegativeButton("No", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialogInterface, int which){
                        dialogInterface.cancel();
                    }
                });
                dialog.show();
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                boolean found = false;
                for(int i=0; i<listView.getCount(); i++){
                    if(listView.getAdapter().getItem(i).toString().toLowerCase().contains(query.toLowerCase())){
                        adapter.getFilter().filter(query);
                        found = true;
                    }
                    if(!found){
                        Toast.makeText(MainActivity.this, "No Match found",Toast.LENGTH_LONG).show();
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //    adapter.getFilter().filter(newText);
                return false;
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });


        FloatingActionButton fabgallery = (FloatingActionButton) findViewById(R.id.fabGallery);
        fabgallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
                onActivityResult(1,1,intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            String txt = getTextFromImage(bitmap);
            //txt.replaceAll("\n", "");
            String email = checkStringForEmail(txt);
            String phone = checkStringForPhoneNumber(txt);
//            Log.i("email", " !!!!!!! " + email + " !!!!!!! ");

            Intent i = new Intent(this, PhotoCard.class);
            i.putExtra("imageText", txt);
            i.putExtra("Email", email);
            i.putExtra("Phone", phone);
            i.putExtra("Name", name);
            i.putExtra("Address", address);
            i.putExtra("Company", company);
            startActivity(i);
        }
        else if (requestCode == SELECT_FILE && resultCode == RESULT_OK) {
            Bitmap bm=null;
            if (data != null) {
                try {
                    bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String txt =  getTextFromImage(bm);
            String email = checkStringForEmail(txt);

            Intent i = new Intent(this, PhotoCard.class);
            i.putExtra("imageText", txt);
            i.putExtra("Email", email);
            startActivity(i);
        }
    }

    private String getTextFromImage(Bitmap bitmap){
        String str= "";
        TextRecognizer tr = new TextRecognizer.Builder(getApplicationContext()).build();
        if(!tr.isOperational())
            Log.e("ERROR", "Detector dependencies are not yet available");
        else{
            Frame frame =  new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> items = tr.detect(frame);

            for(int i=0; i<items.size(); i++){
                TextBlock item = items.valueAt(i);
                String s = item.getValue();
                Log.i("regex", s + " (!!!!!!! "+ i + " !!!!!!!)");


                String a = s.replaceAll("\\s+", " ");
                Log.i("Textttt", a + " (@@@@ ::: " + i + " @@@@)");
                for(int k=0; k < addresses.length; k++){
                    if(s.contains(addresses[k])){
//                        Log.i("regex", a + " (### Address ::: " + i + " ###)");
                        address = a;
                    }
                }

                // line by line extraction
                String[] arr =  s.split("\n");
                for(int j=0; j< arr.length; j++){
                    if(arr[j].matches("[a-zA-Z ]+$")){
                        for(int k=0; k < names.length; k++){
                            if(arr[j].contains(names[k])){
//                                Log.i("regex", arr[j] + " (### NAME ::: " + i + " ###)");
                                name = arr[j];
                            }
                        }
                    }
                }


                for(int j=0; j< arr.length; j++){
                    if(arr[j].matches("[a-zA-Z ]+$")){
                            for(int l=0; l < otherss.length;l++){
                                if(!arr[j].toLowerCase().contains(otherss[l])){
                                    for (int q=0; q<names.length; q++){
                                        if(!arr[j].toLowerCase().contains(names[q])){
                                            Log.i("regex", arr[j] + " (### ??? NAME ::: " + i + " ###)");
                                            company = arr[j];
                                        }
                                    }
                                }
                            }
                        }
                    }


                // word by word extraction
                String[] strArray =  s.split("\\s+");
                for(int j=0; j< strArray.length; j++){
//                    Log.i("regex", strArray[j] + " (### "+ i + " ###)");
                    str += strArray[j] + " ";
                }
            }
            Log.i("taaaaa", str);
        }
        return str;
    }
    public String checkStringForEmail(String str){
        String[] strArray =  str.split(" ");
        for(int i=0; i< strArray.length; i++){
            Log.i("word", i + ":::split::: " + strArray[i]);
           if(strArray[i].contains("@") && strArray[i].contains(".com")){
               Log.i("email", " !!!!!!! " + strArray[i] + " !!!!!!! ");
               return strArray[i];
           }
        }
        return "";
    }

    public String checkStringForPhoneNumber(String str){
        String[] strArray =  str.split(" ");
        for(int i=0; i< strArray.length; i++){
            Log.i("word", i + ":::split::: " + strArray[i]);
//            strArray[i].matches(".*\\d+.*"))
            if(strArray[i].contains("[a-zA-Z]+$")){
//                Log.i("PPPPPP", " !!!!!!!---- " + strArray[i] + " !!!!!!! ");
                continue;
            }
            else if(strArray[i].matches("[0-9+]+$") && strArray[i].length() > 9){
                Log.i("PPPPPP", " !!!!!!!++++ " + strArray[i] + " !!!!!!! ");
                return strArray[i];
            }
        }
        return "";
    }

    @Override
    public void onBackPressed() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private String readFileFromRawDirectory(int resourceId){
        InputStream iStream = context.getResources().openRawResource(resourceId);
        ByteArrayOutputStream byteStream = null;
        try {
            byte[] buffer = new byte[iStream.available()];
            iStream.read(buffer);
            byteStream = new ByteArrayOutputStream();
            byteStream.write(buffer);
            byteStream.close();
            iStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteStream.toString();
    }
}

