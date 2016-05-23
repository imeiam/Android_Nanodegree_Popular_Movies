package gop.akiladeshwar.movies_1;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by AkilAdeshwar on 17-05-2016.
 */
public class Movie implements Parcelable{


    public static final String MOVIE_TAG = "MOVIE-TAG";

    String name;
    String posterPath;
    String overview;
    String releaseDate;
    String vote_average;
    String backdropPath;

    public String getBackdropPath() {return this.backdropPath;}

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getVote_average() {
        return vote_average;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(name);
        dest.writeString(posterPath);
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeString(vote_average);
        dest.writeString(backdropPath);
    }


    public Movie(Parcel in){

        name = in.readString();
        posterPath = in.readString();
        overview = in.readString();
        releaseDate = in.readString();
        vote_average = in.readString();
        backdropPath = in.readString();

    }


    public Movie(String name){
        this.name = name;
    }

    public Movie(){

    }

    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator(){
                public Movie createFromParcel(Parcel in) {
                    return new Movie(in);
                }
                public Movie[] newArray(int size) {
                    return new Movie[size];
                }
            };
}
