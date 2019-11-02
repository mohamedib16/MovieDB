package com.devmohamedibrahim1997.populartest.BottomNavigationFragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.devmohamedibrahim1997.populartest.UI.MovieViewModel;
import com.devmohamedibrahim1997.populartest.R;
import com.devmohamedibrahim1997.populartest.UI.DetailsActivity;
import com.devmohamedibrahim1997.populartest.adapter.MovieAdapter;
import com.devmohamedibrahim1997.populartest.databinding.FragmentNowPlayingBinding;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.devmohamedibrahim1997.populartest.Utils.HelperClass.getScreenOrientation;
import static com.devmohamedibrahim1997.populartest.Utils.HelperClass.isNetworkAvailable;
import static com.devmohamedibrahim1997.populartest.Utils.HelperClass.showSnackBar;

public class NowPlayingFragment extends Fragment {

    private RecyclerView movieRecyclerView;

    private FragmentActivity activity;
    private MovieViewModel movieViewModel;
    private MovieAdapter recyclerViewAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentNowPlayingBinding nowPlayingBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_now_playing, container, false);
        movieRecyclerView = nowPlayingBinding.nowPlayingRecyclerView;
        activity = getActivity();
        initRecyclerView();
        initViewModel();

        return nowPlayingBinding.getRoot();
    }

    private void initViewModel() {
        if (isNetworkAvailable(activity)) {
            movieViewModel = ViewModelProviders.of(this).get(MovieViewModel.class);
            getMovies();
            onMovieClick();
        } else {
            showSnackBar(activity);
        }
    }


    private void getMovies() {
        movieViewModel.getMovies("now_playing").observe(this, movies -> {
            if (movies!= null) {
                recyclerViewAdapter.submitList(movies);
                movieRecyclerView.setAdapter(recyclerViewAdapter);
            }
        });
    }

    private void onMovieClick() {
        recyclerViewAdapter.setOnItemClickListener((position, v) -> {
            Intent intent = new Intent(activity, DetailsActivity.class);
            if (movieViewModel.getMovies("now_playing").getValue() != null) {
                intent.putExtra("movieId", movieViewModel.getMovies("now_playing").getValue().get(position).getId());
            }
            startActivity(intent);
        });
    }

    private void initRecyclerView() {
        int spanCount = getScreenOrientation(activity);
        movieRecyclerView.setLayoutManager(new GridLayoutManager(activity, spanCount));
        recyclerViewAdapter = new MovieAdapter(activity);
    }
}
