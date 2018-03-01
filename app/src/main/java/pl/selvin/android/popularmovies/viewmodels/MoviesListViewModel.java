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

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import java.util.List;

import pl.selvin.android.popularmovies.R;
import pl.selvin.android.popularmovies.data.MoviesService;
import pl.selvin.android.popularmovies.models.Movie;
import pl.selvin.android.popularmovies.models.MoviesResponse;
import pl.selvin.android.popularmovies.utils.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoviesListViewModel extends ViewModel {

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

    private MutableLiveData<MoviesData> movies;

    public LiveData<MoviesData> getMovies() {
        if (movies == null) {
            movies = new MutableLiveData<>();
            loadMovies();
        }
        return movies;
    }

    private void loadMovies() {
        MoviesService.Service.getInstance().getPopularMovies(Constants.LANG, null, null).enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(@NonNull Call<MoviesResponse> call, @NonNull Response<MoviesResponse> response) {
                if (response.isSuccessful()) {
                    final MoviesResponse body = response.body();
                    if (body != null) {
                        loadFinished(body.getResults(), null, -1);
                    } else
                        loadFinished(null, null, R.string.error_body_is_null);
                } else
                    loadFinished(null, null, R.string.error_response_not_successful);
            }

            @Override
            public void onFailure(@NonNull Call<MoviesResponse> call, @NonNull Throwable throwable) {
                loadFinished(null, throwable.getMessage(), -1);
                throwable.printStackTrace();
            }
        });
    }

    private void loadFinished(List<Movie> movies, String message, int messageRes) {
        ((MutableLiveData<MoviesData>) getMovies()).setValue(new MoviesData(message == null && messageRes == -1, movies, message, messageRes));
    }
}
