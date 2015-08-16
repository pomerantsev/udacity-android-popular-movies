package ru.pomerantsevp.udacity.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

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

        String[] movieTitles = {
                "Whiplash",
                "Birdman",
                "Interstellar",
                "Avengers: Age of Ultron",
                "Mad Max: Fury Road",
                "Ant-Man",
                "Guardians of the Galaxy",
                "Kingsman: Secret Service"
        };
        ArrayList<String> movieTitlesList = new ArrayList<>();
        for (String title : movieTitles) {
            movieTitlesList.add(title);
        }

        ListView listView = (ListView) rootView.findViewById(R.id.movies_list);

        mListAdapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.list_item_movie,
                movieTitlesList
        );
        listView.setAdapter(mListAdapter);

        return rootView;
    }
}
