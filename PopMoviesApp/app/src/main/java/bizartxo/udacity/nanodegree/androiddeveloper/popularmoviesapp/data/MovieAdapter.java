package bizartxo.udacity.nanodegree.androiddeveloper.popularmoviesapp.data;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import bizartxo.udacity.nanodegree.androiddeveloper.popularmoviesapp.R;
import bizartxo.udacity.nanodegree.androiddeveloper.popularmoviesapp.UiActivity;
import bizartxo.udacity.nanodegree.androiddeveloper.popularmoviesapp.utils.NetworkUtils;



public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {


    Context context;

    Cursor mMovieDataCursor;

    private ListItemClickListener mOnClickListener;

    public MovieAdapter(Context context, ListItemClickListener listener){

        this.context = context;
        mOnClickListener = listener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int layoutIdForListItem = R.layout.movie_rv_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentInmediately = false;

        View view  = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentInmediately);
        MovieViewHolder movieViewHolder = new MovieViewHolder(view);
        return movieViewHolder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {

        mMovieDataCursor.moveToPosition(position);

        String moviePoster = mMovieDataCursor.getString(UiActivity.INDEX_MOVIE_POSTER);
        String movieTitle = mMovieDataCursor.getString(UiActivity.INDEX_MOVIE_TITLE);
        String movieSynopsis = mMovieDataCursor.getString(UiActivity.INDEX_MOVIE_SYNOPSIS);
        String movieVote = mMovieDataCursor.getString(UiActivity.INDEX_MOVIE_VOTE);
        String movieRelease = mMovieDataCursor.getString(UiActivity.INDEX_MOVIE_RELEASE);

        MovieData mdata = new MovieData();

        mdata.setMovie_poster(moviePoster);
        mdata.setTitle(movieTitle);
        mdata.setPlot_synopsis(movieSynopsis);
        mdata.setVote_average(movieVote);
        mdata.setRelease_date(movieRelease);

        holder.bind(mdata);
    }

    //
    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imageView; // Poster on RecyclerView
        TextView textView; // Title

        public MovieViewHolder(View itemView){
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            textView = (TextView) itemView.findViewById(R.id.textView);
            itemView.setOnClickListener(this);
        }

        void bind(MovieData moviedata){
            //  Get poster
            String picasourl = NetworkUtils.MOVIEDB_BASE_URL_IMAGE + NetworkUtils.IMAGE_SIZE_185 + moviedata.getMovie_poster();

            Picasso.with(context).load(picasourl).placeholder(R.mipmap.file_movie).into(imageView);

            textView.setText(moviedata.getTitle());
            textView.setVisibility(View.INVISIBLE);
            // Few seconds to show the poster before title appears
            textView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    textView.setVisibility(View.VISIBLE);
                }
            }, 3000);

        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getPosition();
            mOnClickListener.onListItemClick(clickedPosition);

        }
    }

    //
    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }
    //

    @Override
    public int getItemCount() {
        if (null == mMovieDataCursor) return 0;

        return mMovieDataCursor.getCount();
    }


    public MovieData getItem(int position){
        MovieData md = new MovieData();

        mMovieDataCursor.moveToPosition(position);
        md.setId(mMovieDataCursor.getString(mMovieDataCursor.getColumnIndex(MovieDataContract.MovieDataEntry.COLUMN_NAME_ID)));
        md.setTitle(mMovieDataCursor.getString(mMovieDataCursor.getColumnIndex(MovieDataContract.MovieDataEntry.COLUMN_NAME_TITLE)));
        md.setMovie_poster(mMovieDataCursor.getString(mMovieDataCursor.getColumnIndex(MovieDataContract.MovieDataEntry.COLUMN_NAME_POSTER)));
        md.setPlot_synopsis(mMovieDataCursor.getString(mMovieDataCursor.getColumnIndex(MovieDataContract.MovieDataEntry.COLUMN_NAME_SYNOPSIS)));
        md.setRelease_date(mMovieDataCursor.getString(mMovieDataCursor.getColumnIndex(MovieDataContract.MovieDataEntry.COLUMN_NAME_RELEASE)));
        md.setVote_average(mMovieDataCursor.getString(mMovieDataCursor.getColumnIndex(MovieDataContract.MovieDataEntry.COLUMN_NAME_VOTE)));

        return md;
    }


    public void  swapCursor(Cursor newCursor) {

        mMovieDataCursor = newCursor;
        notifyDataSetChanged();

    }


}

