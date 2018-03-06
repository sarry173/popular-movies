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
package pl.selvin.android.popularmovies.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.concurrent.TimeUnit;

import pl.selvin.android.popularmovies.api.ApiResponse;
import pl.selvin.android.popularmovies.api.MoviesService;
import pl.selvin.android.popularmovies.api.MoviesServiceResponse;
import pl.selvin.android.popularmovies.data.MoviesDatabase;
import pl.selvin.android.popularmovies.models.Movie;
import pl.selvin.android.popularmovies.models.MovieDetails;
import pl.selvin.android.popularmovies.models.MovieWithDetails;
import pl.selvin.android.popularmovies.models.Resource;
import pl.selvin.android.popularmovies.utils.AppExecutors;
import pl.selvin.android.popularmovies.utils.LiveDataCallAdapterFactory;
import pl.selvin.android.popularmovies.utils.RateLimiter;
import pl.selvin.android.popularmovies.viewmodels.MoviesListViewModel.MoviesToShow;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static pl.selvin.android.popularmovies.utils.Constants.LANG;
import static pl.selvin.android.popularmovies.utils.Constants.SERVICE_BASE_URL;

public class MoviesRepository {
    private static final String SETTINGS_KEY = "MOVIES_LIST_SETTINGS";
    private static final String MOVIES_TO_SHOW = "MOVIES_TO_SHOW_STRING";
    private static final String SHOW_BOTTOM_NAVIGATION = "SHOW_BOTTOM_NAVIGATION";
    private static final MoviesToShow DEFAULT_MOVIE_TO_SHOW = MoviesToShow.POPULAR;
    private final SharedPreferences settings;
    private final AppExecutors appExecutors;
    private final MoviesDatabase.MovieDao movieDao;
    private final RateLimiter<String> listRateLimit = new RateLimiter<>(10, TimeUnit.MINUTES);
    private final MoviesService moviesService;

    private MoviesRepository(Context context) {
        final MoviesDatabase database = Room.databaseBuilder(context.getApplicationContext(), MoviesDatabase.class, "movies")
                .fallbackToDestructiveMigration().build();
        settings = context.getSharedPreferences(SETTINGS_KEY, 0);
        appExecutors = AppExecutors.INSTANCE;
        movieDao = database.movieDao();
        moviesService = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .baseUrl(SERVICE_BASE_URL).build().create(MoviesService.class);
    }

    public LiveData<Resource<List<Movie>>> loadPopularMovies() {
        return new NetworkBoundResource<List<Movie>, MoviesServiceResponse<Movie>>(appExecutors) {
            private static final String POPULAR_KEY = "POPULAR_KEY";

            @Override
            protected void saveCallResult(@NonNull MoviesServiceResponse<Movie> item) {
                if (item.getResults() != null)
                    movieDao.insertPopular(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Movie> data) {
                return data == null || data.isEmpty() || listRateLimit.shouldFetch(POPULAR_KEY);
            }

            @NonNull
            @Override
            protected LiveData<List<Movie>> loadFromDb() {
                return movieDao.loadPopularMovies();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<MoviesServiceResponse<Movie>>> createCall() {
                return moviesService.getPopularMovies(LANG, null, null);
            }

            @Override
            protected void onFetchFailed() {
                listRateLimit.reset(POPULAR_KEY);
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<Movie>>> loadTopRatedMovies() {
        return new NetworkBoundResource<List<Movie>, MoviesServiceResponse<Movie>>(appExecutors) {
            private static final String TOP_RATED_KEY = "TOP_RATED_KEY";

            @Override
            protected void saveCallResult(@NonNull MoviesServiceResponse<Movie> item) {
                movieDao.insertTopRated(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Movie> data) {
                return data == null || data.isEmpty() || listRateLimit.shouldFetch(TOP_RATED_KEY);
            }

            @NonNull
            @Override
            protected LiveData<List<Movie>> loadFromDb() {
                return movieDao.loadTopRatedMovies();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<MoviesServiceResponse<Movie>>> createCall() {
                return moviesService.getTopRatedMovies(LANG, null, null);
            }

            @Override
            protected void onFetchFailed() {
                listRateLimit.reset(TOP_RATED_KEY);
            }
        }.asLiveData();
    }

    private static MoviesRepository INSTANCE = null;

    public static synchronized MoviesRepository getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new MoviesRepository(context);
        }
        return INSTANCE;
    }

    public LiveData<Resource<List<Movie>>> loadFavourite() {
        return new NetworkBoundResource<List<Movie>, MoviesServiceResponse<Movie>>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull MoviesServiceResponse<Movie> item) {
            }

            @Override
            protected boolean shouldFetch(@Nullable List<Movie> data) {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<List<Movie>> loadFromDb() {
                return movieDao.loadFavourite();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<MoviesServiceResponse<Movie>>> createCall() {
                return new MutableLiveData<>();
            }

            @Override
            protected void onFetchFailed() {

            }
        }.asLiveData();
    }

    public LiveData<Resource<MovieWithDetails>> loadMovieDetails(final long id) {
        return new NetworkBoundResource<MovieWithDetails, MovieDetails>(appExecutors) {
            private final String MOVIE_DETAILS_KEY = "MOVIE_DETAILS_KEY_" + id;

            @Override
            protected void saveCallResult(@NonNull MovieDetails item) {
                movieDao.saveMovieDetails(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable MovieWithDetails data) {
                return data == null || listRateLimit.shouldFetch(MOVIE_DETAILS_KEY);
            }

            @NonNull
            @Override
            protected LiveData<MovieWithDetails> loadFromDb() {
                return movieDao.loadMovieDetails(id);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<MovieDetails>> createCall() {
                return moviesService.getMovieDetails(id, LANG);
            }

            protected void onFetchFailed() {
                listRateLimit.reset(MOVIE_DETAILS_KEY);
            }
        }.asLiveData();
    }

    public MoviesToShow loadMoviesToShow() {
        return MoviesToShow.valueOf(settings.getString(MOVIES_TO_SHOW, DEFAULT_MOVIE_TO_SHOW.toString()));
    }

    public void saveMoviesToShow(MoviesToShow moviesToShowIn) {
        settings.edit().putString(MOVIES_TO_SHOW, moviesToShowIn.toString()).apply();
    }

    public void saveMovie(final Movie movie) {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                movieDao.update(movie);
            }
        });
    }

    public boolean getShowBottomNavigation() {
        return settings.getBoolean(SHOW_BOTTOM_NAVIGATION, true);
    }

    public void setShowBottomNavigation(boolean value) {
        settings.edit().putBoolean(SHOW_BOTTOM_NAVIGATION, value).apply();
    }
}
