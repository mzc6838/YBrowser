package com.mzc6838.ybrowser;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.Adapter.History_Adapter;
import com.BaseClass.History;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mzc6838 on 2018/4/11.
 */

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView historyRecyclerView;
    private History_Adapter history_adapter;
    private Toolbar history_toolbar;

    private static List<History> historyList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_layout);

        init(savedInstanceState);
    }

    @SuppressLint("RestrictedApi")
    public void init(@Nullable Bundle s){
        LitePal.getDatabase();

        historyRecyclerView = (RecyclerView) findViewById(R.id.history_recyclerview);
        history_toolbar = (Toolbar) findViewById(R.id.history_toolbar);

        historyList = new ArrayList<>();

        historyList = DataSupport
                .select("title", "url", "addTime")
                .order("id desc")
                .find(History.class);
        if(historyList.isEmpty()){
            ((TextView) findViewById(R.id.history_hint)).setVisibility(View.VISIBLE);
            historyRecyclerView.setVisibility(View.GONE);
        }else{
            ((TextView) findViewById(R.id.history_hint)).setVisibility(View.GONE);
            historyRecyclerView.setVisibility(View.VISIBLE);
        }

        history_adapter = new History_Adapter(historyList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        historyRecyclerView.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        historyRecyclerView.setAdapter(history_adapter);
        historyRecyclerView.setItemAnimator(new DefaultItemAnimator());
        historyRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        history_toolbar.setTitle("历史记录");
        setSupportActionBar(history_toolbar);
        history_toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case(R.id.delete_all_history):{
                        popAlertDialog();
                        break;
                    }
                }
                return true;
            }
        });
        history_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        history_adapter.setOnItemClickListener(new History_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent();
                intent.putExtra("title", historyList.get(position).getTitle());
                intent.putExtra("url", historyList.get(position).getUrl());
                setResult(777, intent);
                finish();
            }
        });
        history_adapter.setOnItemLongClickListener(new History_Adapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, final int position) {
                PopupMenu popupMenu = new PopupMenu(HistoryActivity.this, view);
                final MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.history_item_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case (R.id.delete_history):{
                                DataSupport.deleteAll(History.class, "title = ? and url = ? and addTime = ?",
                                        historyList.get((position)).getTitle(),
                                        historyList.get(position).getUrl(),
                                        historyList.get(position).getAddTime());
                                historyList.remove(position);
                                history_adapter.notifyDataSetChanged();
                                Toast.makeText(HistoryActivity.this, "该历史已被删除", Toast.LENGTH_SHORT).show();
                                return true;
                            }
                            case (R.id.history_open_in_new_window):{
                                Intent intent = new Intent();
                                intent.putExtra("title", historyList.get(position).getTitle());
                                intent.putExtra("url", historyList.get(position).getUrl());
                                setResult(778,intent);
                                finish();
                                return true;
                            }
                            default:return false;
                        }
                    }
                });
                try{
                    Field field = popupMenu.getClass().getDeclaredField("mPopup");
                    field.setAccessible(true);
                    MenuPopupHelper menuPopupHelper = (MenuPopupHelper) field.get(popupMenu);
                    menuPopupHelper.setForceShowIcon(true);
                }catch (Exception e){
                    e.printStackTrace();
                }
                popupMenu.show();
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.history_deleteall, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void removeAllHistory(){
        DataSupport.deleteAll(History.class);
        historyList.clear();
        history_adapter.notifyDataSetChanged();
        ((TextView) findViewById(R.id.history_hint)).setVisibility(View.VISIBLE);
        historyRecyclerView.setVisibility(View.GONE);
    }

    public void popAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(HistoryActivity.this);
        builder.setTitle("你确定吗？")
                .setMessage("你将要删除所有的历史记录，这个操作不可逆。你确定要删除所有的历史记录吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeAllHistory();
                        Toast.makeText(HistoryActivity.this, "已清除浏览历史", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
