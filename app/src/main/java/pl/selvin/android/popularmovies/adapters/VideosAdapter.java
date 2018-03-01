package pl.selvin.android.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.selvin.android.popularmovies.R;
import pl.selvin.android.popularmovies.models.Video;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.ViewHolder> {
    private final LayoutInflater mInflater;
    private final List<Video> mVideos;

    public VideosAdapter(Context context, List<Video> videos) {
        mInflater = LayoutInflater.from(context);
        mVideos = videos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.videos_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(mVideos.get(position));
    }

    @Override
    public int getItemCount() {
        return mVideos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.videos_list_item_name)
        TextView videoName;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Video video) {
            videoName.setText(video.getName());
            videoName.setTag(video.getVideoUri());
        }

        @OnClick(R.id.videos_list_item_decorator)
        void onClick(View view) {
            final Context context = view.getContext();
            context.startActivity(new Intent(Intent.ACTION_VIEW).setData((Uri) videoName.getTag()));
        }
    }
}
