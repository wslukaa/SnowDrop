package com.ihs.demo.message_2013011392;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.ihs.account.api.account.HSAccountManager;
import com.ihs.account.api.account.HSAccountManager.HSAccountSessionState;
import com.ihs.account.api.tpaccount.HSTPAccountEvent;
import com.ihs.account.api.tpaccount.HSTPAccountEvent.HSTPAccountEventType;
import com.ihs.account.api.tpaccount.HSTPAccountManager;
import com.ihs.account.api.tpaccount.HSTPAccountManager.HSSocialType;
import com.ihs.account.api.tpaccount.ITPAccount.ITPAccountObserver;
import com.ihs.account.api.tpaccount.ITPAccountPhone;
import com.ihs.account.api.tpaccount.ITPAccountPhone.HSPhoneAuthType;
import com.ihs.commons.notificationcenter.HSGlobalNotificationCenter;
import com.ihs.commons.notificationcenter.INotificationObserver;
import com.ihs.commons.utils.HSBundle;
import com.ihs.message_2013011392.R;

public class MtVerifyActivity extends ProBaseActivity implements View.OnClickListener, android.content.DialogInterface.OnClickListener, ITPAccountObserver, INotificationObserver {

    public final static String EXTRA_KEY_FORMATTED_NUMBER = "formatted_number";
    public static final String EXTRA_KEY_UP_AUTHENTICATION_TYPE = "up_authentication_type";
    public static final String EXTRA_KEY_UP_VERIFY_CODE = "verify_code";
    public static final String EXTRA_KEY_UP_TARGET_ADDR = "target_addr";

    private class CountDownThread extends Thread {
        private Handler handler;
        private boolean needStop;
        private int counter;
        private TextView textView;

        public CountDownThread(Handler handler, TextView textView) {
            this.handler = handler;
            this.textView = textView;
            this.needStop = false;
            this.counter = 60;
        }

        public void stopCountDown() {
            this.needStop = true;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (!needStop && this.counter > 0) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(String.format(getString(R.string.prov_sms_verif_nocode_hint1), counter) + " " + getString(R.string.prov_sms_verif_nocode_hint2)
                                + getString(R.string.prov_sms_verif_nocode_hint3));
                    }
                });
                this.counter--;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (this.counter > 0) {
                // has been stopped.
                return;
            }

            // make the "No Code" clickable;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    // no good design for the hardcode;
                    textView.setText("");
                    textView.append(String.format(getString(R.string.prov_sms_verif_nocode_hint1), 60) + " ");
                    textView.append(Html.fromHtml("<a href=\"\"><u>" + getString(R.string.prov_sms_verif_nocode_hint2) + "</u></a>"));
                    textView.append(getString(R.string.prov_sms_verif_nocode_hint3));
                    CharSequence text = textView.getText();
                    Spannable spannable = (Spannable) text;
                    textView.setMovementMethod(LinkMovementMethod.getInstance());
                    SpannableStringBuilder style = new SpannableStringBuilder(text);
                    style.clearSpans();
                    URLSpan[] urls = spannable.getSpans(0, text.length(), URLSpan.class);
                    for (URLSpan url : urls) {
                        ClickableText myURLSpan = new ClickableText(textView.getCurrentTextColor());
                        style.setSpan(myURLSpan, spannable.getSpanStart(url), spannable.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    textView.setText(style);
                }
            });

        }
    }

    // hack the clickableSpan to implement the feature of clickable of part of
    // TextView;
    private class ClickableText extends ClickableSpan {

        private int color;

        ClickableText(int color) {
            this.color = color;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(color);
            ds.setUnderlineText(true);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onClick(View widget) {
            showDialog(DIALOG_NO_CODE_CLICK);
        }
    }

    private EditText et_accessCode;
    private TextView bt_continue;
    private TextView tv_noCodeHint;

    private CountDownThread countDownThread;
    private Handler handler;

    private static final int DIALOG_NO_CODE_CLICK = 1;

    private InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");

        setContentView(R.layout.activity_mt_verify);

        et_accessCode = (EditText) findViewById(R.id.access_code_inputbox);
        et_accessCode.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        tv_noCodeHint = (TextView) findViewById(R.id.sms_verification_nocode_hint);
        tv_noCodeHint.setText(String.format(getString(R.string.prov_sms_verif_nocode_hint1), 60) + " " + getString(R.string.prov_sms_verif_nocode_hint2)
                + getString(R.string.prov_sms_verif_nocode_hint3));

        bt_continue = (TextView) findViewById(R.id.sms_verification_continue);
        bt_continue.setOnClickListener(this);

        handler = new Handler();
        countDownThread = new CountDownThread(handler, tv_noCodeHint);
        countDownThread.start();

        enableBackKey();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

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

    @SuppressWarnings("deprecation")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        countDownThread.stopCountDown();
        removeDialog(DIALOG_NO_CODE_CLICK);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sms_verification_continue:
                v.requestFocus();
                inputMethodManager.hideSoftInputFromWindow(et_accessCode.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                onBtContinueClicked();
                break;

            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void onBtContinueClicked() {
        String accessCode = et_accessCode.getText().toString().trim();
        if (TextUtils.isEmpty(accessCode)) {
            return;
        }

        displayProgress();
        ITPAccountPhone accountPhone = (ITPAccountPhone) HSTPAccountManager.getInstance().getTPAccount(HSSocialType.PHONE);
        accountPhone.addObserver(this);
        accountPhone.setPhoneAuthType(HSPhoneAuthType.SMS_MT);
        accountPhone.verify(accessCode);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_NO_CODE_CLICK:
                return new AlertDialog.Builder(this).setItems(R.array.nocode_item_list, this).create();
        }
        return super.onCreateDialog(id);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case 0:
                ITPAccountPhone accountPhone = (ITPAccountPhone) HSTPAccountManager.getInstance().getTPAccount(HSSocialType.PHONE);
                accountPhone.addObserver(this);
                accountPhone.setPhoneAuthType(HSPhoneAuthType.SMS_MT);
                accountPhone.startAuth(null);
                displayProgress();
                break;
            default:
                break;
        }
    }

    @Override
    public void onTPAccountUpdated(HSTPAccountEvent event) {
        super.onTPAccountUpdated(event);
        ITPAccountPhone accountPhone = (ITPAccountPhone) HSTPAccountManager.getInstance().getTPAccount(HSSocialType.PHONE);
        accountPhone.removeObserver(this);
        if (event.getErrorDesc() != null) {
            return;
        }

        HSTPAccountEventType eventType = event.getEventType();
        switch (eventType) {
            case PHONE_VERIFY_CODE_READY:
                enableBackKey();
                dismissProgress();
                countDownThread = new CountDownThread(handler, tv_noCodeHint);
                countDownThread.start();
                break;
            case AUTH_SUCCEEDED:
                if (HSAccountManager.getInstance().getSessionState() != HSAccountSessionState.VALID) {
                    HSAccountManager.getInstance().signin(accountPhone);
                } else {
                    HSAccountManager.getInstance().bind(accountPhone, false);
                }
            default:
                break;
        }
    }

    public void onReceive(String arg0, HSBundle arg1) {
        super.onReceive(arg0, arg1);

        if (HSAccountManager.HS_ACCOUNT_NOTIFICATION_SIGNIN_DID_FINISH.equals(arg0) || HSAccountManager.HS_ACCOUNT_NOTIFICATION_BIND_DID_FINISH.equals(arg0)) {
            Intent intent = new Intent(this, CongratulationActivity.class);
            startActivity(intent);
            DemoApplication.doInit();
            finish();
        }
        dismissProgress();
        enableBackKey();
    }
}
