package com.example.fludde.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.example.fludde.R;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fludde.User;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

public class SearchFragmentAdapter extends RecyclerView.Adapter<SearchFragmentAdapter.UserViewHolder> {
    Context context;
    List<ParseUser> users;

    public SearchFragmentAdapter(Context context, List<ParseUser> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.users_query, parent, false);
        return new UserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        ParseUser user = users.get(position);
        holder.bind((User) user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder{

       private TextView tvUName;
        private ImageView ivUserImage;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUName = itemView.findViewById(R.id.tvUName);
            ivUserImage = itemView.findViewById(R.id.ivUserImage);
        }

        public void bind(ParseUser user) {
            tvUName.setText(user.getUsername());

            ParseFile userImage = user.getParseFile("image");

            Glide.with(context)
                .load(userImage != null ? userImage.getUrl() : null)
                .apply(new RequestOptions().transform(new CenterCrop()))
                .placeholder(R.drawable.placeholder_avatar)
                .error(R.drawable.placeholder_avatar)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivUserImage);
        }
    }
}
