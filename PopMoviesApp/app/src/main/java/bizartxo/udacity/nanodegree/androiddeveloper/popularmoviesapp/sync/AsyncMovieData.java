package bizartxo.udacity.nanodegree.androiddeveloper.popularmoviesapp.sync;

import android.os.AsyncTask;
import android.provider.Contacts;
import android.util.Log;

import java.io.IOException;
import java.net.URL;

import bizartxo.udacity.nanodegree.androiddeveloper.popularmoviesapp.MovieDetailActivity;
import bizartxo.udacity.nanodegree.androiddeveloper.popularmoviesapp.UiActivity;
import bizartxo.udacity.nanodegree.androiddeveloper.popularmoviesapp.utils.NetworkUtils;



public class AsyncMovieData extends AsyncTask<URL, Void, String> {

    String movieDBSearchResults = "";

    public UiActivity.OnMovieDataReceived gateway;
    public UiActivity.OnBulkDataReceived gateway_bulk;
    public MovieDetailActivity.OnMovieDataReceived gateway_detail;

    URL searchUrl;

    public AsyncMovieData(UiActivity.OnMovieDataReceived onMovieDataReceived){
        gateway = onMovieDataReceived;
    }

    public AsyncMovieData(MovieDetailActivity.OnMovieDataReceived onMovieDataReceived){
        gateway_detail = onMovieDataReceived;
    }

    public AsyncMovieData(UiActivity.OnBulkDataReceived onMovieDataReceived){
        gateway_bulk = onMovieDataReceived;
    }

    @Override
    protected String doInBackground(URL... urls) {
        searchUrl = urls[0];

        try {
            movieDBSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return movieDBSearchResults;

    }

    @Override
    protected void onPostExecute(String movieDBSearchResults) {
        super.onPostExecute(movieDBSearchResults);

        if (gateway!=null){
            gateway.loadMovieData(movieDBSearchResults);
        }
        else if(gateway_detail!=null){
            if (searchUrl.toString().contains(NetworkUtils.REVIEWS_PATH))
                gateway_detail.loadMovieReviews(movieDBSearchResults);
            else
                gateway_detail.loadMovieTrailers(movieDBSearchResults);
            } else if (gateway_bulk!=null){
                if (searchUrl.toString().contains(NetworkUtils.POPULAR_PATH))
                    gateway_bulk.bulkDataPopular(movieDBSearchResults);
                else
                    gateway_bulk.bulkDataTopRated(movieDBSearchResults);
        }

    }



}

