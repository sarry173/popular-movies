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

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import pl.selvin.android.popularmovies.data.MoviesDatabase.MoviesDef;

@SuppressWarnings("unused,WeakerAccess")
public class Movie {

    @SerializedName("id")
    private long id;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("adult")
    private Boolean adult;

    @SerializedName("overview")
    private String overview;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("genre_ids")
    private List<Integer> genreIds;

    @SerializedName("original_title")
    private String originalTitle;

    @SerializedName("original_language")
    private String originalLanguage;

    @SerializedName("title")
    private String title;

    @SerializedName("backdrop_path")
    private String backdropPath;

    @SerializedName("popularity")
    private double popularity;

    @SerializedName("vote_count")
    private int voteCount;

    @SerializedName("video")
    private Boolean video;

    @SerializedName("vote_average")
    private double voteAverage;

    private boolean topRated;

    private boolean popular;

    private boolean favourite;

    public boolean isTopRated() {
        return topRated;
    }

    public void setTopRated(boolean topRated) {
        this.topRated = topRated;
    }

    public boolean isPopular() {
        return popular;
    }

    public void setPopular(boolean popular) {
        this.popular = popular;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

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
        return genreIds;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public void setGenreIds(List<Integer> genreIds) {
        this.genreIds = genreIds;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Movie movie = (Movie) o;
        return id == movie.id;
    }

    @Override
    public int hashCode() {
        return (int) id;
    }

    public static Movie fromCursor(Cursor cursor) {
        final Movie ret = new Movie();
        ret.setId(cursor.getLong(cursor.getColumnIndex(MoviesDef.ID)));
        ret.setTitle(cursor.getString(cursor.getColumnIndex(MoviesDef.TITLE)));
        ret.setOverview(cursor.getString(cursor.getColumnIndex(MoviesDef.OVERVIEW)));
        ret.setReleaseDate(cursor.getString(cursor.getColumnIndex(MoviesDef.RELEASE_DATE)));
        ret.setBackdropPath(cursor.getString(cursor.getColumnIndex(MoviesDef.BACKDROP_PATH)));
        ret.setTopRated(cursor.getInt(cursor.getColumnIndex(MoviesDef.TOP_RATED)) > 0);
        ret.setFavourite(cursor.getInt(cursor.getColumnIndex(MoviesDef.FAVOURITE)) > 0);
        ret.setPopular(cursor.getInt(cursor.getColumnIndex(MoviesDef.POPULAR)) > 0);
        ret.setVoteCount(cursor.getInt(cursor.getColumnIndex(MoviesDef.VOTE_COUNT)));
        ret.setVoteAverage(cursor.getDouble(cursor.getColumnIndex(MoviesDef.VOTE_AVERAGE)));
        ret.setPopularity(cursor.getDouble(cursor.getColumnIndex(MoviesDef.POPULARITY)));
        ret.setPosterPath(cursor.getString(cursor.getColumnIndex(MoviesDef.POSTER_PATH)));
        final int adultColumnIndex = cursor.getColumnIndex(MoviesDef.ADULT);
        ret.setAdult(cursor.isNull(adultColumnIndex) ? null : (cursor.getInt(adultColumnIndex) > 0));
        ret.setOriginalTitle(cursor.getString(cursor.getColumnIndex(MoviesDef.ORIGINAL_TITLE)));
        ret.setOriginalLanguage(cursor.getString(cursor.getColumnIndex(MoviesDef.ORIGINAL_LANGUAGE)));
        final int videoColumnIndex = cursor.getColumnIndex(MoviesDef.VIDEO);
        ret.setVideo(cursor.isNull(videoColumnIndex) ? null : (cursor.getInt(videoColumnIndex) > 0));
        return ret;
    }

    public ContentValues toContentValue() {
        final ContentValues ret = new ContentValues();
        ret.put(MoviesDef.ID, id);
        ret.put(MoviesDef.TITLE, title);
        ret.put(MoviesDef.OVERVIEW, overview);
        ret.put(MoviesDef.RELEASE_DATE, releaseDate);
        ret.put(MoviesDef.BACKDROP_PATH, backdropPath);
        ret.put(MoviesDef.TOP_RATED, topRated ? 1 : 0);
        ret.put(MoviesDef.FAVOURITE, favourite ? 1 : 0);
        ret.put(MoviesDef.POPULAR, popular ? 1 : 0);
        ret.put(MoviesDef.VOTE_COUNT, voteCount);
        ret.put(MoviesDef.VOTE_AVERAGE, voteAverage);
        ret.put(MoviesDef.POPULARITY, popularity);
        ret.put(MoviesDef.POSTER_PATH, posterPath);
        ret.put(MoviesDef.ADULT, adult == null ? null : adult ? 1 : 0);
        ret.put(MoviesDef.ORIGINAL_TITLE, originalTitle);
        ret.put(MoviesDef.ORIGINAL_LANGUAGE, originalLanguage);
        ret.put(MoviesDef.VIDEO, video == null ? null : video ? 1 : 0);
        return ret;
    }
}
