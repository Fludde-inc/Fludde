package com.example.fludde.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fludde.R;
import com.example.fludde.model.MovieContent;
import com.example.fludde.utils.GlideExtensions;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class MovieChildAdapter extends RecyclerView.Adapter<MovieChildAdapter.MovieViewHolder> {

    public interface OnMovieContentListener {
        void onMovieContentClick(int position);
        void onMovieContentLongClick(int position);
    }

    private final Context context;
    private final List<MovieContent> contents;
    private final OnMovieContentListener listener;

    public MovieChildAdapter(Context context, List<MovieContent> contents, OnMovieContentListener listener) {
        this.context = context;
        this.contents = contents;
        this.listener = listener;
        setHasStableIds(true);
    }

    @Override public long getItemId(int position) {
        String key = contents.get(position).getTitle() + "|" + position;
        return key.hashCode();
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.content_post_img, parent, false);
        v.setBackgroundResource(R.drawable.list_item_bg);
        return new MovieViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        holder.bind(contents.get(position));
    }

    @Override
    public int getItemCount() { return contents.size(); }

    class MovieViewHolder extends RecyclerView.ViewHolder {
        private final ShapeableImageView ivPoster;
        private final TextView tvTitle;

        MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.ivContentImage);
            tvTitle = itemView.findViewById(R.id.tvImageContentTitle);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onMovieContentClick(pos);
                }
            });

            itemView.setOnLongClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onMovieContentLongClick(pos);
                }
                return true;
            });
        }

        void bind(MovieContent c) {
            tvTitle.setText(c.getTitle());
            // FIXED: Changed getImageURL() to getImagePath()
            GlideExtensions.loadPoster(ivPoster, c.getImagePath());
        }
    }
}