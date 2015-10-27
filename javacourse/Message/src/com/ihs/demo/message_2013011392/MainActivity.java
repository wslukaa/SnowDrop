package com.ihs.demo.message_2013011392;

import test.contacts.demo.friends.api.HSContactFriendsMgr;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.ihs.commons.utils.HSLog;
import com.ihs.message_2013011392.R;
import com.ihs.message_2013011392.managers.HSMessageManager;

public class MainActivity extends HSActionBarActivity {

    private final static String TAG = MainActivity.class.getName();
    private Tab tabs[];
    private NotificationManager myNoManager;

    //重载onStart函数使从图标进入应用时全部通知消失
    @Override
    protected void onStart(){
    	super.onStart();
    	String mySer = Context.NOTIFICATION_SERVICE;
    	myNoManager = (NotificationManager) getSystemService(mySer);    	
    	myNoManager.cancelAll();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar bar = this.getSupportActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        int[] tabNames = { R.string.contacts, R.string.messages, R.string.settings, R.string.sample };
        tabs = new Tab[4];
        for (int i = 0; i < 4; i++) {
            Tab tab = bar.newTab();
            tabs[i] = tab;
            tab.setText(tabNames[i]);
            tab.setTabListener(new TabListener() {

                @Override
                public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
                    HSLog.d(TAG, "unselected " + arg0);
                }

                @Override
                public void onTabSelected(Tab arg0, FragmentTransaction arg1) {
                    HSLog.d(TAG, "selected " + arg0);
                    if (tabs[0] == arg0) {
                        Fragment f = new ContactsFragment();
                        arg1.replace(android.R.id.content, f);
                    } else if (tabs[1] == arg0) {
                        Fragment f = new MessagesFragment();
                        arg1.replace(android.R.id.content, f);
                    } else if (tabs[2] == arg0) {
                        Fragment f = new SettingsFragment();
                        arg1.replace(android.R.id.content, f);
                    } else if (tabs[3] == arg0) {
                        Fragment f = new SampleFragment();
                        arg1.replace(android.R.id.content, f);
                    }
                }

                @Override
                public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
                    HSLog.d(TAG, "reselected " + arg0);
                }
            });
            bar.addTab(tab);
        }

        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onResume() {
        super.onResume();
        HSMessageManager.getInstance().pullMessages();
        HSContactFriendsMgr.startSync(true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
