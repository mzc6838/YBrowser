package com.mzc6838.ybrowser;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.BookmarkWithServer.BookmarkServer;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by mzc6838 on 2018/4/8.
 */

public class BookmarkActivity extends AppCompatActivity {

    private RecyclerView bookmarkList_recyclerview;
    private TextView bookhint;
    private static List<Bookmark> bookmarkList;
    private static Bookmark_Adapter bookmark_adapter;
    private PopupWindow popupWindow;
    private Toolbar toolbar;
    private SharedPreferences sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookmark_layout);

        init(savedInstanceState);
    }

    @SuppressLint("RestrictedApi")
    public void init(@Nullable Bundle savedInstanceState) {
        LitePal.getDatabase();

        bookmarkList_recyclerview = (RecyclerView) findViewById(R.id.bookmark_recyclerview);
        bookhint = (TextView) findViewById(R.id.bookmark_hint);
        toolbar = (Toolbar) findViewById(R.id.bookmark_toolbar);

        sp = getSharedPreferences("UserInfo", MODE_PRIVATE);

        bookmarkList = new ArrayList<>();

        bookmarkList = DataSupport.findAll(Bookmark.class);
        if (bookmarkList.isEmpty()) {
            bookhint.setVisibility(View.VISIBLE);
            bookmarkList_recyclerview.setVisibility(View.GONE);
        } else {
            bookhint.setVisibility(View.GONE);
            bookmarkList_recyclerview.setVisibility(View.VISIBLE);
        }
        bookmark_adapter = new Bookmark_Adapter(bookmarkList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        bookmarkList_recyclerview.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        bookmarkList_recyclerview.setAdapter(bookmark_adapter);
        bookmarkList_recyclerview.setItemAnimator(new DefaultItemAnimator());
        bookmarkList_recyclerview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        bookmark_adapter.setOnItemClickListener(new Bookmark_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent();
                intent.putExtra("title", bookmarkList.get(position).getTitle());
                intent.putExtra("url", bookmarkList.get(position).getUrl());
                setResult(777, intent);
                finish();
            }
        });

        bookmark_adapter.setOnItemLongClickListener(new Bookmark_Adapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, final int position) {
                PopupMenu popupMenu = new PopupMenu(BookmarkActivity.this, view);
                final MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.bookmark_item_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case (R.id.edit_bookmark): {
                                showPopupWindow(bookmarkList.get(position).getTitle(), bookmarkList.get(position).getUrl(), position);
                                return true;
                            }
                            case (R.id.delete_bookmark): {
                                DataSupport.deleteAll(Bookmark.class, "title = ? and url = ?",
                                        bookmarkList.get(position).getTitle(),
                                        bookmarkList.get(position).getUrl());
                                bookmarkList.remove(position);
                                bookmark_adapter.notifyDataSetChanged();
                                Toast.makeText(BookmarkActivity.this, "书签已删除", Toast.LENGTH_SHORT).show();
                                if (bookmarkList.isEmpty()) {
                                    bookhint.setVisibility(View.VISIBLE);
                                    bookmarkList_recyclerview.setVisibility(View.GONE);
                                }
                                return true;
                            }
                            case (R.id.open_in_new_window): {
                                Intent intent = new Intent();
                                intent.putExtra("title", bookmarkList.get(position).getTitle());
                                intent.putExtra("url", bookmarkList.get(position).getUrl());
                                setResult(778, intent);
                                finish();
                                return true;
                            }
                            default:
                                return false;
                        }
                    }
                });

                try {
                    Field field = popupMenu.getClass().getDeclaredField("mPopup");
                    field.setAccessible(true);
                    MenuPopupHelper menuPopupHelper = (MenuPopupHelper) field.get(popupMenu);
                    menuPopupHelper.setForceShowIcon(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                popupMenu.show();
                return true;
            }
        });

        toolbar.setTitle("书签");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case (R.id.upload_bookmark): {
                        if (!sp.getBoolean("ifLogin", false)) {
                            popAlertDialog();
                        } else {
                            Toast.makeText(BookmarkActivity.this, "上传中，请稍后", Toast.LENGTH_SHORT).show();
                            Gson gson = new Gson();
                            BookmarkServer bookmarkUpload = new BookmarkServer();
                            bookmarkUpload.setBookmark(bookmarkList);
                            String t = gson.toJson(bookmarkUpload);

                            String string = "";
                            string = string
                                    + "name=" + sp.getString("name", "") + "&"
                                    + "tooken=" + sp.getString("tooken", "") + "&"
                                    + "bookmark=" + t + "&";

                            Log.d("json:", string);

                            OkHttpClient okHttpClient = new OkHttpClient();
                            RequestBody requestBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded; charset=utf-8"), string);
                            Request request = new Request.Builder()
                                    .url("http://toothless.mzc6838.xyz/browser/uploadBookmark.php")
                                    .post(requestBody)
                                    .build();
                            Call call = okHttpClient.newCall(request);
                            call.enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {

                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    Gson res = new Gson();
                                    ErrorResponse errorResponse = res.fromJson(response.body().string(), ErrorResponse.class);
                                    if (errorResponse.getErrCode() == 12) {
                                        getSharedPreferences("UserInfo", MODE_PRIVATE).edit().putBoolean("ifLogin", false).apply();
                                        popTookenAlertDialog();
                                    } else {
                                        final Runnable runnable = new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    Toast.makeText(BookmarkActivity.this, "上传完成", Toast.LENGTH_SHORT).show();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        };
                                        new Thread() {
                                            @Override
                                            public void run() {
                                                new Handler(Looper.getMainLooper()).post(runnable);
                                            }
                                        }.start();
                                    }
                                }
                            });
                        }
                        return true;
                    }
                    case (R.id.download_bookmark): {
                        if (!sp.getBoolean("ifLogin", false)) {
                            popAlertDialog();
                        } else {
                            Toast.makeText(BookmarkActivity.this, "正在同步至本地, 请稍后", Toast.LENGTH_SHORT).show();
                            OkHttpClient okHttpClient = new OkHttpClient();
                            FormBody.Builder builder = new FormBody.Builder();
                            builder.add("name", sp.getString("name", ""));
                            builder.add("tooken", sp.getString("tooken", ""));
                            RequestBody requestBody = builder.build();
                            Request request = new Request.Builder()
                                    .url("http://toothless.mzc6838.xyz/browser/downloadBookmark.php")
                                    .post(requestBody)
                                    .build();
                            Call call = okHttpClient.newCall(request);
                            call.enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {

                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    Gson responseFromServer = new Gson();
                                    ErrorResponse errorResponse = responseFromServer.fromJson(response.body().string(), ErrorResponse.class);
                                    if (errorResponse.getErrCode() == 12) {
                                        getSharedPreferences("UserInfo", MODE_PRIVATE).edit().putBoolean("ifLogin", false).apply();
                                        popTookenAlertDialog();
                                    } else {
                                        Gson bookmarkFromServer = new Gson();
                                        BookmarkServer bookmarkServer = bookmarkFromServer.fromJson(errorResponse.getBody(), BookmarkServer.class);
                                        if (!bookmarkServer.getBookmark().isEmpty() && !bookmarkList.isEmpty()) {
                                            for (int i = 0; i < bookmarkServer.getBookmark().size(); i++) {
                                                int tag = 0;
                                                for (int j = 0; j < bookmarkList.size(); j++) {
                                                    if ((bookmarkList.get(j).getTitle().equals(bookmarkServer.getBookmark().get(i).getTitle()))
                                                            && (bookmarkList.get(j).getUrl().equals(bookmarkServer.getBookmark().get(i).getUrl()))) {
                                                        tag = 1;
                                                    }
                                                }
                                                if (tag != 1) {
                                                    Bookmark bookmark = new Bookmark();
                                                    bookmark.setTitle(bookmarkServer.getBookmark().get(i).getTitle());
                                                    bookmark.setUrl(bookmarkServer.getBookmark().get(i).getUrl());
                                                    bookmark.save();
                                                    bookmarkList.add(bookmark);
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            bookmark_adapter.notifyDataSetChanged();
                                                        }
                                                    });
                                                }
                                            }
                                        } else if (!bookmarkServer.getBookmark().isEmpty() && bookmarkList.isEmpty()) {
                                            Log.d("bookmarkServer", "isEmpty");
                                            for (int i = 0; i < bookmarkServer.getBookmark().size(); i++) {
                                                Bookmark bookmark = new Bookmark();
                                                bookmark.setTitle(bookmarkServer.getBookmark().get(i).getTitle());
                                                bookmark.setUrl(bookmarkServer.getBookmark().get(i).getUrl());
                                                bookmark.save();
                                                bookmarkList.add(bookmark);
                                            }
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    bookhint.setVisibility(View.GONE);
                                                    bookmarkList_recyclerview.setVisibility(View.VISIBLE);
                                                    bookmark_adapter.notifyDataSetChanged();
                                                }
                                            });
                                        } else if (bookmarkServer.getBookmark().isEmpty() && bookmarkList.isEmpty()) {

                                        } else if (bookmarkServer.getBookmark().isEmpty() && !bookmarkList.isEmpty()) {

                                        }
                                        final Runnable runnable = new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    Toast.makeText(BookmarkActivity.this, "同步完成", Toast.LENGTH_SHORT).show();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        };
                                        new Thread() {
                                            @Override
                                            public void run() {
                                                new Handler(Looper.getMainLooper()).post(runnable);
                                            }
                                        }.start();
                                    }
                                }
                            });
                        }
                        return true;
                    }
                    default:
                        return false;
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bookmark_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        if (menu != null) {
            if (menu.getClass() == MenuBuilder.class) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onPrepareOptionsPanel(view, menu);
    }

    public void showPopupWindow(final String title, final String url, final int position) {
        View contentView = LayoutInflater.from(BookmarkActivity.this).inflate(R.layout.bookmark_popwindow, null);
        popupWindow = new PopupWindow(contentView, bookmarkList_recyclerview.getWidth() - 100, LinearLayout.LayoutParams.WRAP_CONTENT, true);
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
        popupWindow.showAtLocation(rootview, Gravity.CENTER, 0, 0);
    }

    private void popAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BookmarkActivity.this);
        builder.setMessage("似乎你还没有登录，请先去登录")
                .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(BookmarkActivity.this, SettingActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("不用了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void popTookenAlertDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(BookmarkActivity.this);
        builder.setMessage("登录态已失效，请重新登录")
                .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(BookmarkActivity.this, SettingActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("不用了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        };
        new Thread(){
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(runnable);
            }
        }.start();

    }

}
