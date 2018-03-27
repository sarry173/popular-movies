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
package pl.selvin.android.popularmovies.utils;

import java.util.Locale;

import pl.selvin.android.popularmovies.BuildConfig;

public interface Constants {
    //String LANG = Locale.getDefault().getLanguage();
    String LANG = Locale.ENGLISH.getLanguage();
    String API_KEY = BuildConfig.THEMOVIEDB_ORG_KEY;
    String SERVICE_BASE_URL = "http://api.themoviedb.org/3/";
    String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    String IMAGE_BASE_URL_SIZED = IMAGE_BASE_URL + "w342/";
    String YOUTUBE_BASE_URL  = "https://youtu.be/";
}
