package com.ihs.demo.message_2013011392;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.ihs.account.api.account.HSAccountError;
import com.ihs.account.api.account.HSAccountManager;
import com.ihs.account.api.account.HSAccountManager.HSAccountSessionState;
import com.ihs.account.api.tpaccount.HSTPAccountEvent;
import com.ihs.account.api.tpaccount.HSTPAccountEvent.HSTPAccountEventType;
import com.ihs.account.api.tpaccount.HSTPAccountManager;
import com.ihs.account.api.tpaccount.HSTPAccountManager.HSSocialType;
import com.ihs.account.api.tpaccount.ITPAccount.ITPAccountObserver;
import com.ihs.account.api.tpaccount.ITPAccountPhone;
import com.ihs.account.api.tpaccount.ITPAccountPhone.HSPhoneAuthType;
import com.ihs.app.framework.HSApplication;
import com.ihs.app.framework.activity.HSActivity;
import com.ihs.commons.notificationcenter.HSGlobalNotificationCenter;
import com.ihs.commons.notificationcenter.INotificationObserver;
import com.ihs.commons.utils.HSBundle;
import com.ihs.commons.utils.HSLog;
import com.ihs.message_2013011392.R;

public class LoginActivity extends HSActivity implements ITPAccountObserver, INotificationObserver {

    private EditText inputEditText;

    public LoginActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_login);

        this.inputEditText = (EditText) findViewById(R.id.phonenumber_editor);
        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = inputEditText.getText().toString();
                PhoneNumberUtil instance = PhoneNumberUtil.getInstance();
                try {
                    PhoneNumber phoneNumber = instance.parse(number, "CN");
                    String e164 = instance.format(phoneNumber, PhoneNumberFormat.E164);
                    if (TextUtils.isEmpty(e164) || e164.length() != 14) {
                        new AlertDialog.Builder(LoginActivity.this).setTitle("Wrong Phone Number").setMessage("Number format is wrong.").setNeutralButton("OK", null).show();
                        return;
                    }

                    ITPAccountPhone accountPhone = (ITPAccountPhone) HSTPAccountManager.getInstance().getTPAccount(HSSocialType.PHONE);
                    accountPhone.setPhoneNumber(e164);
                    if (HSAccountManager.getInstance().getSessionState() != HSAccountSessionState.VALID) {
                        HSAccountManager.getInstance().signin(accountPhone);
                    } else {
                        HSAccountManager.getInstance().bind(accountPhone, false);
                    }
                } catch (NumberParseException e) {
                    new AlertDialog.Builder(LoginActivity.this).setTitle("Wrong Phone Number").setMessage("Number format is wrong.").setNeutralButton("OK", null).show();
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        HSGlobalNotificationCenter.addObserver(HSAccountManager.HS_ACCOUNT_NOTIFICATION_SIGNIN_DID_FINISH, this);
        HSGlobalNotificationCenter.addObserver(HSAccountManager.HS_ACCOUNT_NOTIFICATION_SIGNIN_FAILED, this);
        HSGlobalNotificationCenter.addObserver(HSAccountManager.HS_ACCOUNT_NOTIFICATION_BIND_DID_FINISH, this);
        HSGlobalNotificationCenter.addObserver(HSAccountManager.HS_ACCOUNT_NOTIFICATION_BIND_FAILED, this);
    }

    @Override
    protected void onPause() {
        HSGlobalNotificationCenter.removeObserver(this);
        super.onPause();
    }

    @Override
    public void onReceive(String arg0, HSBundle arg1) {
        if (HSAccountManager.HS_ACCOUNT_NOTIFICATION_SIGNIN_DID_FINISH.equals(arg0)) {
            HSLog.d("Signin Finished");
            Toast.makeText(HSApplication.getContext(), "Provision Finished", Toast.LENGTH_LONG).show();
            finish();
            DemoApplication.doInit();
        } else if (HSAccountManager.HS_ACCOUNT_NOTIFICATION_BIND_DID_FINISH.equals(arg0)) {
            HSLog.d("Bind Finished");
            Toast.makeText(HSApplication.getContext(), "Provision Finished", Toast.LENGTH_LONG).show();
            finish();
            DemoApplication.doInit();
        } else if (HSAccountManager.HS_ACCOUNT_NOTIFICATION_SIGNIN_FAILED.equals(arg0) || HSAccountManager.HS_ACCOUNT_NOTIFICATION_BIND_FAILED.equals(arg0)) {
            JSONObject response = (JSONObject) arg1.getObject(HSAccountManager.KEY_HS_ACCOUNT_NOTIFICATION_BUDDLE_INFO);
            if (response.optString(HSAccountManager.KEY_HS_ACCOUNT_NOTIFICATION_INFO_ERROR).equals(HSAccountError.EXCEPTION_NEED_AUTH)) {
                ITPAccountPhone accountPhone = (ITPAccountPhone) HSTPAccountManager.getInstance().getTPAccount(HSSocialType.PHONE);
                accountPhone.addObserver(this);
                accountPhone.setPhoneAuthType(HSPhoneAuthType.SMS_MT);
                accountPhone.setPackageName("com.ihs.app.template");
                accountPhone.startAuth(null);
            } else if (response.optString(HSAccountManager.KEY_HS_ACCOUNT_NOTIFICATION_INFO_ERROR).equals(HSAccountError.EXCEPTION_ALREADY_EXIST)) {
                new AlertDialog.Builder(this).setTitle("HINT").setMessage("Already Exist, Logout First!").setNeutralButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LoginActivity.this.finish();
                    }

                }).show();
            }
        }
    }

    @Override
    public void onTPAccountUpdated(HSTPAccountEvent event) {
        ITPAccountPhone accountPhone = (ITPAccountPhone) HSTPAccountManager.getInstance().getTPAccount(HSSocialType.PHONE);
        accountPhone.removeObserver(this);
        HSTPAccountEventType eventType = event.getEventType();
        switch (eventType) {
            case PHONE_VERIFY_CODE_READY:
                Intent intent = new Intent(this, MtVerifyActivity.class);
                startActivity(intent);
                finish();
                break;

            default:
                break;
        }
    }
}
