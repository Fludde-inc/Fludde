package com.example.fludde.utils;

import androidx.annotation.AnimRes;
import androidx.fragment.app.FragmentTransaction;

import com.example.fludde.R;

/** Applies subtle, fast transitions when swapping fragments/tabs. */
public final class FragmentTransitions {
    private FragmentTransitions(){}

    public static void applyFastFade(FragmentTransaction tx) {
        if (tx == null) return;
        tx.setCustomAnimations(
                R.anim.fade_in_fast,   // enter
                R.anim.fade_out_fast,  // exit
                R.anim.fade_in_fast,   // pop enter
                R.anim.fade_out_fast   // pop exit
        );
    }
}
