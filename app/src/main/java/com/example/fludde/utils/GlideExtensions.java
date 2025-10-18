package com.example.fludde.utils;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.example.fludde.R;

/** Centralized image loading with fade-in to avoid pop-in. */
public final class GlideExtensions {
    private GlideExtensions() {}

    public static void loadPoster(ImageView target, String url) {
        if (target == null) return;
        Glide.with(target.getContext())
                .load(url)
                .apply(new RequestOptions().transform(new CenterCrop()))
                .placeholder(R.drawable.placeholder_poster)
                .error(R.drawable.placeholder_poster)
                .transition(DrawableTransitionOptions.withCrossFade(140)) // match fast anim
                .into(target);
    }

    public static void loadSquare(ImageView target, String url) {
        if (target == null) return;
        Glide.with(target.getContext())
                .load(url)
                .apply(new RequestOptions().transform(new CenterCrop()))
                .placeholder(R.drawable.placeholder_square)
                .error(R.drawable.placeholder_square)
                .transition(DrawableTransitionOptions.withCrossFade(140))
                .into(target);
    }

    public static void loadAvatar(ImageView target, String urlOrNull) {
        if (target == null) return;
        Glide.with(target.getContext())
                .load(urlOrNull)
                .apply(new RequestOptions().transform(new CenterCrop()))
                .placeholder(R.drawable.placeholder_avatar)
                .error(R.drawable.placeholder_avatar)
                .transition(DrawableTransitionOptions.withCrossFade(140))
                .into(target);
    }
}
