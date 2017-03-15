package bizartxo.udacity.nanodegree.androiddeveloper.popularmoviesapp;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Movie;
import android.support.v4.app.LoaderManager;
import android.database.Cursor;
import android.net.Uri;

import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import bizartxo.udacity.nanodegree.androiddeveloper.popularmoviesapp.data.MovieAdapter;
import bizartxo.udacity.nanodegree.androiddeveloper.popularmoviesapp.data.MovieData;
import bizartxo.udacity.nanodegree.androiddeveloper.popularmoviesapp.data.MovieDataContract;
import bizartxo.udacity.nanodegree.androiddeveloper.popularmoviesapp.sync.AsyncMovieData;
import bizartxo.udacity.nanodegree.androiddeveloper.popularmoviesapp.utils.NetworkUtils;

public class UiActivity extends AppCompatActivity implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>{


    public static final String[] MAIN_MOVIES_PROJECTION = {
            MovieDataContract.MovieDataEntry.COLUMN_NAME_ID,
            MovieDataContract.MovieDataEntry.COLUMN_NAME_TITLE,
            MovieDataContract.MovieDataEntry.COLUMN_NAME_RELEASE,
            MovieDataContract.MovieDataEntry.COLUMN_NAME_POSTER,
            MovieDataContract.MovieDataEntry.COLUMN_NAME_VOTE,
            MovieDataContract.MovieDataEntry.COLUMN_NAME_SYNOPSIS
    };

    public static final int INDEX_MOVIE_ID = 0;
    public static final int INDEX_MOVIE_TITLE = 1;
    public static final int INDEX_MOVIE_RELEASE = 2;
    public static final int INDEX_MOVIE_POSTER = 3;
    public static final int INDEX_MOVIE_VOTE = 4;
    public static final int INDEX_MOVIE_SYNOPSIS = 5;


    private static final int ID_TOP_RATED_LOADER = 77;
    private static final int ID_POPULAR_LOADER = 88;
    private static final int ID_FAVORITES_LOADER = 99;

    private static final String IDMENU = "idmenu";
    private static int idMenu; // top rated 0 - popular 1 - favorites 2
    public static final int TOPRATED = 0;
    public static final int POPULAR = 1;
    public static final int FAVORITES = 2;

    private int mPosition = RecyclerView.NO_POSITION;

    // Textview that show error if not network connection available
    TextView tverror;

    // Recycler and adapter of MovieData objects
    RecyclerView rcmovie;
    MovieAdapter mAdapter;
    private ProgressBar mLoadingIndicator;


    // Interface with Ui Activity to event network data received
    public static OnMovieDataReceived onMovieDataReceived;
    public static OnBulkDataReceived onBulkDataReceived;

