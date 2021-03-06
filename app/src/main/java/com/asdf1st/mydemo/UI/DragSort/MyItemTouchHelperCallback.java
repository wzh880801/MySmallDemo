package com.asdf1st.mydemo.UI.DragSort;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

/**
 * Created by User on 2018/7/13.
 */

public class MyItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private String TAG="ItemTouchHelperCallback";
    private RecyclerView.Adapter adapter;
    public Context context;
    public OnItemMoveListener itemMoveListener;
    public OnItemRemoveListener itemRemoveListener;
    public boolean dragEnable=false;
    public boolean swipeEnable=false;
    public MyItemTouchHelperCallback(Context context, RecyclerView.Adapter adapter){
        this.context=context;
        this.adapter=adapter;
    }

    public MyItemTouchHelperCallback(Context context, RecyclerView.Adapter adapter, OnItemMoveListener itemMoveListener){
        this.context=context;
        this.adapter=adapter;
        this.itemMoveListener=itemMoveListener;
        dragEnable=true;
        swipeEnable=false;
    }
    public MyItemTouchHelperCallback(Context context, RecyclerView.Adapter adapter, OnItemRemoveListener itemRemoveListener){
        this.context=context;
        this.adapter=adapter;
        this.itemRemoveListener=itemRemoveListener;
        swipeEnable=true;
        dragEnable=false;
    }

    /**
     * 设置 允许拖拽和滑动删除的方向  0代表不能滑动
     *
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        // 指定可 拖拽方向 和 滑动消失的方向
        int dragFlags, swipeFlags;
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager || manager instanceof StaggeredGridLayoutManager) {
            // 上下左右都可以拖动
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        } else {
            // 可以上下拖动
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        }
        // 可以左右方向滑动消失
        swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        // 如果某个值传 0 , 表示不支持该功能
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    /**
     * 拖拽后回调,一般通过接口暴露给adapter, 让adapter去处理数据的交换
     *
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

        // 相同 viewType 之间才能拖动交换
        if (viewHolder.getItemViewType() == target.getItemViewType()) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            Log.e(TAG, "onMove: from "+ fromPosition+" to "+toPosition);
            if (fromPosition < toPosition) {
                //途中所有的item位置都要移动
                for (int i = fromPosition; i < toPosition; i++) {
                    if (itemMoveListener!=null){
                        itemMoveListener.onItemMove(i,i+1);
                    }
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    if (itemMoveListener!=null){
                        itemMoveListener.onItemMove(i,i-1);
                    }
                }
            }
            adapter.notifyItemMoved(fromPosition, toPosition);
            return true;
        }
        return false;
    }

    /**
     * 滑动删除后回调,一般通过接口暴露给adapter, 让adapter去删除该条数据
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        if (itemRemoveListener!=null){
            itemRemoveListener.onItemRemove(position);
        }
        // adapter 刷新
        adapter.notifyItemRemoved(position);
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState== ItemTouchHelper.ACTION_STATE_DRAG){
            //拖拽状态
            viewHolder.itemView.setScaleX((float) 1.2);
            viewHolder.itemView.setScaleY((float) 1.2);
        }else if (actionState==ItemTouchHelper.ACTION_STATE_SWIPE){
            viewHolder.itemView.setBackgroundColor(Color.RED);
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            //滑动时改变Item的透明度
            final float alpha = 1 - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
            viewHolder.itemView.setAlpha(alpha);
            viewHolder.itemView.setTranslationX(dX);
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            viewHolder.itemView.setScaleX((float) 1.0);
            viewHolder.itemView.setScaleY((float) 1.0);

        super.clearView(recyclerView, viewHolder);
    }

    /**
     * 是否支持长按开始拖拽,默认开启
     * 可以不开启,然后在长按 item 的时候,手动 调用 mItemTouchHelper.startDrag(myHolder) 开启,更加灵活
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return dragEnable;
    }

    /**
     * 是否支持滑动删除,默认开启
     * 可以不开启,然后在长按 item 的时候,手动 调用 mItemTouchHelper.startSwipe(myHolder) 开启,更加灵活
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return swipeEnable;
    }

    public interface OnItemMoveListener{
        void onItemMove(int fromPosition,int toPosition);
    }

    public OnItemMoveListener getItemMoveListener() {
        return itemMoveListener;
    }

    public void setItemMoveListener(OnItemMoveListener itemMoveListener) {
        this.itemMoveListener = itemMoveListener;
    }

    public interface OnItemRemoveListener{
        void onItemRemove(int position);
    }

    public OnItemRemoveListener getItemRemoveListener() {
        return itemRemoveListener;
    }

    public void setItemRemoveListener(OnItemRemoveListener itemRemoveListener) {
        this.itemRemoveListener = itemRemoveListener;
    }
}
