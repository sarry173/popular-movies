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
public class MoviesResponse {
    @Expose
    private int page;
    @Expose
    private List<Movie> results;
    @Expose
    private int total_results;
    @Expose
    private int total_pages;

    public int getPage() {
        return page;
    }

    public List<Movie> getResults() {
        return results;
    }

    public int getTotalResults() {
        return total_results;
    }

    public int getTotalPages() {
        return total_pages;
    }
}
