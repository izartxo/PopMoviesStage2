package bizartxo.udacity.nanodegree.androiddeveloper.popularmoviesapp.data;

import android.os.Parcel;
import android.os.Parcelable;


public class MovieData implements Parcelable{

    public static final String RESULTS = "results";
    public static final String RESULTS_ID = "id";
    public static final String RESULTS_TITLE = "title";
    public static final String RESULTS_RELEASE_DATE = "release_date";
    public static final String RESULTS_POSTER_PATH = "poster_path";
    public static final String RESULTS_VOTE_AVERAGE = "vote_average";
    public static final String RESULTS_OVERVIEW = "overview";

    public static final String RESULTS_REVIEWS_AUTHOR = "author";
    public static final String RESULTS_REVIEWS_CONTENT = "content";


    public String id; // id
    public String title; // title
    public String release_date; // release_date
    public String movie_poster; // poster_path
    public String vote_average; // vote_average
    public String plot_synopsis; // overview
    public String type; // overview


    public MovieData(){
        id = "";
        title = "";
        release_date = "";
        movie_poster = "";
        vote_average = "";
        plot_synopsis = "";
        type = "";

    }


    public MovieData(String lid, String ltitle, String lrelease_date, String lmovie_poster, String lvote_average, String lplot_synopsis, String ltype){
        id = lid;
        title = ltitle;
        release_date = lrelease_date;
        movie_poster = lmovie_poster;
        vote_average = lvote_average;
        plot_synopsis = lplot_synopsis;
        type = ltype;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getMovie_poster() {
        return movie_poster;
    }

    public void setMovie_poster(String movie_poster) {
        this.movie_poster = movie_poster;
    }

    public String getVote_average() {
        return vote_average;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    public String getPlot_synopsis() {
        return plot_synopsis;
    }

    public void setPlot_synopsis(String plot_synopsis) {
        this.plot_synopsis = plot_synopsis;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public MovieData(Parcel in){
        String[] data = new String[7];

        in.readStringArray(data);

        this.id = data[0];
        this.title = data[1];
        this.release_date = data[2];
        this.movie_poster = data[3];
        this.vote_average = data[4];
        this.plot_synopsis = data[5];
        this.type = data[6];

    }

   @Override
    public int describeContents(){
       return 0;
   }

   @Override
    public void writeToParcel(Parcel dest, int flags){
       dest.writeStringArray(new String[]{
          this.id,
               this.title,
               this.release_date,
               this.movie_poster,
               this.vote_average,
               this.plot_synopsis,
               this.type

       });
   }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public MovieData createFromParcel(Parcel in){
            return new MovieData(in);
        }

        public MovieData[] newArray(int size){
            return new MovieData[size];
        }
    };

}
