package com.app.sample.fchat.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.sample.fchat.R;
import com.app.sample.fchat.data.SettingsAPI;
import com.app.sample.fchat.model.ChatMessage;

import java.util.List;

public class ChatDetailsListAdapter extends BaseAdapter {

    private List<ChatMessage> mMessages;
    private Context mContext;
    SettingsAPI set;

    public ChatDetailsListAdapter(Context context, List<ChatMessage> messages) {
        super();
        this.mContext = context;
        this.mMessages = messages;
        set = new SettingsAPI(mContext);
    }

    @Override
    public int getCount() {
        return mMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return mMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage msg = (ChatMessage) getItem(position);
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.row_chat_details, parent, false);
            holder.time = (TextView) convertView.findViewById(R.id.text_time);
            holder.message = (TextView) convertView.findViewById(R.id.text_content);
            holder.lyt_thread = (CardView) convertView.findViewById(R.id.lyt_thread);
            holder.lyt_parent = (LinearLayout) convertView.findViewById(R.id.lyt_parent);
            holder.image_status = (ImageView) convertView.findViewById(R.id.image_status);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.message.setText(msg.getText());
        holder.time.setText(msg.getReadableTime());

        if (msg.getReceiver().getId().equals(set.readSetting("myid"))) {
            holder.lyt_parent.setPadding(15, 10, 100, 10);
            holder.lyt_parent.setGravity(Gravity.LEFT);
            holder.lyt_thread.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            holder.image_status.setImageResource(android.R.color.transparent);
        } else {
            holder.lyt_parent.setPadding(100, 10, 15, 10);
            holder.lyt_parent.setGravity(Gravity.RIGHT);
            holder.lyt_thread.setCardBackgroundColor(mContext.getResources().getColor(R.color.me_chat_bg));
            holder.image_status.setImageResource(R.drawable.baseline_done_24);
            holder.image_status.setColorFilter(ContextCompat.getColor(mContext, android.R.color.darker_gray), android.graphics.PorterDuff.Mode.MULTIPLY);

            if (msg.isRead()) {
                holder.image_status.setImageResource(R.drawable.baseline_done_all_24);
                holder.image_status.setColorFilter(ContextCompat.getColor(mContext, android.R.color.holo_blue_dark), android.graphics.PorterDuff.Mode.MULTIPLY);
            }
        }
        return convertView;
    }

    /**
     * remove data item from messageAdapter
     **/
    public void remove(int position) {
        mMessages.remove(position);
    }

    /**
     * add data item to messageAdapter
     **/
    public void add(ChatMessage msg) {
        mMessages.add(msg);
    }

    private static class ViewHolder {
        TextView time;
        TextView message;
        LinearLayout lyt_parent;
        CardView lyt_thread;
        ImageView image_status;
    }
}
