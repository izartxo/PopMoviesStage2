package bizartxo.udacity.nanodegree.androiddeveloper.popularmoviesapp.data;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import bizartxo.udacity.nanodegree.androiddeveloper.popularmoviesapp.R;
import bizartxo.udacity.nanodegree.androiddeveloper.popularmoviesapp.data.TrailerData;

import static android.content.Intent.ACTION_VIEW;



public class TrailerAdapter extends BaseAdapter {

    Context context;
    ArrayList<TrailerData> trailers;
    LayoutInflater inflater = null;

    public TrailerAdapter(Context context, ArrayList<TrailerData> trailerList){
        this.context = context;
        this.trailers = trailerList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return trailers.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View root = inflater.inflate(R.layout.trailer_item, null);

        final TextView trailerText = (TextView) root.findViewById(R.id.tvtrailer);
        trailerText.setText(trailers.get(position).getText());
        trailerText.setTag(trailers.get(position).getUrl());

        trailerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent trailerVideoIntent = new Intent();
                trailerVideoIntent.setAction(ACTION_VIEW);
                trailerVideoIntent.setData(Uri.parse(trailerText.getTag().toString()));

                context.startActivity(Intent.createChooser(trailerVideoIntent, context.getString(R.string.share_message)));
            }
        });

        return root;
    }

    public void modifyData(ArrayList<TrailerData> newData){
        trailers = newData;
        notifyDataSetChanged();
    }
}