    // Manage clicks on Recyclerview and adapter
    MovieAdapter.ListItemClickListener onClickListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ui);

        // Initialization of navigation
        idMenu = TOPRATED;

        // Load API KEY from strings id
        NetworkUtils.setApiKey(getString(R.string.api_key));

        onClickListener = new MovieAdapter.ListItemClickListener() {
            @Override
            public void onListItemClick(int clickedItemIndex) {

                // Get MovieData object to create new detailed activity
                fillMovieDetailActivity(clickedItemIndex);

            }
        };

        // View instances
        tverror = (TextView) findViewById(R.id.error_message);
        rcmovie = (RecyclerView) findViewById(R.id.rc_movie);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        // RecyclerView with two columns
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),2);

        rcmovie.setLayoutManager(gridLayoutManager);

        // RecyclerView background
        rcmovie.setBackground(getResources().getDrawable(R.drawable.back_drawable,null));

        rcmovie.setHasFixedSize(true);

        // FROM NETWORK
        onMovieDataReceived = new OnMovieDataReceived(){
            @Override
            public void loadMovieData(String movieData) {
                // After received data list is updated
                // always from Internet
                updateMovieDataListFromNetwork(movieData);
            }


        };

        // FROM NETWORK
        onBulkDataReceived = new OnBulkDataReceived(){

            @Override
            public void bulkDataPopular(String movieData) {
                // After received data list is updated
                bulkMovieDataListFromNetwork(movieData, getString(R.string.popular_type));
            }
            @Override
            public void bulkDataTopRated(String movieData) {
                // After received data list is updated
                bulkMovieDataListFromNetwork(movieData, getString(R.string.toprated_type));
                startMovies();
            }
        };

        mAdapter = new MovieAdapter(this, onClickListener);

        rcmovie.setAdapter(mAdapter);


    }

    @Override
    protected void onResume(){
        super.onResume();

        switch (idMenu){
            case UiActivity.FAVORITES:
                loadFavorites();
                break;
            case UiActivity.TOPRATED:
            case UiActivity.POPULAR:
            default:
                loadFirstData();

        }
    }

    // Declaring interface to manage received data when AsyncTask finish
    public interface OnMovieDataReceived{
        void loadMovieData(String movieData); // -->  updateMovieDataListFromNetwork(movieData);

    }

    public interface OnBulkDataReceived{
        void bulkDataPopular(String movieData); // --> bulkMovieDataListFromNetwork(movieData, "P");
        void bulkDataTopRated(String movieData); // --> bulkMovieDataListFromNetwork(movieData, "T");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ui_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        // Navigation update
        idMenu = actualMenu(item.getTitle().toString());

        if (id == R.id.action_settings) {
            if (item.getTitle().equals(getString(R.string.refresh))) {

                // Delete data on database and update from network
                refreshNetworkData();

                return true;
            }

            if (item.getTitle().equals(getString(R.string.favorites))) {

                // Show favorites
                loadFavorites();

                return true;
            }

            if (item.getTitle().equals(getString(R.string.toprated)) ||
                    item.getTitle().equals(getString(R.string.popular))) {

                loadFirstData(); // Start loading data
            }


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Create the intent with all movie data to detail activity
    void fillMovieDetailActivity(int clickedItemIndex){
        MovieData movieData = mAdapter.getItem(clickedItemIndex);

        Intent detailActivityIntent = new Intent(getApplicationContext(), MovieDetailActivity.class);

        detailActivityIntent.putExtra(MovieData.RESULTS_ID,movieData.getId());
        detailActivityIntent.putExtra(MovieData.RESULTS_TITLE, movieData.getTitle());
        detailActivityIntent.putExtra(MovieData.RESULTS_RELEASE_DATE, movieData.getRelease_date());
        detailActivityIntent.putExtra(MovieData.RESULTS_VOTE_AVERAGE, movieData.getVote_average());
        detailActivityIntent.putExtra(MovieData.RESULTS_OVERVIEW, movieData.getPlot_synopsis());
        detailActivityIntent.putExtra(MovieData.RESULTS_POSTER_PATH, movieData.getMovie_poster());

        // Start Movie Detail Activity
        startActivity(detailActivityIntent);
    }

    //
    void updateMovieDataListFromNetwork(String movieData){

        ContentResolver dataContent = getContentResolver();

        // I need to difference between TopRated and Popular results
        String optiontype = (idMenu==TOPRATED ? getString(R.string.toprated_type) : getString(R.string.popular_type));

        try {
            JSONObject jsonMovieData = new JSONObject(movieData);
            JSONArray results = jsonMovieData.getJSONArray(MovieData.RESULTS);

            for (int item = 0; item < results.length(); item++) {
                ContentValues contentValues = new ContentValues();
                JSONObject localItem = results.getJSONObject(item);
                MovieData movieDataItem = new MovieData(localItem.getString(MovieData.RESULTS_ID),
                            localItem.getString(MovieData.RESULTS_TITLE),
                            localItem.getString(MovieData.RESULTS_RELEASE_DATE),
                            localItem.getString(MovieData.RESULTS_POSTER_PATH),
                            localItem.getString(MovieData.RESULTS_VOTE_AVERAGE),
                            localItem.getString(MovieData.RESULTS_OVERVIEW),
                            optiontype);


                contentValues.put(MovieDataContract.MovieDataEntry.COLUMN_NAME_ID, movieDataItem.getId());
                contentValues.put(MovieDataContract.MovieDataEntry.COLUMN_NAME_POSTER, movieDataItem.getMovie_poster());
                contentValues.put(MovieDataContract.MovieDataEntry.COLUMN_NAME_RELEASE, movieDataItem.getRelease_date());
                contentValues.put(MovieDataContract.MovieDataEntry.COLUMN_NAME_SYNOPSIS, movieDataItem.getPlot_synopsis());
                contentValues.put(MovieDataContract.MovieDataEntry.COLUMN_NAME_TITLE, movieDataItem.getTitle());
                contentValues.put(MovieDataContract.MovieDataEntry.COLUMN_NAME_VOTE, movieDataItem.getVote_average());
                contentValues.put(MovieDataContract.MovieDataEntry.COLUMN_NAME_TYPE, movieDataItem.getType());

                dataContent.insert(MovieDataContract.CONTENT_URI, contentValues);

            } //
        } catch (JSONException je) {
            je.printStackTrace();
        }

        startMovies();

    }


    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {

        showLoading();

        switch (loaderId) {

            case ID_POPULAR_LOADER:

                Uri popularQueryUri = MovieDataContract.CONTENT_URI;

                return new android.support.v4.content.CursorLoader(this,
                        popularQueryUri,
                        MAIN_MOVIES_PROJECTION,
                        "type = ?",
                        new String[]{getString(R.string.popular_type)},
                        null);

            case ID_TOP_RATED_LOADER:

                Uri topratedQueryUri = MovieDataContract.CONTENT_URI;

                return new android.support.v4.content.CursorLoader(this,
                        topratedQueryUri,
                        MAIN_MOVIES_PROJECTION,
                        "type = ?",
                        new String[]{getString(R.string.toprated_type)},
                        null);

            case ID_FAVORITES_LOADER:

                return populateFavorites();


            default:
                throw new RuntimeException(getString(R.string.loader_error) + loaderId);
        }
    }



    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data.getCount() != 0)
            showMovieDataView();

        mAdapter.swapCursor(data);

        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;

        rcmovie.smoothScrollToPosition(mPosition);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader){
        mAdapter.swapCursor(null);
    }

    // If connection error hide recyclerview and show the error
    void showNetworkError(){
        rcmovie.setVisibility(View.INVISIBLE);
        tverror.setVisibility(View.VISIBLE);

    }

    private void showLoading() {
        tverror.setVisibility(View.INVISIBLE);

        rcmovie.setVisibility(View.INVISIBLE);

        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    private void showMovieDataView() {
        tverror.setVisibility(View.INVISIBLE);

        mLoadingIndicator.setVisibility(View.INVISIBLE);

        rcmovie.setVisibility(View.VISIBLE);

    }


    public CursorLoader populateFavorites(){

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(MovieDataContract.CONTENT_URI_FAVORITES,MAIN_MOVIES_PROJECTION,null,null,null);
        if (cursor.getCount()==0){
            cursor.close();
            showMovieDataView();
            mAdapter.swapCursor(null);

            Toast.makeText(this, getString(R.string.favorites_message), Toast.LENGTH_SHORT).show();

            return null;
        }

        int count = cursor.getCount();
        cursor.moveToFirst();

        String[] ids = new String[count];
        ids[count-1] = cursor.getString(cursor.getColumnIndex(MovieDataContract.MovieDataEntry.COLUMN_NAME_ID));

        while (cursor.moveToNext()){
            count--;
            ids[count-1] = cursor.getString(cursor.getColumnIndex(MovieDataContract.MovieDataEntry.COLUMN_NAME_ID));
        }

        StringBuffer sb = new StringBuffer();
        for ( int i = 0; i < ids.length; i++ ){
            sb.append("?,");
        }
        String questionMarks = sb.substring(0,sb.length()-1);

        Uri favoritesQueryUri = MovieDataContract.CONTENT_URI_FAVORITES;

        contentResolver.notifyChange(favoritesQueryUri,null);

        return new android.support.v4.content.CursorLoader(this,
                favoritesQueryUri,
                MAIN_MOVIES_PROJECTION,
                "id in ("+questionMarks+")",
                ids,
                null);
    }


    public void loadFirstData(){
        ContentResolver contentResolver = getContentResolver();
        Cursor testCursor = contentResolver.query(MovieDataContract.CONTENT_URI,null,null,null,null);
        if (testCursor.getCount()>0) {
            startMovies();
            return;
        }

        if (!NetworkUtils.isNetworkAvailable(this)){
            showNetworkError();
            return;
        }else{
            showMovieDataView();
        }

        URL url = null;
        // Obtain Popular Movies to store
        url = NetworkUtils.buildUrl(NetworkUtils.POPULAR,"");
        AsyncMovieData asyncMovieData = new AsyncMovieData(onBulkDataReceived);
        asyncMovieData.execute(url);
        // Obtain TopRated Movies to store
        url = NetworkUtils.buildUrl(NetworkUtils.TOP_RATED,"");
        AsyncMovieData asyncMovieData2 = new AsyncMovieData(onBulkDataReceived);
        asyncMovieData2.execute(url);
    }

    void bulkMovieDataListFromNetwork(String movieData, String option){

        ContentResolver dataContent = getContentResolver();
        List<ContentValues> movieDataValues = new ArrayList<ContentValues>();
        try {
            JSONObject data = new JSONObject(movieData);
            JSONArray results = data.getJSONArray(MovieData.RESULTS);

            for (int item = 0; item < results.length(); item++) {
                ContentValues contentValues = new ContentValues();
                JSONObject localItem = results.getJSONObject(item);
                MovieData movieDataItem = new MovieData(localItem.getString(MovieData.RESULTS_ID),
                        localItem.getString(MovieData.RESULTS_TITLE),
                        localItem.getString(MovieData.RESULTS_RELEASE_DATE),
                        localItem.getString(MovieData.RESULTS_POSTER_PATH),
                        localItem.getString(MovieData.RESULTS_VOTE_AVERAGE),
                        localItem.getString(MovieData.RESULTS_OVERVIEW),
                        option
                        );


                contentValues.put(MovieDataContract.MovieDataEntry.COLUMN_NAME_ID, movieDataItem.getId());
                contentValues.put(MovieDataContract.MovieDataEntry.COLUMN_NAME_POSTER, movieDataItem.getMovie_poster());
                contentValues.put(MovieDataContract.MovieDataEntry.COLUMN_NAME_RELEASE, movieDataItem.getRelease_date());
                contentValues.put(MovieDataContract.MovieDataEntry.COLUMN_NAME_SYNOPSIS, movieDataItem.getPlot_synopsis());
                contentValues.put(MovieDataContract.MovieDataEntry.COLUMN_NAME_TITLE, movieDataItem.getTitle());
                contentValues.put(MovieDataContract.MovieDataEntry.COLUMN_NAME_VOTE, movieDataItem.getVote_average());
                contentValues.put(MovieDataContract.MovieDataEntry.COLUMN_NAME_TYPE, movieDataItem.getType());
                movieDataValues.add(contentValues);

            }

            dataContent.bulkInsert(MovieDataContract.CONTENT_URI, movieDataValues.toArray(new ContentValues[movieDataValues.size()]));

        } catch (JSONException je) {
            je.printStackTrace();
        }

    }


    private void refreshNetworkData(){

        ContentResolver contentResolver = getContentResolver();

        Cursor cursor = contentResolver.query(MovieDataContract.CONTENT_URI_FAVORITES,MAIN_MOVIES_PROJECTION,null,null,null);

        if (cursor.getCount()==0) {
            loadFirstData();
            cursor.close();
        }
        int num = contentResolver.delete(MovieDataContract.CONTENT_URI, "type=?", new String[]{getString(R.string.popular_type)});

        if (num <= 0){
            return;
        }
        num = contentResolver.delete(MovieDataContract.CONTENT_URI, "type=?", new String[]{getString(R.string.toprated_type)});

        if (num <= 0){
            return;
        }

        // Bulk new data again!!!
        loadFirstData();

        cursor.close();
    }

    private int actualMenu(String option){

        if (option.equals(getString(R.string.toprated)))
            return TOPRATED;
        else if (option.equals(getString(R.string.popular)))
            return POPULAR;
        else if (option.equals(getString(R.string.favorites)))
            return FAVORITES;
        else
            return TOPRATED; // if not match return Top Rated
    }

    private void loadFavorites(){

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader loader = loaderManager.getLoader(ID_FAVORITES_LOADER);


        if (loader==null){

            loaderManager.destroyLoader(ID_FAVORITES_LOADER);
            getSupportLoaderManager().initLoader(ID_FAVORITES_LOADER, null, this);

        }
        else{

            getSupportLoaderManager().restartLoader(ID_FAVORITES_LOADER, null, this);
        }
    }

    private void startMovies(){
        int id_running = (idMenu==TOPRATED?ID_TOP_RATED_LOADER:ID_POPULAR_LOADER);
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader loader = loaderManager.getLoader(id_running);
        if (loader==null){

            getSupportLoaderManager().initLoader(id_running, null, this);
        }
        else{

            getSupportLoaderManager().restartLoader(id_running, null, this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(IDMENU, idMenu);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        idMenu = savedInstanceState.getInt(IDMENU);

    }

}

