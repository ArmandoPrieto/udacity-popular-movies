package com.udacity.popularMovies;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.udacity.popularMovies.model.Movie;
import com.udacity.popularMovies.model.Video;
import com.udacity.popularMovies.utils.JsonUtils;
import com.udacity.popularMovies.utils.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MovieDetailActivity extends AppCompatActivity implements VideosAdapter.ViewHolder.OnVideoListener{

    private TextView mMovieTitle;
    private TextView mMovieOverview;
    private TextView mVoteAverage;
    private TextView mReleaseDate;
    private ImageView mMoviePoster;
    private ArrayList<Video> videoList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private String trailerUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        mMovieTitle = findViewById(R.id.tv_movie_title);
        mMovieOverview = findViewById(R.id.tv_movie_overview);
        mVoteAverage = findViewById(R.id.tv_movie_vote_average);
        mReleaseDate = findViewById(R.id.tv_movie_release_date);
        mMoviePoster = findViewById(R.id.iv_movie_poster_detail);
        mRecyclerView = findViewById(R.id.rv_movie_video_list);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

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
        if(currentIntent.hasExtra(Movie.POSTER_PATH))
            Picasso.get().load(currentIntent.getStringExtra(Movie.POSTER_PATH)).into(mMoviePoster);
        mAdapter = new VideosAdapter(this,videoList,this);
        mRecyclerView.setAdapter(mAdapter);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),mLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(mDividerItemDecoration);

        if(currentIntent.hasExtra(Movie.ID))
            loadMovieVideos(Integer.valueOf(currentIntent.getIntExtra(Movie.ID,0)));


    }
    private void loadMovieVideos(int movieId){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        URL url = NetworkUtils.buildVideosUrl(movieId);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JsonUtils.parseMovieVideoListJson(response, videoList);
                        mAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), getString(R.string.onVideoLoadingError), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
    }

    @Override
    public void onVideoClick(int position) {
        Video video = videoList.get(position);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v="+video.getKey()));
        startActivity(intent);

    }
}
