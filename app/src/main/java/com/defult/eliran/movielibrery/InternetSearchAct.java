package com.defult.eliran.movielibrery;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class InternetSearchAct extends AppCompatActivity {
    //searching a movie on the internet with AsyncTask
    EditText SearchET;
    ListView IntMovieLV;
    Button GoBtn;
    Button CancelBtn;
    SqlHelper sqlHelper;
    protected ArrayAdapter searchadapter;
    protected ArrayList<MovieObj> AllSearchMovie;
    String LinkToApi = "http://www.omdbapi.com/?s=";
    String BodyId;
    String body;
    String url;
    String moviename;
    TextView searchTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_search);
        SearchET = (EditText) findViewById(R.id.SearchIntET);
        IntMovieLV = (ListView) findViewById(R.id.IntSrchLV);
        GoBtn = (Button) findViewById(R.id.SearchIntBtn);
        CancelBtn = (Button) findViewById(R.id.CancelIntBtn);
        searchTV = (TextView) findViewById(R.id.searchIntTV);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        sqlHelper = new SqlHelper(this);
        AllSearchMovie = new ArrayList<>();
        searchadapter = new ArrayAdapter(this, R.layout.internetitem, R.id.InternetItemTV, AllSearchMovie);
        IntMovieLV.setAdapter(searchadapter);
        CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cancel the internet search activity
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_righ);
            }
        });
        SearchET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH)
                {
                    searchonInternet();
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return  true;
                }
                return false;
            }
        });
        GoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start the AsyncTask to search a movies
                //Start AsyncTask to omdb and get Movies list
                searchonInternet();
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


            }
        });
        IntMovieLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieObj currentMovie = AllSearchMovie.get(position);
                Intent intent = new Intent(InternetSearchAct.this, AddEditAct.class);
                intent.putExtra("moviename", currentMovie.MovieName);
                intent.putExtra("imagepath", currentMovie.Url);
                intent.putExtra("body", currentMovie.imdbID);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                Log.d("dsdsd", "sds");

            }
        });
    }


    public class DownLoadWebSite extends AsyncTask<String, Long, String> {
        //accsess to omdb site and searching movies
        ProgressDialog progressDialog;
        boolean noconnection = false;

        @Override
        protected void onPreExecute() {
            //popup progress dialog while searching movies
            progressDialog = new ProgressDialog(InternetSearchAct.this);
            progressDialog.setTitle("please wait...");
            progressDialog.setCancelable(false);
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "cancel search", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    progressDialog.dismiss();
                }
            });
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            // get api movies

            StringBuilder response = null;
            if (!new CheckConnection(InternetSearchAct.this).isNetworkAvailable()) {

                noconnection = true;
                progressDialog.dismiss();
                //Toast.makeText(InternetSearchAct.this, "no internet connection", Toast.LENGTH_SHORT).show();
            }
            if (noconnection == false) {
                try {
                    URL website = new URL(params[0]);
                    URLConnection connection = website.openConnection();
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(
                                    connection.getInputStream()));
                    response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null)
                        response.append(inputLine);
                    in.close();

                } catch (Exception ee) {

                    ee.printStackTrace();
                }
            }

            if (response == null) {
                response = new StringBuilder();
                response.append("error");
            }
            return response.toString();
        }
        @Override
        protected void onPostExecute(String JsonResults) {
            //convert api to JSON and save it to arreylist
            try {
                if (JsonResults.contains("Error")) {
                    Toast.makeText(InternetSearchAct.this, "MOVIE NOT FOUND,PLEASE TRY AGAIN.", Toast.LENGTH_SHORT).show();
                    AllSearchMovie.clear();
                    progressDialog.dismiss();
                } else if (JsonResults.equals("error")) {
                    Toast.makeText(InternetSearchAct.this, "no internet connection", Toast.LENGTH_SHORT).show();
                } else {
                    JSONObject mainObject = new JSONObject(JsonResults);

                    JSONArray resultsArray = mainObject.getJSONArray("Search");
                    Log.d("dsd", "dsds");
                    AllSearchMovie.clear();

                    for (int i = 0; i < resultsArray.length(); i++) {
                        JSONObject currentObject = resultsArray.getJSONObject(i);
                        moviename = currentObject.getString("Title");
                        url = currentObject.getString("Poster");
                        BodyId = currentObject.getString("imdbID");
                        MovieObj movieObj = new MovieObj(moviename, url, BodyId);
                        AllSearchMovie.add(movieObj);

                    }
                }
                progressDialog.dismiss();
                //TextView srchTV= (TextView) findViewById(R.id.searchIntTV);
                GoBtn.requestFocus();
                searchadapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public  void  searchonInternet()
    {
        DownLoadWebSite downLoadWebSite = new DownLoadWebSite();
        String searchet = SearchET.getText().toString();
        searchet=searchet.trim();
        if (searchet.length()>0) {
            try {
                String url = URLEncoder.encode(searchet, "UTF-8");
                downLoadWebSite.execute(LinkToApi + url);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        else {
            Toast.makeText(InternetSearchAct.this, "please insert name of movie", Toast.LENGTH_SHORT).show();
        }

        searchTV.requestFocus();
    }
}

