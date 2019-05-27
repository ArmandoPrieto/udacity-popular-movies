package com.udacity.popularMovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.udacity.popularMovies.model.Movie;

public class MovieDetailActivity extends AppCompatActivity {
    private TextView mMovieTitle;
    private TextView mMovieOverview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        mMovieTitle = (TextView) findViewById(R.id.tv_movie_title);
        mMovieOverview = (TextView) findViewById(R.id.tv_movie_overview);
        Intent currentIntent = getIntent();
        if(currentIntent.hasExtra(Movie.TITLE))
            mMovieTitle.setText(currentIntent.getStringExtra(Movie.TITLE));
        if(currentIntent.hasExtra(Movie.OVERVIEW))
            mMovieOverview.setText(currentIntent.getStringExtra(Movie.OVERVIEW));


    }
}
