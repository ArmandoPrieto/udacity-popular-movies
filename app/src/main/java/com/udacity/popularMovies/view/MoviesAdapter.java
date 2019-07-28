package com.udacity.popularMovies.view;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.udacity.popularMovies.R;
import com.udacity.popularMovies.model.Movie;

import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {
    private List<Movie> mMoviesDataSet;
    private Context mContext;
    private ViewHolder.OnMovieListener mOnMovieListener;

    public MoviesAdapter(Context context, List<Movie> moviesDataSet, ViewHolder.OnMovieListener onMovieListener){
        mMoviesDataSet = moviesDataSet;
        mContext = context;
        mOnMovieListener = onMovieListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.movies_view,parent,false);
        ViewHolder vh = new ViewHolder(view, mOnMovieListener);
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

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView mImageView;
        OnMovieListener onMovieListener;
        ViewHolder(View itemView, OnMovieListener onMovieListener) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.iv_movie_poster);
            this.onMovieListener = onMovieListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onMovieListener.onMovieClick(getAdapterPosition());
        }

        public interface OnMovieListener{
                void onMovieClick(int position);
        }
    }

}
