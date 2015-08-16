package ru.pomerantsevp.udacity.popularmovies;

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
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ArrayAdapter<String> mListAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.movies_list);

        mListAdapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.list_item_movie
        );
        listView.setAdapter(mListAdapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    private void updateMovies() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOrder = prefs.getString(getString(R.string.pref_order_key),
                getString(R.string.pref_order_default));
        new FetchMoviesTask().execute(sortOrder);
    }

    private class FetchMoviesTask extends AsyncTask<String, Void, String[]> {
        private final String TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected String[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieListJsonStr = null;

            try {
                // Construct the URL for theMovieDB query
                Uri builtUri = Uri.parse("http://api.themoviedb.org/3/discover/movie").buildUpon()
                        .appendQueryParameter("sort_by", params[0])
                        .appendQueryParameter("api_key", getString(R.string.movie_db_key))
                        .build();
                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieListJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
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
                int arrayLength = movieListJsonArray.length();
                String[] results = new String[arrayLength];
                for (int i = 0; i < arrayLength; i++) {
                    JSONObject movieJson = movieListJsonArray.getJSONObject(i);
                    results[i] = movieJson.getString("original_title");
                }
                return results;
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            if (strings != null) {
                mListAdapter.clear();
                for (String string : strings) {
                    mListAdapter.add(string);
                }
            }
        }
    }
}
