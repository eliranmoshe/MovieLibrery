package com.defult.eliran.movielibrery;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AlertDialogLayout;
import android.support.v7.widget.PopupMenu;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends AppCompatActivity {
    SqlHelper sqlHelper;
    Cursor cursor;
    ListView MovieLV;
    customAdapter adapter;
    int CurrentPosition;
    int Id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MovieLV = (ListView) findViewById(R.id.MovieLV);
        sqlHelper = new SqlHelper(this);
        cursor = sqlHelper.getReadableDatabase().query(DbConstant.tablename, null, null, null, null, null, null);
        adapter=new customAdapter(this,cursor);
        MovieLV.setAdapter(adapter);
        registerForContextMenu(MovieLV);



        MovieLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //get the cuurent movie ID that the user touched
                cursor.moveToPosition(i);
                Id = cursor.getInt(cursor.getColumnIndex(DbConstant.idname));
                intenttoedit(""+Id);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        FloatingActionButton floatingActionButton= (FloatingActionButton) findViewById(R.id.PlusBtn);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.widget.PopupMenu popupMenu = new android.widget.PopupMenu(getApplicationContext(), v);
                popupMenu.inflate(R.menu.popup_menu);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new android.widget.PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        DbConstant.isEditAct = false;
                        switch (item.getItemId()) {
                            case R.id.InternetAddIten:
                                //on internet item we intent to InternetSearchAct
                                Intent Internetintent = new Intent(MainActivity.this, InternetSearchAct.class);
                                startActivity(Internetintent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                DbConstant.isInternetAct = true;
                                break;
                            case R.id.ManualAddItem:
                                //on manual item we intent to AddEditAct
                                Intent Manualintent1 = new Intent(MainActivity.this, AddEditAct.class);
                                startActivity(Manualintent1);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                DbConstant.isInternetAct = false;

                                break;
                        }
                        return true;
                    }
                });
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //open the option menu on the up bar with DeleteAll or Exit the app
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //here we choose what the exit/clear all items does
        if (item.getItemId() == R.id.ExitItem) {
            //Exit the App
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setMessage("DO YOU WANT TO EXIT?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            alert.setTitle("QUIT");
            alert.create().show();
        }
        if (item.getItemId() == R.id.DelAllItem) {
            //Delete all Data from Sql
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setMessage("THIS WILL DELETE ALL MOVIES !!!!\n ARE YOU SURE YOU WANT TO DELETE ALL???")
                    .setPositiveButton("DELETE ALL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sqlHelper.getWritableDatabase().delete(DbConstant.tablename, null, null);
                            cursor = sqlHelper.getReadableDatabase().query(DbConstant.tablename, null, null, null, null, null, null);
                            adapter.swapCursor(cursor);
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton("KEEP MOVIES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            alert.setTitle("WARNING");

            alert.create().show();
        }
        if (item.getItemId()==R.id.DelWatchItem)
        {
            cursor=sqlHelper.getReadableDatabase().query(DbConstant.tablename,null,null,null,null,null,null);
            while (cursor.moveToNext())
            {
                int id=cursor.getInt(cursor.getColumnIndex(DbConstant.idname));
                if (cursor.getString(cursor.getColumnIndex(DbConstant.ismarkedCB)).equals("1"))
                {
                    sqlHelper.getWritableDatabase().delete(DbConstant.tablename,"_id=?",new String[]{""+id});
                }

            }
            cursor = sqlHelper.getReadableDatabase().query(DbConstant.tablename, null, null, null, null, null, null);
            adapter.swapCursor(cursor);
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        //open the context menu with Delete or Edit current movie
        CurrentPosition = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
        getMenuInflater().inflate(R.menu.context_menu, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)  {
        //here we choose what the delete/edit  items does
        cursor.moveToPosition(CurrentPosition);
        Id = cursor.getInt(cursor.getColumnIndex(DbConstant.idname));
        if (item.getItemId() == R.id.EditItem) {
            //call the intent to AddEditAct
            intenttoedit("" + Id);

        }
        if (item.getItemId() == R.id.DelItem) {
            //put alert dialog to delete the current movie
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setMessage(getResources().getString(R.string.Deletemovie))
                    .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String[] wherearg = new String[]{"" + Id};
                            sqlHelper.getWritableDatabase().delete(DbConstant.tablename, "_id=?", wherearg);
                            cursor = sqlHelper.getReadableDatabase().query(DbConstant.tablename, null, null, null, null, null, null);
                            adapter.swapCursor(cursor);
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton("KEEP MOVIE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            alert.setTitle("WARNING");
            alert.create().show();
        }
        if (item.getItemId()==R.id.SahreItem)
        {
            String ImdbID=cursor.getString(cursor.getColumnIndex(DbConstant.ImdbID));
            String website="http://www.imdb.com/title/"+ImdbID+"/?ref_=fn_al_tt_1";
            Intent sharingIntent=new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Movie Details");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, website);
            startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.SearchET)));
        }
        return true;
    }

    @Override
    protected void onResume() {
        //refresh the ListView while back from other activity
        cursor = sqlHelper.getReadableDatabase().query(DbConstant.tablename, null, null, null, null, null, null);
        adapter.swapCursor(cursor);
        super.onResume();
    }

    public void intenttoedit(String id) {
        //start an intent to AddEditAct with All data about the movie that the user choose
        Intent intent = new Intent(MainActivity.this, AddEditAct.class);
        MovieObj editmovie=new MovieObj(cursor.getString(cursor.getColumnIndex(DbConstant.moviename)),cursor.getString(cursor.getColumnIndex(DbConstant.urlpath)),
                cursor.getString(cursor.getColumnIndex(DbConstant.body)), cursor.getString(cursor.getColumnIndex(DbConstant.rating)),
                cursor.getString(cursor.getColumnIndex(DbConstant.imagebase64)),id );
        intent.putExtra("editmovie",editmovie);
        DbConstant.isEditAct = true;
        DbConstant.isInternetAct = false;
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

}
