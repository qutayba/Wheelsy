package site.qutayba.wheelsy.utilities;

import android.os.Handler;
import android.widget.AbsListView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class EndlessScrollListener extends RecyclerView.OnScrollListener {

    private final Handler handler = new Handler();
    private final Runnable runnable;

    public EndlessScrollListener() {
        runnable = new Runnable() {
            @Override
            public void run() {
                onLoadMore();
            }
        };
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (dy > 0) {
            // Recycle view scrolling down...
            if (recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN) == false) {
                handler.postDelayed(runnable, 500);
            }
        } else {
            handler.removeCallbacksAndMessages(runnable);
        }

    }


    // Defines the process for actually loading more data based on page
    // Returns true if more data is being loaded; returns false if there is no more data to load.
    public abstract boolean onLoadMore();

}
