package com.xth.intelligentassistant.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

import com.xth.intelligentassistant.R;
import com.xth.intelligentassistant.db.Device;
import com.xth.intelligentassistant.db.OperateDB;
import com.xth.intelligentassistant.db.Sence;
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

public class ExpandableListFragment extends Fragment implements ExpandableListView.OnGroupClickListener{

    private int groupPosition;

    private Context context;
    private Map<String,Object> expandMenuItemMap;
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
        LogUtil.e(groupList.size()+"");
        for(int i=0;i<groupList.size();i++){
            List<Map<String, Object>> expandMenuItemList = new ArrayList<>();
            groupChildLists.add(expandMenuItemList);
            List<Device> deviceList = DataSupport.findAll(Device.class);

            for (Device sence:deviceList){
                if(groupList.get(i).equals(sence.getSenceName())){
                    expandMenuItemMap = new HashMap<String, Object>();
                    expandMenuItemMap.put(Constant.SWIPE_DIVICE_KEY, sence.getDeviceName());
                    expandMenuItemList.add(expandMenuItemMap);//子项列表添加内容
                }
            }
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.d("onCreateView");
        View view = inflater.inflate(R.layout.expand_list_view,container,false);
        expandableListView = (ExpandableListView)view.findViewById(R.id.main_expand_list_view);
        adapter = new MyBaseExpandableListAdapter(context,groupList,groupChildLists);
        expandableListView.setAdapter(adapter);
        expandableListView.setOnGroupClickListener(this);
        return view;
    }
    //主列表为空，不能选择或者张开主列表；子列表应该区分开
    public void expandListViewAddItem(String key,Object value) {
        expandMenuItemMap = new HashMap<String, Object>();
        expandMenuItemMap.put(key, value);
        groupChildLists.get(groupPosition).add(expandMenuItemMap);//子项列表添加内容

        OperateDB.addName(new Device(),(String)value, (String) groupList.get(groupPosition).get(key));

        adapter.notifyDataSetChanged();
        expandableListView.expandGroup(groupPosition);
        expandableListView.smoothScrollToPosition(groupPosition);
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        this.groupPosition = groupPosition;
        return false;
    }
}
