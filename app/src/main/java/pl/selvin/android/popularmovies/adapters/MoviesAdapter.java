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
package pl.selvin.android.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.selvin.android.popularmovies.R;
import pl.selvin.android.popularmovies.models.Movie;

import static pl.selvin.android.popularmovies.utils.Constants.IMAGE_BASE_URL_SIZED;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.Holder> {
    private final List<Movie> mMovies;
    private final LayoutInflater mInflater;
    private final ViewHolderListener mListener;

    public MoviesAdapter(@NonNull Context context, @NonNull List<Movie> movies, @NonNull ViewHolderListener listener) {
        mInflater = LayoutInflater.from(context);
        mListener = listener;
        mMovies = movies;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return mMovies == null ? -1 : mMovies.get(position).getId();
    }

    public void setMovies(List<Movie> movies) {
        mMovies.clear();
        mMovies.addAll(movies);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(mInflater.inflate(R.layout.movies_list_item, parent, false), mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(mMovies.get(position));
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull Holder holder) {
        holder.detached();
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public int getItemCount() {
        return mMovies == null ? 0 : mMovies.size();
    }

    public interface ViewHolderListener {
        void onItemClick(Holder holder);
    }

    public static class Holder extends RecyclerView.ViewHolder {
        private final ViewHolderListener mListener;
        @BindView(R.id.movies_list_item_image)
        public ImageView image;
        @BindView(R.id.movies_list_item_title)
        public TextView title;
        @BindView(R.id.movies_list_item_progress)
        View progress;

        Holder(View itemView, @NonNull ViewHolderListener listener) {
            super(itemView);
            mListener = listener;
            ButterKnife.bind(this, itemView);
        }

        void bind(final Movie movie) {
            title.setText(movie.getTitle());
            progress.setVisibility(View.VISIBLE);
            ViewCompat.setTransitionName(title, "title" + getItemId());
            ViewCompat.setTransitionName(image, "image" + getItemId());
            Picasso.get()
                    .load(IMAGE_BASE_URL_SIZED + movie.getPosterPath())
                    .placeholder(R.drawable.placeholder_background)
                    .error(R.drawable.ic_error)
                    .into(image, new Callback() {
                        @Override
                        public void onSuccess() {
                            progress.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            progress.setVisibility(View.GONE);
                        }
                    });
        }

        @OnClick(R.id.movies_list_item_decorator)
        void onClick(View unused) {
            mListener.onItemClick(this);
        }

        void detached() {
            ViewCompat.setTransitionName(title, null);
            ViewCompat.setTransitionName(image, null);

        }
    }
}
