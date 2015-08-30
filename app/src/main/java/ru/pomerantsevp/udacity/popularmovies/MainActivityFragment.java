package ru.pomerantsevp.udacity.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivityFragment extends Fragment {

    private final String MOVIES_KEY = "movies";
    private final String SORT_ORDER_KEY = "sort_order";

    private ImageAdapter mImageAdapter;
    private JSONArray mMovies;

    private String mSortOrder;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            String moviesString = savedInstanceState.getString(MOVIES_KEY);
            mSortOrder = savedInstanceState.getString(SORT_ORDER_KEY);
            if (moviesString != null) {
                try {
                    mMovies = new JSONArray(moviesString);
                } catch (JSONException e) {}
            }
        }

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.movies_list);

        mImageAdapter = new ImageAdapter(getActivity());
        gridView.setAdapter(mImageAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, mImageAdapter.getItem(position).toString());
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mMovies != null) {
            outState.putString(MOVIES_KEY, mMovies.toString());
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
        new FetchMoviesTask().execute(sortOrder);
    }

    private void displayMovies(JSONArray movies) {
        mMovies = movies;
        if (movies != null) {
            mImageAdapter.clear();
            try {
                for (int i = 0; i < movies.length(); i++) {
                    mImageAdapter.add(movies.getJSONObject(i));
                }
            } catch (JSONException e) {}
        }
    }

    private class FetchMoviesTask extends AsyncTask<String, Void, JSONArray> {
        private final String TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected JSONArray doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieListJsonStr = null;

            try {
                Uri builtUri = Uri.parse("http://api.themoviedb.org/3/discover/movie").buildUpon()
                        .appendQueryParameter("sort_by", params[0])
                        .appendQueryParameter("api_key", getString(R.string.movie_db_key))
                        .build();
                URL url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    return null;
                }
                movieListJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(TAG, "Error ", e);
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                JSONObject movieListJson = new JSONObject(movieListJsonStr);
                JSONArray movieListJsonArray = movieListJson.getJSONArray("results");
                return movieListJsonArray;
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONArray movies) {
            displayMovies(movies);
        }
    }

}
