package ru.pomerantsevp.udacity.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import ru.pomerantsevp.udacity.popularmovies.data.Movie;

public class MainActivity extends AppCompatActivity
        implements MainActivityFragment.ListClickCallback {

    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.movie_detail_container) == null) {
            mTwoPane = false;
        } else {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailActivityFragment(),
                                DETAILFRAGMENT_TAG)
                        .commit();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Movie movie) {
        if (mTwoPane) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.movie_detail_container, DetailActivityFragment.newInstance(movie),
                            DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra(SharedConstants.MOVIE_KEY, movie);
            startActivity(intent);
        }
    }
}
