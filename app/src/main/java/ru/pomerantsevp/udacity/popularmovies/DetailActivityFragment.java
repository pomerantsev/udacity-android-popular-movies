package ru.pomerantsevp.udacity.popularmovies;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Activity activity = getActivity();
        Intent intent = activity.getIntent();
        if (intent != null && intent.hasExtra(SharedConstants.MOVIE_KEY)) {
            Movie movie = intent.getParcelableExtra(SharedConstants.MOVIE_KEY);
            String imageUrl = SharedConstants.IMAGE_PATH_PREFIX + movie.poster_path;
            ImageView poster = (ImageView) rootView.findViewById(R.id.poster);
            Picasso.with(activity)
                    .load(imageUrl)
                    .placeholder(R.drawable.loading_image)
                    .into(poster);

            TextView title = (TextView) rootView.findViewById(R.id.title);
            title.setText(movie.original_title);

            TextView releaseDate = (TextView) rootView.findViewById(R.id.release_date);
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy");
            try {
                Date date = inputFormat.parse(movie.release_date);
                releaseDate.setText(outputFormat.format(date));
            } catch (ParseException e) {}

            TextView rating = (TextView) rootView.findViewById(R.id.rating);
            rating.setText(movie.vote_average + "/10");

            TextView plotSynopsis = (TextView) rootView.findViewById(R.id.plot_synopsis);
            plotSynopsis.setText(movie.overview);
        }

        return rootView;
    }
}
