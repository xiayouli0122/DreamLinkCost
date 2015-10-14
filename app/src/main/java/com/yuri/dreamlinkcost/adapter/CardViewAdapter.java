package com.yuri.dreamlinkcost.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yuri.dreamlinkcost.BR;
import com.yuri.dreamlinkcost.Bmob.BmobCost;
import com.yuri.dreamlinkcost.R;
import com.yuri.dreamlinkcost.interfaces.RecyclerViewClickListener;
import com.yuri.dreamlinkcost.log.Log;
import com.yuri.dreamlinkcost.model.CardItem;
import com.yuri.dreamlinkcost.model.Cost;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.BindingHolder> {

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

    public void sortByPrice() {
        Collections.sort(mLocalCostList, Cost.PRICE_COMPARATOR);
        Collections.sort(mCostList, BmobCost.PRICE_COMPARATOR);
        notifyDataSetChanged();
    }

    public void sortByDate() {
        Collections.sort(mLocalCostList, Cost.DATE_COMPARATOR);
        Collections.sort(mCostList, BmobCost.DATE_COMPARATOR);
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

    /**获取网络列表*/
    public List<BmobCost> getCostList() {
        return this.mCostList;
    }

    /**获取本地列表*/
    public List<Cost> getLocalList() {
        return this.mLocalCostList;
    }

    @Override
    public CardViewAdapter.BindingHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
                R.layout.item_cardview,
                viewGroup,
                false);
        BindingHolder holder = new BindingHolder(binding.getRoot());
        holder.setBinding(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(BindingHolder viewHolder, final int position) {
        Object object = getItem(position);
        CardItem cardItem;
        if (object instanceof BmobCost) {
            BmobCost bmobCost = (BmobCost) object;
            cardItem = new CardItem(mContext).getCardItem(bmobCost);
            viewHolder.getBinding().setVariable(BR.cardItem, cardItem);
        } else {
            Cost cost = (Cost) object;
            cardItem = new CardItem(mContext).getCardItem(cost);
            viewHolder.getBinding().setVariable(BR.cardItem, cardItem);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClick(view, position);
                }
            }
        });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });
        viewHolder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mLocalCostList.size() + mCostList.size();
    }

    public static class BindingHolder extends RecyclerView.ViewHolder {
        private ViewDataBinding binding;

        public BindingHolder(View itemView) {
            super(itemView);
        }

        public ViewDataBinding getBinding() {
            return binding;
        }

        public void setBinding(ViewDataBinding binding) {
            this.binding = binding;
        }
    }
}
