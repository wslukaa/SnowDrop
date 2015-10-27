package com.ihs.demo.message_2013011392;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ihs.account.api.account.HSAccountManager;
import com.ihs.account.api.tpaccount.HSTPAccountManager;
import com.ihs.account.api.tpaccount.HSTPAccountManager.HSSocialType;
import com.ihs.commons.keepcenter.HSKeepCenter;
import com.ihs.commons.notificationcenter.HSGlobalNotificationCenter;
import com.ihs.commons.notificationcenter.INotificationObserver;
import com.ihs.commons.utils.HSBundle;
import com.ihs.message_2013011392.R;

public class SettingsFragment extends Fragment implements INotificationObserver {

    private ListView listView;
    private ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        listView = (ListView) view.findViewById(R.id.settings_list);
        ArrayList<String> objects = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, objects);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent intent = new Intent(SettingsFragment.this.getActivity(), LoginActivity.class);
                    startActivity(intent);
                } else if (position == 1) {
                    new AlertDialog.Builder(getActivity()).setTitle("HINT").setMessage("Are you sure to LOG OUT?").setPositiveButton("YES", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            HSKeepCenter.getInstance().disconnect();
                            HSTPAccountManager.getInstance().getTPAccount(HSSocialType.PHONE).clearData();
                            HSAccountManager.getInstance().logout();
                            adapter.notifyDataSetChanged();
                        }

                    }).setNegativeButton("NO", null).show();
                }
            }

        });
        refresh();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HSGlobalNotificationCenter.addObserver(HSAccountManager.HS_ACCOUNT_NOTIFICATION_LOGOUT_DID_FINISH, this);
        HSGlobalNotificationCenter.addObserver(HSAccountManager.HS_ACCOUNT_NOTIFICATION_SIGNIN_DID_FINISH, this);
    }

    @Override
    public void onDestroy() {
        HSGlobalNotificationCenter.removeObserver(this);
        super.onDestroy();
    }

    void refresh() {
        adapter.clear();
        String objects[] = new String[] {
                HSAccountManager.getInstance().getSessionState() == HSAccountManager.HSAccountSessionState.VALID ? ("Account: "
                        + HSAccountManager.getInstance().getMainAccount().getMID() + " Number: " + HSAccountManager.getInstance().getMainAccount()
                        .getSubAccount(HSSocialType.PHONE).getSID()) : "Login", "Logout" };
        adapter.addAll(objects);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onReceive(String arg0, HSBundle arg1) {
        this.refresh();
    }
}
