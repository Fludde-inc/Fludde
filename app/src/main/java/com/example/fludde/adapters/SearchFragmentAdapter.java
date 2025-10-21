package com.example.fludde.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.example.fludde.R;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fludde.model.UserUi;

import java.util.List;

public class SearchFragmentAdapter extends RecyclerView.Adapter<SearchFragmentAdapter.UserViewHolder> {
    private final Context context;
    private final List<UserUi> users;

    public SearchFragmentAdapter(Context context, List<UserUi> users) {
        this.context = context;
        this.users = users;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        String key = users.get(position).getUsername() + "|" + position;
        return key.hashCode();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.users_query, parent, false);
        return new UserViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.bind(users.get(position));
    }

    @Override
    public int getItemCount() { return users.size(); }

    class UserViewHolder extends RecyclerView.ViewHolder{
        private final TextView tvUName;
        private final ImageView ivUserImage;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUName = itemView.findViewById(R.id.tvUName);
            ivUserImage = itemView.findViewById(R.id.ivUserImage);
        }

        public void bind(UserUi user) {
            tvUName.setText(user.getUsername());

            Glide.with(context)
                .load(user.getImageUrl().isEmpty() ? null : user.getImageUrl())
                .apply(new RequestOptions().transform(new CenterCrop()))
                .placeholder(R.drawable.placeholder_avatar)
                .error(R.drawable.placeholder_avatar)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivUserImage);
        }
    }
}
