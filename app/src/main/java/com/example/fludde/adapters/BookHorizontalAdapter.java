package com.example.fludde.adapters;

import android.content.Context;
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

public class BookHorizontalAdapter extends RecyclerView.Adapter<BookHorizontalAdapter.ViewHolder> {
    Context context;
    List<BooksContent> booksList;

    public BookHorizontalAdapter(Context context, List<BooksContent> booksList) {
        this.context = context;
        this.booksList = booksList;
    }


    @NonNull
    @Override
    public BookHorizontalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View bookView = LayoutInflater.from(context).inflate(R.layout.content_post_img,parent,false);
        return new ViewHolder(bookView);
    }

    @Override
    public void onBindViewHolder(@NonNull BookHorizontalAdapter.ViewHolder holder, int position) {
        BooksContent bookContent = booksList.get(position);
        holder.bind(bookContent);
    }

    @Override
    public int getItemCount() {
        return booksList.size();
    }

    class ViewHolder extends  RecyclerView.ViewHolder{

        ImageView ivContentImage;
        TextView tvImageContentTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivContentImage = itemView.findViewById(R.id.ivContentImage);
            tvImageContentTitle = itemView.findViewById(R.id.tvImageContentTitle);


        }

        public void bind(BooksContent booksContent) {
            tvImageContentTitle.setText(booksContent.getTitle());

            String imageURL;
            imageURL = booksContent.getImageURL();
            Glide.with(context).load(imageURL).into(ivContentImage);

        }

    }

}
