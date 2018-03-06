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
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import java.util.List;

import pl.selvin.android.popularmovies.repository.MoviesRepository;
import pl.selvin.android.popularmovies.models.Movie;
import pl.selvin.android.popularmovies.models.Resource;

public class MoviesListViewModel extends AndroidViewModel {
    private final MoviesRepository repository;
    private final MutableLiveData<MoviesToShow> moviesToShow = new MutableLiveData<>();

    public final LiveData<Resource<List<Movie>>> movies =
            Transformations.switchMap(moviesToShow, new Function<MoviesToShow, LiveData<Resource<List<Movie>>>>() {
                @Override
                public LiveData<Resource<List<Movie>>> apply(MoviesToShow input) {
                    switch (input) {
                        case FAVOURITE:
                            return repository.loadFavourite();
                        case TOP_RATED:
                            return repository.loadTopRatedMovies();
                        case POPULAR:
                            return repository.loadPopularMovies();
                    }
                    return null;
                }
            });

    public MoviesListViewModel(@NonNull Application application) {
        super(application);
        repository = MoviesRepository.getInstance(application);
        moviesToShow.setValue(repository.loadMoviesToShow());
    }

    public void setMoviesToShow(MoviesToShow moviesToShowIn) {
        if (moviesToShow.getValue() != moviesToShowIn) {
            moviesToShow.setValue(moviesToShowIn);
            repository.saveMoviesToShow(moviesToShowIn);
        }
    }

    public MoviesToShow getMoviesToShow() {
        return moviesToShow.getValue();
    }

    public boolean getShowBottomNavigation() {
        return repository.getShowBottomNavigation();
    }

    public boolean setShowBottomNavigation(boolean value) {
        return repository.setShowBottomNavigation(value);
    }

    public enum MoviesToShow {
        POPULAR,
        TOP_RATED,
        FAVOURITE
    }
}
