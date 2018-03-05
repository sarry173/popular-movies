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
package pl.selvin.android.popularmovies.viewmodels;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import pl.selvin.android.popularmovies.R;
import pl.selvin.android.popularmovies.data.MoviesDatabase;
import pl.selvin.android.popularmovies.data.MoviesService;
import pl.selvin.android.popularmovies.models.Movie;
import pl.selvin.android.popularmovies.models.MoviesResponse;
import pl.selvin.android.popularmovies.utils.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoviesListViewModel extends AndroidViewModel {

    private final static String SETTINGS_KEY = "MOVIES_LIST_SETTINGS";
    private final static String MOVIES_TO_SHOW = "MOVIES_TO_SHOW";
    public final static int MOVIES_TO_SHOW_POPULAR = 0;
    public final static int MOVIES_TO_SHOW_TOP_RATED = 1;
    public final static int MOVIES_TO_SHOW_FAVOURITE = 2;


    private final SharedPreferences settings;
    private final AtomicLong currentRequestId = new AtomicLong(0);
    private int moviesToShow;

    public MoviesListViewModel(@NonNull Application application) {
        super(application);
        settings = application.getSharedPreferences(SETTINGS_KEY, 0);
        moviesToShow = settings.getInt(MOVIES_TO_SHOW, MOVIES_TO_SHOW_POPULAR);
    }

    @Override
    protected void onCleared() {
        if (favouriteMovies != null)
            favouriteMovies.removeObserver(favouriteMoviesObserver);
        super.onCleared();
    }

    public static class MoviesData {
        public final boolean successful;
        public final List<Movie> movies;
        public final String errorString;
        public final int errorRes;

        MoviesData(boolean successful, List<Movie> movies, String errorString, int errorRes) {
            this.successful = successful;
            this.movies = movies;
            this.errorString = errorString;
            this.errorRes = errorRes;
        }
    }

    public int getMoviesToShow() {
        return moviesToShow;
    }

    public void setMoviesToShow(int moviesToShowIn) {
        if (moviesToShow != moviesToShowIn) {
            getMoviesInternal().setValue(null);
            moviesToShow = moviesToShowIn;
            settings.edit().putInt(MOVIES_TO_SHOW, moviesToShow).apply();
            loadMovies();
        }
    }

    private MutableLiveData<MoviesData> movies;

    private MutableLiveData<MoviesData> getMoviesInternal() {
        if (movies == null) {
            movies = new MutableLiveData<>();
            loadMovies();
        }
        return movies;
    }

    public LiveData<MoviesData> getMovies() {
        return getMoviesInternal();
    }

    private LiveData<List<Movie>> favouriteMovies;
    private Observer<List<Movie>> favouriteMoviesObserver = new Observer<List<Movie>>() {
        @Override
        public void onChanged(@Nullable List<Movie> movies) {
            loadFinished(favouriteMovies.getValue(), null, -1, favouriteRequestId);
        }
    };

    private long favouriteRequestId = -1;

    private void loadMovies() {
        final long requestId = currentRequestId.addAndGet(1);
        if (moviesToShow == MOVIES_TO_SHOW_FAVOURITE) {
            favouriteRequestId = requestId;
            if (favouriteMovies == null) {
                favouriteMovies = MoviesDatabase.getInstance(getApplication()).movieDao().getAll();
                favouriteMovies.observeForever(favouriteMoviesObserver);
            } else {
                loadFinished(favouriteMovies.getValue(), null, -1, requestId);
            }
        } else {
            final Call<MoviesResponse> moviesResponseCall;
            if (moviesToShow == MOVIES_TO_SHOW_POPULAR)
                moviesResponseCall = MoviesService.Service.getInstance().getPopularMovies(Constants.LANG, null, null);
            else
                moviesResponseCall = MoviesService.Service.getInstance().getTopRatedMovies(Constants.LANG, null, null);
            moviesResponseCall.enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(@NonNull Call<MoviesResponse> call, @NonNull Response<MoviesResponse> response) {
                    if (response.isSuccessful()) {
                        final MoviesResponse body = response.body();
                        if (body != null) {
                            loadFinished(body.getResults(), null, -1, requestId);
                        } else
                            loadFinished(null, null, R.string.error_body_is_null, requestId);
                    } else
                        loadFinished(null, null, R.string.error_response_not_successful, requestId);
                }

                @Override
                public void onFailure(@NonNull Call<MoviesResponse> call, @NonNull Throwable throwable) {
                    loadFinished(null, throwable.getMessage(), -1, requestId);
                    throwable.printStackTrace();
                }
            });
        }
    }

    private void loadFinished(List<Movie> movies, String message, int messageRes, long requestId) {
        synchronized (currentRequestId) {
            if (currentRequestId.get() == requestId) {
                getMoviesInternal().setValue(new MoviesData(message == null && messageRes == -1, movies, message, messageRes));
            }
        }
    }
}
