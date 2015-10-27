package com.ihs.demo.message_2013011392;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.KeyEvent;

import com.ihs.account.api.account.HSAccountError;
import com.ihs.account.api.account.HSAccountManager;
import com.ihs.account.api.tpaccount.HSTPAccountEvent;
import com.ihs.account.api.tpaccount.ITPAccount.ITPAccountObserver;
import com.ihs.app.framework.activity.HSActivity;
import com.ihs.commons.utils.HSBundle;
import com.ihs.message_2013011392.R;

public class ProBaseActivity extends HSActivity implements OnClickListener, ITPAccountObserver {
    protected static final int DIALOG_PROGRESS = 100;
    protected static final int DIALOG_MOPLUS_SERVICE_UNAVAILABLE = 101;
    protected static final int DIALOG_SIP_NO_NETWORK = 102;
    protected static final int DIALOG_GMAIL_ACCOUNT_DISABLED = 103;
    protected static final int DIALOG_GMAIL_INVALID_SECOND_FACTOR = 104;
    protected static final int DIALOG_GMAIL_NAME_OR_PASSWORD_WRONG = 105;
    protected static final int DIALOG_GMAIL_WEB_LOGIN_REQUIRED = 106;
    protected static final int DIALOG_GMAIL_NO_NETWORK = 107;
    protected static final int DIALOG_GMAIL_SERVICE_UNAVAILABLE = 108;
    protected static final int DIALOG_BIND_FAIL_TEL_EXISTS_IN_OTHER_ACCOUNT = 109;
    protected static final int DIALOG_BIND_GOOGLE_ACCOUNT_HAS_MONEY = 110;
    protected static final int DIALOG_VERIFY_GMAIL_ACCOUNT = 111;
    protected static final int DIALOG_VERIFICATION_ERROR = 112;
    protected static final int DIALOG_VERIFICATION_TIMEOUT = 113;
    protected static final int DIALOG_UPSMS_HINT = 114;
    protected static final int DIALOG_UPMMS_HINT = 115;
    protected static final int DIALOG_UP_VERIFICATION_WRONG_NUMBER = 116;

    private boolean isBackEnabled;
    protected boolean isVisible;

    @Override
    protected void onStart() {
        super.onStart();
        isVisible = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isVisible = false;
    }

    protected void enableBackKey() {
        isBackEnabled = true;
    }

    protected void disableBackKey() {
        isBackEnabled = false;
    }

    protected void displayProgress() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                safeShowDialog(DIALOG_PROGRESS, true);
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && !isBackEnabled) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void dismissProgress() {
        this.runOnUiThread(new Runnable() {
            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                removeDialog(DIALOG_PROGRESS);
            }
        });

    }

    public void safeShowDialog(int id) {
        safeShowDialog(id, false);
    }

    public void safeShowDialog(int id, Bundle bundle) {
        safeShowDialog(id, bundle, false);
    }

    @SuppressWarnings("deprecation")
    public void safeShowDialog(int id, boolean forceShow) {
        if (!this.isFinishing() && (forceShow || isVisible)) {
            showDialog(id);
        }
    }

    @SuppressWarnings("deprecation")
    public void safeShowDialog(int id, Bundle bundle, boolean forceShow) {
        if (!this.isFinishing() && (forceShow || isVisible)) {
            showDialog(id, bundle);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_PROGRESS:
                ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("wait a minute");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
                return progressDialog;           
            case DIALOG_SIP_NO_NETWORK:
                return new AlertDialog.Builder(this).setTitle(R.string.prov_network_error_title).setMessage(R.string.prov_network_error_body)
                        .setPositiveButton(android.R.string.ok, this).create();
            case DIALOG_BIND_FAIL_TEL_EXISTS_IN_OTHER_ACCOUNT:
                return new AlertDialog.Builder(this).setMessage(R.string.prov_has_account_alert).setNegativeButton(android.R.string.ok, this).create();
            case DIALOG_VERIFICATION_ERROR:
                return new AlertDialog.Builder(this).setTitle(R.string.prov_verifycode_error_title).setMessage(R.string.prov_sms_verifycode_error_body)
                        .setPositiveButton(android.R.string.ok, null).create();
            case DIALOG_VERIFICATION_TIMEOUT:
                return new AlertDialog.Builder(this).setMessage(R.string.prov_verifycode_timeout_body).setPositiveButton(android.R.string.ok, null).create();
            case DIALOG_UP_VERIFICATION_WRONG_NUMBER:
                return new AlertDialog.Builder(this).setMessage("").setTitle(R.string.prov_alert_verif_wrong_number_title).setPositiveButton(android.R.string.ok, null).create();
            case DIALOG_UPMMS_HINT:
            case DIALOG_UPSMS_HINT:
                Dialog up_sms_verification_Dialog = new Dialog(this, R.style.UpVerificationConfirmDialog);
                return up_sms_verification_Dialog;
        }
        return super.onCreateDialog(id);
    }

    protected void onVerifyCodeReady() {

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }

    @Override
    public void onTPAccountUpdated(HSTPAccountEvent event) {
        String errorDesc = event.getErrorDesc();
        if (errorDesc == null) {
            return;
        }

        enableBackKey();
        dismissProgress();

        if (errorDesc.equals(HSTPAccountEvent.EXCEPTION_WRONG_VERIFY_CODE)) {
            safeShowDialog(DIALOG_VERIFICATION_ERROR);
        } else if (errorDesc.equals(HSTPAccountEvent.EXCEPTION_TIME_OUT)) {
            safeShowDialog(DIALOG_VERIFICATION_TIMEOUT);
        } else if (errorDesc.equals(HSTPAccountEvent.EXCEPTION_NETWORK_ERROR)) {
            safeShowDialog(DIALOG_SIP_NO_NETWORK);
        } else if (errorDesc.equals(HSTPAccountEvent.EXCEPTION_WRONG_NUMBER)) {
            safeShowDialog(DIALOG_UP_VERIFICATION_WRONG_NUMBER);
        } else if (errorDesc.equals(HSTPAccountEvent.EXCEPTION_AUTH_FAILED)) {
            safeShowDialog(DIALOG_MOPLUS_SERVICE_UNAVAILABLE);
        }
    }

    public void onReceive(String arg0, HSBundle arg1) {
        JSONObject response = (JSONObject) arg1.getObject(HSAccountManager.KEY_HS_ACCOUNT_NOTIFICATION_BUDDLE_INFO);
        String error = response.optString(HSAccountManager.KEY_HS_ACCOUNT_NOTIFICATION_INFO_ERROR);
        if (error != null) {
            enableBackKey();
            dismissProgress();
            if (error.equals(HSAccountError.EXCEPTION_NETWORK_ERROR)) {
                safeShowDialog(DIALOG_SIP_NO_NETWORK);
            } else {
                safeShowDialog(DIALOG_MOPLUS_SERVICE_UNAVAILABLE);
            }
        }

    }
}
