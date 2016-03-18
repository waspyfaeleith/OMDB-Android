package com.example.sandy.omdbandroid;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by sandy on 16/03/2016.
 */
public class DetailActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String IMAGE_URL_BASE = "http://covers.openlibrary.org/b/id/"; // 13
    private static final String QUERY_URL = "http://www.omdbapi.com/?i=";
    String mImageURL; // 13
    String mTitle;
    String mYear;
    String mRating;
    String mDirector;
    String mImdbRating;
    String mImdbId;
    String mPlot;

    ShareActionProvider mShareActionProvider; // 14

    @Override
    public void onClick(View v) {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("omg android", this.getIntent().toString());

        // Tell the activity which XML layout is right
        setContentView(com.example.sandy.omdbandroid.R.layout.activity_detail);

        // Enable the "Up" button for more navigation options
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitle = this.getIntent().getExtras().getString("title");
        mYear = this.getIntent().getExtras().getString("year");
        //mRating = this.getIntent().getExtras().getString("rated");
        /*mImdbRating = this.getIntent().getExtras().getString("imdbRating");
        mDirector = this.getIntent().getExtras().getString("director");*/
        mImdbId = this.getIntent().getExtras().getString("imdbID");
        queryFilms(mImdbId);

        FloatingActionButton fab = (FloatingActionButton) findViewById(com.example.sandy.omdbandroid.R.id.fabFilmDetails);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showSimplePopUp();
            }
        });

        FloatingActionButton fabPlot = (FloatingActionButton) findViewById(com.example.sandy.omdbandroid.R.id.fabFilmPlot);
        fabPlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showPlotPopUp();
            }
        });

        // Access the imageview from XML
        ImageView imageView = (ImageView) findViewById(com.example.sandy.omdbandroid.R.id.img_cover);

        // 13. unpack the imageURL from its trip inside your Intent
        String imageURL = this.getIntent().getExtras().getString("posterURL");

        // See if there is a valid imageURL
        if (imageURL.length() > 0) {

            // Use the ID to construct an image URL
            mImageURL = imageURL;

            // Use Picasso to load the image
            Picasso.with(this).load(mImageURL).placeholder(com.example.sandy.omdbandroid.R.drawable.movies).into(imageView);
        }
    }

    private void showSimplePopUp() {

        AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
        Log.d("omg android", " popup data - Rated: " + mRating + " IMDB Rating: " + mImdbRating + " Director: " + mDirector);
        String popUpTitle = mTitle + " - " + mYear ;
        String filmDetails = "\n\tRating:\t" + mRating + "\n\tDirector:\t" + mDirector + "\n\tIMDB Rating:\t" + mImdbRating;
        helpBuilder.setTitle(popUpTitle);
        helpBuilder.setMessage(filmDetails);
        helpBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                    }
                });

        // Remember, create doesn't show the dialog
        AlertDialog helpDialog = helpBuilder.create();
        helpDialog.show();
    }

    private void showPlotPopUp() {

        AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
        Log.d("omg android", " popup data - Rated: " + mRating + " IMDB Rating: " + mImdbRating + " Director: " + mDirector);
        String popUpTitle = mTitle + " - " + mYear ;
        String filmPlot = mPlot;
        helpBuilder.setTitle(popUpTitle);
        helpBuilder.setMessage(filmPlot);
        helpBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                    }
                });

        // Remember, create doesn't show the dialog
        AlertDialog helpDialog = helpBuilder.create();
        helpDialog.show();
    }

    private void queryFilms(String imdbId) {

        // Prepare your search string to be put in a URL
        // It might have reserved characters or something
        String urlString = "";
        try {
            urlString = URLEncoder.encode(imdbId, "UTF-8");
        } catch (UnsupportedEncodingException e) {

            // if this fails for some reason, let the user know why
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        // Create a client to perform networking
        AsyncHttpClient client = new AsyncHttpClient();


        String URL = QUERY_URL + imdbId;//urlString;
        Log.d("omg android", "URL: " + URL);

        // Have the client get a JSONArray of data
        // and define how to respond
        client.get(URL,//QUERY_URL urlString,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        // 8. For now, just log results
                        Log.d("omg android", jsonObject.toString());
                        mRating = jsonObject.optString("Rated");
                        mImdbRating =  jsonObject.optString("imdbRating");
                        mDirector = jsonObject.optString("Director");
                        mPlot = jsonObject.optString("Plot");
                        Log.d("omg android", "Rated: " + mRating + " IMDB Rating: " + mImdbRating + " Director: " + mDirector);
                    }

                    @Override
                    public void onFailure(int statusCode, Throwable throwable, JSONObject error) {
                        // Display a "Toast" message
                        // to announce the failure
                        Toast.makeText(getApplicationContext(), "Error: " + statusCode + " " + throwable.getMessage(), Toast.LENGTH_LONG).show();

                        // Log error message
                        // to help solve any problems
                        Log.e("omg android", statusCode + " " + throwable.getMessage());
                    }
                });
    }

    private void setShareIntent() {

        // create an Intent with the contents of the TextView
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT,
                "Film Recommendation!");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mImageURL);

        // Make sure the provider knows
        // it should work with that Intent
        mShareActionProvider.setShareIntent(shareIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu
        // this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.example.sandy.omdbandroid.R.menu.menu_main, menu);

        // Access the Share Item defined in menu XML
        MenuItem shareItem = menu.findItem(com.example.sandy.omdbandroid.R.id.menu_item_share);

        // Access the object responsible for
        // putting together the sharing submenu
        if (shareItem != null) {
            mShareActionProvider
                    = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        }

        setShareIntent();

        return true;
    }
}
