package bizartxo.udacity.nanodegree.androiddeveloper.popularmoviesapp;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;

import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;


import bizartxo.udacity.nanodegree.androiddeveloper.popularmoviesapp.data.MovieData;
import bizartxo.udacity.nanodegree.androiddeveloper.popularmoviesapp.data.MovieDataContract;
import bizartxo.udacity.nanodegree.androiddeveloper.popularmoviesapp.data.TrailerData;
import bizartxo.udacity.nanodegree.androiddeveloper.popularmoviesapp.data.TrailerAdapter;
import bizartxo.udacity.nanodegree.androiddeveloper.popularmoviesapp.sync.AsyncMovieData;
import bizartxo.udacity.nanodegree.androiddeveloper.popularmoviesapp.utils.NetworkUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Intent.ACTION_SEND;
import static android.content.Intent.ACTION_VIEW;


public class MovieDetailActivity extends AppCompatActivity {

    private static final String DATA = "data";
    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";

    @BindView(R.id.tvtitle)  TextView tvtitle;
    @BindView(R.id.tvreleasedate) TextView tvreleasedate;
    @BindView(R.id.tvvoteaverage) TextView tvvoteaverage;
    @BindView(R.id.tvsynopsis) TextView tvsynopsis;
    @BindView(R.id.tverror) TextView tverror;

    @BindView(R.id.ivposter) ImageView ivposter;
    @BindView(R.id.ivheader) ImageView ivheader;
    @BindView(R.id.bt_favorite) ImageView btstar;


    private ShareActionProvider mShareActionProvider;

    private ListView trailerListView;
    TrailerAdapter trailerAdapter;
    static ArrayList<TrailerData> trailerList;

    private static String movieId;
    static MovieData mdata;


    // Interface with Ui Activity to event network data received
    OnMovieDataReceived onMovieDataReceived;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        trailerListView = (ListView) findViewById(R.id.lista);

        ArrayList<TrailerData> zero = new ArrayList<TrailerData>();

        trailerAdapter = new TrailerAdapter(this, zero);

        trailerListView.setAdapter(trailerAdapter);

        // Instantiate all views
        ButterKnife.bind(this);

