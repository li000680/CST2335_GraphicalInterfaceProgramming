package com.example.cst2335_graphicalinterfaceprogramming;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * The class is the main responsible the main page funtion
 *  @author Jianchuan Li
 * @version 1.0
 */
public class RecipeSearchToolBar extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    public final static String ITEM_TITLE = "TITLE";
    public final static String ITEM_URL = "URL";
    public final static String ITEM_INGREDIENTS = "INGREDIENTS";
    public final static String ITEM_ID = "_id";
    private ProgressBar pb;
    ArrayList<Recipes> elements = new ArrayList<>();
    public MyListAdapter myAdapter;
//    private SQLiteDatabase db;

    ListView recipeList;

    /** prefs is used to store the recipe and ingredients input by user last time.*/

    SharedPreferences prefs = null;
  //  RecipeSearchDetailsFragment dFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_search_tool_bar);
        prefs = getSharedPreferences("FileName", Context.MODE_PRIVATE);
        pb = findViewById(R.id.progressBar);
        pb.setVisibility(View.VISIBLE);
        recipeList=findViewById(R.id.listfavorite);
        boolean isTablet = findViewById(R.id.frameLayout) != null;
      //  RecipeSearchMyOpener dbOpener = new RecipeSearchMyOpener(this);


     //   db = dbOpener.getWritableDatabase();



        /** savedString is used to store the recipe */
        String savedString = prefs.getString("Recipe", "");
        /** et is the editText where users input the recipe*/
        EditText et = findViewById(R.id.editRecipe);
        /** set the text to be the recipe input by user last time.*/
        et.setText(savedString);
        /** When users input, there is a toast indicating that only one recipe can be accepted.*/
        et.setOnClickListener(v -> Toast.makeText(this, getResources().getString(R.string.Recipe_toast_message), Toast.LENGTH_LONG).show());
        /** savedString1 is used to store the ingredients */
        String savedString1 = prefs.getString("Ingredients", "");
        /** et1 is the editText where users input the ingredients*/
        EditText et1 = findViewById(R.id.editIngredients);
        /** set the text to be the ingredients input by user last time.*/
        et1.setText(savedString1);


        /** When users input, there is a snackBar indicating that ingredients should be separated by comma.*/
        et1.setOnClickListener(v -> Snackbar.make(et1,getResources().getString(R.string.Ingredients_snackBar_message),Snackbar.LENGTH_LONG).show());


        /** search button is for search information which would show in next activity.*/
        Button search = findViewById(R.id.button);
        search.setOnClickListener(bt -> {
                    /** the recipe and ingredients would be storage in files in the cellphone.*/
                    String recipe=et.getText().toString();
                    String ingredients=et1.getText().toString();
                    if (recipe.trim().equals("") || ingredients.trim().equals("")) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                        alertDialogBuilder.setTitle(getResources().getString(R.string.RecipeInputError))
                                .setMessage(getResources().getString(R.string.RecipeErrorMessage))
                                .setNeutralButton("OK", (click, args) -> {
                                })
                                .create().show();
                    } else {

                        elements.clear();
                        saveSharedPrefs(recipe);
                        saveSharedPrefs1(ingredients);
                        ForecastQuery req = new ForecastQuery();
                        req.execute("http://www.recipepuppy.com/api/?i=", ingredients, "&q=", recipe, "&format=xml");

                    }
                });

        recipeList.setOnItemClickListener((list, item, position, id) -> {
            //Create a bundle to pass data to the new fragment
            Bundle dataToPass = new Bundle();
            dataToPass.putString(ITEM_TITLE, elements.get(position).getTitle() );

            dataToPass.putLong(ITEM_ID, id);
            dataToPass.putString(ITEM_URL, elements.get(position).getHref());
            dataToPass.putString(ITEM_INGREDIENTS, elements.get(position).getIngredients());
            if(isTablet)
            {
                //add a DetailFragment
                RecipeSearchDetailsFragment dFragment = new RecipeSearchDetailsFragment();
                dFragment.setArguments( dataToPass ); //pass it a bundle for information
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayout, dFragment) //Add the fragment in FrameLayout
                        .commit(); //actually load the fragment. Calls onCreate() in DetailFragment
            }
            else //isPhone
            {
                Intent nextActivity = new Intent(this, RecipeSearchEmptyActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivity(nextActivity); //make the transition
            }
        });

                //This gets the toolbar from the layout:
        Toolbar tBar = (Toolbar)findViewById(R.id.recipeSearchtoolbar);

        /**This loads the toolbar, which calls onCreateOptionsMenu below*/
        setSupportActionBar(tBar);
        //For NavigationDrawer:
        DrawerLayout drawer = findViewById(R.id.recipeSearchdrawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, tBar, R.string.recipeSearchopen, R.string.recipeSearchclose);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        setResult(500);

    }

    /** saveSharedPrefs is used to store the recipe input by user last time.*/
    private void saveSharedPrefs(String stringToSave){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Recipe", stringToSave);
        editor.commit();
    }
    /** saveSharedPrefs1 is used to store the ingredients input by user last time.*/
    private void saveSharedPrefs1 (String stringToSave){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Ingredients", stringToSave);
        editor.commit();
    }

    /**
     * Initialize the contents of the Activity's standard options menu.
     *
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.recipe_search_menu, menu);




        return true;
    }
    /**
     * This method is called whenever an item in your options menu is selected.
     *
     * @param item The menu item that was selected. Help would show the help file, go to login would lead to main page,
     * favorate leads to saved recipes/
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String message = null;
        //Look at your menu XML file. Put a case for every id in that file:
        switch(item.getItemId())
        {
            //what to do when the menu item is selected:
            case R.id.recipeSearchFavorite:
                Intent nextPage = new Intent(this, ListOfRecipes.class);
                startActivity(nextPage);
                break;



            case R.id.recipeSearchHelp:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getResources().getString(R.string.RecipeHelp) + ": ")
                        .setMessage(getResources().getString(R.string.WelcomeRecipeSearch)+getResources().getString(R.string.editRecipe))
                        .setNeutralButton(getResources().getString(R.string.recipeAlertNB), (click, b) -> { })
                        .create().show();
                break;
            case R.id.recipeSearchLogin:
                Intent nextPage1 = new Intent(this, MainActivity.class);
                startActivity(nextPage1);
                break;

        }

        return true;
    }
    /**
     * Called when an item in the navigation menu is selected.
     *
     * @param item The selected item like in ToolBar Items.
     * @return true to display the item as the selected item
     */
    @Override
    public boolean onNavigationItemSelected( MenuItem item) {

        String message = null;

        switch(item.getItemId())
        {

            case R.id.navigation_home:
                Intent nextPage1 = new Intent(this, MainActivity.class);
                startActivity(nextPage1);
                break;
            case R.id.navigation_recipe:
                Intent nextPage2 = new Intent(this, TicketMasterActivity.class);
                startActivity(nextPage2);
                break;
            case R.id.navigation_covid:
                Intent nextPage3 = new Intent(this,  Covid19Activity.class);
                startActivity(nextPage3);
                break;
            case R.id.navigation_audio:
                Intent nextPage4 = new Intent(this,  TheAudioDatabase.class);
                startActivity(nextPage4);
                break;
            }



        DrawerLayout drawerLayout = findViewById(R.id.recipeSearchdrawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);


        return false;
    }
        /** visit http://www.recipepuppy.com/api and get the recipes in the background*/
        private class ForecastQuery extends AsyncTask<String, Integer, String> {
            //string variables for the UV, min, max, and current temperature
            String title;
            String href;
            String ingredients;
            /**
             * this method to perform a computation on a background thread. The
             * specified parameters are the parameters passed to {@link #execute}
             * by the caller of this task.
             *
             * @param args The parameters of the task
             * @return A result, defined by the subclass of this task
             */

            @Override
            public String doInBackground(String... args) {
                try {
                    String a = URLEncoder.encode(args[1], "UTF-8");
                    //create a URL object of what server to contact:
                    URL url = new URL(args[0] + a + args[2] + args[3] +args[4]);

                    //open the connection
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                    //wait for data:
                    InputStream response = urlConnection.getInputStream();



                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(false);
                    XmlPullParser xpp = factory.newPullParser();
                    xpp.setInput(response, "UTF-8"); //response is data from the server




                    int eventType = xpp.getEventType(); //The parser is currently at START_DOCUMENT

                    while (eventType != XmlPullParser.END_DOCUMENT) {

                        if (eventType == XmlPullParser.START_TAG) {
                            //If you get here, then you are pointing at a start tag
                            if (xpp.getName().equals("title")) {
                                //If you get here, then you are pointing to a <Weather> start tag
                                xpp.next();
                                title = xpp.getText().trim();
                                publishProgress(25);
                            }else if (xpp.getName().equals("href")) {
                                xpp.next();
                                href = xpp.getText().trim();
                                publishProgress(50);
                            }else if (xpp.getName().equals("ingredients")) {
                                xpp.next();
                                ingredients = xpp.getText().trim();
                                publishProgress(75);
                                Recipes recipe = new Recipes(title, href, ingredients);
                                elements.add(recipe);
                            }

                        }


                        eventType = xpp.next(); //move to the next xml event and store it in a variable
                    }

                } catch (Exception e) {
                    Log.i(String.valueOf(e), "not connected");
                }
                publishProgress(100);
                return "done";
            }

            /**
             * Runs on the UI thread after {@link #publishProgress} is invoked.
             * The specified values are the values passed to {@link #publishProgress}.
             * The default version does nothing.
             * @param value The values indicating progress.
             */
            //Type 2
            public void onProgressUpdate(Integer... value) {
                pb.setVisibility(View.VISIBLE);
                pb.setProgress(value[0]);

            }
            /**
             * <p>Runs on the UI thread after {@link #doInBackground}. The
             * specified result is the value returned by {@link #doInBackground}.
             * <p>If searching recipes that been found greater than 0, it shows the number of recipes has been loaded.
             * Otherwise no recipe found.
             * <p> the progress bar become invisible and recipes titles shows in lists
             * @param fromDoInBackground The result of the operation computed by {@link #doInBackground}.
             */
            //Type3
            public void onPostExecute(String fromDoInBackground) {
                Toast toast;
                if(elements.size() > 0) {
                    toast = Toast.makeText(RecipeSearchToolBar.this.getApplicationContext(), elements.size()+" "+getResources().getString(R.string.recipeLoad), Toast.LENGTH_LONG);
                }
                else {
                    toast = Toast.makeText(RecipeSearchToolBar.this.getApplicationContext(), getResources().getString(R.string.recipeNoFound), Toast.LENGTH_LONG);
                }
                toast.show();
                recipeList.setAdapter(myAdapter = new MyListAdapter());
                pb.setVisibility(View.INVISIBLE);
            }


        }
        /**
         * The inner class is an adapter for ListView
         */
        private class MyListAdapter extends BaseAdapter {
            /**
             * Method counts how many recipes are in a list
             * @return number of recipes in a list
             */
            @Override
            public int getCount() {
                return elements.size();
            }
            /**
             * Method gets a recipe from a list
             * @param position position of a recipe in a list
             * @return recipe
             */
            @Override
            public Object getItem(int position) {
                return elements.get(position);
            }
            /**
             * Method gets a event's id from a database
             * @param position position of a event in a list
             * @return id of a event
             */
            @Override
            public long getItemId(int position) {
                return elements.get(position).getId();
            }
            /**
             * Method returns a view for a recipe
             * @param position position of a event in a list
             * @param convertView recycled view
             * @param parent view that can contain other views
             * @return view of a event (row to the ListView)
             */
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = getLayoutInflater();
                View newView = inflater.inflate(R.layout.recipe_list_view, parent, false);

/** In listView, only the title of recipe would be shown*/
                TextView tView = newView.findViewById(R.id.recipeTitle);
                tView.setText(elements.get(position).getTitle());

                return newView;

            }

        }
