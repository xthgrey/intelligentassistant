package com.xth.intelligentassistant.Dialogue;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xth.intelligentassistant.R;
import com.xth.intelligentassistant.util.LogUtil;

import java.util.List;

/**
 * Created by XTH on 2017/5/15.
 */

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {
    private List<Msg> mMsgList;

    static class ViewHolder extends RecyclerView.ViewHolder{

        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView leftMsg;
        TextView rightMsg;

        public ViewHolder(View itemView) {
            super(itemView);
            LogUtil.d("MsgAdapter：ViewHolder：开始创建适配器");
            leftLayout = (LinearLayout)itemView.findViewById(R.id.dialogue_layout_left_layout);
            rightLayout = (LinearLayout)itemView.findViewById(R.id.dialogue_layout_right_layout);
            leftMsg = (TextView)itemView.findViewById(R.id.dialogue_layout_left_msg);
            rightMsg = (TextView)itemView.findViewById(R.id.dialogue_layout_right_msg);
        }
    }

    public MsgAdapter(List<Msg> mMsgList) {
        LogUtil.d(getClass().getName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + " : 获取表");
        this.mMsgList = mMsgList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LogUtil.d(getClass().getName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + " : ");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialogue_layout_msg_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LogUtil.d(getClass().getName() + "---" + new Throwable().getStackTrace()[0].getMethodName() + " : ");
        Msg msg = mMsgList.get(position);
        if (msg.getType() == Msg.TYPE_RECEIVED){//消息是接收的
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftMsg.setText(msg.getContent());//设置消息内容
        }else if (msg.getType() == Msg.TYPE_SENT){//消息是发送的
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightMsg.setText(msg.getContent());//设置消息内容
        }
    }

    @Override
    public int getItemCount() {
        return mMsgList.size();
    }
}
