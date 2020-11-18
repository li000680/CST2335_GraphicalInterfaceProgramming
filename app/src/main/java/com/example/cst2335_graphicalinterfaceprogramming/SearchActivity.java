package com.example.cst2335_graphicalinterfaceprogramming;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
/**
 * The class is used to search covid data according to user's input
 *  @author June Li
 * @version 1.0
 */
public class SearchActivity extends AppCompatActivity {
    /**
     * The ArrayList is used to store search result
     */
    ArrayList<SearchResult> resultList = new ArrayList<>();
    /**
     * The ArrayList is used to store the country and date of search result you want to save
     */
    ArrayList<String> favorites=new ArrayList<>();
    /**
     * Adapter used to listview
     */
    MyAdapter myAdapter= new MyAdapter();;
    /**
     * ProgressBar is used to show download progress
     */
    ProgressBar progressBar;
    /**
     * The fQuery is used to call execute to get data from website
     */
    CovidQuery fQuery = new CovidQuery();
    /**
     * These variables is the elementary of each search
     */
    String country,fromDate,endDate,province,date;
    int caseNumber;
    SearchResult newSearch;
    ListView listView;
    @Override
    /**
     * The method is the entry of execute,it equivalent to main method
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        country = getIntent().getStringExtra("country");
        fromDate = getIntent().getStringExtra("fromDate");
        endDate = getIntent().getStringExtra("endDate");
        listView = findViewById(R.id.listView);
        TextView headerView=findViewById(R.id.header);
        String urlString = "https://api.covid19api.com/country/" + country + "/status/confirmed/live?from=" + fromDate + "T00:00:00Z&to=" + endDate + "T00:00:00Z";
        fQuery.execute(urlString);
        progressBar = findViewById(R.id.bar);
        progressBar.setVisibility(View.VISIBLE);
        Button saveButton = findViewById(R.id.save);
        saveButton.setOnClickListener(clk -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Do you want to save to favorite list?")
                    //What is the message:
                    .setPositiveButton("Yes", (click, arg) -> {
                        favorites.add(country + " from " + fromDate + " to " + endDate);
                        Toast.makeText(this, "Add successfully!", Toast.LENGTH_LONG).show();

                    })
                    .setNegativeButton("No", (click, arg) -> {
                       // final TextView myTextView = findViewById(R.id.topText);
                        Snackbar.make(headerView,"You didn't do anything!",BaseTransientBottomBar.LENGTH_SHORT).show();
                    })
                    .setView(getLayoutInflater().inflate(R.layout.alert_layout, null))
                    .create().show();
        });
        listView.setOnItemClickListener((list, view, position, id) -> {
            Intent detailsIntent = new Intent(this, DetailsActivity.class);

            detailsIntent.putExtra("country", resultList.get(position).getCountry());
            detailsIntent.putExtra("province", resultList.get(position).getProvince());
            detailsIntent.putExtra("cases", resultList.get(position).getCase()+"");
            detailsIntent.putExtra("date", resultList.get(position).getDate());
            startActivity(detailsIntent);
        });
    }
    /**
     * The inner class is used to read date from website using JSON
     */
    private class CovidQuery extends AsyncTask< String, Integer, String> {
        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This will normally run on a background thread. But to better
         * support testing frameworks, it is recommended that this also tolerates
         * direct execution on the foreground thread, as part of the {@link #execute} call.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param args The parameters of the task.
         * @return A result, defined by the subclass of this task.
         */
        @Override
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
                        JSONObject job = json.getJSONObject(i);
                        province = job.getString("Province");
                        caseNumber=job.getInt("Cases");
                        date = job.getString("Date");
                        newSearch= new SearchResult(country,province,caseNumber,date);
                        resultList.add(newSearch);
                        //myAdapter.notifyDataSetChanged();
                       publishProgress(i*100/json.length());
                    }
                }
            }
            catch (Exception e) {  e.printStackTrace();}
            return "done";
        }

        /**
         * Runs on the UI thread after {@link #publishProgress} is invoked.
         * The specified values are the values passed to {@link #publishProgress}.
         * The default version does nothing.
         *
         * @param values The values indicating progress.
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(values[0]);
        }

        /**
         * <p>Runs on the UI thread after {@link #doInBackground}. The
         * specified result is the value returned by {@link #doInBackground}.
         * To better support testing frameworks, it is recommended that this be
         * written to tolerate direct execution as part of the execute() call.
         * The default version does nothing.</p>
         *
         * <p>This method won't be invoked if the task was cancelled.</p>
         *
         * @param fromDoInBackground The result of the operation computed by {@link #doInBackground}.
         */
        @Override
        protected void onPostExecute(String fromDoInBackground) {
           // super.onPostExecute(s);
            Log.i("HTTP", fromDoInBackground);
            listView.setAdapter(myAdapter);
        }

//        //protected void onPostExecute(String fromDoInBackground)
//        {
//            Log.i("HTTP", fromDoInBackground);
//
//        }
    }
    /**
     * The inner class is used for listView
     */
    protected class MyAdapter extends BaseAdapter {
        /**
         * How many items are in the data set represented by this Adapter.
         *
         * @return Count of items.
         */
        @Override
        public int getCount() {
            return resultList.size();
        }

        /**
         * Get the data item associated with the specified position in the data set.
         *
         * @param position Position of the item whose data we want within the adapter's
         *                 data set.
         * @return The data at the specified position.
         */
        @Override
        public SearchResult getItem(int position){
            return resultList.get(position);
        }

        /**
         * Get the row id associated with the specified position in the list.
         *
         * @param position The position of the item within the adapter's data set whose row id we want.
         * @return The id of the item at the specified position.
         */
        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * Get a View that displays the data at the specified position in the data set. You can either
         * create a View manually or inflate it from an XML layout file. When the View is inflated, the
         * parent View (GridView, ListView...) will apply default layout parameters unless you use
         * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
         * to specify a root view and to prevent attachment to the root.
         *
         * @param position    The position of the item within the adapter's data set of the item whose view
         *                    we want.
         * @param old         The old view to reuse, if possible. Note: You should check that this view
         *                    is non-null and of an appropriate type before using. If it is not possible to convert
         *                    this view to display the correct data, this method can create a new view.
         *                    Heterogeneous lists can specify their number of view types, so that this View is
         *                    always of the right type (see {@link #getViewTypeCount()} and
         *                    {@link #getItemViewType(int)}).
         * @param parent      The parent that this view will eventually be attached to
         * @return A View corresponding to the data at the specified position.
         */
        @Override
        public View getView(int position, View old, ViewGroup parent) {
            SearchResult sr = getItem(position);
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.activity_search_result, parent, false);
                if (sr != null) {
                    TextView searchView = view.findViewById(R.id.searchResult);
                    searchView.setText(sr.getDate().substring(0,10)+":"+sr.getProvince()+":"+sr.getCase());
                }
            return view;
            }

    }
}