package com.xth.intelligentassistant.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xth.intelligentassistant.R;
import com.xth.intelligentassistant.util.Constant;
import com.xth.intelligentassistant.util.LogUtil;

import java.util.List;
import java.util.Map;

/**
 * Created by XTH on 2017/5/25.
 */

public class ListViewAdapter extends BaseAdapter {
    private List<Map<String, Object>> mData;
    private LayoutInflater layoutInflater;
    public ViewHolder viewHolder = null;

    public ListViewAdapter(Context context,List<Map<String, Object>> mData) {
        this.layoutInflater = LayoutInflater.from(context);
        this.mData = mData;
    }

    public class ViewHolder {
        public TextView senceText;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.swipe_menu_list_view_item, null);
            viewHolder.senceText = (TextView) convertView.findViewById(R.id.sence_text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.senceText.setText((String)mData.get(position).get(Constant.SWIPE_SENCE_KEY));
        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }
}
