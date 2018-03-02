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
package pl.selvin.android.popularmovies.data;

import pl.selvin.android.popularmovies.models.Movie;
import pl.selvin.android.popularmovies.models.MoviesResponse;
import pl.selvin.android.popularmovies.models.VideosResponse;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import static pl.selvin.android.popularmovies.utils.Constants.API_KEY;
import static pl.selvin.android.popularmovies.utils.Constants.SERVICE_BASE_URL;

public interface MoviesService {
    @GET("movie/popular?api_key=" + API_KEY)
    Call<MoviesResponse> getPopularMovies(@Query("language") String language, @SuppressWarnings("SameParameterValue") @Query("page") Integer page,
                                          @SuppressWarnings("SameParameterValue") @Query("region") String region);

    @GET("movie/top_rated?api_key=" + API_KEY)
    Call<MoviesResponse> getTopRatedMovies(@Query("language") String language, @SuppressWarnings("SameParameterValue") @Query("page") Integer page,
                                          @SuppressWarnings("SameParameterValue") @Query("region") String region);

    @GET("movie/{movie_id}?api_key=" + API_KEY)
    Call<Movie> getMovie(@Path("movie_id") long id, @Query("language") String language);

    @GET("movie/{movie_id}/videos?api_key=" + API_KEY)
    Call<VideosResponse> getVideosForMovie(@Path("movie_id") long id, @Query("language") String language);

    enum Service {
        INSTANCE;

        Service() {
            moviesService = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(SERVICE_BASE_URL).build().create(MoviesService.class);
        }

        private MoviesService moviesService;

        private MoviesService getServiceInstance() {
            return moviesService;
        }

        public static MoviesService getInstance() {
            return INSTANCE.getServiceInstance();
        }
    }
}
