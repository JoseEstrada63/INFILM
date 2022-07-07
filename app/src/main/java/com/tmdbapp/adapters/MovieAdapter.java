package com.tmdbapp.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.squareup.picasso.Picasso;
import com.tmdbapp.R;
import com.tmdbapp.api.APIClient;
import com.tmdbapp.models.MovieModel;
import com.tmdbapp.views.MovieDetailsActivity;

import java.text.DecimalFormat;

public class MovieAdapter extends PagedListAdapter<MovieModel, MovieAdapter.MovieViewHolder> {
    private static final int MAX_LENGTH = 25;
    private final Context context;
    private String fromWhere;

    public MovieAdapter(Context context, String fromWhere) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.fromWhere = fromWhere;
    }

    @NonNull
    @Override
    public MovieAdapter.MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(this.context).inflate(R.layout.movie_item, parent, false);
        return new MovieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapter.MovieViewHolder holder, int position) {
        MovieModel item = this.getItem(position);

        if (item != null) holder.bindTo(item);
    }

    public void setFromWhere(String fromWhere) {
        this.fromWhere = fromWhere;
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        private final ImageView poster;

        MovieViewHolder(View itemView) {
            super(itemView);
            this.poster = itemView.findViewById(R.id.item_movie_poster);
        }

        private void bindTo(MovieModel movie) {
            String poster = APIClient.getFullPosterPath(movie.getPosterPath());
            Glide.with(context).load(poster).transform(new CenterCrop()).into(this.poster);
            this.poster.setOnClickListener(view -> {
                Intent intent = new Intent(context, MovieDetailsActivity.class);
                intent.putExtra("id", movie.getId());
                intent.putExtra("fromWhere", fromWhere);
                ActivityOptions transitionActivity =
                        ActivityOptions.makeSceneTransitionAnimation((Activity) context,
                                                                     this.poster,
                                                                     context.getResources().getString(R.string.poster_transition));
                context.startActivity(intent, transitionActivity.toBundle());
            });
        }
    }

    private static final DiffUtil.ItemCallback<MovieModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<MovieModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull MovieModel oldItem,
                                       @NonNull MovieModel newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull MovieModel oldItem,
                                          @NonNull MovieModel newItem) {
            return oldItem == newItem;
        }
    };
}
