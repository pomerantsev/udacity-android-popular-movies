package ru.pomerantsevp.udacity.popularmovies;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import ru.pomerantsevp.udacity.popularmovies.data.Movie;
import ru.pomerantsevp.udacity.popularmovies.data.MovieContract;
import ru.pomerantsevp.udacity.popularmovies.data.MovieService;
import ru.pomerantsevp.udacity.popularmovies.data.Review;
import ru.pomerantsevp.udacity.popularmovies.data.ReviewsResponse;
import ru.pomerantsevp.udacity.popularmovies.data.Trailer;
import ru.pomerantsevp.udacity.popularmovies.data.TrailersResponse;

public class DetailActivityFragment extends Fragment {

    public static final String TAG = DetailActivityFragment.class.getName();
    private static final String MOVIE_TAG = "movie";

    private boolean mFavorite;
    private Movie mMovie;
    private Button mFavoriteButton;
    private List<Trailer> mTrailers;
    private ListView mTrailersListView;
    private List<Review> mReviews;
    private ListView mReviewsListView;
    private MenuItem mShareMenuItem;

    public DetailActivityFragment() {
    }

    public static DetailActivityFragment newInstance(Movie movie) {
        DetailActivityFragment f = new DetailActivityFragment();
        Bundle args = new Bundle();
        args.putParcelable(MOVIE_TAG, movie);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);
        mShareMenuItem = menu.findItem(R.id.action_share_trailer);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share_trailer) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, mTrailers.get(0).getYoutubeUrl());
            startActivity(Intent.createChooser(shareIntent,
                    getString(R.string.share_dialog_title)));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Activity activity = getActivity();
        Intent intent = activity.getIntent();
        if (intent != null && intent.hasExtra(SharedConstants.MOVIE_KEY)) {
            mMovie = intent.getParcelableExtra(SharedConstants.MOVIE_KEY);
        } else if (getArguments() != null) {
            mMovie = getArguments().getParcelable(MOVIE_TAG);
        }
        if (mMovie != null) {
            rootView.findViewById(R.id.content_container).setVisibility(View.VISIBLE);
            String imageUrl = SharedConstants.IMAGE_PATH_PREFIX + mMovie.poster_path;
            ImageView poster = (ImageView) rootView.findViewById(R.id.poster);
            Picasso.with(activity)
                    .load(imageUrl)
                    .placeholder(R.drawable.loading_image)
                    .into(poster);

            TextView title = (TextView) rootView.findViewById(R.id.title);
            title.setText(mMovie.original_title);

            TextView releaseDate = (TextView) rootView.findViewById(R.id.release_date);
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy");
            try {
                Date date = inputFormat.parse(mMovie.release_date);
                releaseDate.setText(outputFormat.format(date));
            } catch (ParseException e) {}
              catch (NullPointerException e) {}


            TextView rating = (TextView) rootView.findViewById(R.id.rating);
            rating.setText(mMovie.vote_average + "/10");

            mFavoriteButton = (Button) rootView.findViewById(R.id.favorite);
            Cursor findMovieCursor = getActivity().getContentResolver().query(
                    MovieContract.MovieEntry.buildMovieUri(mMovie.id),
                    null, null, null, null
            );
            if (findMovieCursor.getCount() == 0) {
                setFavoriteView(false);
            } else {
                setFavoriteView(true);
            }
            findMovieCursor.close();
            mFavoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggleFavorite();
                }
            });

            TextView plotSynopsis = (TextView) rootView.findViewById(R.id.plot_synopsis);
            plotSynopsis.setText(mMovie.overview);

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint("http://api.themoviedb.org")
                    .build();

            MovieService movieService = restAdapter.create(MovieService.class);

            mTrailersListView = (ListView) rootView.findViewById(R.id.trailers_list);
            mTrailersListView.setEmptyView(rootView.findViewById(R.id.trailers_empty));

            mTrailersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(mTrailers.get(position).getYoutubeUrl())
                    ));
                }
            });

            movieService.listTrailers(Integer.toString(mMovie.id), getString(R.string.movie_db_key),
                    new Callback<TrailersResponse>() {
                        @Override
                        public void success(TrailersResponse trailersResponse, Response response) {
                            mTrailers = new ArrayList<>(Arrays.asList(trailersResponse.results));
                            TrailersAdapter trailersAdapter = new TrailersAdapter(
                                    getActivity(),
                                    mTrailers
                            );
                            mTrailersListView.setAdapter(trailersAdapter);
                            if (!mTrailers.isEmpty()) {
                                mShareMenuItem.setVisible(true);
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                        }
                    });

            mReviewsListView = (ListView) rootView.findViewById(R.id.reviews_list);
            mReviewsListView.setEmptyView(rootView.findViewById(R.id.reviews_empty));

            movieService.listReviews(Integer.toString(mMovie.id), getString(R.string.movie_db_key),
                    new Callback<ReviewsResponse>() {
                        @Override
                        public void success(ReviewsResponse reviewsResponse, Response response) {
                            mReviews = new ArrayList<>(Arrays.asList(reviewsResponse.results));
                            ReviewsAdapter reviewsAdapter = new ReviewsAdapter(
                                    getActivity(),
                                    mReviews
                            );
                            mReviewsListView.setAdapter(reviewsAdapter);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                        }
                    });
        } else {
            rootView.findViewById(R.id.content_container).setVisibility(View.GONE);
        }

        return rootView;
    }

    private void setFavoriteView(boolean favorite) {
        mFavorite = favorite;
        mFavoriteButton.setCompoundDrawablesWithIntrinsicBounds(
                favorite ? android.R.drawable.star_on : android.R.drawable.star_off, 0, 0, 0);
    }

    private void toggleFavorite() {
        if (mMovie != null) {
            if (mFavorite) {
                int rowsDeleted = getActivity().getContentResolver().delete(
                        MovieContract.MovieEntry.buildMovieUri(mMovie.id), null, null
                );
                if (rowsDeleted > 0) {
                    setFavoriteView(false);
                }
            } else {
                ContentValues movieEntry = new ContentValues();
                movieEntry.put(MovieContract.MovieEntry.COLUMN_TMDB_ID, mMovie.id);
                movieEntry.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, mMovie.poster_path);
                movieEntry.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, mMovie.original_title);
                movieEntry.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, mMovie.release_date);
                movieEntry.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, mMovie.vote_average);
                movieEntry.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, mMovie.overview);

                getActivity().getContentResolver().insert(
                        MovieContract.MovieEntry.CONTENT_URI,
                        movieEntry
                );

                setFavoriteView(true);
            }
        }
    }
}
