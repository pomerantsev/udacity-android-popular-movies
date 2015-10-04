package ru.pomerantsevp.udacity.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ru.pomerantsevp.udacity.popularmovies.data.Trailer;

/**
 * Created by pavel on 10/3/15.
 */
public class TrailersAdapter extends ArrayAdapter<Trailer> {

    private static final int LAYOUT = R.layout.list_item_trailer;

    public TrailersAdapter(Context context, List<Trailer> objects) {
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

        Trailer trailer = getItem(position);
        TextView nameView = (TextView) container.findViewById(R.id.name);
        nameView.setText(trailer.name);
        TextView typeView = (TextView) container.findViewById(R.id.type);
        typeView.setText(trailer.type);

        return container;
    }
}
