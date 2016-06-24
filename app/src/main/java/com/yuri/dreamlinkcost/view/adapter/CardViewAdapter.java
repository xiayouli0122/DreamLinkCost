package com.yuri.dreamlinkcost.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yuri.dreamlinkcost.R;
import com.yuri.dreamlinkcost.bean.Bmob.BmobCost;
import com.yuri.dreamlinkcost.bean.table.CardItem;
import com.yuri.dreamlinkcost.bean.table.Cost;
import com.yuri.dreamlinkcost.interfaces.RecyclerViewClickListener;
import com.yuri.xlog.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CardViewAdapter extends AnimRecyclerViewAdapter<CardViewAdapter.BindingHolder> {

    private List<BmobCost> mCostList = new ArrayList<>();
    private List<Cost> mLocalCostList = new ArrayList<>();

    private RecyclerViewClickListener mListener;

    private Context mContext;

    public CardViewAdapter(Context context, List<Cost> localList, List<BmobCost> list) {
        mContext = context;
        this.mCostList = list;
        this.mLocalCostList = localList;
    }

    public void clearList() {
        Log.d();
        mLocalCostList.clear();
        mCostList.clear();
    }

    public void setCostList(List<Cost> localList, List<BmobCost> list) {
        this.mCostList = list;
        this.mLocalCostList = localList;
    }

    public void addCostList(List<BmobCost> list) {
        for (int i = 0; i < list.size(); i++) {
            addItem(i, list.get(i));
        }
    }

    public void addLocalCostList(List<Cost> list) {
        for (int i = 0; i < list.size(); i++) {
            addLocalItem(i, list.get(i));
        }
    }

    public void sortByPriceAsc() {
        Collections.sort(mLocalCostList, Cost.PRICE_ASC_COMPARATOR);
        Collections.sort(mCostList, BmobCost.PRICE_ASC_COMPARATOR);
        notifyDataSetChanged();
    }

    public void sortByPriceDesc() {
        Collections.sort(mLocalCostList, Cost.PRICE_DESC_COMPARATOR);
        Collections.sort(mCostList, BmobCost.PRICE_DESC_COMPARATOR);
        notifyDataSetChanged();
    }

    public void sortByDateAsc() {
        Collections.sort(mLocalCostList, Cost.DATE_ASC_COMPARATOR);
        Collections.sort(mCostList, BmobCost.DATE_ASC_COMPARATOR);
        notifyDataSetChanged();
    }

    public void sortByDateDesc() {
        Collections.sort(mLocalCostList, Cost.DATE_DESC_COMPARATOR);
        Collections.sort(mCostList, BmobCost.DATE_DESC_COMPARATOR);
        notifyDataSetChanged();
    }

    public void addItem(int position, BmobCost cost) {
        mCostList.add(position, cost);
        notifyItemInserted(position);
    }

    public void addLocalItem(int position, Cost cost) {
        mLocalCostList.add(position, cost);
        notifyItemChanged(position);
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

    public Object getItem(int position) {
        int localListCount = mLocalCostList.size();
        if (position < localListCount) {
            //本地数据在前面
            return mLocalCostList.get(position);
        }
        return mCostList.get(position - localListCount);
    }

    /**
     * 获取网络列表
     */
    public List<BmobCost> getCostList() {
        return this.mCostList;
    }

    /**
     * 获取本地列表
     */
    public List<Cost> getLocalList() {
        return this.mLocalCostList;
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
        Object object = getItem(position);
        CardItem cardItem;
        if (object instanceof BmobCost) {
            BmobCost bmobCost = (BmobCost) object;
            cardItem = new CardItem(mContext).getCardItem(bmobCost);

            holder.mTitleView.setText(cardItem.title);
            holder.mTotalPayView.setText(cardItem.info);
            holder.mHeaderView.setText(CardItem.getHeaderText(cardItem.header));
            holder.mHeaderView.setBackgroundDrawable(cardItem.getItemBackgroudRes());

        } else {
            Cost cost = (Cost) object;
            cardItem = new CardItem(mContext).getCardItem(cost);

            holder.mTitleView.setText(cardItem.title);
            holder.mTotalPayView.setText(cardItem.info);
            holder.mHeaderView.setText(CardItem.getHeaderText(cardItem.header));
            holder.mHeaderView.setBackgroundDrawable(cardItem.getItemBackgroudRes());
        }

        if (mOnScrollIdle) {
            showItemAnim(holder.itemView, position);
        }
    }

    @Override
    public int getItemCount() {
        return mLocalCostList.size() + mCostList.size();
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
        @BindView(R.id.commit_status)
        TextView mCommitStatus;
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
