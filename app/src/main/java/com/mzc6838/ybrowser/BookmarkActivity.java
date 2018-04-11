package com.mzc6838.ybrowser;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mzc6838 on 2018/4/8.
 */

public class BookmarkActivity extends Activity {

    private RecyclerView bookmarkList_recyclerview;
    private TextView bookhint;
    private static List<Bookmark> bookmarkList;
    private static bookmark_Adapter bookmark_adapter;
    private PopupWindow popupWindow;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookmark_layout);

        init(savedInstanceState);
    }

    public void init(@Nullable Bundle savedInstanceState){
        LitePal.getDatabase();

        bookmarkList_recyclerview = (RecyclerView) findViewById(R.id.bookmark_recyclerview);
        bookhint = (TextView) findViewById(R.id.bookmark_hint);
        toolbar = (Toolbar) findViewById(R.id.bookmark_toolbar);

        bookmarkList = new ArrayList<>();

        bookmarkList = DataSupport.findAll(Bookmark.class);
        if(bookmarkList.isEmpty()){
            bookhint.setVisibility(View.VISIBLE);
            bookmarkList_recyclerview.setVisibility(View.GONE);
        }else{
            bookhint.setVisibility(View.GONE);
            bookmarkList_recyclerview.setVisibility(View.VISIBLE);

            bookmark_adapter = new bookmark_Adapter(bookmarkList);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            bookmarkList_recyclerview.setLayoutManager(linearLayoutManager);
            linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
            bookmarkList_recyclerview.setAdapter(bookmark_adapter);
            bookmarkList_recyclerview.setItemAnimator(new DefaultItemAnimator());
            bookmarkList_recyclerview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

            bookmark_adapter.setOnItemClickListener(new bookmark_Adapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    Intent intent = new Intent();
                    intent.putExtra("title", bookmarkList.get(position).getTitle());
                    intent.putExtra("url", bookmarkList.get(position).getUrl());
                    setResult(777, intent);
                    finish();
                }
            });

            bookmark_adapter.setOnItemLongClickListener(new bookmark_Adapter.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(View view, final int position) {
                    PopupMenu popupMenu = new PopupMenu(BookmarkActivity.this, view);
                    final MenuInflater inflater = popupMenu.getMenuInflater();
                    inflater.inflate(R.menu.bookmark_item_menu, popupMenu.getMenu());

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case(R.id.edit_bookmark):{
                                    showPopupWindow(bookmarkList.get(position).getTitle(), bookmarkList.get(position).getUrl(), position);
                                    return true;
                                }
                                case (R.id.delete_bookmark):{
                                    DataSupport.deleteAll(Bookmark.class, "title = ? and url = ?",
                                            bookmarkList.get(position).getTitle(),
                                            bookmarkList.get(position).getUrl());
                                    bookmarkList.remove(position);
                                    bookmark_adapter.notifyDataSetChanged();
                                    Toast.makeText(BookmarkActivity.this, "书签已删除", Toast.LENGTH_SHORT).show();
                                    return true;
                                }
                                case (R.id.open_in_new_window):{
                                    Intent intent = new Intent();
                                    intent.putExtra("title", bookmarkList.get(position).getTitle());
                                    intent.putExtra("url", bookmarkList.get(position).getUrl());
                                    setResult(778, intent);
                                    finish();
                                    return true;
                                }
                                default:return false;
                            }
                        }
                    });

                    popupMenu.show();
                    return true;
                }
            });
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void showPopupWindow(final String title, final String url, final int position){
        View contentView = LayoutInflater.from(BookmarkActivity.this).inflate(R.layout.bookmark_popwindow, null);
        popupWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.WRAP_CONTENT + 1200, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setContentView(contentView);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.4f;
        getWindow().setAttributes(lp);

        TextView windowTitle = (TextView) contentView.findViewById(R.id.bookmark_popwindow_title);
        final TextInputEditText title_edit = (TextInputEditText) contentView.findViewById(R.id.bookmark_popwindow_edit_title);
        final TextInputEditText url_edit = (TextInputEditText) contentView.findViewById(R.id.bookmark_popwindow_edit_url);
        Button checkInput = (Button) contentView.findViewById(R.id.check_bookmark);

        windowTitle.setText("编辑书签");
        title_edit.setText(title);
        url_edit.setText(url);

        checkInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bookmark bookmark = new Bookmark();
                bookmark.setUrl(url_edit.getText().toString());
                bookmark.setTitle(title_edit.getText().toString());
                bookmark.updateAll("title = ? and url = ?", title, url);
                bookmarkList.get(position).setTitle(title_edit.getText().toString());
                bookmarkList.get(position).setUrl(url_edit.getText().toString());
                bookmark_adapter.notifyDataSetChanged();
                Toast.makeText(BookmarkActivity.this, "书签已修改", Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });

        View rootview = LayoutInflater.from(BookmarkActivity.this).inflate(R.layout.bookmark_layout, null);
        popupWindow.showAtLocation(rootview, Gravity.CENTER, 0,0);
    }


}
