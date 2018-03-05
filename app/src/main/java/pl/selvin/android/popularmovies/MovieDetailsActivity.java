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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ImageViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.selvin.android.popularmovies.adapters.MoviesAdapter;
import pl.selvin.android.popularmovies.adapters.VideosAdapter;
import pl.selvin.android.popularmovies.data.MoviesDatabase;
import pl.selvin.android.popularmovies.data.MoviesService;
import pl.selvin.android.popularmovies.models.Movie;
import pl.selvin.android.popularmovies.models.Video;
import pl.selvin.android.popularmovies.models.VideosResponse;
import pl.selvin.android.popularmovies.utils.Constants;
import pl.selvin.android.popularmovies.viewmodels.MovieDetailsViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static pl.selvin.android.popularmovies.utils.Constants.IMAGE_BASE_URL_SIZED;

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String MOVIE_ID = "MOVIE_ID";
    public static final String POSITION = "POSITION";
    public static final String FROM_FAVOURITE = "FROM_FAVOURITE";
    private final View.OnClickListener dismissOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };

    @SuppressWarnings("unchecked")
    public static void startDetailsActivityForResult(@NonNull Activity context, boolean fromFavourite, MoviesAdapter.Holder holder, View progress, int code) {
        final long movieId = holder.getItemId();
        final int position = holder.getAdapterPosition();
        ViewCompat.setTransitionName(progress, "progress" + movieId);
        final ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(context,
                Pair.<View, String>create(holder.image, "image" + movieId),
                Pair.<View, String>create(holder.title, "title" + movieId),
                Pair.create(progress, "progress" + movieId));
        ActivityCompat.startActivityForResult(context, new Intent(context, MovieDetailsActivity.class)
                .putExtra(MOVIE_ID, movieId).putExtra(POSITION, position).putExtra(FROM_FAVOURITE, fromFavourite), code, options.toBundle());
    }

    private MovieDetailsViewModel model;
    private Movie movie = null;

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
    @BindView(R.id.movie_details_duration)
    TextView durationView;
    @BindView(R.id.movie_details_description)
    TextView descriptionView;
    @BindView(R.id.movie_details_divider)
    View dividerView;
    @BindView(R.id.movie_details_videos)
    RecyclerView videosView;
    @BindView(R.id.movie_details_videos_title)
    TextView videosTitleView;
    @BindView(R.id.movie_details_image_progress)
    View imageProgress;
    @BindView(R.id.movie_details_fav)
    FloatingActionButton favourite;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.scroll)
    NestedScrollView scroll;
    @BindView(R.id.movie_details_activity_progress)
    View progress;

    @SuppressLint("StaticFieldLeak")
    @OnClick(R.id.movie_details_fav)
    void favOnClick(View view) {
        if (movie != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    final MoviesDatabase.MovieDao movieDao = MoviesDatabase.getInstance(getApplication()).movieDao();
                    try {
                        movieDao.insert(movie);
                    } catch (SQLiteConstraintException exception) {
                        movieDao.delete(movie);
                    }
                    return null;
                }
            }.execute();

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
            ViewCompat.setTransitionName(progress, "progress" + id);
            if (id != -1) {
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... voids) {
                        MoviesDatabase.getInstance(getApplication()).movieDao().count(id).observe(MovieDetailsActivity.this, new Observer<Integer>() {
                            @Override
                            public void onChanged(@Nullable Integer count) {
                                if (count != null && count > 0) {
                                    ImageViewCompat.setImageTintList(favourite, ContextCompat.getColorStateList(MovieDetailsActivity.this, R.color.colorPrimary));
                                } else {
                                    ImageViewCompat.setImageTintList(favourite, ContextCompat.getColorStateList(MovieDetailsActivity.this, android.R.color.white));
                                }
                            }
                        });
                        return null;
                    }
                }.execute();

                setTitle(R.string.movie_detail_title);
                progress.setVisibility(View.VISIBLE);
                imageProgress.setVisibility(View.VISIBLE);
                model = ViewModelProviders.of(this).get(MovieDetailsViewModel.class);
                model.getMovie(id, intent.getBooleanExtra(FROM_FAVOURITE, false)).observe(this, new Observer<MovieDetailsViewModel.MovieData>() {
                    @Override
                    public void onChanged(@Nullable MovieDetailsViewModel.MovieData movieData) {
                        if (movieData != null) {
                            if (movieData.successful) {
                                movie = movieData.movie;
                                titleView.setText(movie.getTitle());
                                if (!movie.getTitle().equals(movie.getOriginalTitle())) {
                                    titleOrgView.setText(getString(R.string.movie_details_title_org_format, movie.getOriginalTitle()));
                                } else {
                                    titleOrgView.setVisibility(View.GONE);
                                    titleView.setPadding(titleView.getPaddingLeft(), titleView.getPaddingTop(), titleView.getPaddingRight(), titleView.getPaddingTop());
                                }
                                yearView.setText(movie.getReleaseDate().substring(0, 4));
                                durationView.setText(getString(R.string.movie_details_duration_format, movie.getRuntime()));
                                descriptionView.setText(movie.getOverview());
                                ratingView.setText(getString(R.string.movie_details_rating_format, movie.getVoteAverage()));
                                progress.setVisibility(View.GONE);
                                Picasso.with(imageView.getContext())
                                        .load(IMAGE_BASE_URL_SIZED + movie.getPosterPath())
                                        .placeholder(R.drawable.indeterminate_progress_bar)
                                        .noFade()
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
                                videosView.setLayoutManager(new GridLayoutManager(MovieDetailsActivity.this, getResources().getInteger(R.integer.videos_span_count)));
                                MoviesService.Service.getInstance().getVideosForMovie(id, Constants.LANG).enqueue(new Callback<VideosResponse>() {
                                    @Override
                                    public void onResponse(@NonNull Call<VideosResponse> call, @NonNull Response<VideosResponse> response) {
                                        if (response.isSuccessful()) {
                                            final VideosResponse videosResponse = response.body();
                                            if (videosResponse != null) {
                                                final List<Video> videos = videosResponse.getResults();
                                                if (videos.size() > 0) {
                                                    dividerView.setVisibility(View.VISIBLE);
                                                    videosView.setVisibility(View.VISIBLE);
                                                    videosTitleView.setVisibility(View.VISIBLE);
                                                    videosView.setAdapter(new VideosAdapter(MovieDetailsActivity.this, videos));
                                                }
                                            }
                                        }
                                        scroll.scrollTo(0, 0);
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<VideosResponse> call, @NonNull Throwable throwable) {
                                    }
                                });
                            } else {
                                final Snackbar snackBar;
                                if (movieData.errorRes == -1)
                                    snackBar = Snackbar.make(findViewById(android.R.id.content), movieData.errorString, Snackbar.LENGTH_INDEFINITE);
                                else
                                    snackBar = Snackbar.make(findViewById(android.R.id.content), movieData.errorRes, Snackbar.LENGTH_INDEFINITE);
                                snackBar.setAction(R.string.snackbar_dismiss, dismissOnClick).show();
                            }
                        }
                    }
                });
                return;
            }
        }
        finish();
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
