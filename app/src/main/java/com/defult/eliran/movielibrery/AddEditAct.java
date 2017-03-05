package com.defult.eliran.movielibrery;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import static android.R.attr.targetSdkVersion;


public class AddEditAct extends AppCompatActivity implements View.OnClickListener {
    //Edit exist movie | Add new Manual movie | Add new Internet Movie
    SqlHelper sqlHelper;
    EditText MovieNameET;
    EditText BodyET;
    EditText UrlET;
    Button RatingET;
    ImageView movieIV;
    Button ShowBtn;
    Button OkBtn;
    Button CancelBtn;
    String ImageUrl;
    String EditId;
    String bodyId;
    View activity_add_edit;
    Cursor cursor;
    String imagebase64;
    LinearLayout linearLayout;
    boolean exist;

    String LinkToBody = "http://www.omdbapi.com/?i=";
    Bitmap bitmap;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        activity_add_edit = findViewById(R.id.activity_add_edit);
        sqlHelper = new SqlHelper(this);
        MovieNameET = (EditText) findViewById(R.id.MovieNameET);
        BodyET = (EditText) findViewById(R.id.BodyET);
        RatingET = (Button) findViewById(R.id.RatingBtn);
        linearLayout = (LinearLayout) findViewById(R.id.editLayout);
        UrlET = (EditText) findViewById(R.id.UrlET);
        movieIV = (ImageView) findViewById(R.id.movieIV);
        //movieIV.setVisibility(View.INVISIBLE);
        movieIV.setImageResource(R.drawable.camera);
        ShowBtn = (Button) findViewById(R.id.ShowBtn);
        OkBtn = (Button) findViewById(R.id.OkBtn);
        CancelBtn = (Button) findViewById(R.id.CancelBtn);
        ShowBtn.setOnClickListener(this);
        OkBtn.setOnClickListener(this);
        CancelBtn.setOnClickListener(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        if (DbConstant.isInternetAct == true) {
            //if getting from InternetSearchAct get all data and Set to EditText
            Intent intent = getIntent();
            String movieName = intent.getStringExtra("moviename");
            bodyId = intent.getStringExtra("body");
            ImageUrl = intent.getStringExtra("imagepath");
            DownLoadBody downLoadBody = new DownLoadBody();
            downLoadBody.execute(LinkToBody + bodyId);
            ShowBtn.setVisibility(View.INVISIBLE);
            UrlET.setVisibility(View.INVISIBLE);
            if (ImageUrl.contains("https://")) {
                Picasso.with(AddEditAct.this).load(ImageUrl).into(movieIV);
            }
            MovieNameET.setText(movieName);
            UrlET.setText(ImageUrl);
        }
        if (DbConstant.isInternetAct == false) {
            Bitmap bitmap = ((BitmapDrawable) movieIV.getDrawable()).getBitmap();
            movieIV.setImageBitmap(bitmap);
        }
        if (DbConstant.isEditAct == true) {
            //if getting from Edit option from MainActivity Set all data to EditText
            Intent intent = getIntent();
            MovieObj currentmovie=intent.getParcelableExtra("editmovie");
            MovieNameET.setText(currentmovie.MovieName);
            BodyET.setText(currentmovie.Body);
            EditId = currentmovie.id;
            UrlET.setText(currentmovie.Url);
            imagebase64 = currentmovie.imagebase64;
            RatingET.setText(currentmovie.imdbRating);
            if (!imagebase64.equals("")) {
                movieIV.setImageBitmap(decodeBase64(imagebase64));
                //linearLayout.setBackground(movieIV.getDrawable());

            }
        }
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = ((BitmapDrawable) movieIV.getDrawable()).getBitmap();
                imagebase64 = encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 100);
                Intent ImageFullScreenintent = new Intent(AddEditAct.this, ImageFullScreenAct.class);
                ImageFullScreenintent.putExtra(DbConstant.imagebase64, imagebase64);
                startActivity(ImageFullScreenintent);
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        //what the Buttons doas
        switch (v.getId()) {
            case R.id.ShowBtn:
                //set image bitmap with decode

                android.widget.PopupMenu popupMenu = new android.widget.PopupMenu(getApplicationContext(), v);
                popupMenu.inflate(R.menu.cameramenu);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new android.widget.PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {

                            case R.id.CameraMenu:
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                                    if (targetSdkVersion >= Build.VERSION_CODES.M) {
                                        if (checkSelfPermission(Manifest.permission.READ_CONTACTS)
                                                != PackageManager.PERMISSION_GRANTED) {
                                            requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
                                        }
                                    }
                                }
                                else {
                                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                    startActivityForResult(cameraIntent, 2);
                                }
                                break;
                            case R.id.DownImageMenu:
                                if (UrlET.getText().toString().contains("https://")) {
                                    Picasso.with(AddEditAct.this).load(UrlET.getText().toString()).into(movieIV);
                                } else {
                                    Toast.makeText(AddEditAct.this, "no image to download", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            /*case R.id.chosefromgallery:
                                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(i, 4);
                                break;*/
                            //TODO chose from gallery
                        }
                        return true;
                    }
                });
                break;
            case R.id.OkBtn:
                boolean nomoviename=false;
                String moviename = MovieNameET.getText().toString();
                moviename = moviename.trim();
                ContentValues contentValues = new ContentValues();
                if (moviename.length() > 0) {
                    Bitmap bitmap = ((BitmapDrawable) movieIV.getDrawable()).getBitmap();
                    //Save the data to SqlHelper
                    if (DbConstant.isEditAct==false) {
                        contentValues.put(DbConstant.ismarkedCB, "0");
                        contentValues.put(DbConstant.ImdbID, bodyId);
                    }
                    contentValues.put(DbConstant.moviename, moviename);
                    contentValues.put(DbConstant.body, BodyET.getText().toString());
                    contentValues.put(DbConstant.urlpath, UrlET.getText().toString());
                    contentValues.put(DbConstant.rating, RatingET.getText().toString());
                    imagebase64 = encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 100);
                    contentValues.put(DbConstant.imagebase64, imagebase64);
                } else {

                    nomoviename=true;
                }
                if (DbConstant.isEditAct == true) {
                    //update the SqlHepler to current movie
                    String[] delid = new String[]{"" + EditId};
                    sqlHelper.getWritableDatabase().update(DbConstant.tablename, contentValues, "_id=?", delid);
                    intentToMain();
                }
                if (DbConstant.isEditAct == false) {
                    //Add a New MovieObj to SqlHelper
                    // sqlHelper.getWritableDatabase().insert(DbConstant.tablename,null,contentValues);
                    cursor = sqlHelper.getReadableDatabase().query(DbConstant.tablename, new String[]{DbConstant.moviename}, null, null, null, null, null);

                    while (cursor.moveToNext()) {
                        //cheking if the movie exist
                        String currentmoviename = cursor.getString(cursor.getColumnIndex(DbConstant.moviename));
                        if (currentmoviename.equals(MovieNameET.getText().toString())) {

                            exist = true;
                        }
                    }
                    if (exist == true) {
                        Toast.makeText(AddEditAct.this, "movie exist", Toast.LENGTH_SHORT).show();
                    } else if (exist == false) {
                        if (nomoviename==true)
                        {
                            Toast.makeText(this, "please enter movie name", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            sqlHelper.getWritableDatabase().insert(DbConstant.tablename, null, contentValues);
                            Log.d("hbgfd", "bvfc");
                            intentToMain();
                        }

                    }
                }


                break;
            case R.id.CancelBtn:
                //TODO how to make that if there is no changes dont alert dialogd
                if (DbConstant.isInternetAct==false) {
                    if (!MovieNameET.getText().toString().equals("")) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(AddEditAct.this);
                        alert.setIcon(R.drawable.exclamationark);
                        //getResources().getString(R.string.nassage);
                        alert.setMessage("ALL CHANGES WILL BE LOST\n" + "ARE YOU SURE YOU WANT TO BACK")
                                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_righ);
                                        dialog.cancel();
                                    }
                                })
                                .setNegativeButton("STAY", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        alert.setTitle("WARNING");
                        alert.create().show();
                    }
                }
                else
                {
                    finish();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_righ);
                }
                break;
        }

    }

    public class DownLoadBody extends AsyncTask<String, Long, String> {
        //if AddEditAct start From InternetAct Auto start AsyncTask to download the Body
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            // popup progressdialog while loading movie body
            progressDialog = new ProgressDialog(AddEditAct.this);
            progressDialog.setTitle("loading data! \n please wait.....");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            //get the api of the ImdbID
            StringBuilder response = null;
            response = new StringBuilder();
            try {
                URL website = new URL(params[0]);
                URLConnection connection = website.openConnection();
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                connection.getInputStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    response.append(inputLine);
                in.close();

            } catch (Exception ee) {
                ee.printStackTrace();
            }
            if (response == null) {
                response.append("error");
            }

            return response.toString();


        }

        @Override
        protected void onPostExecute(String resultObject) {
            //convert Api to JSON and SetText to BodyET
            try {
                if (resultObject == "error") {
                    Toast.makeText(AddEditAct.this, "DID NOT FIND MOVIE,PLEASE TRY AGAIN.", Toast.LENGTH_SHORT).show();
                } else {
                    JSONObject mainObject = new JSONObject(resultObject);
                    Log.d("************dsds", resultObject);
                    BodyET.setText(mainObject.getString("Plot"));
                    RatingET.setText(mainObject.getString("imdbRating"));
                    progressDialog.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

 /*  public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(AddEditAct.this);
            progressDialog.setTitle("loading data! \n please wait.....");
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
        }

        //when pressed the ShowBtn start the imageUrl AsyncTask
        protected Bitmap doInBackground(String... urls) {
            //get the image from Url
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        protected void onPostExecute(Bitmap result) {
            //set the image to image view
            bitmap = result;
            imagebase64 = encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 100);
            movieIV.setImageBitmap(decodeBase64(imagebase64));
            //linearLayout.setBackground(movieIV.getDrawable());
            progressDialog.dismiss();
            activity_add_edit.setFocusable(true);
            activity_add_edit.getDrawingCache(true);

        }

    }

    ProgressDialog progressDialog;*/

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (resultCode == -1) {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                imagebase64 = encodeToBase64(image, Bitmap.CompressFormat.JPEG, 100);
                movieIV.setImageBitmap(decodeBase64(imagebase64));
                linearLayout.setBackground(movieIV.getDrawable());
            }

        }
        //TODO this request code doeasnt work
        if (requestCode==4)
        {
            if (resultCode==-1)
            {
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                UrlET.setText(picturePath);
                cursor.close();
                Picasso.with(AddEditAct.this).load(selectedImage).into(movieIV);
                //TODo save this path to database
            }
        }

    }



    public void intentToMain() {
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_righ);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 2);

                } else {

                    Toast.makeText(this, "access denied,no permission", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'switch' lines to check for other
            // permissions this app might request
        }
    }
}
