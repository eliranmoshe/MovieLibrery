package com.defult.eliran.movielibrery;

/**
 * Created by eliran on 2/7/2017.
 */

public class MovieObj {
    //make a movie object that help to get all data
    String MovieName;
    String Url;
    String Body;
    String imdbID;
    String imdbRating;
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

    @Override
    public String toString() {
        return MovieName;
    }
}
