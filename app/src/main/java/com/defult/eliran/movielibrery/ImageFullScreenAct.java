package com.defult.eliran.movielibrery;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class ImageFullScreenAct extends AppCompatActivity {
ImageView FullScreenIV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO show me the old image
        setContentView(R.layout.activity_image_full_screen);
        FullScreenIV= (ImageView) findViewById(R.id.FullScreenIV);
       Intent intent=getIntent();
        /// intent.getStringExtra("bitmap");
       String imagepath= intent.getStringExtra(DbConstant.imagebase64);
        Bitmap bitmap=decodeBase64(imagepath);

        FullScreenIV.setImageBitmap(bitmap);

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

}
