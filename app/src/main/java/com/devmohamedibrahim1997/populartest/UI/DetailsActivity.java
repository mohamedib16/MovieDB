package com.devmohamedibrahim1997.populartest.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.devmohamedibrahim1997.populartest.R;
import com.devmohamedibrahim1997.populartest.Room.WatchLaterMoviesViewModel;
import com.devmohamedibrahim1997.populartest.adapter.CastAdapter;
import com.devmohamedibrahim1997.populartest.adapter.GenreAdapter;
import com.devmohamedibrahim1997.populartest.adapter.RecommendedMovieAdapter;
import com.devmohamedibrahim1997.populartest.adapter.SimilarMovieAdapter;
import com.devmohamedibrahim1997.populartest.databinding.ActivityDetailsBinding;
import com.devmohamedibrahim1997.populartest.model.DetailsResponse;
import com.devmohamedibrahim1997.populartest.model.MovieEntity;
import com.devmohamedibrahim1997.populartest.model.Videos;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.devmohamedibrahim1997.populartest.Utils.HelperClass.isNetworkAvailable;
import static com.devmohamedibrahim1997.populartest.Utils.HelperClass.showSnackBar;
import static com.devmohamedibrahim1997.populartest.Utils.HelperClass.showToast;

public class DetailsActivity extends AppCompatActivity {


    @BindView(R.id.detailWatchLaterImageButton)
    ImageButton watchLaterImageButton;
    @BindView(R.id.detailVideoPlayImageButton)
    ImageButton videoPlayImageButton;

    @BindView(R.id.detailSimilarRecyclerView)
    RecyclerView similarMoviesRecyclerView;
    @BindView(R.id.detailRecommendationRecyclerView)
    RecyclerView recommendedMoviesRecyclerView;
    @BindView(R.id.detailGenresRecyclerView)
    RecyclerView genresRecyclerView;
    @BindView(R.id.detailCastRecyclerView)
    RecyclerView castRecyclerView;


    private CastAdapter castAdapter;
    private SimilarMovieAdapter similarAdapter;
    private RecommendedMovieAdapter recommendedAdapter;
    private GenreAdapter genreAdapter;

