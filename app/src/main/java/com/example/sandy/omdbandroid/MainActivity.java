package com.example.sandy.omdbandroid;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener {

    TextView mainTextView;
    Button mainButton;
    EditText mainEditText;
    EditText mainEditYear;
    ListView mainListView;
    ProgressDialog mDialog;
    //ArrayAdapter mArrayAdapter;

    JSONAdapter mJSONAdapter;
    ArrayList mNameList = new ArrayList();
    ShareActionProvider mShareActionProvider;
    private static final String PREFS = "prefs";
    private static final String PREF_NAME = "name";
    SharedPreferences mSharedPreferences;

    //private static final String QUERY_URL = "http://openlibrary.org/search.json?q=";
    private static final String QUERY_URL="http://www.omdbapi.com/?s=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.sandy.omdbandroid.R.layout.activity_main);

        // 1. Access the TextView defined in layout XML
        // and then set its text
        mainTextView = (TextView) findViewById(com.example.sandy.omdbandroid.R.id.main_textview);

        // 2. Access the Button defined in layout XML
        // and listen for it here
        mainButton = (Button) findViewById(com.example.sandy.omdbandroid.R.id.main_button);
        mainButton.setOnClickListener(this);

        // 3. Access the EditText defined in layout XML
        mainEditText = (EditText) findViewById(com.example.sandy.omdbandroid.R.id.main_edittext);

        // 4. Access the ListView
        mainListView = (ListView) findViewById(com.example.sandy.omdbandroid.R.id.main_listview);

        // 5. Set this activity to react to list items being pressed
        mainListView.setOnItemClickListener(this);

        /*
        // Create an ArrayAdapter for the ListView
        mArrayAdapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1,
                mNameList);

        // Set the ListView to use the ArrayAdapter
        mainListView.setAdapter(mArrayAdapter);
        */

        Toolbar toolbar = (Toolbar) findViewById(com.example.sandy.omdbandroid.R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(com.example.sandy.omdbandroid.R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // 7. Greet the user, or ask for their name if new
        displayWelcome();

        // 10. Create a JSONAdapter for the ListView
        mJSONAdapter = new JSONAdapter(this, getLayoutInflater());

        // Set the ListView to use the ArrayAdapter
        mainListView.setAdapter(mJSONAdapter);

        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Searching for Film");
        mDialog.setCancelable(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu.
        // Adds items to the action bar if it is present.
        getMenuInflater().inflate(com.example.sandy.omdbandroid.R.menu.menu_main, menu);

        // Access the Share Item defined in menu XML
        MenuItem shareItem = menu.findItem(com.example.sandy.omdbandroid.R.id.menu_item_share);

        // Access the object responsible for
        // putting together the sharing submenu
        if (shareItem != null) {
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        }

        // Create an Intent to share your content
        setShareIntent();

        return true;
    }

    private void setShareIntent() {

        if (mShareActionProvider != null) {

            // create an Intent with the contents of the TextView
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Android Development");
            shareIntent.putExtra(Intent.EXTRA_TEXT, mainTextView.getText());

            // Make sure the provider knows
            // it should work with that Intent
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override
    public void onClick(View v) {

        // 9. Take what was typed into the EditText and use in search
        String filmTitle = mainEditText.getText().toString().replace(' ', '+');
        //Log.d("omg android", filmTitle);
        String queryString = filmTitle + "&y=&plot=short&r=json";
        //Log.d("omg android", queryString);
        queryFilms(queryString);

        // 6. The text you'd like to share has changed,
        // and you need to update
        setShareIntent();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        // 12. Now that the user's chosen a book, grab the cover data
        JSONObject jsonObject = (JSONObject) mJSONAdapter.getItem(position);
        String imageURL = jsonObject.optString("Poster","");
        String title = jsonObject.optString("Title","");
        String year = jsonObject.optString("Year", "");
        String director = jsonObject.optString("Director", "");
        String rated = jsonObject.optString("Rated", "");
        String imdbRating = jsonObject.optString("imdbRating", "");
        String imdbID = jsonObject.optString("imdbID", "");

        //Log.d("omg android", jsonObject.toString());

        Log.d("omg android", jsonObject.optString("title",""));

        // create an Intent to take you over to a new DetailActivity
        Intent detailIntent = new Intent(this, DetailActivity.class);

        // pack away the data about the cover
        // into your Intent before you head out
        detailIntent.putExtra("posterURL", imageURL);

        // TODO: add any other data you'd like as Extras
        detailIntent.putExtra("title", title);
        detailIntent.putExtra("year", year);
        detailIntent.putExtra("imdbID", imdbID);
        detailIntent.putExtra("director", director);
        detailIntent.putExtra("imdbRating", imdbRating);
        detailIntent.putExtra("rated", rated);

        // start the next Activity using your prepared Intent
        startActivity(detailIntent);
    }

    public void displayWelcome() {

        // Access the device's key-value storage
        mSharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE);

        // Read the user's name,
        // or an empty string if nothing found
        String name = mSharedPreferences.getString(PREF_NAME, "");

        if (name.length() > 0) {

            // If the name is valid, display a Toast welcoming them
            Toast.makeText(this, "Welcome back, " + name + "!", Toast.LENGTH_LONG).show();
        } else {

            // otherwise, show a dialog to ask for their name
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Hello!");
            alert.setMessage("What is your name?");

            // Create EditText for entry
            final EditText input = new EditText(this);
            alert.setView(input);

            // Make an "OK" button to save the name
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int whichButton) {

                    // Grab the EditText's input
                    String inputName = input.getText().toString();

                    // Put it into memory (don't forget to commit!)
                    SharedPreferences.Editor e = mSharedPreferences.edit();
                    e.putString(PREF_NAME, inputName);
                    e.commit();

                    // Welcome the new user
                    Toast.makeText(getApplicationContext(), "Welcome, " + inputName + "!", Toast.LENGTH_LONG).show();
                }
            });

            // Make a "Cancel" button
            // that simply dismisses the alert
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int whichButton) {}
            });

            alert.show();
        }
    }

    private void queryFilms(String searchString) {

        // Prepare your search string to be put in a URL
        // It might have reserved characters or something
        String urlString = "";
        try {
            urlString = URLEncoder.encode(searchString, "UTF-8");
        } catch (UnsupportedEncodingException e) {

            // if this fails for some reason, let the user know why
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        // Create a client to perform networking
        AsyncHttpClient client = new AsyncHttpClient();

        // Show ProgressDialog to inform user that a task in the background is occurring
        mDialog.show();

        String URL = QUERY_URL + searchString;//urlString;
        Log.d("omg android", "URL: " + URL);

        // Have the client get a JSONArray of data
        // and define how to respond
        client.get(URL,//QUERY_URL urlString,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        // 11. Dismiss the ProgressDialog
                        mDialog.dismiss();
                        // Display a "Toast" message
                        // to announce your success


                        // 8. For now, just log results
                        Log.d("omg android", jsonObject.toString());
                        // update the data in your custom method.

                        JSONArray result = jsonObject.optJSONArray("Search");
                        if (result != null) {
                            mJSONAdapter.updateData(result);
                            Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Film Not Found :-)", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Throwable throwable, JSONObject error) {
                        // 11. Dismiss the ProgressDialog
                        mDialog.dismiss();
                        // Display a "Toast" message
                        // to announce the failure
                        Toast.makeText(getApplicationContext(), "Error: " + statusCode + " " + throwable.getMessage(), Toast.LENGTH_LONG).show();

                        // Log error message
                        // to help solve any problems
                        Log.e("omg android", statusCode + " " + throwable.getMessage());
                    }
                });
    }
}
