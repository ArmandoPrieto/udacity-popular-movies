package com.udacity.popularMovies;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.udacity.popularMovies.model.Review;
import com.udacity.popularMovies.model.Video;
import com.udacity.popularMovies.utils.JsonUtils;
import com.udacity.popularMovies.utils.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

public class MovieDetailActivity extends AppCompatActivity implements VideosAdapter.ViewHolder.OnVideoListener,
 ReviewsAdapter.ViewHolder.OnReviewListener{

    public static final String YOUTUBE_URL = "http://www.youtube.com/watch?v=";
    private TextView mMovieTitle;
    private TextView mMovieOverview;
    private TextView mVoteAverage;
    private TextView mReleaseDate;
    private TextView mIsFavoriteTextView;
    private ImageView mMoviePoster;
    private Button mFavoriteButton;
    private ArrayList<Video> videoList = new ArrayList<>();
    private ArrayList<Review> reviewList = new ArrayList<>();
    private RecyclerView mVideosRecyclerView;
    private RecyclerView.Adapter mVideosAdapter;
    private LinearLayoutManager mVideosLayoutManager;
    private RecyclerView mReviewsRecyclerView;
    private RecyclerView.Adapter mReviewsAdapter;
    private LinearLayoutManager mReviewsLayoutManager;
    private int reviewsPage = 1;
    private boolean isFavorite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        mMovieTitle = findViewById(R.id.tv_movie_title);
        mMovieOverview = findViewById(R.id.tv_movie_overview);
        mVoteAverage = findViewById(R.id.tv_movie_vote_average);
        mReleaseDate = findViewById(R.id.tv_movie_release_date);
        mMoviePoster = findViewById(R.id.iv_movie_poster_detail);
        mIsFavoriteTextView = findViewById(R.id.tv_is_favorite);
        mVideosRecyclerView = findViewById(R.id.rv_movie_video_list);
        mFavoriteButton = findViewById(R.id.bt_favorite);
        mVideosLayoutManager = new LinearLayoutManager(this);
        mVideosRecyclerView.setLayoutManager(mVideosLayoutManager);
        mReviewsRecyclerView = findViewById(R.id.rv_movie_review_list);
        mReviewsLayoutManager = new LinearLayoutManager(this);
        mReviewsRecyclerView.setLayoutManager(mReviewsLayoutManager);

        final Intent currentIntent = getIntent();
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
        if(currentIntent.hasExtra(Movie.IS_FAVORITE)) {
            isFavorite = currentIntent.getBooleanExtra(Movie.IS_FAVORITE, false);
            mIsFavoriteTextView.setText(String.valueOf(isFavorite));
        }
        mVideosAdapter = new VideosAdapter(this,videoList,this);
        mVideosRecyclerView.setAdapter(mVideosAdapter);
        mReviewsAdapter = new ReviewsAdapter(this,reviewList,this);
        mReviewsRecyclerView.setAdapter(mReviewsAdapter);

        mVideosRecyclerView.setNestedScrollingEnabled(false);
        mReviewsRecyclerView.setNestedScrollingEnabled(false);
        if(currentIntent.hasExtra(Movie.ID))
            loadComponents(currentIntent.getIntExtra(Movie.ID, 0));

        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                isFavorite = !isFavorite;
                mIsFavoriteTextView.setText(String.valueOf(isFavorite));
                Intent replyIntent = new Intent();
                setResult(RESULT_OK, replyIntent);
                replyIntent.putExtra(Movie.ID, currentIntent.getIntExtra(Movie.ID, 0));
                replyIntent.putExtra(Movie.TITLE, currentIntent.getStringExtra(Movie.TITLE));
                replyIntent.putExtra(Movie.IS_FAVORITE, isFavorite);
            }
        });

    }
    private void loadComponents(int movieID) {
        loadMovieVideos(movieID);
        loadMovieReviews(movieID,reviewsPage);
    }

    private void loadMovieVideos(int movieId){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        URL url = NetworkUtils.buildVideosUrl(movieId);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JsonUtils.parseMovieVideoListJson(response, videoList);
                        mVideosAdapter.notifyDataSetChanged();
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

    private void loadMovieReviews(int movieId, int page){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        URL url = NetworkUtils.buildReviewsUrl(movieId, page);
        final boolean clearReviewList = (page == 1);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JsonUtils.parseMovieReviewListJson(response, reviewList, clearReviewList);
                        mReviewsAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), getString(R.string.onError), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
    }

    @Override
    public void onVideoClick(int position) {
        Video video = videoList.get(position);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_URL +video.getKey()));
        startActivity(intent);

    }

    @Override
    public void onReviewClick(int position) {
        Toast.makeText(this,"Review clicked: #"+position,Toast.LENGTH_SHORT).show();
    }


}
