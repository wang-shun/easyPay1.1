//package recyclerview;
//
//import android.content.Context;
//import android.support.annotation.NonNull;
//import android.util.Log;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.FrameLayout;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.acewill.ordermachine.R;
//import com.acewill.ordermachine.model.DishModel;
//import com.acewill.ordermachine.model.OptionCategory;
//
//import java.util.List;
//import java.util.Map;
//
//
///**
// * Created by taro on 16/6/22.
// */
//public class HeaderAdapterOption implements
//		HeaderRecycleAdapter.IHeaderAdapterOption<DishModel, DishModel>, IAdjustCountOption {
//	private static final String  TAG           = "HeaderAdapterOption";
//	private              boolean mIsMultiType  = false;
//	private              boolean mIsSetBgColor = false;
//	public               int     mAdjustCount  = NO_USE_ADJUST_COUNT;
//
//	public HeaderAdapterOption(boolean isMultiType, boolean isSetBgColor) {
//		mIsMultiType = isMultiType;
//		mIsSetBgColor = isSetBgColor;
//	}
//
//	@Override
//	public int getHeaderViewType(int groupId, int position) {
//		if (mIsMultiType) {
//			if (groupId > 6) {
//				return -3;
//			} else if (groupId > 3) {
//				return -1;
//			} else {
//				return -2;
//			}
//		} else {
//			return -1;
//		}
//	}
//
//	@Override
//	public int getItemViewType(int position, int groupId, int childId, boolean isHeaderItem, boolean isShowHeader) {
//		if (isHeaderItem) {
//			return getHeaderViewType(groupId, position);
//		} else {
//			if (mIsMultiType) {
//				if (childId > 3) {
//					return 0;
//				} else {
//					return 1;
//				}
//			} else {
//				return 0;
//			}
//		}
//	}
//
//	@Override
//	public int getLayoutId(int viewType) {
//		switch (viewType) {
//			case 0:
//			case NO_HEADER_TYPE:
//				return R.layout.item_content_2;// 0   item用到这个
//			case 1:
//				return R.layout.item_content;
//			case -1:
//				return R.layout.item_header;// -1    头用到这个
//			case -2:
//				return R.layout.item_header_2;
//			case -3:
//				return R.layout.item_header_3;
//			default:
//				return R.layout.item_content;
//		}
//	}
//
//	@Override
//	public void setHeaderHolder(final Context context, final HeaderRecycleAdapter<DishModel, DishModel> adapter, final int groupId, final Map<Integer, DishModel> mHeaderMap, @NonNull final HeaderRecycleViewHolder holder, final int position) {
//		final DishModel    model         = mHeaderMap.get(groupId);
//		TextView           dishname_tv   = holder.getView(R.id.adapterview_dialog_dishname_tv);
//		final LinearLayout haedLayout    = holder.getView(R.id.adapterview_dialog_header_layout);
//		TextView           totalcount_tv = holder.getView(R.id.adapter_dialog_item_totalcount_tv);
//		TextView           dishprice_tv  = holder.getView(R.id.adapterview_dialog_dishprice_tv);
//		FrameLayout        cut_btn       = holder.getView(R.id.adapterview_dialog_cut);
//		FrameLayout        add_btn       = holder.getView(R.id.adapterview_dialog_add);
//		cut_btn.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Log.e(TAG, "click position>>" + position);
//				Toast.makeText(context, "dianji cut", Toast.LENGTH_SHORT).show();
//				model.quantity--;
//				if (model.quantity <= 0) {
//					adapter.notifyItemRemoved(position);
//					mHeaderMap.remove(groupId);
//					if (position != adapter
//							.getItemCount()) {      // 这个判断的意义就是如果移除的是最后一个，就不用管它了
//						adapter.notifyItemRangeChanged(position, adapter.getItemCount() - position);
//					}
//				} else {
//					//					Cart.getInstance().addItem(model);
//					adapter.notifyItemChanged(holder.getAdapterPosition());
//				}
//			}
//		});
//		add_btn.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Log.e(TAG, "click position>>" + position);
//				Toast.makeText(context, "dianji add", Toast.LENGTH_SHORT).show();
//			}
//		});
//
//		if (dishname_tv != null) {
//			dishname_tv.setText(model.dishName);
//		}
//		if (totalcount_tv != null) {
//			totalcount_tv.setText(String.valueOf(model.quantity));
//		}
//		if (dishprice_tv != null) {
//			dishprice_tv.setText(String.valueOf(model.getTotalPrice()));
//		}
//	}
//
//	@Override
//	public void setViewHolder(Context context, HeaderRecycleAdapter<DishModel, DishModel> adapter, int groupId, int childId, final int position, List<DishModel> modelList, @NonNull HeaderRecycleViewHolder holder) {
//		DishModel    model       = modelList.get(childId);
//		TextView     category_tv = holder.getView(R.id.adapterview_dialog_category_tv);
//		TextView     option_tv   = holder.getView(R.id.adapterview_dialog_category_tv2);
//		LinearLayout view        = holder.getView(R.id.adapterview_dialog_category_layout);
//		view.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Log.e(TAG, "click position2>>" + position);
//			}
//		});
//		if (model.isPackage()) {
//			DishModel.Package aPackage = model.subItemList.get(childId);
//			option_tv.setVisibility(View.GONE);
//			category_tv.setVisibility(View.VISIBLE);
//			category_tv.setText(aPackage.dishName);
//		} else if (model.optionList != null && model.optionList.size() > 0) {
//			OptionCategory.Option option = model.optionList.get(childId);
//			category_tv.setVisibility(View.GONE);
//			option_tv.setVisibility(View.VISIBLE);
//			option_tv.setText(option.optionName);
//		} else {
//			view.setVisibility(View.GONE);
//			category_tv.setVisibility(View.GONE);
//			category_tv.setVisibility(View.GONE);
//		}
//	}
//
//	//
//	//    //// TODO: 2017/4/30 anch
//	//    @Override
//	//    public void setViewHolder(int groupId, int childId, int position, String itemData, HeaderRecycleViewHolder holder) {
//	//        TextView  category_tv = holder.getView(R.id.adapterview_dialog_category_tv);
//	//        TextView  option_tv   = holder.getView(R.id.adapterview_dialog_category_tv2);
//	//
//	//        DishModel model = mData.get(parentPos);
//	//        if (model.isPackage()) {
//	//            DishModel.Package aPackage = model.subItemList.get(childPos);
//	//            option_tv.setVisibility(View.GONE);
//	//            category_tv.setVisibility(View.VISIBLE);
//	//            category_tv.setText(aPackage.dishName);
//	//        } else if (model.optionList != null && model.optionList.size() > 0) {
//	//            OptionCategory.Option option = model.optionList.get(childPos);
//	//            category_tv.setVisibility(View.GONE);
//	//            option_tv.setVisibility(View.VISIBLE);
//	//            option_tv.setText(option.optionName);
//	//        } else {
//	//            view.setVisibility(View.GONE);
//	//            category_tv.setVisibility(View.GONE);
//	//            category_tv.setVisibility(View.GONE);
//	//        }
//	//
//	//
//	//        if (holder.getItemViewType() == 1) {
//	//            BubbleBoxLayout layout = (BubbleBoxLayout) holder.getRootView();
//	//            layout.setIsDrawableTest(true);
//	//            layout.setButtomText("小洪是SB,哇咔哫");
//	//        }
//	//        if (mIsSetBgColor) {
//	//            holder.getRootView().setBackgroundColor(Color.parseColor("#99cc99"));
//	//        }
//	//    }
//	//	public void removeItem(int position){
//	//		data.remove(posiiton);
//	//		notifyItemRemoved(position);
//	//		if(deleteIndex != data.size()){      // 这个判断的意义就是如果移除的是最后一个，就不用管它了，= =whatever，老板还不发工资啊啊啊啊啊啊
//	//			notifyItemRangeChanged(position, data.size() - position);
//	//		}
//	//	}
//	@Override
//	public int getAdjustCount() {
//		return mAdjustCount;
//	}
//
//	@Override
//	public void setAdjustCount(int adjustCount) {
//		mAdjustCount = adjustCount;
//	}
//
//	@Override
//	public void onCreateViewEverytime(@NonNull View itemView, @NonNull ViewGroup parentView, @NonNull HeaderRecycleAdapter adapter, int viewType) {
//
//	}
//
//
//}