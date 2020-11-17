package com.example.cst2335_graphicalinterfaceprogramming;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
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

public class Covid19Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid19);
        EditText country1 = findViewById(R.id.country);
        EditText fromDate1= findViewById(R.id.fromDate);
        EditText endDate1=findViewById(R.id.endDate);
        Button searchButton= findViewById(R.id.entrySearch);
        searchButton.setOnClickListener(clk->{
            Intent searchIntent= new Intent(this,SearchActivity.class);
            searchIntent.putExtra("country",country1.getText().toString());
            searchIntent.putExtra("fromDate",fromDate1.getText().toString());
            searchIntent.putExtra("endDate",endDate1.getText().toString());
            startActivity(searchIntent);
        });
        //add to the database and get the new ID
//            ContentValues newRowValues = new ContentValues();
//            newRowValues.put(MyOpener.COL_COUNTRY, country);
//            newRowValues.put(MyOpener.COL_PROVOINCE, province);
//            newRowValues.put(MyOpener.COL_CASE, caseNumber);
//            newRowValues.put(MyOpener.COL_DATE, date);
//            //Insert in the database:
//            long newId = db.insert(MyOpener.TABLE_NAME, null, newRowValues);

        //Create the Message object
//            SearchResult newSearch = new SearchResult(country, fQuery.getProvince(),fQuery.caseNumber,date);
//            //Add the new message to the list:
//            resultList.add(newSearch);
//            //update the listView:
//            myAdapter.notifyDataSetChanged();
//            //clear the EditText fields:
    }

    //This class needs 4 functions to work properly:
//    protected class MyAdapter extends BaseAdapter {
//        @Override
//        public int getCount() {
//            return resultList.size();
//        }
//        @Override
//        public SearchResult getItem(int position){
//            return resultList.get(position);
//        }
//        @Override
//        public View getView(int position, View old, ViewGroup parent) {
//            SearchResult sr = getItem(position);
//            LayoutInflater inflater = getLayoutInflater();
//            View view = inflater.inflate(R.layout.activity_search_result, parent, false);
//            if (sr != null) {
//                    TextView resultView = view.findViewById(R.id.resultView);
//                    resultView.setText(sr.getCountry());
//                    resultView.setText(sr.getDate());
//                }
//            return view;
//        }
//        @Override
//        public long getItemId(int position) {
//            return getItem(position).getId();
//        }
//    }
//        private class CovidQuery extends AsyncTask< String, Integer, String> {
//            private String province,date;
//            private int caseNumber;
//
//            public String doInBackground(String ... args)
//            {
//                try {
//                    URL url=new URL(args[0]);
//                    //open the connection
//                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                    //wait for data:
//                    InputStream response = urlConnection.getInputStream();
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
//                    StringBuilder sb = new StringBuilder();
//                    String line = null;
//                    while ((line = reader.readLine()) != null)
//                    {
//                        sb.append(line + "\n");
//                    }
//                    String result = sb.toString(); //result is the whole string
//                    // convert string to JSON: Look at slide 27:
//                    JSONObject detail = new JSONObject(result);
//                    //get the double associated with "value"
//                    province = detail.getString("province");
//                    caseNumber=detail.getInt("case");
//                    date=detail.getString("Date");
//                }
//                catch (Exception e) {  e.printStackTrace();}
//                return "done";
//            }
//
//            //Type 2
//            protected void onProgressUpdate(Integer ... values)
//            {
//                progressBar.setVisibility(View.VISIBLE);
//                progressBar.setProgress(values[0]);
//            }
//            //Type3
//            protected void onPostExecute(String fromDoInBackground)
//            {
////            Log.i("HTTP", fromDoInBackground);
////            TextView currentTemp=findViewById(R.id.current);
////            currentTemp.setText("Current Temperature: "+value);
////            TextView minTemp=findViewById(R.id.min);
////            minTemp.setText("Min Temperature: "+min);
////            TextView maxTemp=findViewById(R.id.max);
////            maxTemp.setText("Max Temperature: "+max);
////            TextView uvValue=findViewById(R.id.uv);
////            uvValue.setText("The UV Rating: "+uv);
////            ImageView iconImage=findViewById(R.id.image);
////            iconImage.setImageBitmap(image);
////            progressBar.setVisibility(View.INVISIBLE);
//            }
//            public boolean fileExistance(String fname){
//                File file = getBaseContext().getFileStreamPath(fname);
//                return file.exists();
//            }
//            public String getProvince(){
//                return province;
//            }
//            public int getCaseNumber(){
//                return caseNumber;
//            }
//        }
}