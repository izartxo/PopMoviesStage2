package bizartxo.udacity.nanodegree.androiddeveloper.popularmoviesapp.data;

import android.net.Uri;
import android.provider.BaseColumns;



public final class MovieDataContract {

    private MovieDataContract(){}

    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "bizartxo.udacity.nanodegree.androiddeveloper.popularmoviesapp";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);


    public static final String PATH_MOVIES = "movies";
    public static final String PATH_FAVORITES = "favorites";

    // TaskEntry content URI = base content URI + path
    public static final Uri CONTENT_URI =
            BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();
    public static final Uri CONTENT_URI_FAVORITES =
            BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();


    public static final class MovieDataEntry implements BaseColumns {
        public static final String TABLE_NAME = "MovieData";
        public static final String TABLE_FAVORITE = "FavoriteMovie";

        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_RELEASE = "release";
        public static final String COLUMN_NAME_POSTER = "poster";
        public static final String COLUMN_NAME_VOTE = "vote";
        public static final String COLUMN_NAME_SYNOPSIS = "synopsis";
        public static final String COLUMN_NAME_TYPE = "type";
    }


}
