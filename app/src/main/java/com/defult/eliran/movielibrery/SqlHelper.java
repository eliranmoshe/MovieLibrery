package com.defult.eliran.movielibrery;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by eliran on 2/7/2017.
 */

public class SqlHelper extends SQLiteOpenHelper {
    //create al SqlTable
    public SqlHelper(Context context) {
        super(context, "MovieLabrery.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+DbConstant.tablename+"(_id INTEGER PRIMARY KEY AUTOINCREMENT, "+DbConstant.moviename+" TEXT ,"+DbConstant.urlpath+" TEXT ,"+DbConstant.ImdbID+" TEXT ,"+DbConstant.rating+" TEXT ,"+DbConstant.ismarkedCB+" TEXT ,"+DbConstant.imagebase64+" TEXT ,"+DbConstant.body+" TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
