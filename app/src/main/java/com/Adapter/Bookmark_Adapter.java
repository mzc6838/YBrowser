package com.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.BaseClass.Bookmark;
import com.mzc6838.ybrowser.R;

import java.util.List;

/**
 * Created by mzc6838 on 2018/4/8.
 */

public class Bookmark_Adapter extends RecyclerView.Adapter<Bookmark_Adapter.ViewHolder> {

    private List<Bookmark> bookmarkList;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView bookmarkTitle;
        TextView bookmarkUrl;

        public ViewHolder(View view){
            super(view);

            bookmarkTitle = (TextView) view.findViewById(R.id.bookmark_title);
            bookmarkUrl = (TextView) view.findViewById(R.id.bookmark_url);
        }
    }

    public Bookmark_Adapter(List<Bookmark> _bookmarkList){
        bookmarkList = _bookmarkList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Bookmark bookmark = bookmarkList.get(position);
        holder.bookmarkUrl.setText(bookmark.getUrl());
        holder.bookmarkTitle.setText(bookmark.getTitle());

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
        return bookmarkList.size();
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
