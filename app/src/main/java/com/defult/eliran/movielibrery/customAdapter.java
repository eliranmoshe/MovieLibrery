package com.defult.eliran.movielibrery;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by eliran on 2/12/2017.
 */

public class customAdapter extends CursorAdapter {
    TextView movieItemTV;
    ImageView movieIV;
    CheckBox watchcB;
    SqlHelper sqlHelper;
    TextView RatingItemTV;


    public customAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.movie_list_item, null);

        return v;
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        sqlHelper = new SqlHelper(context);
        watchcB = (CheckBox) view.findViewById(R.id.WatchCB);
        watchcB.setTag(R.id.WatchCB, cursor.getInt(cursor.getColumnIndex(DbConstant.idname)));
        movieItemTV = (TextView) view.findViewById(R.id.MovieItemTV);
        movieIV = (ImageView) view.findViewById(R.id.ItemIV);
        RatingItemTV = (TextView) view.findViewById(R.id.RatingItemTV);

        if (cursor.getString(cursor.getColumnIndex(DbConstant.ismarkedCB)).equals("1")) {
            watchcB.setChecked(true);
        } else {
            watchcB.setChecked(false);
        }
        String moviename=cursor.getString(cursor.getColumnIndex(DbConstant.moviename));
        movieItemTV.setText(moviename);
        RatingItemTV.setText(cursor.getString(cursor.getColumnIndex(DbConstant.rating)));
       /* if (cursor.getString(cursor.getColumnIndex(DbConstant.imagebase64)).equals("")) {
            movieIV.setImageResource(R.drawable.video);
        } else {*/

        movieIV.setImageBitmap(decodeBase64(cursor.getString(cursor.getColumnIndex(DbConstant.imagebase64))));
        movieIV.setTag(R.id.movieIV,cursor.getString(cursor.getColumnIndex(DbConstant.imagebase64)));
        Log.d("vdsvsd","fdfsds");
        //}
        watchcB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DbConstant.ismarkedCB, "1");
                    // int id=cursor.getInt(cursor.getColumnIndex(DbConstant.idname));
                    int id1 = (int) buttonView.getTag(R.id.WatchCB);
                    sqlHelper.getWritableDatabase().update(DbConstant.tablename, contentValues, "_id=?", new String[]{"" + id1});
                    Log.d("fdsf", "fdsfds");
                }
                if (isChecked == false) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DbConstant.ismarkedCB, "0");
                    int id1 = (int) buttonView.getTag(R.id.WatchCB);
                    sqlHelper.getWritableDatabase().update(DbConstant.tablename, contentValues, "_id=?", new String[]{"" + id1});
                }

            }
        });
        movieIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ImageFullScreenAct.class);
                String s= (String) v.getTag(R.id.movieIV);

                intent.putExtra(DbConstant.imagebase64, s);

                context.startActivity(intent);
            }
        });
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
