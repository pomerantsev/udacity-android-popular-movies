package ru.pomerantsevp.udacity.popularmovies;

import android.app.Fragment;
import android.content.ContentValues;
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
        if (mMovies == null || !mSortOrder.equals(sortOrder)) {
            updateAndDisplayMovies(sortOrder);
        } else {
            displayMovies(mMovies);
        }
        mSortOrder = sortOrder;
    }

    private void updateAndDisplayMovies(String sortOrder) {
        if (NetworkHelper.isNetworkAvailable(getActivity())) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint("http://api.themoviedb.org")
                    .build();

            MovieService service = restAdapter.create(MovieService.class);
            service.listMovies(sortOrder, getString(R.string.movie_db_key), new Callback<MoviesResponse>() {
                @Override
                public void success(MoviesResponse moviesResponse, Response response) {
                    displayMovies(new ArrayList<>(Arrays.asList(moviesResponse.results)));
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

    private void displayMovies(ArrayList<Movie> movies) {
        mMovies = movies;
        if (movies != null) {
            mImageAdapter.clear();
            for (int i = 0; i < movies.size(); i++) {
                insertMovieIntoDb(movies.get(i));
                mImageAdapter.add(movies.get(i));
            }
            if (movies.size() > 0) {
                mEmptyView.setVisibility(View.GONE);
            } else {
                mEmptyView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void insertMovieIntoDb(Movie movie) {
        Cursor findMovieCursor = getActivity().getContentResolver().query(
                MovieContract.MovieEntry.buildMovieUri(movie.id),
                null, null, null, null
        );
        if (findMovieCursor.getCount() == 0) {
            ContentValues movieEntry = new ContentValues();
            movieEntry.put(MovieContract.MovieEntry.COLUMN_TMDB_ID, movie.id);
            movieEntry.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.poster_path);
            movieEntry.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, movie.original_title);
            movieEntry.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.release_date);
            movieEntry.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.vote_average);
            movieEntry.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.overview);

            getActivity().getContentResolver().insert(
                    MovieContract.MovieEntry.CONTENT_URI,
                    movieEntry
            );
        }
        findMovieCursor.close();
    }
}
