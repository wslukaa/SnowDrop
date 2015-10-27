package com.ihs.demo.message_2013011392;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;

import com.ihs.app.framework.HSSessionMgr;
import com.ihs.app.framework.activity.IDialogHolder;

public class HSActionBarActivity extends ActionBarActivity implements IDialogHolder {

    protected AlertDialog dialog;
    private boolean isBackPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HSSessionMgr.onActivityCreate(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissDialog();
        HSSessionMgr.onActivityDestroy(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        HSSessionMgr.onActivityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        HSSessionMgr.onActivityStop(this, isBackPressed);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            isBackPressed = false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * ATTENTION!!! If you really have to override onBackPressed() in any subclass, and somehow you still want to exit
     * your activity on back key pressed, then remember to call super.onBackPressed() instead of simply calling
     * finish(); Otherwise, the Back key event cannot be recorded and handled by our HSActivity
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isBackPressed = true;
    }

    @Override
    public boolean showDialog(AlertDialog alertDialog) {
        this.dismissDialog();

        this.dialog = alertDialog;
        this.dialog.show();
        return true;
    }

    @Override
    public void dismissDialog() {
        if (this.dialog != null) {
            this.dialog.dismiss();
            this.dialog = null;
        }
    }

}
