package com.example.cst2335_graphicalinterfaceprogramming;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    ArrayList<SearchResult> resultList = new ArrayList<>();
    MyAdapter myAdapter;
    ProgressBar progressBar;
    CovidQuery fQuery = new CovidQuery();
    String country,fromDate,endDate,province,date;
    int caseNumber;
    SearchResult newSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        country=getIntent().getStringExtra("country");
        fromDate=getIntent().getStringExtra("fromDate");
        endDate=getIntent().getStringExtra("endDate");
        ListView listView=findViewById(R.id.listView);
            String urlString="https://api.covid19api.com/country/"+country+"/status/confirmed/live?from="+fromDate+"T00:00:00Z&to="+endDate+"T00:00:00Z";
            fQuery.execute(urlString);
        myAdapter = new MyAdapter();
        listView.setAdapter(myAdapter);
        progressBar= findViewById(R.id.bar);
        progressBar.setVisibility(View.VISIBLE);
        Button saveButton=findViewById(R.id.save);

    }

    //This class needs 4 functions to work properly:
//
    private class CovidQuery extends AsyncTask< String, Integer, String> {

        public String doInBackground(String ... args)
        {
            try {
                URL url = new URL(args[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream response = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                String result = sb.toString(); //result is the whole string
                JSONArray json = new JSONArray(result);
                if (json.length() > 0) {
                    for (int i=0;i<json.length();i++) {
                        JSONObject job = json.getJSONObject(i);  // 遍历 jsonarray 数组，把每一个对象转成 json 对象
                        province = job.getString("Province");// 得到 每个对象中的属性值
                        caseNumber=job.getInt("Cases");
                        date = job.getString("Date");
                        newSearch= new SearchResult(country,province,caseNumber,date);
                        resultList.add(newSearch);
                        publishProgress(i/json.length()*100);
                    }
                }
            }
            catch (Exception e) {  e.printStackTrace();}
            return "done";
        }
                //Type 2
        protected void onProgressUpdate(Integer ... values)
        {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(values[0]);
        }
        //Type3
        protected void onPostExecute(String fromDoInBackground)
        {
            Log.i("HTTP", fromDoInBackground);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    protected class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return resultList.size();
        }
        @Override
        public SearchResult getItem(int position){
            return resultList.get(position);
        }
        @Override
        public View getView(int position, View old, ViewGroup parent) {
            SearchResult sr = getItem(position);
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.activity_search_result, parent, false);
                if (sr != null) {
                    TextView resultView = view.findViewById(R.id.searchResult);
                    resultView.setText(sr.getDate().substring(0,10)+":"+sr.getProvince() + ":" + sr.getCase());
                }
            return view;
            }
        @Override
        public long getItemId(int position) {
            return position;
        }
    }
}