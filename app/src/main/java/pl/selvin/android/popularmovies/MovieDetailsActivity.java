/*
 Copyright (c) 2018 Selvin
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.
 */

package pl.selvin.android.popularmovies;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.selvin.android.popularmovies.adapters.MoviesAdapter;
import pl.selvin.android.popularmovies.adapters.ReviewsAdapter;
import pl.selvin.android.popularmovies.adapters.VideosAdapter;
import pl.selvin.android.popularmovies.models.Movie;
import pl.selvin.android.popularmovies.models.MovieDetails;
import pl.selvin.android.popularmovies.models.MovieWithDetails;
import pl.selvin.android.popularmovies.models.Resource;
import pl.selvin.android.popularmovies.models.Review;
import pl.selvin.android.popularmovies.models.Status;
import pl.selvin.android.popularmovies.models.Video;
import pl.selvin.android.popularmovies.viewmodels.MovieDetailsViewModel;

import static pl.selvin.android.popularmovies.utils.Constants.IMAGE_BASE_URL_SIZED;

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String MOVIE_ID = "MOVIE_ID";
    private static final String POSITION = "POSITION";

    @SuppressWarnings("unchecked")
    public static void startDetailsActivityForResult(@NonNull Activity context, MoviesAdapter.Holder holder, int code) {
        final long movieId = holder.getItemId();
        final int position = holder.getAdapterPosition();
        final ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(context,
                Pair.<View, String>create(holder.image, "image" + movieId),
                Pair.<View, String>create(holder.title, "title" + movieId));
        ActivityCompat.startActivityForResult(context, new Intent(context, MovieDetailsActivity.class)
                .putExtra(MOVIE_ID, movieId).putExtra(POSITION, position), code, options.toBundle());
    }

    private MovieDetailsViewModel model;
    private Movie movie = null;
    private Snackbar lastSnack = null;

    @BindView(R.id.movie_details_request_focus)
    View requestFocusView;
    @BindView(R.id.movie_details_title)
    TextView titleView;
    @BindView(R.id.movie_details_title_org)
    TextView titleOrgView;
    @BindView(R.id.movie_details_image)
    ImageView imageView;
    @BindView(R.id.movie_details_rating)
    TextView ratingView;
    @BindView(R.id.movie_details_year)
    TextView yearView;
    @BindView(R.id.movie_details_status)
    TextView statusView;
    @BindView(R.id.movie_details_duration)
    TextView durationView;
    @BindView(R.id.movie_details_description)
    TextView descriptionView;
    @BindView(R.id.movie_details_videos_divider)
    View videosDividerView;
    @BindView(R.id.movie_details_videos)
    RecyclerView videosView;
    @BindView(R.id.movie_details_videos_title)
    TextView videosTitleView;
    @BindView(R.id.movie_details_reviews_divider)
    View reviewsDividerView;
    @BindView(R.id.movie_details_reviews)
    RecyclerView reviewsView;
    @BindView(R.id.movie_details_reviews_title)
    TextView reviewsTitleView;
    @BindView(R.id.movie_details_image_progress)
    View imageProgress;
    @BindView(R.id.movie_details_fav)
    FloatingActionButton favourite;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.scroll)
    NestedScrollView scroll;
    @BindView(R.id.movie_details_coordinator_layout)
    View coordinatorLayout;

    @OnClick(R.id.movie_details_fav)
    void favOnClick(View view) {
        if (movie != null) {
            final boolean fav = !movie.isFavourite();
            movie.setFavourite(fav);
            model.saveMovie(movie).observe(this, new Observer<Integer>() {
                @Override
                public void onChanged(@Nullable Integer integer) {
                    if (lastSnack != null)
                        lastSnack.dismiss();
                    lastSnack = Snackbar.make(coordinatorLayout, fav ? "Movie added to favorites"
                            : "Movie removed from favorites", Snackbar.LENGTH_SHORT);
                    lastSnack.show();
                }
            });
        }
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details_activity);
        if (savedInstanceState == null)
            supportPostponeEnterTransition();
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        final Intent intent = getIntent();
        videosView.setNestedScrollingEnabled(false);
        if (intent != null) {
            final long id = intent.getLongExtra(MOVIE_ID, -1);
            ViewCompat.setTransitionName(imageView, "image" + id);
            ViewCompat.setTransitionName(titleView, "title" + id);
            if (id != -1) {
                setTitle(R.string.movie_detail_title);
                imageProgress.setVisibility(View.VISIBLE);
                model = ViewModelProviders.of(this).get(MovieDetailsViewModel.class);
                model.getMovieDetails(id).observe(this, new Observer<Resource<MovieWithDetails>>() {
                    @Override
                    public void onChanged(@Nullable Resource<MovieWithDetails> movieDetails) {
                        if (movieDetails != null && movieDetails.data != null) {
                            movie = movieDetails.data.movie;
                            final MovieDetails details = movieDetails.data.details;
                            if (movie != null) {
                                titleView.setText(movie.getTitle());
                                descriptionView.setText(movie.getOverview());
                                ratingView.setText(getString(R.string.movie_details_rating_format, movie.getVoteAverage()));
                                if (movie.isFavourite()) {
                                    favourite.setImageResource(R.drawable.ic_favorite_active);
                                } else {
                                    favourite.setImageResource(R.drawable.ic_favorite);
                                }

                                if (!movie.getTitle().equals(movie.getOriginalTitle())) {
                                    titleOrgView.setText(getString(R.string.movie_details_title_org_format, movie.getOriginalTitle()));
                                } else {
                                    titleOrgView.setVisibility(View.GONE);
                                    titleView.setPadding(titleView.getPaddingLeft(), titleView.getPaddingTop(), titleView.getPaddingRight(), titleView.getPaddingTop());
                                }
                                yearView.setText(movie.getReleaseDate().substring(0, 4));
                                if (details != null) {
                                    if (details.getRuntime() == null || details.getRuntime() == 0)
                                        durationView.setText(getString(R.string.movie_details_duration_unknown));
                                    else
                                        durationView.setText(getString(R.string.movie_details_duration_format, details.getRuntime()));
                                    statusView.setVisibility(View.VISIBLE);
                                    statusView.setText(details.getStatus());
                                } else {
                                    durationView.setText(getString(R.string.movie_details_duration_unknown));
                                    statusView.setVisibility(View.INVISIBLE);
                                }
                                Picasso.with(imageView.getContext())
                                        .load(IMAGE_BASE_URL_SIZED + movie.getPosterPath())
                                        .placeholder(R.drawable.placeholder_background)
                                        .into(imageView, new com.squareup.picasso.Callback() {
                                            @Override
                                            public void onSuccess() {
                                                imageProgress.setVisibility(View.GONE);
                                                if (savedInstanceState == null)
                                                    supportStartPostponedEnterTransition();
                                            }

                                            @Override
                                            public void onError() {
                                                imageProgress.setVisibility(View.GONE);
                                                if (savedInstanceState == null)
                                                    supportStartPostponedEnterTransition();
                                            }
                                        });

                            }
                        }
                    }
                });
                videosView.setLayoutManager(new GridLayoutManager(MovieDetailsActivity.this, getResources().getInteger(R.integer.videos_span_count)));
                final VideosAdapter videosAdapter = new VideosAdapter(this, new ArrayList<Video>());
                videosView.setAdapter(videosAdapter);
                model.getVideosForMovie(id).observe(this, new Observer<Resource<List<Video>>>() {
                    @Override
                    public void onChanged(@Nullable Resource<List<Video>> videosData) {
                        if (videosData != null) {
                            if (videosData.data != null) {
                                videosAdapter.setVideos(videosData.data);
                            }
                            if (videosData.status == Status.SUCCESS) {
                                setVideosVisible(videosData.data != null && videosData.data.size() > 0);
                            } else if (videosData.status == Status.ERROR) {
                                setVideosVisible(false);
                                Snackbar.make(coordinatorLayout, videosData.message != null ? videosData.message : "An error appear", Snackbar.LENGTH_SHORT)
                                        .show();
                            } else {
                                if (videosData.data == null) {
                                    setVideosVisible(false);
                                }
                            }
                        } else {
                            setVideosVisible(false);
                        }
                    }
                });
                reviewsView.setLayoutManager(new LinearLayoutManager(MovieDetailsActivity.this));
                final ReviewsAdapter reviewsAdapter = new ReviewsAdapter(this, new ArrayList<Review>());
                reviewsView.setAdapter(reviewsAdapter);
                reviewsView.addItemDecoration(new DividerItemDecoration(MovieDetailsActivity.this,
                        DividerItemDecoration.VERTICAL));
                model.getReviewsForMovie(id).observe(this, new Observer<Resource<List<Review>>>() {
                    @Override
                    public void onChanged(@Nullable Resource<List<Review>> reviewsData) {
                        if (reviewsData != null) {
                            if (reviewsData.data != null) {
                                reviewsAdapter.setReviews(reviewsData.data);
                            }
                            if (reviewsData.status == Status.SUCCESS) {
                                setReviewsVisible(reviewsData.data != null && reviewsData.data.size() > 0);
                            } else if (reviewsData.status == Status.ERROR) {
                                setReviewsVisible(false);
                                Snackbar.make(coordinatorLayout, reviewsData.message != null ? reviewsData.message : "An error appear", Snackbar.LENGTH_SHORT)
                                        .show();
                            } else {
                                if (reviewsData.data == null) {
                                    setReviewsVisible(false);
                                }
                            }
                        } else {
                            setReviewsVisible(false);
                        }
                    }
                });
                return;
            }
        }
        finish();
    }

    private void setVideosVisible(boolean visible) {
        final int visibility = visible ? View.VISIBLE : View.GONE;
        videosDividerView.setVisibility(visibility);
        videosTitleView.setVisibility(visibility);
        videosView.setVisibility(visibility);
    }

    private void setReviewsVisible(boolean visible) {
        final int visibility = visible ? View.VISIBLE : View.GONE;
        reviewsDividerView.setVisibility(visibility);
        reviewsTitleView.setVisibility(visibility);
        reviewsView.setVisibility(visibility);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