        btstar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFavorite();
            }
        });

        // Inerfaces to deal with async data
        onMovieDataReceived = new OnMovieDataReceived(){
            @Override
            public void loadMovieTrailers(String movieData) {

                trailerList = createTrailers(movieData);

                if (trailerList.size() < 1){
                    hideTrailer(true);
                    setShareIntent(false);

                }else{
                    hideTrailer(false);
                    setShareIntent(true);
                }

                fillList(trailerList);

            }

            @Override
            public void loadMovieReviews(String movieData) {
                createReviews(movieData);
            }
        };


        // Detail data load
        Intent intent = getIntent();

        fillViewsMovieDetailedData(intent);

    }



    // Declaring interface to manage received data when AsyncTask finish
    public interface OnMovieDataReceived{
        void loadMovieTrailers(String movieData);
        void loadMovieReviews(String movieData);
    }

    void fillViewsMovieDetailedData(Intent intent){

            if (intent.hasExtra(MovieData.RESULTS_TITLE)) {
                // Show views if they are invisible because of previous error
                showDefaultView();

                movieId = intent.getStringExtra(MovieData.RESULTS_ID);

                if (NetworkUtils.isNetworkAvailable(getApplicationContext())){
                    obtainTrailers();
                    obtainReviews();
                }


                ContentResolver contentResolver = getContentResolver();
                Cursor cursor = contentResolver.query(MovieDataContract.CONTENT_URI_FAVORITES, null, "id=?", new String[]{movieId},null);
                String favorite = "N";
                if (cursor.getCount()!=0){
                    cursor.moveToFirst();

                    favorite = cursor.getString(cursor.getColumnIndex(MovieDataContract.MovieDataEntry.COLUMN_NAME_ID));

                }

                cursor.close();

                String title = intent.getStringExtra(MovieData.RESULTS_TITLE);
                String release_date = intent.getStringExtra(MovieData.RESULTS_RELEASE_DATE);
                String vote_average = intent.getStringExtra(MovieData.RESULTS_VOTE_AVERAGE);
                String synopsis = intent.getStringExtra(MovieData.RESULTS_OVERVIEW);
                String movie_poster = intent.getStringExtra(MovieData.RESULTS_POSTER_PATH);


                if (favorite.equals("N"))
                    btstar.setImageDrawable(getResources().getDrawable(android.R.drawable.btn_star_big_off,null));
                else
                    btstar.setImageDrawable(getResources().getDrawable(android.R.drawable.btn_star_big_on,null));

                mdata = new MovieData(movieId, title, release_date, movie_poster, vote_average, synopsis, "");


                tvtitle.setText(title);

                tvreleasedate.append(release_date);

                tvvoteaverage.append(vote_average);

                tvsynopsis.append("\n" + synopsis);


                 Picasso.with(getApplicationContext()).load(NetworkUtils.MOVIEDB_BASE_URL_IMAGE + NetworkUtils.IMAGE_SIZE_342 + movie_poster).placeholder(R.mipmap.file_movie).fit().centerCrop().into(ivheader);
                 Picasso.with(getApplicationContext()).load(NetworkUtils.MOVIEDB_BASE_URL_IMAGE + NetworkUtils.IMAGE_SIZE_342 + movie_poster).placeholder(R.mipmap.file_movie).into(ivposter);


                trailerListView.setFocusable(false);

            } else {
                // No extras!
                showDefaultError(getString(R.string.error_message_default));
            }

    }

    void showDefaultError(String error){

        tvtitle.setVisibility(View.INVISIBLE);
        tvreleasedate.setVisibility(View.INVISIBLE);
        tvsynopsis.setVisibility(View.INVISIBLE);
        tvvoteaverage.setVisibility(View.INVISIBLE);
        ivheader.setVisibility(View.INVISIBLE);
        ivposter.setVisibility(View.INVISIBLE);

        tverror.setText(error);
        tverror.setVisibility(View.VISIBLE);
    }

    void showDefaultView(){
        tverror.setVisibility(View.GONE);

        tvtitle.setVisibility(View.VISIBLE);
        tvreleasedate.setVisibility(View.VISIBLE);
        tvsynopsis.setVisibility(View.VISIBLE);
        tvvoteaverage.setVisibility(View.VISIBLE);
        ivheader.setVisibility(View.VISIBLE);
        ivposter.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        MovieData parcelableObject = new MovieData();
        parcelableObject.title = tvtitle.getText().toString();
        parcelableObject.release_date = tvreleasedate.getText().toString();
        parcelableObject.vote_average = tvvoteaverage.getText().toString();
        parcelableObject.plot_synopsis = tvsynopsis.getText().toString();

        outState.putParcelable(DATA, parcelableObject);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        MovieData movieData = savedInstanceState.getParcelable(DATA);

        tvtitle.setText(movieData.getTitle());
        tvreleasedate.setText(movieData.getRelease_date());
        tvsynopsis.setText(movieData.getPlot_synopsis());
        tvvoteaverage.setText(movieData.getVote_average());
    }


    private void toggleFavorite(){
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(MovieDataContract.CONTENT_URI_FAVORITES, null, "id=?", new String[]{movieId},null);
        ContentValues cv = new ContentValues();
        if (cursor.getCount()==0){
            cv.put(MovieDataContract.MovieDataEntry.COLUMN_NAME_ID, mdata.getId());
            cv.put(MovieDataContract.MovieDataEntry.COLUMN_NAME_POSTER, mdata.getMovie_poster());
            cv.put(MovieDataContract.MovieDataEntry.COLUMN_NAME_RELEASE, mdata.getRelease_date());
            cv.put(MovieDataContract.MovieDataEntry.COLUMN_NAME_SYNOPSIS, mdata.getPlot_synopsis());
            cv.put(MovieDataContract.MovieDataEntry.COLUMN_NAME_TITLE, mdata.getTitle());
            cv.put(MovieDataContract.MovieDataEntry.COLUMN_NAME_VOTE, mdata.getVote_average());
            cv.put(MovieDataContract.MovieDataEntry.COLUMN_NAME_TYPE, mdata.getType());

            contentResolver.insert(MovieDataContract.CONTENT_URI_FAVORITES, cv);
            btstar.setImageDrawable(getResources().getDrawable(android.R.drawable.btn_star_big_on,null));
        }else{
            cursor.moveToFirst();
            int deleted;
            deleted = contentResolver.delete(MovieDataContract.CONTENT_URI_FAVORITES,"id=?", new String[]{movieId});
            if (deleted >0)
                    btstar.setImageDrawable(getResources().getDrawable(android.R.drawable.btn_star_big_off,null));
        }

        cursor.close();

    }

    private ArrayList<TrailerData> createTrailers(String data){
        ArrayList<TrailerData> trailerList = new ArrayList<TrailerData>();

        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONArray results = jsonObject.getJSONArray(MovieData.RESULTS);

            if (results.length()==0)
                return trailerList;

            for (int r = 0; r < results.length() ; r++) {
                TrailerData trailer = new TrailerData();
                JSONObject item = (JSONObject) results.get(r);


                trailer.setText(item.getString(TrailerData.RESULTS_NAME));
                trailer.setUrl(YOUTUBE_BASE_URL + item.getString(TrailerData.RESULTS_KEY));


                trailerList.add(trailer);
            }

        }catch(JSONException jsonexception){

        }


        return trailerList;
    }

    private void obtainTrailers(){
        URL url = null;

        url = NetworkUtils.buildUrl(NetworkUtils.TRAILERS, movieId);

        // Outside main thread obtain network data passing interface instance to notify ui activity
        AsyncMovieData asyncMovieData = new AsyncMovieData(onMovieDataReceived);

        asyncMovieData.execute(url);
    }

    private void createReviews(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray results = jsonObject.getJSONArray(MovieData.RESULTS);

            if (results.length()==0)
                return;

            TextView[] textViews = new TextView[results.length()];

            for (int r = 0; r < results.length() ; r++) {
                JSONObject item = (JSONObject) results.get(r);
                final TextView tv = new TextView(this);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                tv.setPadding(10,0,10,0);
                tv.setText(getString(R.string.reviews_author) +
                        item.getString(MovieData.RESULTS_REVIEWS_AUTHOR) +
                        "\r\n" + getString(R.string.reviews_review) +
                        item.getString(MovieData.RESULTS_REVIEWS_CONTENT));

                textViews[r] = tv;
            }

            LinearLayout ll = (LinearLayout) findViewById(R.id.trailer_layout);

            ll.findViewById(R.id.tv_noreviews).setVisibility(View.GONE);
            ViewGroup.LayoutParams lp = ll.getLayoutParams();

            for (int y = 0 ; y < textViews.length ; y++) {
                ll.addView(textViews[y], ll.getChildCount(), lp);
            }

        }catch(JSONException jsonexception){

        }

    }

    private void obtainReviews(){
        URL url = null;

        url = NetworkUtils.buildUrl(NetworkUtils.REVIEWS, movieId);

        // Outside main thread obtain network data passing interface instance to notify ui activity
        AsyncMovieData asyncMovieData = new AsyncMovieData(onMovieDataReceived);

        asyncMovieData.execute(url);
    }

    private void fillList(ArrayList<TrailerData> trailers){
        trailerAdapter.modifyData(trailers);
    }

