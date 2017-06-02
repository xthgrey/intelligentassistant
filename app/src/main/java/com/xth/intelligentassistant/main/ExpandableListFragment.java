package com.xth.intelligentassistant.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.xth.intelligentassistant.R;
import com.xth.intelligentassistant.db.Device;
import com.xth.intelligentassistant.db.OperateDB;
import com.xth.intelligentassistant.util.Constant;
import com.xth.intelligentassistant.util.LogUtil;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by XTH on 2017/5/31.
 */

public class ExpandableListFragment extends Fragment implements ExpandableListView.OnGroupClickListener {

    private int groupPosition;

    private Context context;
    private Map<String, Object> expandMenuItemMap;
    private List<Map<String, Object>> groupList;
    private ArrayList<List<Map<String, Object>>> groupChildLists;
    private MyBaseExpandableListAdapter adapter;
    private ExpandableListView expandableListView;


    public void setGroupList(List<Map<String, Object>> groupList) {
        this.groupList = groupList;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        groupChildLists = new ArrayList<>();
        LogUtil.e(groupList.size() + "");
        for (int i = 0; i < groupList.size(); i++) {
            List<Map<String, Object>> expandMenuItemList = new ArrayList<>();
            List<Device> deviceList = DataSupport.findAll(Device.class);
            boolean flag = false;
            for (Device device : deviceList) {
                if (groupList.get(i).get(Constant.SWIPE_SENCE_KEY).toString().equals(device.getSenceName())) {
                    expandMenuItemMap = new HashMap<String, Object>();
                    expandMenuItemMap.put(Constant.SWIPE_DIVICE_KEY, device.getDeviceName());
                    expandMenuItemList.add(expandMenuItemMap);//子项列表添加内容
                    flag = true;
                }else {
                    if(flag){
                        break;//检测到父项列出子项之后直接退出for循环
                    }
                }
            }
            groupChildLists.add(expandMenuItemList);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.expand_list_view, container, false);
        expandableListView = (ExpandableListView) view.findViewById(R.id.main_expand_list_view);
        adapter = new MyBaseExpandableListAdapter(context, groupList, groupChildLists);
        expandableListView.setAdapter(adapter);
        expandableListView.setOnGroupClickListener(this);
        return view;
    }

    //主列表为空，不能选择或者张开主列表；子列表应该区分开
    public void expandListViewAddItem(String key, Object value) {
        OperateDB.addName(new Device(), (String) groupList.get(groupPosition).get(Constant.SWIPE_SENCE_KEY), (String) value);
        expandMenuItemMap = new HashMap<String, Object>();
        expandMenuItemMap.put(key, value);
        groupChildLists.get(groupPosition).add(expandMenuItemMap);//子项列表添加内容


        LogUtil.i((String) groupList.get(groupPosition).get(Constant.SWIPE_SENCE_KEY));

        adapter.notifyDataSetChanged();
        expandableListView.expandGroup(groupPosition);
        expandableListView.smoothScrollToPosition(groupPosition);
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        this.groupPosition = groupPosition;//获取父项的id
        return false;
    }
}
