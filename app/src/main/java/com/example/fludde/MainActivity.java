package com.example.fludde;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.fludde.fragments.HomeFragment;
import com.example.fludde.fragments.PostFragment;
import com.example.fludde.fragments.ProfileFragment;
import com.example.fludde.fragments.SearchFragment;
import com.example.fludde.fragments.ComposeParentFragment;
import com.example.fludde.utils.FragmentTransitions;
import com.example.fludde.utils.InsetsUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

/**
 * Single-activity host:
 * - Owns bottom navigation and fragment swaps.
 * - No styling hardcodes; all appearance driven by theme/styles.
 * - Edge-to-edge insets handled centrally via InsetsUtils (WindowCompat + ViewCompat).
 * - Predictable back behavior:
 *      * If not on Home, back selects Home.
 *      * If on Home, back finishes Activity.
 *      * (Per-component back stacks can be added later if a tab pushes details.)
 */
public class MainActivity extends AppCompatActivity {

    private static final String STATE_SELECTED_TAB = "state:selected_tab";

    private BottomNavigationView bottomNav;

    // Keep fragments by tag to avoid re-creating on tab switch.
    private final Map<Integer, String> tagsById = new HashMap<>();
    private int currentItemId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottomNavigation);

        // Edge-to-edge: pad the container and bottom nav for system bars.
        InsetsUtils.applyEdgeToEdge(
                this,
                findViewById(R.id.flContainer),
                bottomNav
        );

        // Map menu IDs to stable fragment tags
        tagsById.put(R.id.action_home, "tab:home");
        tagsById.put(R.id.action_feed, "tab:feed");
        tagsById.put(R.id.action_compose, "tab:compose");
        tagsById.put(R.id.action_search, "tab:search");
        tagsById.put(R.id.action_profile, "tab:profile");

        bottomNav.setOnItemSelectedListener(item -> {
            switchTo(item.getItemId());
            return true;
        });

        // Re-selection: pop child back stack or scroll-to-top could go here later.
        bottomNav.setOnItemReselectedListener(item -> {
            Fragment f = getSupportFragmentManager().findFragmentByTag(tagsById.get(item.getItemId()));
            if (f != null) {
                // If a tab uses a nested stack later, you can do:
                // f.getChildFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                // For now, no-op to keep behavior simple and predictable.
            }
        });

        // Back: if not on Home, go Home. If on Home, finish.
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() {
                if (currentItemId != R.id.action_home) {
                    bottomNav.setSelectedItemId(R.id.action_home);
                } else {
                    setEnabled(false);
                    onBackPressed(); // default finish()
                }
            }
        });

        final int startTab = savedInstanceState != null
                ? savedInstanceState.getInt(STATE_SELECTED_TAB, R.id.action_home)
                : R.id.action_home;

        bottomNav.setSelectedItemId(startTab);
        switchTo(startTab);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_TAB, currentItemId);
    }

    private void switchTo(@IdRes int itemId) {
        if (currentItemId == itemId) return;
        currentItemId = itemId;

        final String tag = tagsById.get(itemId);
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        FragmentTransitions.applyFastFade(tx);

        // Hide all current fragments
        for (Map.Entry<Integer, String> e : tagsById.entrySet()) {
            Fragment f = getSupportFragmentManager().findFragmentByTag(e.getValue());
            if (f != null && !f.isHidden()) {
                tx.hide(f);
            }
        }

        Fragment target = getSupportFragmentManager().findFragmentByTag(tag);
        if (target == null) {
            target = createFragmentFor(itemId);
            tx.add(R.id.flContainer, target, tag);
        } else {
            tx.show(target);
        }

        tx.commit();
    }

    private Fragment createFragmentFor(@IdRes int itemId) {
        if (itemId == R.id.action_home) return new HomeFragment();
        if (itemId == R.id.action_feed) return new PostFragment();
        if (itemId == R.id.action_compose) return new ComposeParentFragment();
        if (itemId == R.id.action_search) return new SearchFragment();
        if (itemId == R.id.action_profile) return new ProfileFragment();
        // Fallback
        return new HomeFragment();
    }
}