//    protected void showMessage(int position)
//    {
//        Recipes selectedRecipe = elements.get(position);
//        isTablet= findViewById(R.id.fragmentLocation) != null;
//        View contact_view = getLayoutInflater().inflate(R.layout.recipe_search_favorite_edit, null);
//
//        TextView rowTitle = contact_view.findViewById(R.id.recipeTitle);
//        TextView rowURL = contact_view.findViewById(R.id.recipeURL);
//        TextView rowIngredients = contact_view.findViewById(R.id.recipeIngredients);
//
//        //set the fields for the alert dialog
//        rowTitle.setText(selectedRecipe.getTitle());
//        rowURL.setText(selectedRecipe.getHref());
//        rowIngredients.setText(selectedRecipe.getIngredients());
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(selectedRecipe.getTitle())
//                .setMessage(getResources().getString(R.string.recipeURL)+ selectedRecipe.getHref()+"\n"+getResources().getString(R.string.recipeIngredients)+selectedRecipe.getIngredients())
//                .setView(contact_view) //add the 3 edit texts showing the contact information
//                .setPositiveButton(getResources().getString(R.string.recipeSearchSave), (click, b) -> {
//                    selectedRecipe.update(rowTitle.getText().toString(), rowURL.getText().toString(),rowIngredients.getText().toString());
//                    updateMessage(selectedRecipe);
//                    myAdapter.notifyDataSetChanged(); //the recipe_search_help.png and name have changed so rebuild the list
//                })
//                .setNegativeButton(getResources().getString(R.string.recipeSearchDelete), (click, b) -> {
//                    deleteMessage(selectedRecipe); //remove the contact from database
//                    elements.remove(position);
//                    if(isTablet){
//                        getSupportFragmentManager().beginTransaction().remove(dFragment).commit();}//remove the contact from contact list
//                    myAdapter.notifyDataSetChanged(); //there is one less item so update the list
//                })
//                .setNeutralButton(getResources().getString(R.string.recipeAlertNB), (click, b) -> { })
//                .create().show();
//    }
//        protected void updateMessage(Recipes c)
//        {
//            //get a database connection:
//
//            //Create a ContentValues object to represent a database row:
//            ContentValues updatedValues = new ContentValues();
//            updatedValues.put(RecipeSearchMyOpener.COL_TITLE, c.getTitle());
//            updatedValues.put(RecipeSearchMyOpener.COL_URL, c.getHref());
//            updatedValues.put(RecipeSearchMyOpener.COL_INGREDIENTS, c.getIngredients());
//           // updatedValues.put(RecipeSearchMyOpener.COL_ID, c.getId());
//
//
//
//            //Now insert in the database:
//            long newId = db.insert(RecipeSearchMyOpener.TABLE_NAME, null, updatedValues);
//            //now call the update function:
//            //db.update(RecipeSearchMyOpener.TABLE_NAME, updatedValues, RecipeSearchMyOpener.COL_ID + "= ?", new String[] {Long.toString(c.getId())});
//        }

