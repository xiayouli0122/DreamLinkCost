package com.yuri.dreamlinkcost.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yuri.dreamlinkcost.R;

public class LeftMenuAdapter extends RecyclerView.Adapter<LeftMenuAdapter.ViewHolder>{

    private OnItemClickListener mListener;
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public LeftMenuAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.left_menu_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(LeftMenuAdapter.ViewHolder holder, final int position) {
        switch (position) {
            case 0:
                holder.textView.setText("ALL");
                break;
            case 1:
                holder.textView.setText("LIU CHENG");
                break;
            case 2:
                holder.textView.setText("XIAO FEI");
                break;
            case 3:
                holder.textView.setText("YURI");
                break;
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClick(position);
                }
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.left_menu_textview);
        }
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return 4;
    }
}
