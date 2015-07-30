package com.yuri.dreamlinkcost.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yuri.dreamlinkcost.Constant;
import com.yuri.dreamlinkcost.MainFragment;
import com.yuri.dreamlinkcost.R;
import com.yuri.dreamlinkcost.Utils;
import com.yuri.dreamlinkcost.interfaces.RecyclerViewClickListener;
import com.yuri.dreamlinkcost.log.Log;
import com.yuri.dreamlinkcost.model.Cost;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yuri on 2015/7/7.
 */
public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.ViewHolder> {

    private List<Cost> mCostList = new ArrayList<>();

    private MainFragment mainFragment;

    private RecyclerViewClickListener mListener;

    public CardViewAdapter(List<Cost> list, MainFragment mainFragment) {
        this.mCostList = list;
        this.mainFragment = mainFragment;
    }

    public void clearList() {
        Log.d();
        int size = mCostList.size();
        if (size <= 0) {
            return;
        }

        for (int i = 0; i < size; i++) {
            mCostList.remove(0);
        }

        this.notifyItemRangeRemoved(0, size);
    }

    public void addCostList(List<Cost> list) {
        for (int i = 0; i < list.size(); i++) {
            addItem(i, list.get(i));
        }
//        mCostList.addAll(list);
//        notifyItemRangeInserted(0, mCostList.size() - 1);
    }

    public void addItem(int position, Cost cost) {
        mCostList.add(position, cost);
        notifyItemInserted(position);
    }

    public void addItem(Cost cost) {
        addItem(0, cost);
    }

    public void remove(int position) {
        Log.d("position:" + position);
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

    public Cost getItem(int position) {
        Log.d("position:" + position);
        return mCostList.get(position);
    }

    public List<Cost> getCostList() {
        return this.mCostList;
    }

    @Override
    public CardViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_cardview, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
//        Log.d("position:" + position);
        final Cost cost = mCostList.get(position);
        viewHolder.titleView.setText(cost.title);
        viewHolder.totalPayView.setText("¥" + cost.totalPay);
        if (Constant.STATUS_COMMIT_SUCCESS == cost.status) {
            viewHolder.commitStatusView.setText("Commited");
            viewHolder.commitStatusView.setTextColor(Color.GREEN);
        } else {
            viewHolder.commitStatusView.setText("UnCommited");
            viewHolder.commitStatusView.setTextColor(Color.RED);
        }
        viewHolder.dateView.setText(Utils.getDate(cost.createDate));

        switch (cost.author) {
            case Constant.Author.LIUCHENG:
                viewHolder.headerView.setText("L");
                viewHolder.headerView.setBackgroundResource(R.drawable.round_liucheng);
                break;
            case Constant.Author.XIAOFEI:
                viewHolder.headerView.setText("X");
                viewHolder.headerView.setBackgroundResource(R.drawable.round_xiaofei);
                break;
            case  Constant.Author.YURI:
                viewHolder.headerView.setText("Y");
                viewHolder.headerView.setBackgroundResource(R.drawable.round_yuri);
                break;
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
    }

    @Override
    public int getItemCount() {
        return mCostList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView headerView;
        public TextView titleView, totalPayView, commitStatusView;
        public TextView dateView;

        public ViewHolder(View itemView) {
            super(itemView);
            headerView = (TextView) itemView.findViewById(R.id.headerView);
            titleView = (TextView) itemView.findViewById(R.id.titleView);
            totalPayView = (TextView) itemView.findViewById(R.id.totalPayView);
            commitStatusView = (TextView) itemView.findViewById(R.id.commit_status);
            dateView = (TextView) itemView.findViewById(R.id.dateView);
        }
    }
}