//MENU ADDED
@Override
public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.detail_menu, menu);

    MenuItem item = menu.findItem(R.id.menu_item_share);

    // Fetch and store ShareActionProvider
    mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);



    return true;
}


    private void setShareIntent(boolean hasTrailer) {
        Intent shareIntent = new Intent();

        shareIntent.setAction(ACTION_SEND);

        shareIntent.setType("text/plain");

        if (hasTrailer){
            String trailerUrl = getFirstTrailer();
            shareIntent.putExtra(Intent.EXTRA_TEXT, Uri.parse(trailerUrl).toString());
        }else{
            shareIntent = null;
        }

        if (mShareActionProvider != null) {

                mShareActionProvider.setShareIntent(shareIntent);

        }else{
            mShareActionProvider = new ShareActionProvider(this);
            MenuItem menuItem = (MenuItem) findViewById(R.id.menu_item_share);
            MenuItemCompat.setActionProvider(menuItem, mShareActionProvider);

            mShareActionProvider.setShareIntent(shareIntent);
        }
    }
    //

    private String getFirstTrailer(){

        if (trailerList!=null && trailerList.size() > 0){
            String firstTrailer = trailerList.get(0).getUrl();

            return firstTrailer;
        }

        return "";
    }

    private void hideTrailer(boolean hide){
        if (hide){
            trailerListView.setVisibility(View.INVISIBLE);
            TextView tvNoTrailer = (TextView) findViewById(R.id.tv_notrailer);
            tvNoTrailer.setVisibility(View.VISIBLE);

        }else{
            trailerListView.setVisibility(View.VISIBLE);
            TextView tvNoTrailer = (TextView) findViewById(R.id.tv_notrailer);
            tvNoTrailer.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
