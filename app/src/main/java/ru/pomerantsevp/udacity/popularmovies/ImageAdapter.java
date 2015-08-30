package ru.pomerantsevp.udacity.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by pavel on 8/30/15.
 */
class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<JSONObject> mMoviesList;

    public ImageAdapter(Context c) {
        mContext = c;
        mMoviesList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mMoviesList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMoviesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PosterImageView imageView;
        if (convertView == null) {
            imageView = new PosterImageView(mContext);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (PosterImageView) convertView;
        }

        String imageUrl = "";
        try {
            imageUrl = SharedConstants.IMAGE_PATH_PREFIX +
                    mMoviesList.get(position).getString(SharedConstants.POSTER_PATH);
        } catch (JSONException e) {
            // ignore
        } finally {
            Picasso.with(mContext).load(imageUrl).into(imageView);
            return imageView;
        }
    }

    public void clear() {
        mMoviesList.clear();
        notifyDataSetChanged();
    }

    public void add(JSONObject movie) {
        mMoviesList.add(movie);
        notifyDataSetChanged();
    }
}
