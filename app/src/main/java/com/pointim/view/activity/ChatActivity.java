package com.pointim.view.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ListView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.pointim.R;
import com.pointim.adapter.ChatAdapter;
import com.pointim.controller.ChatController;
import com.pointim.inject.annotation.InjectView;
import com.pointim.model.ChatParam;
import com.pointim.smack.SmackManager;
import com.pointim.ui.ChatKeyboard;
import com.pointim.utils.BitmapUtil;
import com.pointim.utils.ChatUtils;
import com.pointim.utils.DateUtil;
import com.pointim.utils.FileUtil;
import com.pointim.utils.SdCardUtil;
import com.pointim.utils.StringUtils;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class ChatActivity extends BaseActivity implements ChatKeyboard.ChatKeyboardOperateListener {
    public static boolean isActive = false;//聊天窗口是否已创建
    public static String chatJid;//记录当前正在聊天对象的id
    public List<ChatParam> chatParams;

    public static int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1;

    /**
     * 聊天内容展示列表
     */
    @InjectView(id=R.id.lv_chat_content)
    private ListView mListView;
    /**
     * 聊天输入控件
     */
    @InjectView(id=R.id.ckb_chat_board)
    private ChatKeyboard mChatKyboard;

    /**
     * 聊天对象用户名
     */
    private String friendRosterUser;
    /**
     * 聊天对象昵称
     */
    private String friendNickname;
    /**
     * 聊天窗口对象
     */
    private Chat chat;
    /**
     * 当前自己昵称
     */
    private String currNickname;
    /**
     * ImageLoader图片加载参数配置
     */
    private DisplayImageOptions options;
    /**
     * 聊天记录展示适配器
     */
    private static ChatAdapter mAdapter;

    /**
     * 文件发送对象
     */
    private String sendUser;
    /**
     * 文件存储目录
     */
    private String fileDir;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        isActive = true;

        mChatKyboard.setChatKeyboardOperateListener(this);
        mChatKyboard.setActivity(this);
        friendRosterUser = getIntent().getStringExtra("user");
        friendNickname = getIntent().getStringExtra("nickname");

        chatJid = SmackManager.getInstance().getChatJidByUser(friendRosterUser);//获取聊天对象的chatJid
        currNickname = SmackManager.getInstance().getAccountName();//获取当前自己的昵称

        chatParams = ChatUtils.getChatParams(chatJid);

        sendUser = SmackManager.getInstance().getFileTransferJidChatJid(chatJid);
        chat = SmackManager.getInstance().createChat(chatJid);

        options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)//图片下载后是否缓存到SDCard
                .cacheInMemory(true)//图片下载后是否缓存到内存
                .bitmapConfig(Bitmap.Config.RGB_565)//图片解码类型，推荐此种方式，减少OOM
                .considerExifParams(true)//是否考虑JPEG图像EXIF参数（旋转，翻转）
                .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
                .showImageOnFail(R.drawable.pic_default)//图片加载失败后显示的图片
                .showImageOnLoading(R.drawable.pic_default)
                .build();

        fileDir = SdCardUtil.getCacheDir(mContext);
        receiveFile();

        mAdapter = new ChatAdapter(mContext, options, chatParams);
        mListView.setAdapter(mAdapter);
    }

    /**
     * 发送消息
     * @param message
     */
    @Override
    public void send(final String message) {
        if(StringUtils.isBlank(message)) {
            return;
        }
        ChatParam param = new ChatParam();
        param.setBody(message);
        param.setTo(chatJid);
        param.setFriendChatJid(chatJid);
        param.setMessage_type(ChatParam.MESSAGE_TYPE_TEXT);
        ChatController.sendMessage(chat, param, new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                ChatParam param1 = (ChatParam) data;
                ChatUtils.addChatParm(param1, handler);
            }
        });
    }

    @SuppressLint("HandlerLeak")
    public static Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            mAdapter.notifyDataSetChanged();
        };
    };

    /**
     * 发送文件
     * @param file
     */
    public void sendFile(final File file, final int type) {
        Log.e("sendFile", sendUser);
        ChatController.sendFile(file, type, sendUser, new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                if(data != null) {
                    OutgoingFileTransfer transfer = (OutgoingFileTransfer) data;
                    checkTransferStatus(transfer, file, type, true);
                }
            }
        });
    }

    /**
     * 接收文件
     */
    public void receiveFile() {

    }

    /**
     * 检查发送文件、接收文件的状态
     * @param transfer
     * @param file		发送或接收的文件
     * @param type		文件类型，语音或图片
     * @param isSend	是否为发送
     */
    public void checkTransferStatus(final FileTransfer transfer, final File file, final int type, final boolean isSend) {
        Log.e("运行到这里", "运行到这里");
        String username = friendNickname;
        if(isSend) {
            username = currNickname;
        }
        final String name = username;
        final com.pointim.model.Message msg = new com.pointim.model.Message(type, name, DateUtil.formatDatetime(new Date()), isSend);
        msg.setFilePath(file.getAbsolutePath());
        msg.setLoadState(0);
        new Thread(){
            public void run() {
                if(transfer.getProgress() < 1) {//传输开始
                    handler.obtainMessage(1, msg).sendToTarget();
                }
                while(!transfer.isDone()) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if(FileTransfer.Status.complete.equals(transfer.getStatus())) {//传输完成
                    Log.e("chat", "文件传输完成");
                    msg.setLoadState(1);
                    handler.obtainMessage(2, msg).sendToTarget();
                } else if(FileTransfer.Status.cancelled.equals(transfer.getStatus())) {
                    Log.e("chat", "文件传输取消");
                    //传输取消
                    msg.setLoadState(-1);
                    handler.obtainMessage(2, msg).sendToTarget();
                } else if(FileTransfer.Status.error.equals(transfer.getStatus())) {
                    Log.e("chat", "文件传输错误" + transfer.getException().getMessage());
                    transfer.getException().printStackTrace();
                    //传输错误
                    msg.setLoadState(-1);
                    handler.obtainMessage(2, msg).sendToTarget();
                } else if(FileTransfer.Status.refused.equals(transfer.getStatus())) {
                    Log.e("chat", "文件传输拒绝");
                    //传输拒绝
                    msg.setLoadState(-1);
                    handler.obtainMessage(2, msg).sendToTarget();
                }
            };
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isActive = false;//修改是否正在聊天的标识符
        chatJid = null;//清空聊天的id
    }

    /**
     * 发送语音消息
     * @param audioFile
     */
    @Override
    public void sendVoice(File audioFile) {
        //发送文件
        Log.e("chat", "这里在发送文件了" + audioFile.length());
        ChatParam param = new ChatParam();
        param.setFile_path(audioFile.getPath());
        param.setFriendChatJid(sendUser);
        param.setMessage_type(ChatParam.TYPE_SOUND);
        param.setSend(true);
        param.setDatetime(new Date());
        param.setFinish(false);
        android.os.Message msg = new android.os.Message();
        msg.obj = param;
        msg.what = 1;
        MainActivity.chatHandler.sendMessage(msg);
        sendFile(audioFile, com.pointim.model.Message.MESSAGE_TYPE_VOICE);
    }

    @Override
    public void recordStart() {
    }

    /**
     * 获取权限回调
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode,grantResults);
    }

    private void doNext(int requestCode, int[] grantResults) {
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                if(mChatKyboard.getChatKeyboardOperateListener() != null) {
                    Log.e("Chat", "running here");
                    mChatKyboard.getChatKeyboardOperateListener().recordStart();
                }
            } else {
                // Permission Denied
            }
        }
    }

    /**
     * 选择图片
     */
    private static final int REQUEST_CODE_GET_IMAGE = 1;
    /**
     * 拍照
     */
    private static final int REQUEST_CODE_TAKE_PHOTO = 2;

    @Override
    public void functionClick(int index) {
        switch(index) {
            case 1://选择图片
                selectImage();
                break;
            case 2://拍照
                takePhoto();
                break;
        }
    }

    /**
     * 从图库选择图片
     */
    public void selectImage() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "选择图片"), REQUEST_CODE_GET_IMAGE);
        } else {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "选择图片"), REQUEST_CODE_GET_IMAGE);
        }
    }

    private String picPath = "";
    /**
     * 拍照
     */
    public void takePhoto() {
        picPath = fileDir + "/" + DateUtil.formatDatetime(new Date(), "yyyyMMddHHmmss") + ".png";
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(picPath)));
        startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if(requestCode == REQUEST_CODE_TAKE_PHOTO) {//拍照成功
                takePhotoSuccess();
            } else if(requestCode == REQUEST_CODE_GET_IMAGE) {//图片选择成功
                Uri dataUri = data.getData();
                if (dataUri != null) {
                    File file = FileUtil.uri2File(mContext, dataUri);
                    sendFile(file, com.pointim.model.Message.MESSAGE_TYPE_IMAGE);
                }
            }
        }
    }

    /**
     * 照片拍摄成功
     */
    public void takePhotoSuccess() {
        Bitmap bitmap = BitmapUtil.createBitmapWithFile(picPath, 640);
        BitmapUtil.createPictureWithBitmap(picPath, bitmap, 80);
        if(!bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        sendFile(new File(picPath), com.pointim.model.Message.MESSAGE_TYPE_IMAGE);
    }
}