    private DetailViewModel detailViewModel;
    ActivityDetailsBinding detailsBinding;
    private boolean exists = false;
    private int movieId;
    private String videoKey;
    private ArrayList<String> videosKeysArrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_details);
        ButterKnife.bind(this);

        getMovieIntent();
        initGenreRecyclerView();
        initCastRecyclerView();
        initSimilarMoviesRecyclerView();
        initRecommendedMoviesRecyclerView();
        initViewModel(savedInstanceState);

    }

    private void getMovieIntent() {
        movieId = getIntent().getIntExtra("movieId", 0);
    }

    private void initViewModel(Bundle savedInstanceState) {
        if (isNetworkAvailable(DetailsActivity.this)) {
            detailViewModel = ViewModelProviders.of(this).get(DetailViewModel.class);
            if (savedInstanceState == null) {
                detailViewModel.init(movieId);
            }

            getMovieDetails();
            getMovieCredit();
            getSimilarMovies();
            onSimilarMoviesRecyclerViewItemClicked();
            getRecommendedMovies();
            onRecommendedMoviesRecyclerViewItemClicked();

        } else {
            showSnackBar(DetailsActivity.this);
        }
    }

    private void getMovieDetails() {
        detailViewModel.getMovieDetails().observe(this, detailsResponse -> {
            if (detailsResponse != null) {
                detailsBinding.setMovieDetails(detailsResponse);
                genreAdapter.setData(detailsResponse.getGenres());
                genreAdapter.notifyDataSetChanged();
            }
        });
    }

    private void getMovieCredit() {
        detailViewModel.getMovieCredit().observe(this, creditResponse -> {
            if (creditResponse != null) {
                if (creditResponse.getCast() != null) {
                    castAdapter.setCast(creditResponse.getCast());
                    castAdapter.notifyDataSetChanged();
                }

                if (creditResponse.getCrew() != null) {
                    try {
                        detailsBinding.setCrewDirector(creditResponse.getCrew().get(0).getName());
                        if (creditResponse.getCrew().size() >= 2) {
                            detailsBinding.setCrewWriters(creditResponse.getCrew().get(1).getName());
                        }
                    }catch (IndexOutOfBoundsException e){
                        Log.e("exception", e.getMessage() );
                    }

                }
            }
        });
    }

    private void getSimilarMovies() {
        detailViewModel.getSimilarMovies().observe(this, movies -> {
            similarAdapter.setData(movies);
            similarAdapter.notifyDataSetChanged();
        });
    }

    private void onSimilarMoviesRecyclerViewItemClicked() {
        similarAdapter.setOnItemClickListener((position, v) -> {
            Intent intent = new Intent(DetailsActivity.this, DetailsActivity.class);
            if (detailViewModel.getSimilarMovies().getValue() != null) {
                intent.putExtra("movieId", detailViewModel.getSimilarMovies().getValue().get(position).getId());
            }
            startActivity(intent);
        });
    }

    private void initGenreRecyclerView() {
        genresRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        genreAdapter = new GenreAdapter(this);
        genresRecyclerView.setAdapter(genreAdapter);
    }

    private void initCastRecyclerView() {
        LinearLayoutManager castLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        castRecyclerView.setLayoutManager(castLayoutManager);
        castAdapter = new CastAdapter(this);
        castRecyclerView.setAdapter(castAdapter);
    }

    private void initSimilarMoviesRecyclerView() {
        LinearLayoutManager similarLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        similarMoviesRecyclerView.setLayoutManager(similarLayoutManager);
        similarAdapter = new SimilarMovieAdapter(this);
        similarMoviesRecyclerView.setAdapter(similarAdapter);
    }

    private void initRecommendedMoviesRecyclerView() {
        LinearLayoutManager recommendedLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recommendedMoviesRecyclerView.setLayoutManager(recommendedLayoutManager);
        recommendedAdapter = new RecommendedMovieAdapter(this);
        recommendedMoviesRecyclerView.setAdapter(recommendedAdapter);
    }

    private void getRecommendedMovies() {
        detailViewModel.getRecommendedMovies().observe(this, movies -> {
            recommendedAdapter.setData(movies);
            recommendedAdapter.notifyDataSetChanged();
        });
    }

    private void onRecommendedMoviesRecyclerViewItemClicked() {
        recommendedAdapter.setOnItemClickListener((position, v) -> {
            Intent intent = new Intent(DetailsActivity.this, DetailsActivity.class);
            if (detailViewModel.getRecommendedMovies().getValue() != null) {
                intent.putExtra("movieId", detailViewModel.getRecommendedMovies().getValue().get(position).getId());
            }
            startActivity(intent);
        });
    }

    private void getMovieVideos() {
        detailViewModel.getMovieVideos().observe(this, videos -> {
            if (videos != null) {
                try {
                    videoKey = videos.get(0).getKey();
                    for(Videos video : videos){
                        videosKeysArrayList.add(video.getKey());
                    }
                }catch (IndexOutOfBoundsException e){
                    showToast(DetailsActivity.this,"No Trailer video");
                }

            }
        });
    }

    @OnClick({R.id.detailVideoPlayImageButton, R.id.detailWatchLaterImageButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.detailVideoPlayImageButton:
                onVideoPlayImageClicked();
                break;
            case R.id.detailWatchLaterImageButton:
                onWatchLaterImageClicked();
                break;
        }
    }

    public void onVideoPlayImageClicked() {
        getMovieVideos();

        //send movie key if its not null
        if(videoKey != null) {
            Intent intent = new Intent(DetailsActivity.this, VideoPlayerActivity.class);
            intent.putExtra("videoKey", videoKey);
            intent.putExtra("videosKeysArrayList",videosKeysArrayList);
            startActivity(intent);
        }
    }

    public void onWatchLaterImageClicked() {

        if (detailViewModel.getMovieDetails().getValue() != null) {

            final DetailsResponse movie = detailViewModel.getMovieDetails().getValue();

            final WatchLaterMoviesViewModel movieViewModel = ViewModelProviders.of(this).get(WatchLaterMoviesViewModel.class);

            movieViewModel.getAllMovies().observe(DetailsActivity.this, movieEntities -> {
                if (movieEntities != null) {
                    for (MovieEntity m : movieEntities) {
                        if (m.getId().equals(movie.getId())) {
                            exists = true;
                        }
                    }
                }

                if (!exists) {
                    movieViewModel.insert(new MovieEntity(movie.getId(), movie.getVoteAverage(), movie.getTitle(),
                            movie.getPosterPath(), movie.getOverview(), movie.getReleaseDate()));
                    showToast(DetailsActivity.this, "Movie added to watch later ");
                }
            });
        }

    }

    public void detailBack(View view) {
        finish();
    }
}
