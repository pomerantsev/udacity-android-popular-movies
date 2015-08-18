package ru.pomerantsevp.udacity.popularmovies;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Activity activity = getActivity();
        Intent intent = activity.getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            try {
                JSONObject movie = new JSONObject(intent.getStringExtra(Intent.EXTRA_TEXT));

                String imageUrl = SharedConstants.IMAGE_PATH_PREFIX +
                        movie.getString(SharedConstants.POSTER_PATH);
                ImageView poster = (ImageView) rootView.findViewById(R.id.poster);
                Picasso.with(activity).load(imageUrl).into(poster);

                TextView title = (TextView) rootView.findViewById(R.id.title);
                title.setText(movie.getString("original_title"));

                TextView releaseDate = (TextView) rootView.findViewById(R.id.release_date);
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy");
                try {
                    Date date = inputFormat.parse(movie.getString("release_date"));
                    releaseDate.setText(outputFormat.format(date));
                } catch (ParseException e) {
                    // ignore
                }

                TextView rating = (TextView) rootView.findViewById(R.id.rating);
                rating.setText(movie.getString("vote_average") + "/10");

                TextView plotSynopsis = (TextView) rootView.findViewById(R.id.plot_synopsis);
                plotSynopsis.setText(movie.getString("overview"));
            } catch (JSONException e) {
                // ignore
            }
        }

        return rootView;
    }
}