//        protected void deleteMessage(Recipes c)
//        {
//            db.delete(RecipeSearchMyOpener.TABLE_NAME, RecipeSearchMyOpener.COL_ID + "= ?", new String[] {Long.toString(c.getId())});
//        }
//        protected  void loadDataFromDatabase()
//        {
//
//
//            // We want to get all of the columns. Look at MyOpener.java for the definitions:
//            String [] columns = {RecipeSearchMyOpener.COL_ID, RecipeSearchMyOpener.COL_TITLE, RecipeSearchMyOpener.COL_URL, RecipeSearchMyOpener.COL_INGREDIENTS};
//            //query all the results from the database:
//            Cursor results = db.query(false, RecipeSearchMyOpener.TABLE_NAME, columns, null, null, null, null, null, null);
//
//
//            //Now the results object has rows of results that match the query.
//            //find the column indices:
//            int titleIndex = results.getColumnIndex(RecipeSearchMyOpener.COL_TITLE);
//            int urlIndex = results.getColumnIndex(RecipeSearchMyOpener.COL_URL);
//            int ingredientsIndex=results.getColumnIndex(RecipeSearchMyOpener.COL_INGREDIENTS);
//            int idColIndex = results.getColumnIndex(RecipeSearchMyOpener.COL_ID);
//
//            //iterate over the results, return true if there is a next item:
//            while(results.moveToNext())
//            {
//                String title = results.getString(titleIndex);
//                String url = results.getString(urlIndex);
//                String ingredients = results.getString(ingredientsIndex);
//                long id = results.getLong(idColIndex);
//                // Log.i(String.valueOf(id),msg+sendButtonIsClicked);
//                //add the new Contact to the array list:
//                elements.add(new Recipes(id, title, url,ingredients));
//            }

            //At this point, the contactsList array has loaded every row from the cursor.
//        }
    }