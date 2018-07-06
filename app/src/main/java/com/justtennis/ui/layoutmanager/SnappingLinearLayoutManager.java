package com.justtennis.ui.layoutmanager;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.justtennis.adapter.viewholder.PalmaresFastViewHolder;

public class SnappingLinearLayoutManager extends LinearLayoutManager {

    public SnappingLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        RecyclerView.SmoothScroller smoothScroller = new TopSnappedSmoothScroller(recyclerView.getContext());
        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }

    private class TopSnappedSmoothScroller extends LinearSmoothScroller {
        public TopSnappedSmoothScroller(Context context) {
            super(context);

        }

        @Override
        public PointF computeScrollVectorForPosition(int targetPosition) {
            return SnappingLinearLayoutManager.this.computeScrollVectorForPosition(targetPosition);
        }

        @Override
        protected int getVerticalSnapPreference() {
            return SNAP_TO_START;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public View findViewByPosition(int position) {
            final int childCount = getChildCount();
            for(int i=0 ; i<childCount ; i++) {
                final View child = getChildAt(i);
                if (getRankingId(child) == position) {
                    return child; // in pre-layout, this may not match
                }
            }
//            final int firstChild = getPosition(getChildAt(0));
//            final int viewPosition = position - firstChild;
//            if (viewPosition >= 0 && viewPosition < childCount) {
//                final View child = getChildAt(viewPosition);
//                if (getRankingId(child) == position) {
//                    return child; // in pre-layout, this may not match
//                }
//            }
            // fallback to traversal. This might be necessary in pre-layout.
            return super.findViewByPosition(position);
        }

        private Long getRankingId(View view) {
            Long ret = ((PalmaresFastViewHolder)view.getTag()).data.getRanking().getId();
            return (ret == null ) ? 0 : ret;
        }
    }
}
