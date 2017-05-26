package com.xth.intelligentassistant.main;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.xth.intelligentassistant.R;
import com.xth.intelligentassistant.util.Constant;
import com.xth.intelligentassistant.util.LogUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * Created by XTH on 2017/5/25.
 */

public class SwipeMenuListFragment extends Fragment implements SwipeMenuListView.OnMenuItemClickListener,SwipeMenuListView.OnSwipeListener,SwipeMenuListView.OnItemLongClickListener{
    private Context context;
    private SwipeMenuListView swipeMenuListView;

    private SwipeMenuCreator creator;
    private String sence;
    private List<Map<String, Object>> swipeMenuItemList;
    private Map<String,Object> swipeMenuItemMap;
    private  ListViewAdapter adapter;

    public String getSence() {
        return sence;
    }

    public void setSence(String sence) {
        this.sence = sence;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        swipeMenuItemList = new ArrayList<Map<String, Object>>();
    }
    public void swipeViewAddItem(String key,Object value) {
        swipeMenuItemMap = new HashMap<String, Object>();
        swipeMenuItemMap.put(key, value);
        swipeMenuItemList.add(swipeMenuItemMap);

        adapter.notifyDataSetChanged();
        swipeMenuListView.smoothScrollToPosition(swipeMenuItemList.size() - 1);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.swipe_menu_list_view, container, false);
        creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(context);
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                openItem.setWidth(dp2px(90));
                // set item title
                openItem.setTitle(Constant.MODIFYTXT);
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        context);
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.drawable.list_view_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        swipeMenuListView = (SwipeMenuListView)view.findViewById(R.id.main_swipe_menu_list_view);
        swipeMenuListView.setMenuCreator(creator);
        // Right
        swipeMenuListView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        //设置选项子菜单点击监听
        swipeMenuListView.setOnMenuItemClickListener(this);
        //设置滑动监听
        swipeMenuListView.setOnSwipeListener(this);
        //设置选项长按监听
        swipeMenuListView.setOnItemLongClickListener(this);

        adapter = new ListViewAdapter(context,swipeMenuItemList);
        swipeMenuListView.setAdapter(adapter);
        return view;
    }
    private int dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    @Override
    public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
        switch (index){
            case Constant.MODIFY:
                swipeMenuItemMap = swipeMenuItemList.get(position);
                editName(swipeMenuItemMap);
                break;
            case Constant.DELETE:
                LogUtil.d("delete"+position +"==="+index);
                swipeMenuItemList.remove(position);
                adapter.notifyDataSetChanged();
                break;
        }
        return false;
    }
    private void editName(final Map<String,Object> swipeMenuItemMap){
        final EditText et = new EditText(context);
        et.setText((String)swipeMenuItemMap.get(Constant.SWIPE_SENCE_KEY));
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("修改场景名称");
        builder.setView(et);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String s = et.getText().toString();
                s = s.replaceAll("\\s", "");
                if (!"".equals(s)) {
                    swipeMenuItemMap.put(Constant.SWIPE_SENCE_KEY,s);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    @Override
    public void onSwipeStart(int position) {
        LogUtil.d("onSwipeStart");
    }

    @Override
    public void onSwipeEnd(int position) {
        LogUtil.d("onSwipeEnd");
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        return true;
    }
}
