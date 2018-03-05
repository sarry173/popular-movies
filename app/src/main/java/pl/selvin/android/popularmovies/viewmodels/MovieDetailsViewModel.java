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
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import pl.selvin.android.popularmovies.R;
import pl.selvin.android.popularmovies.data.MoviesDatabase;
import pl.selvin.android.popularmovies.data.MoviesService;
import pl.selvin.android.popularmovies.models.Movie;
import pl.selvin.android.popularmovies.utils.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailsViewModel extends AndroidViewModel {
    public MovieDetailsViewModel(@NonNull Application application) {
        super(application);
    }

    public static class MovieData {
        public final boolean successful;
        public final Movie movie;
        public final String errorString;
        public final int errorRes;

        MovieData(boolean successful, Movie movie, String errorString, int errorRes) {
            this.successful = successful;
            this.movie = movie;
            this.errorString = errorString;
            this.errorRes = errorRes;
        }
    }

    private boolean isFavourite;
    private long id;
    private MutableLiveData<MovieData> movie;

    private MutableLiveData<MovieData> getMovieInternal(long id) {
        this.id = id;
        if (movie == null) {
            movie = new MutableLiveData<>();
            loadMovie();
        }
        return movie;
    }

    public LiveData<MovieData> getMovie(long id, boolean isFavourite) {
        this.isFavourite = isFavourite;
        return getMovieInternal(id);
    }

    @SuppressLint("StaticFieldLeak")
    private void loadMovie() {

        if (isFavourite) {
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... voids) {
                    final LiveData<Movie> favouriteMovie = MoviesDatabase.getInstance(getApplication()).movieDao().getMovie(id);
                    favouriteMovie.observeForever(new Observer<Movie>() {
                        @Override
                        public void onChanged(@Nullable Movie movie) {
                            favouriteMovie.removeObserver(this);
                            if (favouriteMovie.getValue() != null)
                                loadFinished(favouriteMovie.getValue(), null, -1);
                            else {
                                isFavourite = false;
                                loadMovie();
                            }
                        }
                    });
                    return null;
                }
            }.execute();

        } else
            MoviesService.Service.getInstance().getMovie(id, Constants.LANG).enqueue(new Callback<Movie>() {
                @Override
                public void onResponse(@NonNull Call<Movie> call, @NonNull Response<Movie> response) {
                    if (response.isSuccessful()) {
                        final Movie body = response.body();
                        if (body != null) {
                            loadFinished(body, null, -1);
                        } else
                            loadFinished(null, null, R.string.error_body_is_null);
                    } else
                        loadFinished(null, null, R.string.error_response_not_successful);
                }

                @Override
                public void onFailure(@NonNull Call<Movie> call, @NonNull Throwable throwable) {
                    loadFinished(null, throwable.getMessage(), -1);
                    throwable.printStackTrace();
                }
            });
    }

    private void loadFinished(Movie movie, String message, int messageRes) {
        getMovieInternal(movie != null ? movie.getId() : -1).setValue(new MovieData(message == null && messageRes == -1, movie, message, messageRes));
    }
}
