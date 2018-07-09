package com.yuri.dreamlinkcost.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yuri.dreamlinkcost.R;
import com.yuri.dreamlinkcost.bean.Bmob.BmobCostYuri;
import com.yuri.dreamlinkcost.bean.table.CardItem;
import com.yuri.dreamlinkcost.interfaces.RecyclerViewClickListener;
import com.yuri.xlog.Log;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CardViewAdapter extends AnimRecyclerViewAdapter<CardViewAdapter.BindingHolder> {

    private List<BmobCostYuri> mCostList;
    private RecyclerViewClickListener mListener;

    private Context mContext;

    public CardViewAdapter(Context context, List<BmobCostYuri> list) {
        mContext = context;
        this.mCostList = list;
    }

    public void clearList() {
        Log.d();
        mCostList.clear();
    }

    public void setCostList(List<BmobCostYuri> list) {
        this.mCostList = list;
    }

    public void addCostList(List<BmobCostYuri> list) {
        for (int i = 0; i < list.size(); i++) {
            addItem(i, list.get(i));
        }
    }

    public void sortByPriceAsc() {
        Collections.sort(mCostList, BmobCostYuri.PRICE_ASC_COMPARATOR);
        notifyDataSetChanged();
    }

    public void sortByPriceDesc() {
        Collections.sort(mCostList, BmobCostYuri.PRICE_DESC_COMPARATOR);
        notifyDataSetChanged();
    }

    public void sortByDateAsc() {
        Collections.sort(mCostList, BmobCostYuri.DATE_ASC_COMPARATOR);
        notifyDataSetChanged();
    }

    public void sortByDateDesc() {
        Collections.sort(mCostList, BmobCostYuri.DATE_DESC_COMPARATOR);
        notifyDataSetChanged();
    }

    public void addItem(int position, BmobCostYuri cost) {
        mCostList.add(position, cost);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        if (position == -1 && getItemCount() > 0) {
            position = getItemCount() - 1;
        }

        if (position > -1 && position < getItemCount()) {
            mCostList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void setOnItemClickListener(RecyclerViewClickListener listener) {
        mListener = listener;
    }

    public BmobCostYuri getItem(int position) {
        return mCostList.get(position);
    }

    /**
     * 获取网络列表
     */
    public List<BmobCostYuri> getCostList() {
        return this.mCostList;
    }

    @Override
    public BindingHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.item_cardview,
                viewGroup,
                false);
        return new BindingHolder(view);
    }

    @Override
    public void onBindViewHolder(BindingHolder holder, final int position) {
        BmobCostYuri item = getItem(position);
        CardItem cardItem = new CardItem(mContext).getCardItem(item);

        holder.mTitleView.setText(cardItem.title);
        holder.mTotalPayView.setText(cardItem.info);
        holder.mHeaderView.setText(CardItem.getHeaderText(cardItem.header));
        holder.mHeaderView.setBackgroundDrawable(cardItem.getItemBackgroudRes());
        holder.mDateView.setText(cardItem.date);

        if (mOnScrollIdle) {
            showItemAnim(holder.itemView, position);
        }
    }

    @Override
    public int getItemCount() {
        return mCostList.size();
    }

    private boolean mOnScrollIdle = true;

    public void setOnScrollIdle(boolean idle) {
        mOnScrollIdle = idle;
    }

    public class BindingHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.headerView)
        TextView mHeaderView;
        @BindView(R.id.titleView)
        TextView mTitleView;
        @BindView(R.id.totalPayView)
        TextView mTotalPayView;
        @BindView(R.id.dateView)
        TextView mDateView;

        public BindingHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onItemClick(v, getLayoutPosition());
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return false;
                }
            });
        }


    }
}
