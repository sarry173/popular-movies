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
import pl.selvin.android.popularmovies.models.Review;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {
    private final LayoutInflater mInflater;
    private final List<Review> mReviews;

    public ReviewsAdapter(Context context, List<Review> reviews) {
        mInflater = LayoutInflater.from(context);
        mReviews = reviews;
    }

    public void setReviews(List<Review> reviews) {
        mReviews.clear();
        mReviews.addAll(reviews);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.reviews_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(mReviews.get(position));
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.reviews_list_item_author)
        TextView author;
        @BindView(R.id.reviews_list_item_content)
        TextView content;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(Review review) {
            author.setText(review.getAuthor());
            author.setTag(Uri.parse(review.getUrl()));
            content.setText(review.getContent());
        }

        @OnClick(R.id.reviews_list_item_decorator)
        void onClick(View view) {
            final Context context = view.getContext();
            context.startActivity(new Intent(Intent.ACTION_VIEW).setData((Uri) author.getTag()));
        }
    }
}
