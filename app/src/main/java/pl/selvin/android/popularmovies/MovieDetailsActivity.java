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
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.selvin.android.popularmovies.adapters.MoviesAdapter;
import pl.selvin.android.popularmovies.adapters.VideosAdapter;
import pl.selvin.android.popularmovies.data.MoviesService;
import pl.selvin.android.popularmovies.models.Movie;
import pl.selvin.android.popularmovies.models.Video;
import pl.selvin.android.popularmovies.models.VideosResponse;
import pl.selvin.android.popularmovies.utils.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static pl.selvin.android.popularmovies.utils.Constants.IMAGE_BASE_URL_W185;

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String MOVIE_ID = "MOVIE_ID";
    public static final String POSITION = "POSITION";

    @SuppressWarnings("unchecked")
    public static void startDetailsActivity(@NonNull Activity context, MoviesAdapter.Holder holder) {
        final long movieId = holder.getItemId();
        final int position = holder.getAdapterPosition();
        final ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(context,
                Pair.<View, String>create(holder.image, "image" + movieId),
                Pair.<View, String>create(holder.title, "title" + movieId));
        ActivityCompat.startActivity(context, new Intent(context, MovieDetailsActivity.class)
                .putExtra(MOVIE_ID, movieId).putExtra(POSITION, position), options.toBundle());
    }

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
    @BindView(R.id.movie_details_progress)
    FrameLayout progress;
    @BindView(R.id.movie_details_fav)
    FloatingActionButton favourite;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details_activity);
        supportPostponeEnterTransition();
        ButterKnife.bind(this);
        final Intent intent = getIntent();
        videosView.setNestedScrollingEnabled(false);
        if (intent != null) {
            final long id = intent.getLongExtra(MOVIE_ID, -1);
            ViewCompat.setTransitionName(imageView, "image" + id);
            ViewCompat.setTransitionName(titleView, "title" + id);
            if (id != -1) {
                setSupportActionBar(this.<Toolbar>findViewById(R.id.toolbar));
                final ActionBar actionBar = getSupportActionBar();
                if (actionBar != null)
                    actionBar.setDisplayHomeAsUpEnabled(true);
                setTitle(R.string.movie_detail_title);
                progress.setVisibility(View.VISIBLE);
                MoviesService.Service.getInstance().getMovie(id, Constants.LANG).enqueue(new Callback<Movie>() {
                    @Override
                    public void onResponse(@NonNull Call<Movie> call, @NonNull Response<Movie> response) {
                        if (response.isSuccessful()) {
                            final Movie movie = response.body();
                            if (movie != null) {
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
                                Picasso.with(imageView.getContext())
                                        .load(IMAGE_BASE_URL_W185 + movie.getPosterPath())
                                        .placeholder(R.drawable.indeterminate_progressbar)
                                        .noFade()
                                        .into(imageView, new com.squareup.picasso.Callback() {
                                            @Override
                                            public void onSuccess() {
                                                progress.setVisibility(View.GONE);
                                                supportStartPostponedEnterTransition();
                                            }

                                            @Override
                                            public void onError() {
                                                progress.setVisibility(View.GONE);
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
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<VideosResponse> call, @NonNull Throwable throwable) {
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Movie> call, @NonNull Throwable throwable) {

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
