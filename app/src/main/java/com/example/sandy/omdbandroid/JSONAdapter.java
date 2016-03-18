package com.example.sandy.omdbandroid;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by sandy on 16/03/2016.
 */
public class JSONAdapter extends BaseAdapter {
    private static final String IMAGE_URL_BASE = "http://www.omdbapi.com/?i=tt0076759&plot=short&r=json/";

    Context mContext;
    LayoutInflater mInflater;
    JSONArray mJsonArray;

    public JSONAdapter(Context context, LayoutInflater inflater) {
        mContext = context;
        mInflater = inflater;
        mJsonArray = new JSONArray();
    }

    @Override
    public int getCount() {
        return mJsonArray.length();
    }

    @Override
    public Object getItem(int position) {
        return mJsonArray.optJSONObject(position);
    }

    @Override
    public long getItemId(int position) {
        // your particular dataset uses String IDs
        // but you have to put something in this method
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        // check if the view already exists
        // if so, no need to inflate and findViewById again!
        if (convertView == null) {

            // Inflate the custom row layout from your XML.
            convertView = mInflater.inflate(R.layout.row_film, null);

            // create a new "Holder" with subviews
            holder = new ViewHolder();
            holder.thumbnailImageView = (ImageView) convertView.findViewById(R.id.img_thumbnail);
            holder.titleTextView = (TextView) convertView.findViewById(R.id.text_title);
            holder.yearTextView = (TextView) convertView.findViewById(R.id.text_year);

            // hang onto this holder for future recyclage
            convertView.setTag(holder);
        } else {

            // skip all the expensive inflation/findViewById
            // and just get the holder you already made
            holder = (ViewHolder) convertView.getTag();
        }
        // Get the current film's data in JSON form
        JSONObject jsonObject = (JSONObject) getItem(position);


        if (jsonObject.has("Poster")) {

            // Construct the image URL (specific to API)
            String imageURL = jsonObject.optString("Poster");
            Log.d("omg android", imageURL);

            // Use Picasso to load the image
            // Temporarily have a placeholder in case it's slow to load
            Picasso.with(mContext).load(imageURL).placeholder(R.drawable.movies).into(holder.thumbnailImageView);
        } else {

            // If there is no cover ID in the object, use a placeholder
            holder.thumbnailImageView.setImageResource(R.drawable.movies);
        }

        // Grab the title and year from the JSON
        String filmTitle = "";
        String year = "";


        if (jsonObject.has("Title")) {
            filmTitle = jsonObject.optString("Title");
        }

        if (jsonObject.has("Year")) {
            year = jsonObject.optString("Year");
        }


        // Send these Strings to the TextViews for display
        holder.titleTextView.setText(filmTitle);
        holder.yearTextView.setText(year);

        return convertView;
    }

    // this is used so you only ever have to do
    // inflation and finding by ID once ever per View
    private static class ViewHolder {
        public ImageView thumbnailImageView;
        public TextView titleTextView;
        public TextView yearTextView;
    }

    public void updateData(JSONArray jsonArray) {
        // update the adapter's dataset
        //Log.d("omg android", jsonArray.toString());
        mJsonArray = jsonArray;
        notifyDataSetChanged();
    }
}

