package com.yuri.dreamlinkcost.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yuri.dreamlinkcost.BR;
import com.yuri.dreamlinkcost.R;
import com.yuri.dreamlinkcost.model.User;

import java.util.ArrayList;
import java.util.List;

public class LeftMenuAdapter extends RecyclerView.Adapter<LeftMenuAdapter.BindingHolder>{

    public List<User> list = new ArrayList<>();

    public LeftMenuAdapter() {
        list.add(new User("All"));
        list.add(new User("LIU CHENG"));
        list.add(new User("XIAOFEI"));
        list.add(new User("YURI"));
    }

    private OnItemClickListener mListener;
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.left_menu_item,
                parent,
                false);
        BindingHolder holder = new BindingHolder(binding.getRoot());
        holder.setBinding(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(BindingHolder holder, final int position) {
        User user = list.get(position);
        holder.getBinding().setVariable(BR.user, user);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClick(position);
                }
            }
        });
        holder.getBinding().executePendingBindings();
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

    @Override
    public int getItemCount() {
        return list.size();
    }
}
