package ru.pomerantsevp.udacity.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by pavel on 10/3/15.
 */
public class Review implements Parcelable {
    public String author;
    public String content;

    public Review() {}

    private Review(Parcel in) {
        author = in.readString();
        content = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(content);
    }

    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
}