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
package pl.selvin.android.popularmovies.api;

import android.arch.lifecycle.LiveData;

import pl.selvin.android.popularmovies.models.Movie;
import pl.selvin.android.popularmovies.models.MovieDetails;
import pl.selvin.android.popularmovies.models.Review;
import pl.selvin.android.popularmovies.models.Video;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import static pl.selvin.android.popularmovies.utils.Constants.API_KEY;

public interface MoviesService {
    @GET("movie/popular?api_key=" + API_KEY)
    LiveData<ApiResponse<MoviesServiceResponse<Movie>>> getPopularMovies(@SuppressWarnings("SameParameterValue") @Query("language") String language, @SuppressWarnings("SameParameterValue") @Query("page") Integer page,
                                                                         @SuppressWarnings("SameParameterValue") @Query("region") String region);

    @GET("movie/top_rated?api_key=" + API_KEY)
    LiveData<ApiResponse<MoviesServiceResponse<Movie>>> getTopRatedMovies(@SuppressWarnings("SameParameterValue") @Query("language") String language, @SuppressWarnings("SameParameterValue") @Query("page") Integer page,
                                                                          @SuppressWarnings("SameParameterValue") @Query("region") String region);

    @GET("movie/{movie_id}?api_key=" + API_KEY)
    LiveData<ApiResponse<MovieDetails>> getMovieDetails(@Path("movie_id") long id, @SuppressWarnings("SameParameterValue") @Query("language") String language);

    @GET("movie/{movie_id}/videos?api_key=" + API_KEY)
    LiveData<ApiResponse<MoviesServiceResponse<Video>>> getVideosForMovie(@Path("movie_id") long id, @SuppressWarnings("SameParameterValue") @Query("language") String language);

    @GET("movie/{movie_id}/reviews?api_key=" + API_KEY)
    LiveData<ApiResponse<MoviesServiceResponse<Review>>> getReviewsForMovie(@Path("movie_id") long id, @SuppressWarnings("SameParameterValue") @Query("language") String language);
}
