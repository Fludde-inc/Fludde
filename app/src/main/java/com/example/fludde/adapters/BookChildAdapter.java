package com.example.fludde.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fludde.R;
import com.example.fludde.model.BooksContent;
import com.example.fludde.utils.GlideExtensions;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class BookChildAdapter extends RecyclerView.Adapter<BookChildAdapter.BookViewHolder> {

    public interface OnBookContentListener {
        void onBookContentClick(int position);
        void onBookContentLongClick(int position);
    }

    private final Context context;
    private final List<BooksContent> contents;
    private final OnBookContentListener listener;

    public BookChildAdapter(Context context, List<BooksContent> contents, OnBookContentListener listener) {
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
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.content_post_img, parent, false);
        v.setBackgroundResource(R.drawable.list_item_bg);
        return new BookViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        holder.bind(contents.get(position));
    }

    @Override
    public int getItemCount() { return contents.size(); }

    class BookViewHolder extends RecyclerView.ViewHolder {
        private final ShapeableImageView ivPoster;
        private final TextView tvTitle;

        BookViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.ivContentImage);
            tvTitle = itemView.findViewById(R.id.tvImageContentTitle);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition(); // <-- fix
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onBookContentClick(pos);
                }
            });

            itemView.setOnLongClickListener(v -> {
                int pos = getAdapterPosition(); // <-- fix
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onBookContentLongClick(pos);
                }
                return true;
            });
        }

        void bind(BooksContent c) {
            tvTitle.setText(c.getTitle());
            GlideExtensions.loadPoster(ivPoster, c.getImageURL());
        }
    }
}
