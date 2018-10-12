package recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class RecyclerViewHeight {
	public static int getTotalHeightofListView(RecyclerView listView, HeaderRecycleAdapter mNormalAdapter) {
		int totalHeight = 0;
		for (int i = 0; i < listView.getChildCount(); i++) {
			View mView = listView.getChildAt(i);
			mView.measure(
					View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
					View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
			//mView.measure(0, 0);
			totalHeight += mView.getMeasuredHeight();
			//	        Log.w("HEIGHT" + i, String.valueOf(totalHeight));
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getHeight() * (listView.getChildCount() - 1));
		listView.setLayoutParams(params);
		listView.requestLayout();
		return params.height;
	}
}
