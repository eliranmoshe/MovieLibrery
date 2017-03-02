package com.defult.eliran.movielibrery;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by eliran on 2/7/2017.
 */

public class MovieObj implements Parcelable {
    //make a movie object that help to get all data
    String MovieName;
    String Url;
    String Body;
    String imdbID;
    String imdbRating;
    String imagebase64;
    String id;

    public MovieObj(String movieName, String url, String body,  String imdbRating, String imagebase64, String id) {
        MovieName = movieName;
        Url = url;
        Body = body;
        this.imdbRating = imdbRating;
        this.imagebase64 = imagebase64;
        this.id = id;
    }

    public MovieObj(String body) {
        Body = body;

    }

    public MovieObj(String movieName, String url, String imdbID) {
        MovieName = movieName;
        Url = url;
        this.imdbID = imdbID;
    }

    public MovieObj(String movieName, String url, String imdbid, String imdbRating) {

        this.MovieName = movieName;
        this.Url = url;
        this.imdbID=imdbid;
        this.imdbRating=imdbRating;
    }

    protected MovieObj(Parcel in) {
        MovieName = in.readString();
        Url = in.readString();
        Body = in.readString();
        imdbID = in.readString();
        imdbRating = in.readString();
        imagebase64=in.readString();
        id=in.readString();

    }

    public static final Creator<MovieObj> CREATOR = new Creator<MovieObj>() {
        @Override
        public MovieObj createFromParcel(Parcel in) {
            return new MovieObj(in);
        }

        @Override
        public MovieObj[] newArray(int size) {
            return new MovieObj[size];
        }
    };

    @Override
    public String toString() {
        return MovieName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(MovieName);
        dest.writeString(Url);
        dest.writeString(Body);
        dest.writeString(imdbID);
        dest.writeString(imdbRating);
        dest.writeString(imagebase64);
        dest.writeString(id);
    }
}
