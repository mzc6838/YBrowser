package com.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.BaseClass.History;
import com.mzc6838.ybrowser.R;

import java.util.List;

/**
 * Created by mzc6838 on 2018/4/11.
 */

public class History_Adapter extends RecyclerView.Adapter<History_Adapter.ViewHolder> {

    private List<History> historyList;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView history_title;
        private TextView history_url;

        public ViewHolder(View view){
            super(view);

            history_title = (TextView) view.findViewById(R.id.history_title);
            history_url = (TextView) view.findViewById(R.id.history_url);
        }

    }

    public History_Adapter(List<History> _history){
        historyList = _history;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        History history = historyList.get(position);
        holder.history_title.setText(history.getTitle());
        holder.history_url.setText(history.getUrl());

        if(onItemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(holder.itemView, position);
                }
            });
        }

        if(onItemLongClickListener != null){
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return onItemLongClickListener != null && onItemLongClickListener.onItemLongClick(holder.itemView, position);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener{
        boolean onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener){
        this.onItemLongClickListener = onItemLongClickListener;
    }
}
