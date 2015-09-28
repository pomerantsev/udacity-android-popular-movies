package ru.pomerantsevp.udacity.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by pavel on 9/27/15.
 */
public class Movie implements Parcelable {
    public String poster_path;
    public String original_title;
    public String release_date;
    public String vote_average;
    public String overview;

    private Movie(Parcel in) {
        poster_path = in.readString();
        original_title = in.readString();
        release_date = in.readString();
        vote_average = in.readString();
        overview = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(poster_path);
        dest.writeString(original_title);
        dest.writeString(release_date);
        dest.writeString(vote_average);
        dest.writeString(overview);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
