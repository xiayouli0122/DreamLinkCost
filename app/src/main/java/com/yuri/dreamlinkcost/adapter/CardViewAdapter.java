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
import com.yuri.dreamlinkcost.model.Cost;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yuri on 2015/7/7.
 */
public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.ViewHolder> {

    private List<Cost> mCostList = new ArrayList<>();

    private MainFragment mainFragment;

    public CardViewAdapter(List<Cost> list, MainFragment mainFragment) {
        this.mCostList = list;
        this.mainFragment = mainFragment;
    }

    public void setmCostList(List<Cost> list) {
        mCostList = list;
    }

    public void addCostList(List<Cost> list) {
        mCostList.addAll(list);
        mCostList.addAll(list);
        mCostList.addAll(list);
        mCostList.addAll(list);
        mCostList.addAll(list);
        mCostList.addAll(list);
        mCostList.addAll(list);
        notifyItemRangeInserted(0, list.size() - 1);
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
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
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
                mainFragment.checkItem(cost);
            }
        });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (cost.status == Constant.STATUS_COMMIT_FAILURE) {
                    mainFragment.doCommit(cost.getId());
                    return true;
                }
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
