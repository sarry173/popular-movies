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

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import pl.selvin.android.popularmovies.models.Movie;
import pl.selvin.android.popularmovies.models.MovieWithDetails;
import pl.selvin.android.popularmovies.models.Resource;
import pl.selvin.android.popularmovies.models.Review;
import pl.selvin.android.popularmovies.models.Video;
import pl.selvin.android.popularmovies.repository.MoviesRepository;

public class MovieDetailsViewModel extends AndroidViewModel {
    final private MoviesRepository repository;
    private LiveData<Resource<MovieWithDetails>> movie = null;
    private LiveData<Resource<List<Video>>> videos = null;
    private LiveData<Resource<List<Review>>> reviews = null;

    public MovieDetailsViewModel(@NonNull Application application) {
        super(application);
        repository = MoviesRepository.getInstance(application);
    }

    public LiveData<Resource<MovieWithDetails>> getMovieDetails(long id) {
        if (movie == null)
            movie = repository.loadMovieDetails(id);
        return movie;
    }

    public LiveData<Resource<List<Video>>> getVideosForMovie(long movieId) {
        if (videos == null)
            videos = repository.loadVideosForMovie(movieId);
        return videos;
    }

    public LiveData<Resource<List<Review>>> getReviewsForMovie(long movieId) {
        if (reviews == null)
            reviews = repository.loadReviewsForMovie(movieId);
        return reviews;
    }

    public LiveData<Integer> saveMovie(Movie movie) {
        return repository.saveMovie(movie);
    }
}
