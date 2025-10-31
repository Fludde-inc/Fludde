package com.example.fludde;

import android.os.Looper;
import static org.robolectric.Shadows.shadowOf;

public abstract class RobolectricTestBase {
  protected void drainMain() {
    shadowOf(Looper.getMainLooper()).runToEndOfTasks();
  }
  protected void drainAll() {
    drainMain();
    try { org.robolectric.Robolectric.flushBackgroundThreadScheduler(); } catch (Throwable ignored) {}
    drainMain();
  }
}
