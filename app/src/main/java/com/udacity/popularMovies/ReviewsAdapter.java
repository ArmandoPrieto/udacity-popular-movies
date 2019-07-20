package com.udacity.popularMovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.popularMovies.model.Review;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {
    private List<Review> mReviewsDataSet;
    private Context mContext;
    private ReviewsAdapter.ViewHolder.OnReviewListener mOnReviewListener;

    ReviewsAdapter(Context mContext, List<Review> mReviewsDataSet, ViewHolder.OnReviewListener mOnReviewListener) {
        this.mReviewsDataSet = mReviewsDataSet;
        this.mContext = mContext;
        this.mOnReviewListener = mOnReviewListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.reviews_view,parent,false);
        return new ReviewsAdapter.ViewHolder(view, mOnReviewListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mTextViewContent.setText(mReviewsDataSet.get(position).getContent());
        holder.mTextViewAuthor.setText(mReviewsDataSet.get(position).getAuthor());
    }

    @Override
    public int getItemCount() {
        return mReviewsDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTextViewContent;
        TextView mTextViewAuthor;
        ReviewsAdapter.ViewHolder.OnReviewListener onReviewListener;
        ViewHolder(View itemView, ReviewsAdapter.ViewHolder.OnReviewListener onReviewListener) {
            super(itemView);
            mTextViewContent = itemView.findViewById(R.id.tv_review_content);
            mTextViewAuthor = itemView.findViewById(R.id.tv_review_author);
            this.onReviewListener = onReviewListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onReviewListener.onReviewClick(getAdapterPosition());
        }

        public interface OnReviewListener{
            void onReviewClick(int position);
        }
    }
}
