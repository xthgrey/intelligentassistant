package com.xth.intelligentassistant.main;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

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

public class ExpandableListFragment extends Fragment implements ExpandableListView.OnGroupClickListener, ExpandableListView.OnChildClickListener {

    private int groupPosition;

    private Context context;
    private Map<String, Object> expandMenuItemMap;//子项列表内容
    private List<Map<String, Object>> groupList;//父项列表
    private ArrayList<List<Map<String, Object>>> groupChildLists;//子项列表
    private MyBaseExpandableListAdapter adapter;
    private ExpandableListView expandableListView;

    private PopupList popupList;
    private List<String> popupMenuItemList;

    public void setGroupList(List<Map<String, Object>> groupList) {
        this.groupList = groupList;
    }

    public List<Map<String, Object>> getGroupList() {
        return groupList;
    }

    public int getGroupPosition() {
        return groupPosition;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        groupChildLists = new ArrayList<>();
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
                } else {
                    if (flag) {
                        break;//检测到父项列出子项之后直接退出for循环
                    }
                }
            }
            groupChildLists.add(expandMenuItemList);
        }
    }

    private void setPopupListView() {
        popupMenuItemList = new ArrayList<>();
        popupMenuItemList.add(getString(R.string.update));
        popupMenuItemList.add(getString(R.string.delete));
        popupList = new PopupList(context);
        popupList.setTextSize(popupList.dp2px(30));
        popupList.bind(expandableListView, popupMenuItemList, new PopupList.PopupListListener() {
            @Override
            public boolean showPopupList(View adapterView, View contextView, int groupPos, int childPos) {
                return true;
            }

            @Override
            public void onPopupListClick(View contextView, int groupPos, int childPos, int position) {
                switch (position) {
                    case Constant.MODIFY://修改
                        expandMenuItemMap = groupChildLists.get(groupPos).get(childPos);
                        editName(expandMenuItemMap, groupPos, childPos);
                        break;
                    case Constant.DELETE://删除
                        OperateDB.deleteName((String) groupList.get(groupPos).get(Constant.SWIPE_SENCE_KEY), (String) groupChildLists.get(groupPos).get(childPos).get(Constant.SWIPE_DIVICE_KEY));
                        groupChildLists.get(groupPos).remove(childPos);
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        });
    }

    private void editName(final Map<String, Object> expandMenuItemMap, final int groupPos, final int childPos) {
        View view = View.inflate(context, R.layout.alert_dialog_layout, null);
        final EditText alertDialogEdit = (EditText) view.findViewById(R.id.alert_dialog_edit);
        final String oldName = (String) expandMenuItemMap.get(Constant.SWIPE_DIVICE_KEY);
        LogUtil.d(groupPos+":::::"+childPos+"::"+expandMenuItemMap.get(Constant.SWIPE_DIVICE_KEY));
        alertDialogEdit.setText(oldName);
        alertDialogEdit.setSelection(oldName.length());//将光标移至文字末尾
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(Constant.EDIT_DEVICE_NAME);//修改设备名称
        builder.setPositiveButton(Constant.CONFIRM, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String s = alertDialogEdit.getText().toString();
                s = s.replaceAll("\\s", "");
                if(s.equals(oldName)){

                }else if (!"".equals(s)) {
                    if (OperateDB.isHaveInDB((String) groupList.get(groupPosition).get(Constant.SWIPE_SENCE_KEY), s)!=null) {
                        Toast.makeText(context, Constant.ERROR_DEVICE_NAME, Toast.LENGTH_SHORT).show();
                    } else {
                        OperateDB.updateName(new Device(), (String) groupList.get(groupPos).get(Constant.SWIPE_SENCE_KEY), oldName, s);//修改弹窗
                        expandMenuItemMap.put(Constant.SWIPE_DIVICE_KEY, s);
                        List<Map<String, Object>> expandMenuItemList = groupChildLists.get(groupPos);
                        expandMenuItemList.set(childPos, expandMenuItemMap);//子项列表中的子列表内容更新
                        groupChildLists.set(groupPos, expandMenuItemList);//子项列表更新其子列表
                        for (List<Map<String, Object>> list : groupChildLists) {
                            for (Map<String, Object> map : list) {
                                LogUtil.d((String) map.get(Constant.SWIPE_DIVICE_KEY));
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(context, Constant.ERROR_EMPTY_NAME, Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton(Constant.CANCEL, null);
        AlertDialog tempDialog = builder.create();
        tempDialog.setView(view, 0, 0, 0, 0);
        /** 3.自动弹出软键盘 **/
        tempDialog.setOnShowListener(new AlertDialog.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(alertDialogEdit, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        tempDialog.show();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.expand_list_view, container, false);
        expandableListView = (ExpandableListView) view.findViewById(R.id.main_expand_list_view);
        adapter = new MyBaseExpandableListAdapter(context, groupList, groupChildLists);
        expandableListView.setAdapter(adapter);
        expandableListView.setOnGroupClickListener(this);
        expandableListView.setOnChildClickListener(this);
        setPopupListView();
        return view;
    }

    //主列表为空，不能选择或者张开主列表；子列表应该区分开
    public void expandListViewAddItem(String key, Object value) {
        OperateDB.addName(new Device(), (String) groupList.get(groupPosition).get(Constant.SWIPE_SENCE_KEY), (String) value);
        expandMenuItemMap = new HashMap<String, Object>();
        expandMenuItemMap.put(key, value);
        groupChildLists.get(groupPosition).add(expandMenuItemMap);//子项列表添加内容

        adapter.notifyDataSetChanged();
        expandableListView.expandGroup(groupPosition);
        expandableListView.smoothScrollToPosition(groupPosition);
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        this.groupPosition = groupPosition;//获取父项的id
        return false;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        //groupPosition，childPosition，id 由0开始，childPosition和id值一样
        LogUtil.d("onChildClick" + groupPosition + "," + childPosition + "," + id);
        return false;
    }
}
