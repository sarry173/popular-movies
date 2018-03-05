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

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
@Entity(tableName = "movies")
public class Movie {
    @PrimaryKey
    @Expose
    private int id;
    @SerializedName("poster_path")
    @Expose
    private String posterPath;
    @Expose
    private Boolean adult;
    @Expose
    private String overview;
    @Expose
    @SerializedName("release_date")
    private String releaseDate;
    @Ignore
    @Expose
    private List<Integer> genre_ids;
    @Expose
    @SerializedName("original_title")
    private String originalTitle;
    @Expose
    @SerializedName("original_language")
    private String originalLanguage;
    @Expose
    private String title;
    @Expose
    @SerializedName("backdrop_path")
    private String backdropPath;
    @Expose
    private double popularity;
    @Expose
    @SerializedName("vote_count")
    private int voteCount;
    @Expose
    private Boolean video;
    @Expose
    @SerializedName("vote_average")
    private double voteAverage;
    @Expose
    private Integer runtime;

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public String getTitle() {
        return title;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public double getPopularity() {
        return popularity;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public Boolean hasVideo() {
        return video;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public Boolean isAdult() {
        return adult;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
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

    public void setId(int id) {
        this.id = id;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public void setAdult(Boolean adult) {
        this.adult = adult;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setGenre_ids(List<Integer> genre_ids) {
        this.genre_ids = genre_ids;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public void setVideo(Boolean video) {
        this.video = video;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
    }
}
