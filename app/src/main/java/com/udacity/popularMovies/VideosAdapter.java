package com.udacity.popularMovies;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.udacity.popularMovies.model.Video;
import java.util.List;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.ViewHolder>  {
    private List<Video> mVideosDataSet;
    private Context mContext;
    private VideosAdapter.ViewHolder.OnVideoListener mOnVideoListener;

    public VideosAdapter(Context mContext, List<Video> mVideosDataSet, ViewHolder.OnVideoListener mOnVideoListener) {
        this.mVideosDataSet = mVideosDataSet;
        this.mContext = mContext;
        this.mOnVideoListener = mOnVideoListener;
    }

    @NonNull
    @Override
    public VideosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.videos_view,parent,false);
        VideosAdapter.ViewHolder vh = new VideosAdapter.ViewHolder(view, mOnVideoListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull VideosAdapter.ViewHolder holder, int position) {
        holder.mTextView.setText(mVideosDataSet.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return mVideosDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTextView;
        VideosAdapter.ViewHolder.OnVideoListener onVideoListener;
        ViewHolder(View itemView, VideosAdapter.ViewHolder.OnVideoListener onVideoListener) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.tv_trailerText);
            this.onVideoListener = onVideoListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onVideoListener.onVideoClick(getAdapterPosition());
        }

        public interface OnVideoListener{
            void onVideoClick(int position);
        }
    }
}
