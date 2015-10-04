package ru.pomerantsevp.udacity.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by pavel on 10/3/15.
 */
public class Trailer implements Parcelable {
    public String key;
    public String name;
    public String type;

    public Trailer() {}

    private Trailer(Parcel in) {
        key = in.readString();
        name = in.readString();
        type = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(name);
        dest.writeString(type);
    }

    public static final Parcelable.Creator<Trailer> CREATOR = new Parcelable.Creator<Trailer>() {
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    public String getYoutubeUrl() {
        return "http://youtube.com/watch?v=" + key;
    }
}
