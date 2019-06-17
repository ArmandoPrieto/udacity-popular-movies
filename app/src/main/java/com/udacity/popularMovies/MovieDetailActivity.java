package com.udacity.popularMovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.popularMovies.model.Movie;

public class MovieDetailActivity extends AppCompatActivity {
    private TextView mMovieTitle;
    private TextView mMovieOverview;
    private TextView mVoteAverage;
    private TextView mReleaseDate;
    private ImageView mMoviePoster;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        mMovieTitle = findViewById(R.id.tv_movie_title);
        mMovieOverview = findViewById(R.id.tv_movie_overview);
        mVoteAverage = findViewById(R.id.tv_movie_vote_average);
        mReleaseDate = findViewById(R.id.tv_movie_release_date);
        mMoviePoster = findViewById(R.id.iv_movie_poster_detail);
        Intent currentIntent = getIntent();
        if(currentIntent.hasExtra(Movie.TITLE))
            mMovieTitle.setText(currentIntent.getStringExtra(Movie.TITLE));
        if(currentIntent.hasExtra(Movie.OVERVIEW))
            mMovieOverview.setText(currentIntent.getStringExtra(Movie.OVERVIEW));
        if(currentIntent.hasExtra(Movie.VOTE_AVERAGE))
            mVoteAverage.setText(currentIntent.getStringExtra(Movie.VOTE_AVERAGE));
        if(currentIntent.hasExtra(Movie.RELEASE_DATE))
            mReleaseDate.setText(currentIntent.getStringExtra(Movie.RELEASE_DATE));
        if(currentIntent.hasExtra(Movie.POSTER_PATH))
            Picasso.get().load(currentIntent.getStringExtra(Movie.POSTER_PATH)).into(mMoviePoster);
    }
}
