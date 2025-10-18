package com.example.fludde.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fludde.R;
import com.example.fludde.model.MovieContent;
import com.example.fludde.utils.GlideExtensions;
import com.example.fludde.utils.Haptics;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class MovieChildAdapter extends RecyclerView.Adapter<MovieChildAdapter.MovieViewHolder> {

    private final Context context;
    private final List<MovieContent> items;
    private final OnMovieContentListener listener;

    // single selection support
    private int selectedPos = RecyclerView.NO_POSITION;

    public MovieChildAdapter(@NonNull Context context,
                             @NonNull List<MovieContent> items,
                             @NonNull OnMovieContentListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        String key = items.get(position).getTitle() + position;
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
        holder.bind(items.get(position), position == selectedPos);
    }

    @Override
    public int getItemCount() { return items.size(); }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        ImageView ivContentImage;
        TextView tvImageContentTitle;

        MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            ivContentImage = itemView.findViewById(R.id.ivContentImage);
            tvImageContentTitle = itemView.findViewById(R.id.tvImageContentTitle);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        void bind(MovieContent data, boolean selected) {
            tvImageContentTitle.setText(data.getTitle());
            GlideExtensions.loadPoster(ivContentImage, data.getImagePath());

            MaterialCardView card = (MaterialCardView) itemView;
            card.setChecked(selected);
            itemView.setSelected(selected);
        }

        @Override
        public void onClick(View v) {
            int pos = getBindingAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            int old = selectedPos;
            selectedPos = pos;

            if (old != RecyclerView.NO_POSITION) notifyItemChanged(old);
            notifyItemChanged(selectedPos);

            Haptics.tick(v); // subtle feedback on selection
            if (listener != null) listener.onMovieContentClick(pos);
        }

        @Override
        public boolean onLongClick(View v) {
            int pos = getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION && listener != null) {
                Haptics.longPress(v); // discoverable haptic
                listener.onMovieContentLongClick(pos);
                return true;
            }
            return false;
        }
    }

    public interface OnMovieContentListener {
        void onMovieContentClick(int position);
        void onMovieContentLongClick(int position);
    }
}
