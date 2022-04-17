package com.example.fludde.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fludde.R;
import com.example.fludde.model.BooksContent;

import java.util.List;

public class BookChildAdapter extends RecyclerView.Adapter<BookChildAdapter.BookViewHolder> {
    Context context;
    List<BooksContent> bookContentInfoList;
    private OnBookContentListener bookContentListener;
    private  int selectedItem = -1;

    public BookChildAdapter(Context context, List<BooksContent> bookContentInfoList, OnBookContentListener bookContentListener) {
        this.context = context;
        this.bookContentInfoList = bookContentInfoList;
        this.bookContentListener = bookContentListener;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View books = LayoutInflater.from(context).inflate(R.layout.content_post_img,parent,false);

        return new BookViewHolder(books, bookContentListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        BooksContent booksContents = bookContentInfoList.get(position);

        if(selectedItem == holder.getAdapterPosition())     //add the position to
        {
            holder.itemView.setBackgroundColor(Color.parseColor("#33F5FF")); //Color to change the selected Item too


        }
        else
        {
            holder.itemView.setBackgroundColor(Color.parseColor("#ffffff")); //change it back to write if its not click

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedItem=holder.getAdapterPosition();
                holder.onBookContentListener.onBookContentClick(holder.getAdapterPosition());
                notifyDataSetChanged();
            }
        });
        holder.bind(booksContents);

    }

    @Override
    public int getItemCount() {
        return bookContentInfoList.size();
    }

    class BookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        ImageView ivContentImage;
        TextView tvImageContentTitle;
        OnBookContentListener onBookContentListener;

        public BookViewHolder(@NonNull View itemView, OnBookContentListener onBookContentListener) {
            super(itemView);
            ivContentImage = itemView.findViewById(R.id.ivContentImage);
            tvImageContentTitle = itemView.findViewById(R.id.tvImageContentTitle);
            this.onBookContentListener =onBookContentListener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bind(BooksContent booksContents) {
            tvImageContentTitle.setText(booksContents.getTitle());

            String imageURL;
            imageURL = booksContents.getImageURL();
            Glide.with(context).load(imageURL).into(ivContentImage);
        }

        @Override
        public void onClick(View view) {
            notifyDataSetChanged();
        }

        @Override
        public boolean onLongClick(View view) {
            if(onBookContentListener != null)
            {onBookContentListener.onBookContentLongClick(getAdapterPosition());}
            return true;
        }
    }

    public interface OnBookContentListener{
        void onBookContentClick(int position);
        void onBookContentLongClick(int position);
    }
}
