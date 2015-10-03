package ru.pomerantsevp.udacity.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ru.pomerantsevp.udacity.popularmovies.data.Review;

/**
 * Created by pavel on 10/3/15.
 */
public class ReviewsAdapter extends ArrayAdapter<Review> {
    private static final int LAYOUT = R.layout.list_item_review;

    public ReviewsAdapter(Context context, List<Review> objects) {
        super(context, LAYOUT, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewGroup container;
        if (convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            container = (ViewGroup) inflater.inflate(LAYOUT, parent, false);

        } else {
            container = (ViewGroup) convertView;
        }

        Review review = getItem(position);
        TextView contentView = (TextView) container.findViewById(R.id.content);
        contentView.setText(review.content);
        TextView authorView = (TextView) container.findViewById(R.id.author);
        authorView.setText(review.author);

        return container;
    }
}
