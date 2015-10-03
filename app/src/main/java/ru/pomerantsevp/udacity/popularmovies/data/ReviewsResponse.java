package ru.pomerantsevp.udacity.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by pavel on 10/3/15.
 */
public class ReviewsResponse implements Parcelable {
    public Review[] results;

    private ReviewsResponse(Parcel in) {
        results = in.createTypedArray(Review.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedArray(results, 0);
    }

    public static final Parcelable.Creator<ReviewsResponse> CREATOR = new Parcelable.Creator<ReviewsResponse>() {
        public ReviewsResponse createFromParcel(Parcel in) {
            return new ReviewsResponse(in);
        }

        public ReviewsResponse[] newArray(int size) {
            return new ReviewsResponse[size];
        }
    };
}
