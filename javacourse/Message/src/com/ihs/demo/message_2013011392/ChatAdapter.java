package com.ihs.demo.message_2013011392;

import java.text.DateFormat;
import java.util.List;

import com.ihs.account.api.account.HSAccountManager;
import com.ihs.message_2013011392.R;
import com.ihs.message_2013011392.types.HSBaseMessage;
import com.ihs.message_2013011392.types.HSImageMessage;
import com.ihs.message_2013011392.types.HSTextMessage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ChatAdapter extends BaseAdapter{

    private final List<HSBaseMessage> chatMessages;
    private Activity context;

    public ChatAdapter(Activity context, List<HSBaseMessage> chatMessages) {
        this.context = context;
        this.chatMessages = chatMessages;
    }

    @Override
    public int getCount() {
        if (chatMessages != null) {
            return chatMessages.size();
        } else {
            return 0;
        }
    }

    @Override
    public HSBaseMessage getItem(int position) {
        if (chatMessages != null) {
            return chatMessages.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final HSBaseMessage chatMessage = getItem(position);
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null || ((ViewHolder)convertView.getTag()).msg != chatMessage) {
            convertView = vi.inflate(R.layout.chat_message_list_item, null);
            holder = createViewHolder(convertView);
            holder.msg = chatMessage;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        //是自己发的消息还是对方发的消息
        boolean myMsg;
        if (chatMessage.getFrom().equals(HSAccountManager.getInstance().getMainAccount().getMID()))
        	myMsg = true;
        else
        	myMsg = false;         		
        setAlignment(holder, myMsg);
        
        String text = "";
        //文本消息的显示
        if (chatMessage instanceof HSTextMessage){
        	text = ((HSTextMessage)chatMessage).getText();
        	holder.picture.setVisibility(View.GONE);
        	holder.txtMessage.setVisibility(View.VISIBLE);
        	holder.txtMessage.setText(text);
            holder.txtInfo.setText(DateFormat.getDateTimeInstance().format(chatMessage.getTimestamp()));
        }
        //图片消息的显示
        if (chatMessage instanceof HSImageMessage){
        	holder.txtMessage.setVisibility(View.GONE);
        	holder.txtInfo.setText(DateFormat.getDateTimeInstance().format(chatMessage.getTimestamp()));
        	holder.picture.setVisibility(View.VISIBLE);
        	final String path = ((HSImageMessage)chatMessage).getThumbnailFilePath();
        	holder.picture.setImageBitmap(BitmapFactory.decodeFile(path));
        	holder.picture.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String pathL = ((HSImageMessage)chatMessage).getNormalImageFilePath();
					Intent intent = new Intent(context, ImageActivity.class);
					intent.putExtra("path", pathL);
					context.startActivity(intent);
				}
			});
        }
        return convertView;
    }

    public void add(HSBaseMessage message) {
    	chatMessages.add(message);
    }

    public void add(List<HSBaseMessage> messages) {
        chatMessages.addAll(messages);
    }

    private void setAlignment(ViewHolder holder, boolean isMe) {
    	//根据是否是自己的消息来确定显示在左边还是右边
        if (!isMe) {
            holder.contentWithBG.setBackgroundResource(R.drawable.chat_message_send_pop_bg_pressed);

            LinearLayout.LayoutParams layoutParams = 
            	(LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.END;
            holder.contentWithBG.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp = 
            	(RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.content.setLayoutParams(lp);
            layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
            layoutParams.gravity = Gravity.END;
            holder.txtMessage.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.END;
            holder.txtInfo.setLayoutParams(layoutParams);
        } else {
            holder.contentWithBG.setBackgroundResource(R.drawable.chat_message_receive_pop_bg_pressed);

            LinearLayout.LayoutParams layoutParams = 
            	(LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.START;
            holder.contentWithBG.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp = 
            	(RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            holder.content.setLayoutParams(lp);
            layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
            layoutParams.gravity = Gravity.START;
            holder.txtMessage.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.START;
            holder.txtInfo.setLayoutParams(layoutParams);
        }
    }

    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.txtMessage = (TextView) v.findViewById(R.id.txtMessage);
        holder.content = (LinearLayout) v.findViewById(R.id.content);
        holder.contentWithBG = (LinearLayout) v.findViewById(R.id.contentWithBackground);
        holder.txtInfo = (TextView) v.findViewById(R.id.txtInfo);
        holder.picture = (ImageView) v.findViewById(R.id.imageMessage);
        return holder;
    }

    private static class ViewHolder {
        public TextView txtMessage;
        public TextView txtInfo;
        public ImageView picture;
        public LinearLayout content;
        public LinearLayout contentWithBG;
        public HSBaseMessage msg;
    }
}