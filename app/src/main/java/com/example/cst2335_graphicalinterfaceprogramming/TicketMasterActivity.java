package com.example.cst2335_graphicalinterfaceprogramming;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.ContentValues;
import android.content.Context;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TicketMasterActivity extends AppCompatActivity {
    String city;
    String radius;
    private List<TicketEvent> list = new ArrayList<>();
    MyListAdapter myAdapter;
    SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_master);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        ListView myList=findViewById(R.id.the_list);
        myList.setAdapter(myAdapter = new MyListAdapter());

        EditText City = findViewById(R.id.addEditText1);
        EditText Radius = findViewById(R.id.addEditText2);
        Button searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(click -> {
            if (( City.getText().toString().length() == 0 ) ||
                    ( Radius.getText().toString().length() == 0 )) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Error!")
                        .setMessage("Please input city and radius")
                        .setPositiveButton("OK", (click_ok, arg) -> {
                        })
                        .create().show();
            } else {
                city = City.getText().toString();
                radius = Radius.getText().toString();
                ForecastQuery req = new ForecastQuery();
                req.execute(city, radius);
            }
        });
    }

    public String establishConnection(String reqUrl) {
        String result=null;
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream response = urlConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            result = sb.toString();
        } catch (MalformedURLException e) {
            Log.e("TicketMasterActivity", "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e("TicketMasterActivity", "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e("TicketMasterActivity", "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e("TicketMasterActivity", "Exception: " + e.getMessage());
        }
        return result;
    } // End establishConnection()


    private class ForecastQuery extends AsyncTask<String, Integer, String> {
        protected int size = 0;
        protected int totalElement = 0;
        protected int totalPages = 0;

        private JSONObject getPageParameter(String reqUrl) {
            JSONObject page = null;
            try {
                page = new JSONObject(establishConnection(reqUrl));
            } catch (Exception e) {
                Log.e("TicketMasterActivity", "Exception: " + e.getMessage());
            }
            try {
                size = page.getJSONObject("page").getInt("size");
                totalElement = page.getJSONObject("page").getInt("totalElements");
                totalPages = page.getJSONObject("page").getInt("totalPages");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("getPageParameter", "size: "+ size + "totalElement: "+ totalElement +"totalPages: "+totalPages);
            return page;
        }

        //Type3                      Type1
        public String doInBackground(String... args) {
            try {
                int count = 0;
                String nextPage = "";
                String url = "https://app.ticketmaster.com/discovery/v2/events.json?apikey=naG2r1v48w2ubfr6icXhrvmlWvBW8dmz&" +
                        "city=" + args[0] + "&radius=" + args[1];
                Log.d("doInBackground",url);
                //url = "https://app.ticketmaster.com/discovery/v2/events.json?apikey=naG2r1v48w2ubfr6icXhrvmlWvBW8dmz&city=ottawa&radius=2";
                JSONObject jsonObj = getPageParameter(url);
                JSONArray ticketArray = jsonObj.getJSONObject("_embedded").getJSONArray("events");
                totalElement = (totalElement < size)?totalElement:size;
                for (int k = 0; k < totalElement; k++) {
                    JSONObject price;
                    JSONObject event = ticketArray.getJSONObject(k);
                    JSONArray Array = event.getJSONArray("images");
                    JSONObject image = Array.getJSONObject(0);
                    if(event.has("priceRanges")){
                        Array = event.getJSONArray("priceRanges");
                        price = Array.getJSONObject(0);
                    }
                    else
                    {
                        price = new JSONObject("{min:0, max:0, currency:'CA'}");
                    }


                    list.add(new TicketEvent(event.getString("name"),
                            event.getString("url"),
                            image.getString("url"),
                            args[0],
                            event.getJSONObject("dates").getJSONObject("start").getString("localDate"),
                            event.getJSONObject("dates").getJSONObject("start").getString("localTime"),
                            price.getString("min"),
                            price.getString("max"),
                            price.getString("currency")
                    ));
                    count++;
                    publishProgress(( count * 100 ) / totalElement, count);
                }
                if(count > 0){
                    publishProgress(100, count);
                }
            } catch (Exception e) {
                Log.e("doInBackground", e.toString());
            }

            return "Done";
        }

        public void onProgressUpdate(Integer... args) {
            ProgressBar progressBar = findViewById(R.id.progressBar);
            progressBar.setProgress(args[0]);
            //myAdapter.notifyDataSetChanged();
            Log.d("onProgressUpdate", "Update progress bar to: " + args[0] +"  " +args[1]);
        }

        public void onPostExecute(String fromDoInBackground) {
            Toast toast;
            //Toast toast = Toast.makeText(TicketMasterActivity.this.getApplicationContext(), getResources().getString(R.string.toast), Toast.LENGTH_LONG);
            if(totalElement > 0) {
                toast = Toast.makeText(TicketMasterActivity.this.getApplicationContext(), totalElement+" events has been loaded successfylly!", Toast.LENGTH_LONG);
            }
            else {
                toast = Toast.makeText(TicketMasterActivity.this.getApplicationContext(), " Sorry, No events found!", Toast.LENGTH_LONG);
            }
            toast.show();
        }
    }

    private class MyListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public TicketEvent getItem(int position) {
            return list.get(position);
        }

        @Override
        public View getView(int position, View old, ViewGroup parent) {
            View row = null;
            TicketEvent event = getItem(position);
            LayoutInflater inflater= getLayoutInflater();//this loads xml layouts
            if(event != null){
                    row=inflater.inflate(R.layout.row_layout, parent, false);
                    TextView tView = row.findViewById(R.id.name);
                    tView.setText(event.getName());
            }
            return row;
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).getId();
        }
    }
}