package com.xth.intelligentassistant.dialogue;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xth.intelligentassistant.R;
import com.xth.intelligentassistant.gson.TuringAnalyze;
import com.xth.intelligentassistant.util.Constant;
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

        LinearLayout turingList1;
        TextView turingArticle1;
        TextView turingSource1;
        TextView turingIcon11;
        WebView turingDetailUrl1;
        LinearLayout turingList2;
        TextView turingArticle2;
        TextView turingSource2;
        TextView turingIcon12;
        WebView turingDetailUrl2;
        LinearLayout turingList3;
        TextView turingArticle3;
        TextView turingSource3;
        TextView turingIcon13;
        WebView turingDetailUrl3;
        LinearLayout turingCookBook;
        TextView turingName;
        TextView turingIcon;
        TextView turingInfo;
        WebView turingDetailUrl;

        public ViewHolder(View itemView) {
            super(itemView);
            leftLayout = (LinearLayout)itemView.findViewById(R.id.dialogue_layout_left_layout);
            rightLayout = (LinearLayout)itemView.findViewById(R.id.dialogue_layout_right_layout);
            leftMsg = (TextView)itemView.findViewById(R.id.dialogue_layout_left_msg);
            rightMsg = (TextView)itemView.findViewById(R.id.dialogue_layout_right_msg);

            turingList1 = (LinearLayout)itemView.findViewById(R.id.dialogue_layout_list1);
            turingArticle1 = (TextView)itemView.findViewById(R.id.dialogue_layout_article1);
            turingSource1 = (TextView)itemView.findViewById(R.id.dialogue_layout_source1);
            turingIcon11= (TextView)itemView.findViewById(R.id.dialogue_layout_icon1);
            turingDetailUrl1 = (WebView)itemView.findViewById(R.id.dialogue_layout_detailurl1);

            turingList2 = (LinearLayout)itemView.findViewById(R.id.dialogue_layout_list2);
            turingArticle2 = (TextView)itemView.findViewById(R.id.dialogue_layout_article2);
            turingSource2 = (TextView)itemView.findViewById(R.id.dialogue_layout_source2);
            turingIcon12= (TextView)itemView.findViewById(R.id.dialogue_layout_icon2);
            turingDetailUrl2 = (WebView)itemView.findViewById(R.id.dialogue_layout_detailurl2);

            turingList3 = (LinearLayout)itemView.findViewById(R.id.dialogue_layout_list3);
            turingArticle3 = (TextView)itemView.findViewById(R.id.dialogue_layout_article3);
            turingSource3 = (TextView)itemView.findViewById(R.id.dialogue_layout_source3);
            turingIcon13 = (TextView)itemView.findViewById(R.id.dialogue_layout_icon3);
            turingDetailUrl3 = (WebView)itemView.findViewById(R.id.dialogue_layout_detailurl3);

            turingCookBook = (LinearLayout)itemView.findViewById(R.id.dialogue_layout_cookbook);
            turingName = (TextView)itemView.findViewById(R.id.dialogue_layout_name);
            turingIcon = (TextView)itemView.findViewById(R.id.dialogue_layout_icon);
            turingInfo = (TextView)itemView.findViewById(R.id.dialogue_layout_info);
            turingDetailUrl = (WebView)itemView.findViewById(R.id.dialogue_layout_detailurl);

        }
        public void IsShowView(){
            switch (TuringAnalyze.code){
                case Constant.TURING_COOKBOOK:
                    turingList1.setVisibility(View.GONE);
                    turingList2.setVisibility(View.GONE);
                    turingList3.setVisibility(View.GONE);

                    turingCookBook.setVisibility(View.VISIBLE);
                    turingName.setText(TuringAnalyze.cookBook.list[0].name);
                    turingIcon.setText(TuringAnalyze.cookBook.list[0].icon);
                    turingInfo.setText(TuringAnalyze.cookBook.list[0].info);
                    turingDetailUrl.loadUrl(TuringAnalyze.cookBook.list[0].detailurl);
                    break;
                case Constant.TURING_LINK:
                    turingList1.setVisibility(View.GONE);
                    turingList2.setVisibility(View.GONE);
                    turingList3.setVisibility(View.GONE);
                    turingCookBook.setVisibility(View.GONE);
                    break;
                case Constant.TURING_NEWS:
                    turingCookBook.setVisibility(View.GONE);

                    turingList1.setVisibility(View.VISIBLE);
                    turingArticle1.setText(TuringAnalyze.news.list[0].article);
                    turingSource1.setText(TuringAnalyze.news.list[0].source);
                    turingIcon11.setText(TuringAnalyze.news.list[0].icon);
                    turingDetailUrl1.loadUrl(TuringAnalyze.news.list[0].detailurl);
                    turingList2.setVisibility(View.VISIBLE);
                    turingArticle1.setText(TuringAnalyze.news.list[1].article);
                    turingSource1.setText(TuringAnalyze.news.list[1].source);
                    turingIcon11.setText(TuringAnalyze.news.list[1].icon);
                    turingDetailUrl1.loadUrl(TuringAnalyze.news.list[1].detailurl);
                    turingList3.setVisibility(View.VISIBLE);
                    turingArticle1.setText(TuringAnalyze.news.list[2].article);
                    turingSource1.setText(TuringAnalyze.news.list[2].source);
                    turingIcon11.setText(TuringAnalyze.news.list[2].icon);
                    turingDetailUrl1.loadUrl(TuringAnalyze.news.list[2].detailurl);
                    break;
                default:
                    turingList1.setVisibility(View.GONE);
                    turingList2.setVisibility(View.GONE);
                    turingList3.setVisibility(View.GONE);
                    turingCookBook.setVisibility(View.GONE);
                    break;
            }
        }
    }

    public MsgAdapter(List<Msg> mMsgList) {
        this.mMsgList = mMsgList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialogue_layout_msg_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Msg msg = mMsgList.get(position);
        if (msg.getType() == Msg.TYPE_RECEIVED){//消息是接收的
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftMsg.setText(msg.getContent());//设置消息内容
            holder.IsShowView();
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
