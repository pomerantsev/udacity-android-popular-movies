package ru.pomerantsevp.udacity.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by pavel on 9/28/15.
 */
public class MoviesResponse implements Parcelable {
    public Movie[] results;

    private MoviesResponse(Parcel in) {
        results = in.createTypedArray(Movie.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedArray(results, 0);
    }

    public static final Parcelable.Creator<MoviesResponse> CREATOR = new Parcelable.Creator<MoviesResponse>() {
        public MoviesResponse createFromParcel(Parcel in) {
            return new MoviesResponse(in);
        }

        public MoviesResponse[] newArray(int size) {
            return new MoviesResponse[size];
        }
    };
}
