package com.udacity.popularMovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.popularMovies.model.Movie;

import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {
    private static final String TAG = MoviesAdapter.class.getName();
    private List<Movie> mMoviesDataSet;
    private Context mContext;
    private int mImageSize;

    public MoviesAdapter(Context context, List<Movie> moviesDataSet){
        mMoviesDataSet = moviesDataSet;
        mContext = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.movies_view,parent,false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso.get().load(mMoviesDataSet.get(position).getPosterPath(Movie.IMAGE_SIZE_LARGE)).into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mMoviesDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
       // public LinearLayout mLinearLayout;
        public ImageView mImageView;
        public ViewHolder(View itemView) {
            super(itemView);
           // mLinearLayout = (LinearLayout) itemView.findViewById(R.id.la_movies_view);
            mImageView = itemView.findViewById(R.id.iv_movie_poster);
        }
    }

}
