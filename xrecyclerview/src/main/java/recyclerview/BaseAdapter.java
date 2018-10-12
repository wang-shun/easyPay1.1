package recyclerview;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;


/**
 * Created by taro on 16/10/15.
 */
abstract class BaseAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> implements StickHeaderItemDecoration.IStickerHeaderDecoration, HeaderSpanSizeLookup.ISpanSizeHandler {
    //与此Adapter绑定的recycleView
    private RecyclerView mParentRecycle = null;

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mParentRecycle = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mParentRecycle = null;
    }

    /**
     * 获取当前此adapter绑定的recycleView
     *
     * @return
     */
    @Nullable
    public RecyclerView getParentRecycleView() {
        return mParentRecycle;
    }
}
