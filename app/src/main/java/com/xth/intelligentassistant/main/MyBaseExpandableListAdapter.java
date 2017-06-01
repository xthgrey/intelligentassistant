package com.xth.intelligentassistant.main;

import android.content.ClipData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xth.intelligentassistant.R;
import com.xth.intelligentassistant.util.Constant;
import com.xth.intelligentassistant.util.LogUtil;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by XTH on 2017/5/31.
 */

public class MyBaseExpandableListAdapter extends BaseExpandableListAdapter {
    private List<Map<String, Object>> gData;
    private ArrayList<List<Map<String, Object>>> iData;
    private Context mContext;


    public MyBaseExpandableListAdapter(Context mContext, List<Map<String, Object>> gData, ArrayList<List<Map<String, Object>>> iData) {
        this.gData = gData;
        this.iData = iData;
        this.mContext = mContext;
//        for (Map<String, Object> map : gData) {
//            for (String key : map.keySet()) {
//                LogUtil.d("key= " + key + " and value= " + map.get(key));
//            }
//        }
//        for (List<Map<String, Object>> lists : iData){
//            for (Map<String, Object> map : lists) {
//                for (String key : map.keySet()) {
//                    LogUtil.d("key= " + key + " and value= " + map.get(key));
//                }
//            }
//        }
    }

    @Override
    public int getGroupCount() {
        return gData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return iData.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return gData.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return iData.get(groupPosition).get(childPosition);
    }


    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolderGroup groupHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.expand_list_view_group, parent, false);
            groupHolder = new ViewHolderGroup();
            groupHolder.expandListViewGroupText = (TextView) convertView.findViewById(R.id.expand_list_view_group_name);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (ViewHolderGroup) convertView.getTag();
        }
        groupHolder.expandListViewGroupText.setText((String)gData.get(groupPosition).get(Constant.SWIPE_SENCE_KEY));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolderItem itemHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.expand_list_view_group_child, parent, false);
            itemHolder = new ViewHolderItem();
            itemHolder.expandListViewGroupChildText = (TextView) convertView.findViewById(R.id.expand_list_view_group_child_name);
            convertView.setTag(itemHolder);
        } else {
            itemHolder = (ViewHolderItem) convertView.getTag();
        }
        itemHolder.expandListViewGroupChildText.setText((String)iData.get(groupPosition).get(childPosition).get(Constant.SWIPE_DIVICE_KEY));
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private static class ViewHolderGroup {
        private TextView expandListViewGroupText;
    }

    private static class ViewHolderItem {
        private TextView expandListViewGroupChildText;
    }
}
