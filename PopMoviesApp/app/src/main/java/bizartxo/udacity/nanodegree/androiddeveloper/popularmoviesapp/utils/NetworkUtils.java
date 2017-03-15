package bizartxo.udacity.nanodegree.androiddeveloper.popularmoviesapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;



public class NetworkUtils {

    /* TYPE: 0.-top_rated 1.-popular 2.-movies 3.-reviews */
    public final static int TOP_RATED = 0x00;
    public final static int POPULAR = 0x01;
    public final static int TRAILERS = 0x02;
    public final static int REVIEWS = 0x03;


    public final static String MOVIEDB_BASE_URL_IMAGE =
            "http://image.tmdb.org/t/p/";

    public final static String IMAGE_SIZE_185 = "w185";
    public final static String IMAGE_SIZE_342 = "w342";
    public final static String IMAGE_SIZE_500 = "w500";

    public final static String MOVIEDB_BASE_URL = "http://api.themoviedb.org";

    public final static String COMMON_PATH = "/3/movie/";
    public final static String POPULAR_PATH = COMMON_PATH + "popular";
    public final static String TOPRATED_PATH = COMMON_PATH + "top_rated";
    /* new stage2*/
    public final static String VIDEOS_PATH = "/videos";
    public final static String REVIEWS_PATH = "/reviews";
    /*           */

    static String mApiKey = "";

    /**
     * Builds the URL used to query MovieDB.
     *
     * @param top_rated true popular false
     * @return The URL to use to query the weather server.
     */
    public static URL buildUrl(/*boolean top_rated,*/ int type, String id) {
        /* TYPE: 0.-top_rated 1.-popular 2.-movies 3.-reviews */
        Uri builtUri = null;
        switch(type){
            case TOP_RATED:
                builtUri = Uri.parse(MOVIEDB_BASE_URL + TOPRATED_PATH).buildUpon()
                        .appendQueryParameter("api_key", mApiKey)
                        .build();
                break;
            case POPULAR:
                builtUri = Uri.parse(MOVIEDB_BASE_URL + POPULAR_PATH).buildUpon()
                        .appendQueryParameter("api_key", mApiKey)
                        .build();
                break;
            case TRAILERS:
                builtUri = Uri.parse(MOVIEDB_BASE_URL + COMMON_PATH + id + VIDEOS_PATH).buildUpon()
                        .appendQueryParameter("api_key", mApiKey)
                        .build();
                break;
            case REVIEWS:
                builtUri = Uri.parse(MOVIEDB_BASE_URL + COMMON_PATH + id + REVIEWS_PATH).buildUpon()
                        .appendQueryParameter("api_key", mApiKey)
                        .build();
                break;

            default:
                break;
        }

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    // Method to check Internet connection
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // Set APIKEY to work with it
    public static void setApiKey(String apikey){
        mApiKey = apikey;
    }



}
