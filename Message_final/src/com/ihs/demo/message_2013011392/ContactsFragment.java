package com.ihs.demo.message_2013011392;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.ihs.commons.notificationcenter.HSGlobalNotificationCenter;
import com.ihs.commons.notificationcenter.INotificationObserver;
import com.ihs.commons.utils.HSBundle;
import com.ihs.message_2013011392.R;

public class ContactsFragment extends Fragment implements INotificationObserver {

    private ListView listView;
    private ContactAdapter adapter = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        listView = (ListView) view.findViewById(R.id.contact_list);
        final List<Contact> contacts = new ArrayList<Contact>();

        adapter = new ContactAdapter(this.getActivity(), R.layout.cell_item_contact, contacts);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String mid = contacts.get(position).getMid();
                String name = contacts.get(position).getName();
                Toast.makeText(getActivity(), "你点击了名字为：" + name + " mid为：" + mid + "的联系人，需要在这里跳转到同此人的聊天界面（�?个Activity�?", Toast.LENGTH_LONG).show();
                
                Intent intent = new Intent(ContactsFragment.this.getActivity(), ChatActivity.class);
                intent.putExtra("name",name);
                intent.putExtra("mid",mid);
                startActivity(intent);
            }
        });
        HSGlobalNotificationCenter.addObserver(FriendManager.NOTIFICATION_NAME_FRIEND_CHANGED, this);
        refresh();
        return view;
    }

    void refresh() {
        adapter.getContacts().clear();
        adapter.getContacts().addAll(FriendManager.getInstance().getAllFriends());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onReceive(String arg0, HSBundle arg1) {
        refresh();
    }

}
