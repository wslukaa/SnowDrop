package com.ihs.demo.message_2013011392;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.ihs.account.api.account.HSAccountManager;
import com.ihs.app.framework.HSApplication;
import com.ihs.commons.notificationcenter.HSGlobalNotificationCenter;
import com.ihs.commons.utils.HSBundle;
import com.ihs.commons.utils.HSError;
import com.ihs.commons.utils.HSLog;
import com.ihs.message_2013011392.R;
import com.ihs.message_2013011392.managers.HSMessageManager;
import com.ihs.message_2013011392.managers.HSMessageManager.QueryResult;
import com.ihs.message_2013011392.managers.HSMessageManager.SendMessageCallback;
import com.ihs.message_2013011392.types.HSAudioMessage;
import com.ihs.message_2013011392.types.HSBaseMessage;
import com.ihs.message_2013011392.types.HSImageMessage;
import com.ihs.message_2013011392.types.HSLocationMessage;
import com.ihs.message_2013011392.types.HSOnlineMessage;
import com.ihs.message_2013011392.types.HSTextMessage;

public class SampleFragment extends Fragment {

    public static final String SAMPLE_NOTIFICATION_NAME = "notification sample";
    public static final String SAMPLE_NOTIFICATION_BUNDLE_STRING = "bundleString";
    private ListView listView;
    private final static String TAG = SampleFragment.class.getName();
    private long cursor = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sample, container, false);
        listView = (ListView) view.findViewById(R.id.test_list);
        // @formatter:off
        String objects[] = new String[] {
                "Set Receiver",
                "Send Text", 
                "Send Audio", 
                "Send Location", 
                "Send Image", 
                "Send Online Message", 
                "Query Messages", 
                "Delete All Messages",
                "HSGlobalNotificationCenter Sample",
                "Send Message with Extras"};
        // @formatter:on
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, objects);
        listView.setAdapter(adapter);
        final Context context = getActivity();

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 判断用户是否处于已登陆状�?
                if (HSAccountManager.getInstance().getSessionState() == HSAccountManager.HSAccountSessionState.INVALID) {
                    Toast toast = Toast.makeText(getActivity(), "请先登录", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    String toMid = prefs.getString("SAMPLE_RECEIVER", "");
                    if (toMid == null || toMid.isEmpty()) {
                        toMid = "1";
                    }

                    switch (position) {
                        case 0: {
                            LayoutInflater li = LayoutInflater.from(context);
                            View promptsView = li.inflate(R.layout.prompt_set_receiver, null);
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                            alertDialogBuilder.setView(promptsView);

                            final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogReceiverMid);

                            alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                                    Editor editor = prefs.edit();
                                    editor.putString("SAMPLE_RECEIVER", userInput.getText().toString());
                                    editor.commit();
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                            break;
                        }
                        case 1: { // 文本消息
                            HSMessageManager.getInstance().send(new HSTextMessage(toMid, "这是�?条文本消息，发�?�于 " + (new Date()).toString()), new SendMessageCallback() {

                                @Override
                                public void onMessageSentFinished(HSBaseMessage message, boolean success, HSError error) {
                                    HSLog.d(TAG, "success: " + success);
                                }
                            }, new Handler());
                            break;
                        }
                        case 2: { // 语音消息
                            // 准备示例语音文件
                            final File audioFile = new File(HSApplication.getContext().getCacheDir() + "/" + "audio_test_file.wav");
                            copy("audio_test_file.wav", audioFile);

                            HSAudioMessage audioMessage = new HSAudioMessage(toMid, audioFile.getAbsolutePath(), 2.72);
                            HSMessageManager.getInstance().send(audioMessage, new SendMessageCallback() {

                                @Override
                                public void onMessageSentFinished(HSBaseMessage message, boolean success, HSError error) {
                                    HSLog.d(TAG, "success: " + success);
                                }
                            }, new Handler());
                            break;
                        }
                        case 3: { // 位置消息
                            HSLocationMessage locationMessage = new HSLocationMessage(toMid, 39.99755, 116.34552, "iHandy Inc., Wudaokou, Haidian District");
                            HSMessageManager.getInstance().send(locationMessage, new SendMessageCallback() {
                                @Override
                                public void onMessageSentFinished(HSBaseMessage message, boolean success, HSError error) {
                                    HSLog.d(TAG, "success: " + success);
                                }
                            }, new Handler());
                            break;
                        }
                        case 4: { // 图片消息
                            // 准备示例图片文件
                            final File imageFile = new File(HSApplication.getContext().getCacheDir() + "/" + "golden_gate.jpg");
                            copy("golden_gate.jpg", imageFile);

                            HSImageMessage imageMessage = new HSImageMessage(toMid, imageFile.getAbsolutePath());
                            HSMessageManager.getInstance().send(imageMessage, new SendMessageCallback() {
                                @Override
                                public void onMessageSentFinished(HSBaseMessage message, boolean success, HSError error) {
                                    HSLog.d(TAG, "success: " + success);
                                }
                            }, new Handler());
                            break;
                        }
                        case 5: { // 在线消息
                            JSONObject content = new JSONObject();
                            try {
                                // 可以�? online message 的消息体中放入字典格式的任意定制内容
                                content.put("game-name", "2048");
                                content.put("game-mode", "2-person");
                                JSONObject actionInfo = new JSONObject();
                                actionInfo.put("touch-position", 8);
                                actionInfo.put("swipe-direction", "up");
                                content.put("action", actionInfo);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            HSOnlineMessage onlineMessage = new HSOnlineMessage(toMid, content);
                            HSMessageManager.getInstance().sendOnlineMessage(onlineMessage);
                        }
                        case 6: { // 从数据库中查询消�?
                            QueryResult result = HSMessageManager.getInstance().queryMessages(toMid, 10, cursor);
                            cursor = result.getCursor();
                            List<HSBaseMessage> list = result.getMessages();
                            for (int i = 0; i < list.size(); i++)
                                HSLog.d(TAG, "result " + i + ": " + list.get(i));
                            break;
                        }
                        case 7: { // 删除数据库中�?有消�?
                            HSMessageManager.getInstance().deleteAllMessages();
                            break;
                        }
                        case 8: { // 演示 HSGlobalNotificationCenter 功能，观察�?�接收信息部分的代码�? DemoApplication �?
                            HSBundle bundle = new HSBundle();
                            // 将需要显示的信息加入bundle�?
                            bundle.putString(SAMPLE_NOTIFICATION_BUNDLE_STRING, "演示HSGlobalNotificationCenter功能"
                                    + "\n1.在DemoApplication中添加名为SAMPLE_NOTIFICATION_NAME的观察�?�，以INotificationObserver接口监听�?"
                                    + "\n2.在SampleFragment中将预置的信息写入bundle并在主线程中向名为SAMPLE_NOTIFICATION_NAME的观察�?�发送�?�知" + "\n3.在DemoApplication中实现接收消息并显示此弹�?");
                            //在主线程中将通知发�?�到名为 SAMPLE_NOTIFICATION_NAME 的观察�?�处
                            HSGlobalNotificationCenter.sendNotificationOnMainThread(SAMPLE_NOTIFICATION_NAME, bundle);
                            break;
                        }
                        case 9: { // 在发送的消息中添加额外信息，以实现自定义功能时使�?
                            // 新建�?条带有额外信息的文本消息
                            HSTextMessage textMessage = new HSTextMessage(toMid,
                                    "这是�?条带有额外信息的样例消息，发送消息时通过 setExtra() 方法�? extra 中存放了�?�? String，对方收到此�? HSTextMessage 时可以利�? getExtra() 获取到附加的信息");
                            JSONObject extra = new JSONObject();
                            try {
                                // 放入用户定制的附加信�?
                                extra.put("ads_url", "http://mp.weixin.qq.com/s?__biz=MzAxNzI2OTM5Mg==&mid=207134949&idx=1&sn=584fde52800848dffcc051ada1550207");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            // �? extra info 放进消息
                            textMessage.setExtra(extra);
                            // 发�?�消�?
                            HSMessageManager.getInstance().send(textMessage, new SendMessageCallback() {
                                @Override
                                public void onMessageSentFinished(HSBaseMessage message, boolean success, HSError error) {
                                    HSLog.d(TAG, "success: " + success);
                                }
                            }, new Handler());
                            break;
                        }

                    }
                }
            }

        });
        return view;
    }

    /**
     * �? asset 目录中的文件拷贝到目标文件路�?
     * 
     * @param assetFileName asset 目录中的文件�?
     * @param targetFile �?要拷贝到的目标文件路�?
     */
    static void copy(String assetFileName, File targetFile) {
        final File f = targetFile;
        if (!f.exists())
            try {
                InputStream is = HSApplication.getContext().getAssets().open(assetFileName);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();

                FileOutputStream fos = new FileOutputStream(f);
                fos.write(buffer);
                fos.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
    }

}
