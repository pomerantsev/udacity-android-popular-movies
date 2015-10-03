package ru.pomerantsevp.udacity.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by pavel on 10/3/15.
 */
public class TrailersResponse implements Parcelable {
    public Trailer[] results;

    private TrailersResponse(Parcel in) {
        results = in.createTypedArray(Trailer.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedArray(results, 0);
    }

    public static final Parcelable.Creator<TrailersResponse> CREATOR = new Parcelable.Creator<TrailersResponse>() {
        public TrailersResponse createFromParcel(Parcel in) {
            return new TrailersResponse(in);
        }

        public TrailersResponse[] newArray(int size) {
            return new TrailersResponse[size];
        }
    };
}
