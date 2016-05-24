package com.pointim.adapter;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.pointim.R;
import com.pointim.model.ChatParam;
import com.pointim.model.Message;
import com.pointim.utils.DateUtil;

import java.io.IOException;
import java.util.List;

/**
 * 聊天内容展示适配器
 * @auther: hyl
 * @time: 2015-10-29上午9:50:57
 */
public class ChatAdapter extends BaseAdapter {
	public class ChatViewHolder extends BaseViewAdapter.AbsViewHolder {
		public TextView chatUsername;//消息来源人昵称
		public TextView chatContentTime;//消息时间
		public ImageView chatUserAvatar;//用户头像
		public TextView chatContentText;//文本消息
		public ImageView chatContentImage;//图片消息
		public ImageView chatContentVoice;//语音消息
		public ImageView chatContentLoading;//发送接收文件时的进度条
	}
	
	/**
	 * 上下文
	 */
	private Activity context;
	/**
	 * ImageLoader图片加载参数配置
	 */
	private DisplayImageOptions options;
	/**
	 * 聊天数据
	 */
	private List<ChatParam> list;
	/**
	 * 音频播放器
	 */
	private MediaPlayer mediaPlayer;
	
	public ChatAdapter(Activity context, DisplayImageOptions options,
					   List<ChatParam> list) {
		super();
		this.context = context;
		this.options = options;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public ChatParam getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
    public int getItemViewType(int position) {
        return list.get(position).isSend() ? 1 : 0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }
    
    public void update(ChatParam message) {
    	int idx = list.indexOf(message);
    	if(idx < 0) {
    		list.add(message);
    	} else {
    		list.set(idx, message);
    	}
    	notifyDataSetChanged();
    }
	
	public void add(ChatParam message) {
		list.add(message);
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ChatParam message = list.get(position);
		final ChatViewHolder viewHolder;
		if(convertView == null) {
			viewHolder = new ChatViewHolder();
			if(message.isSend()) {//发送出去的消息，自己发送的消息展示在界面右边
				convertView = View.inflate(context, R.layout.chat_messgae_item_right_layout, null);
			} else {
				convertView = View.inflate(context, R.layout.chat_messgae_item_left_layout, null);
			}
			viewHolder.chatUsername = (TextView) convertView.findViewById(R.id.tv_chat_msg_username);
			viewHolder.chatContentTime = (TextView) convertView.findViewById(R.id.tv_chat_msg_time);
			viewHolder.chatUserAvatar = (ImageView) convertView.findViewById(R.id.iv_chat_avatar);
			viewHolder.chatContentText = (TextView) convertView.findViewById(R.id.tv_chat_msg_content_text);
			viewHolder.chatContentImage = (ImageView) convertView.findViewById(R.id.iv_chat_msg_content_image);
			viewHolder.chatContentVoice = (ImageView) convertView.findViewById(R.id.iv_chat_msg_content_voice);
			viewHolder.chatContentLoading = (ImageView) convertView.findViewById(R.id.iv_chat_msg_content_loading);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ChatViewHolder) convertView.getTag();
		}
		
		viewHolder.chatUsername.setText(message.getUsername());
		if(position >= 1) {
			ChatParam lastChatParam = list.get(position - 1);
			if ((message.getDatetime().getTime() - lastChatParam.getDatetime().getTime()) < (1000*60*5)) {
				viewHolder.chatContentTime.setVisibility(View.GONE);
			} else {
				viewHolder.chatContentTime.setText(DateUtil.formatDatetime(message.getDatetime()));
				viewHolder.chatContentTime.setVisibility(View.VISIBLE);
			}
		} else {
			viewHolder.chatContentTime.setText(DateUtil.formatDatetime(message.getDatetime()));
			viewHolder.chatContentTime.setVisibility(View.VISIBLE);
		}
		setMessageViewVisible(message.getMessage_type(), viewHolder);
		viewHolder.chatContentText.setText(message.getBody());

		if(message.getMessage_type() == ChatParam.MESSAGE_TYPE_TEXT) {//文本消息

		} else if(message.getMessage_type() == ChatParam.TYPE_SOUND) {//语音消息
			viewHolder.chatContentVoice.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					playVoice(viewHolder.chatContentVoice, message.isSend(), message.getFile_path());
				}
			});
			//showLoading(viewHolder, message);
		} else if(message.getMessage_type() == ChatParam.TYPE_IMAGE) {//图片消息
			String url = "file://" + message.getFile_path();
			ImageLoader.getInstance().displayImage(url, viewHolder.chatContentImage, options, new SimpleImageLoadingListener());
			//showLoading(viewHolder, message);
		}
		return convertView;
	}
	
	private void showLoading(ChatViewHolder viewHolder, Message message) {
		switch(message.getLoadState()) {
		case 0://加载开始
			viewHolder.chatContentLoading.setBackgroundResource(R.drawable.chat_file_content_loading_anim);
			final AnimationDrawable animationDrawable =(AnimationDrawable)viewHolder.chatContentLoading.getBackground();
			viewHolder.chatContentLoading.post(new Runnable() {
			    @Override
		        public void run()  {
		            animationDrawable.start();
		        }
			});
			viewHolder.chatContentLoading.setVisibility(View.VISIBLE);
			break;
		case 1://加载完成
			viewHolder.chatContentLoading.setVisibility(View.GONE);
			break;
		case -1:
			viewHolder.chatContentLoading.setBackgroundResource(R.drawable.load_fail);
			break;
		}
	}
	
	/**
	 * 根据消息类型显示对应的消息展示控件
	 * @param type
	 * @param viewHolder
	 */
	private void setMessageViewVisible(int type, ChatViewHolder viewHolder) {
		if(type == ChatParam.MESSAGE_TYPE_TEXT) {//文本消息
			viewHolder.chatContentText.setVisibility(View.VISIBLE);
			viewHolder.chatContentImage.setVisibility(View.GONE);
			viewHolder.chatContentVoice.setVisibility(View.GONE);
		} else if(type == ChatParam.TYPE_IMAGE) {//图片消息
			viewHolder.chatContentText.setVisibility(View.GONE);
			viewHolder.chatContentImage.setVisibility(View.VISIBLE);
			viewHolder.chatContentVoice.setVisibility(View.GONE);
		} else if(type == ChatParam.TYPE_SOUND) {//语音消息
			viewHolder.chatContentText.setVisibility(View.GONE);
			viewHolder.chatContentImage.setVisibility(View.GONE);
			viewHolder.chatContentVoice.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 播放语音信息
	 * @param iv
	 * @param isSend
	 * @param file_path
     */
	private void playVoice(final ImageView iv, final boolean isSend, final String file_path) {
		if(isSend) {
			iv.setBackgroundResource(R.drawable.gxx);
		} else {
			iv.setBackgroundResource(R.drawable.gxu);
		}
		final Drawable animationDrawable = iv.getBackground();
		/*iv.post(new Runnable() {
		    @Override
	        public void run()  {
	            animationDrawable.start();
	        }
		});*/
		if (mediaPlayer == null || !mediaPlayer.isPlaying()) {//点击播放，再次点击停止播放
			// 开始播放录音
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					//animationDrawable.stop();
					// 恢复语音消息图标背景
					if(isSend) {
						iv.setBackgroundResource(R.drawable.gxu);
					} else {
						iv.setBackgroundResource(R.drawable.gxx);
					}
				}
			});
			try {
				mediaPlayer.reset();
				mediaPlayer.setDataSource(file_path);
				mediaPlayer.prepare();
				mediaPlayer.start();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			//animationDrawable.stop();
			// 恢复语音消息图标背景
			if(isSend) {
				iv.setBackgroundResource(R.drawable.gxu);
			} else {
				iv.setBackgroundResource(R.drawable.gxx);
			}
			if (mediaPlayer != null) {
				mediaPlayer.stop();
				mediaPlayer.release();
				mediaPlayer = null;
			}
		}
	}
}
