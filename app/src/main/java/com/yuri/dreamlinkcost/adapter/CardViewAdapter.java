package com.yuri.dreamlinkcost.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yuri.dreamlinkcost.Bmob.BmobCost;
import com.yuri.dreamlinkcost.Constant;
import com.yuri.dreamlinkcost.R;
import com.yuri.dreamlinkcost.Utils;
import com.yuri.dreamlinkcost.interfaces.RecyclerViewClickListener;
import com.yuri.dreamlinkcost.log.Log;
import com.yuri.dreamlinkcost.model.Cost;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.ViewHolder> {

    private List<BmobCost> mCostList = new ArrayList<>();
    private List<Cost> mLocalCostList = new ArrayList<>();

    private RecyclerViewClickListener mListener;

    private DecimalFormat mDecimalFormat=new DecimalFormat(".00");

    public CardViewAdapter(List<Cost> localList, List<BmobCost> list) {
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
    public CardViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_cardview, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d("position:" + position);
        Object object = getItem(position);
        if (object instanceof BmobCost) {
            BmobCost bmobCost = (BmobCost) object;
            viewHolder.titleView.setText(bmobCost.title);
            Log.d("title:" + bmobCost.title);
            String detail = "L:" + (bmobCost.payLC == 0 ? bmobCost.payLC + "" : mDecimalFormat.format(bmobCost.payLC))
                    + ", X:" + (bmobCost.payXF == 0 ? bmobCost.payXF + "" : mDecimalFormat.format(bmobCost.payXF))
                    + ", Y:" + (bmobCost.payYuri == 0 ? bmobCost.payYuri + "" : mDecimalFormat.format(bmobCost.payYuri));
            viewHolder.totalPayView.setText("¥" + bmobCost.totalPay + "\n" + detail);

            viewHolder.commitStatusView.setText("Commited");
            viewHolder.commitStatusView.setTextColor(Color.GREEN);

            viewHolder.dateView.setText(Utils.getDate(bmobCost.createDate));

            switch (bmobCost.author) {
                case Constant.Author.LIUCHENG:
                    viewHolder.headerView.setText("L");
                    viewHolder.headerView.setBackgroundResource(R.drawable.round_liucheng);
                    break;
                case Constant.Author.XIAOFEI:
                    viewHolder.headerView.setText("X");
                    viewHolder.headerView.setBackgroundResource(R.drawable.round_xiaofei);
                    break;
                case Constant.Author.YURI:
                    viewHolder.headerView.setText("Y");
                    viewHolder.headerView.setBackgroundResource(R.drawable.round_yuri);
                    break;
            }
        } else {
            Cost cost = (Cost) object;
            viewHolder.titleView.setText(cost.title);

            String detail = "L:" + (cost.payLC == 0 ? cost.payLC + "" : mDecimalFormat.format(cost.payLC))
                    + ", X:" + (cost.payXF == 0 ? cost.payXF + "" : mDecimalFormat.format(cost.payXF))
                    + ", Y:" + (cost.payYuri == 0 ? cost.payYuri + "" : mDecimalFormat.format(cost.payYuri));
            viewHolder.totalPayView.setText("¥" + cost.totalPay + "\n" + detail);
            viewHolder.commitStatusView.setText("UnCommited");
            viewHolder.commitStatusView.setTextColor(Color.RED);
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
        return mLocalCostList.size() + mCostList.size();
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
