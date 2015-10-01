package ru.pomerantsevp.udacity.popularmovies;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import ru.pomerantsevp.udacity.popularmovies.data.MovieContract;
import ru.pomerantsevp.udacity.popularmovies.utils.NetworkHelper;

public class MainActivityFragment extends Fragment {

    private final static String TAG = MainActivityFragment.class.getName();

    private final String MOVIES_KEY = "movies";
    private final String SORT_ORDER_KEY = "sort_order";

    private View mEmptyView;

    private ImageAdapter mImageAdapter;
    private ArrayList<Movie> mMovies;
    private NetworkNotificationDialogFragment mNetworkNotificationDialogFragment;

    private String mSortOrder;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mMovies = savedInstanceState.getParcelableArrayList(MOVIES_KEY);
            mSortOrder = savedInstanceState.getString(SORT_ORDER_KEY);
        }

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.movies_list);
        mEmptyView = rootView.findViewById(R.id.no_connection_text);

        mImageAdapter = new ImageAdapter(getActivity());
        gridView.setAdapter(mImageAdapter);

        mNetworkNotificationDialogFragment = new NetworkNotificationDialogFragment();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(SharedConstants.MOVIE_KEY, mImageAdapter.getItem(position));
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mMovies != null) {
            outState.putParcelableArrayList(MOVIES_KEY, mMovies);
        }
        if (mSortOrder != null) {
            outState.putString(SORT_ORDER_KEY, mSortOrder);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOrder = prefs.getString(getString(R.string.pref_order_key),
                getString(R.string.pref_order_default));
        if (mMovies == null || !mSortOrder.equals(sortOrder) ||
                // If we're viewing favorites, we need to update them on every activity restart -
                // this list is prone to frequent updates.
                sortOrder == getString(R.string.pref_order_favorite)) {
            updateAndDisplayMovies(sortOrder);
        } else {
            displayMovies();
        }
        mSortOrder = sortOrder;
    }

    private void updateAndDisplayMovies(String sortOrder) {
        if (sortOrder == getString(R.string.pref_order_favorite)) {
            Cursor favoriteMoviesCursor = getActivity().getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI, null, null, null, null
            );
            if (mMovies == null) {
                mMovies = new ArrayList<>();
            }
            mMovies.clear();
            while(favoriteMoviesCursor.moveToNext()) {
                Movie movie = new Movie();
                movie.id = favoriteMoviesCursor.getInt(favoriteMoviesCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TMDB_ID));
                movie.poster_path = favoriteMoviesCursor.getString(favoriteMoviesCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH));
                movie.original_title = favoriteMoviesCursor.getString(favoriteMoviesCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE));
                movie.release_date = favoriteMoviesCursor.getString(favoriteMoviesCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));
                movie.vote_average = favoriteMoviesCursor.getFloat(favoriteMoviesCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE));
                movie.overview = favoriteMoviesCursor.getString(favoriteMoviesCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW));
                mMovies.add(movie);
            }
            displayMovies();
        } else {
            if (NetworkHelper.isNetworkAvailable(getActivity())) {
                RestAdapter restAdapter = new RestAdapter.Builder()
                        .setEndpoint("http://api.themoviedb.org")
                        .build();

                MovieService service = restAdapter.create(MovieService.class);
                service.listMovies(sortOrder, getString(R.string.movie_db_key), new Callback<MoviesResponse>() {
                    @Override
                    public void success(MoviesResponse moviesResponse, Response response) {
                        mMovies = new ArrayList<>(Arrays.asList(moviesResponse.results));
                        displayMovies();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        mEmptyView.setVisibility(View.VISIBLE);
                    }
                });
            } else {
                mNetworkNotificationDialogFragment.show(getFragmentManager(), NetworkNotificationDialogFragment.TAG);
            }
        }
    }

    private void displayMovies() {
        mImageAdapter.clear();
        for (int i = 0; i < mMovies.size(); i++) {
            mImageAdapter.add(mMovies.get(i));
        }
        if (mMovies.size() > 0) {
            mEmptyView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }
}
