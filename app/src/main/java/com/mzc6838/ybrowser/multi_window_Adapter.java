package com.mzc6838.ybrowser;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by mzc6838 on 2018/4/3.
 */

public class multi_window_Adapter extends RecyclerView.Adapter<multi_window_Adapter.ViewHolder> {

    private List<WindowInfo> windowList;
    private OnItemClickListener onItemClickListener;

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView siteIcon;
        TextView siteTitle;
        TextView siteUrl;
        ImageView closeWindow;

        public ViewHolder(View view){
            super(view);
            //TODO: 网站图标和标题的传入
            siteIcon = (ImageView) view.findViewById(R.id.windowIcon);
            siteTitle = (TextView) view.findViewById(R.id.windowTitle);
            siteUrl = (TextView) view.findViewById(R.id.windowUrl);
            closeWindow = (ImageView) view.findViewById(R.id.remove_window);

            closeWindow.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case (R.id.remove_window):{
                    MainActivity.removeWindow(getAdapterPosition());
                    break;
                }
            }
        }
    }

    public multi_window_Adapter(List<WindowInfo> _windowList){
        windowList = _windowList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.multi_window_list, parent, false);
        final ViewHolder _viewHolder = new ViewHolder(view);
        return _viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        WindowInfo windowInfo = windowList.get(position);
        holder.siteIcon.setImageBitmap(windowInfo.getWindowIcon());
        holder.siteTitle.setText(windowInfo.getWindowTitle());
        holder.siteUrl.setText(windowInfo.getWindowUrl());

        if(onItemClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(holder.itemView, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return windowList.size();
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
}
