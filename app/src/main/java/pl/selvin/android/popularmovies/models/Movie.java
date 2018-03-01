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
package pl.selvin.android.popularmovies.models;

import com.google.gson.annotations.Expose;

import java.util.List;

@SuppressWarnings("unused")
public class Movie {
    @Expose
    private String poster_path;
    @Expose
    private Boolean adult;
    @Expose
    private String overview;
    @Expose
    private String release_date;
    @Expose
    private List<Integer> genre_ids;
    @Expose
    private int id;
    @Expose
    private String original_title;
    @Expose
    private String original_language;
    @Expose
    private String title;
    @Expose
    private String backdrop_path;
    @Expose
    private double popularity;
    @Expose
    private int vote_count;
    @Expose
    private Boolean video;
    @Expose
    private double vote_average;
    @Expose
    private Integer runtime;

    public String getOriginalTitle() {
        return original_title;
    }

    public String getOriginalLanguage() {
        return original_language;
    }

    public String getTitle() {
        return title;
    }

    public String getBackdropPath() {
        return backdrop_path;
    }

    public double getPopularity() {
        return popularity;
    }

    public int getVoteCount() {
        return vote_count;
    }

    public Boolean hasVideo() {
        return video;
    }

    public double getVoteAverage() {
        return vote_average;
    }

    public String getPosterPath() {
        return poster_path;
    }

    public Boolean isAdult() {
        return adult;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return release_date;
    }

    public List<Integer> getGenreIds() {
        return genre_ids;
    }

    public int getId() {
        return id;
    }

    public Integer getRuntime() {
        return runtime;
    }
}
