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

import android.net.Uri;

import com.google.gson.annotations.Expose;

import static pl.selvin.android.popularmovies.utils.Constants.YOUTUBE_BASE_URL;

@SuppressWarnings("unused")
public class Video {
    @Expose
    private String id;
    @Expose
    private String key;
    @Expose
    private String name;

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public Uri getVideoUri() {
        return Uri.parse(YOUTUBE_BASE_URL + key);
    }
}
