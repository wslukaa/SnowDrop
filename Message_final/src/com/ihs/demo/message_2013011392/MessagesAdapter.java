package com.ihs.demo.message_2013011392;

import java.text.DateFormat;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ihs.message_2013011392.R;
import com.ihs.message_2013011392.types.HSTextMessage;
import com.ihs.message_2013011392.types.HSBaseMessage;
import com.ihs.message_2013011392.types.HSImageMessage;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MessagesAdapter extends ArrayAdapter<MessagesItem> {

    private List<MessagesItem> messages;
    private Context context;

    DisplayImageOptions options;

    private class ViewHolder {
        ImageView avatarImageView;
        TextView titleTextView;
        TextView detailTextView;
//        TextView numTextView;
        FrameLayout frameLayout;
    }

    public List<MessagesItem> getMessages() {
        return messages;
    }

    public MessagesAdapter(Context context, int resource, List<MessagesItem> objects) {
        super(context, resource, objects);
        this.messages = objects;
        this.context = context;

        options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.chat_avatar_default_icon).showImageForEmptyUri(R.drawable.chat_avatar_default_icon)
                .showImageOnFail(R.drawable.chat_avatar_default_icon).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.cell_item_message, parent, false);
            TextView titleView = (TextView) convertView.findViewById(R.id.title_text_view);
            TextView detailView = (TextView) convertView.findViewById(R.id.detail_text_view);
            TextView numTextView = (TextView) convertView.findViewById(R.id.num_text_view);
            FrameLayout frameLayout = (FrameLayout) convertView.findViewById(R.id.frame_layout);
            holder.frameLayout = frameLayout;
            holder.titleTextView = titleView;
            holder.detailTextView = detailView;
//            holder.numTextView = numTextView;
            holder.avatarImageView = (ImageView) convertView.findViewById(R.id.contact_avatar);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        MessagesItem message = messages.get(position);
        if (message.getLastMessage() instanceof HSTextMessage){
        	holder.titleTextView.setText("" + message.getName() + " " + "mid: " + message.getMid() + "  "+DateFormat.getDateTimeInstance().format(((HSTextMessage)(message.getLastMessage())).getTimestamp()));
        	holder.detailTextView.setText(""+message.getNotReadNum()+"条未读："+((HSTextMessage)(message.getLastMessage())).getText());
//        	holder.numTextView.setText(message.getNotReadNum());
        	ImageLoader.getInstance().displayImage("content://com.android.contacts/contacts/" + message.getContact().getContactId(), holder.avatarImageView, options);
        }
        if (message.getLastMessage() instanceof HSImageMessage){
        	holder.titleTextView.setText("" + message.getName() + " " + "mid: " + message.getMid() + "  "+DateFormat.getDateTimeInstance().format(((HSImageMessage)(message.getLastMessage())).getTimestamp()));
        	holder.detailTextView.setText(""+message.getNotReadNum()+"条未读：【图片消息】");
//        	holder.numTextView.setText(message.getNotReadNum());
        	ImageLoader.getInstance().displayImage("content://com.android.contacts/contacts/" + message.getContact().getContactId(), holder.avatarImageView, options);
        }
        if (message.getNotReadNum() == 0){
        	holder.frameLayout.setVisibility(View.GONE);
        }
        else{
        	holder.frameLayout.setVisibility(View.VISIBLE);
        }
        return convertView;
    }
}
