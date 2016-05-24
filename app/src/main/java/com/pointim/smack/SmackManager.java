package com.pointim.smack;

import android.util.Log;
import android.widget.Toast;

import com.pointim.model.AddFriend;
import com.pointim.model.FriendResult;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.SmackException.NotLoggedInException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.muc.DiscussionHistory;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SmackManager {
	private static final String TAG = "Smack";
	/**
	 * Xmpp服务器地址
	 */
	public static final String SERVER_IP = "10.0.0.4";
    /**
     * Xmpp 服务器端口
     */
    private static final int PORT = 5222;
    /**
     * 服务器名称
     */
    public static String SERVER_NAME = "point-im-server";
    /**
     * 
     */
    public static final String XMPP_CLIENT = "Smack";
    
    private static SmackManager xmppManager;
    /**
     * 连接
     */
    private XMPPTCPConnection connection;

	private Roster roster;

    private SmackManager() {
    	this.connection = connect();
	}
    
    /**
     * 获取操作实例
     * @return
     */
    public static SmackManager getInstance() {
    	if(xmppManager == null) {
    		synchronized (SmackManager.class) {
				if(xmppManager == null) {
					xmppManager = new SmackManager();
				}
			}
    	}
    	return xmppManager;
    }

	public XMPPTCPConnection getConnection() {
		return connection;
	}

	public void cleanConnection() {
		xmppManager = null;
	}
    
    /**
     * 连接服务器
     * @return
     */
    private XMPPTCPConnection connect() {
    	try {
        	XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
        	.setHost(SERVER_IP)//服务器IP地址
        	//服务器端口
        	.setPort(PORT)
        	//服务器名称
        	.setServiceName(SERVER_NAME)
        	//是否开启安全模式
        	.setSecurityMode(XMPPTCPConnectionConfiguration.SecurityMode.disabled)
        	//是否开启压缩
        	.setCompressionEnabled(false)
        	//开启调试模式
        	.setDebuggerEnabled(true).build();
        	
        	XMPPTCPConnection connection = new XMPPTCPConnection(config);
            connection.connect();
			SERVER_NAME = connection.getServiceName();
            return connection;
        } catch (Exception e) {
        	return null;
        }
    }
    
    /**
     * 登陆
     * @param user			用户账号
     * @param password		用户密码
     * @return
     * @throws Exception
     */
    public boolean login(String user, String password) throws Exception {
    	if(!isConnected()) {
    		return false;
    	}
        try {
			Log.e("Login", "用户名" + user + "密码" + password);
			SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");
        	connection.login(user, password);
            return  true;
        } catch (Exception e) {
            e.printStackTrace();
			throw e;
        }
    }
    
    /**
     * 注销
     * @return
     */
    public boolean logout() {
    	if(!isConnected()) {
    		return false;
    	}
        try {
        	connection.instantShutdown();
            connection = null;
			return  true;
        } catch (Exception e) {
            e.printStackTrace();
            return  false;
        }
    }
    
    /**
     * 删除当前登录的用户信息(从服务器上删除当前用户账号)
     * @return
     */
    public boolean deleteUser() {
    	if(!isConnected()) {
    		return false;
    	}
    	try {
			AccountManager.getInstance(connection).deleteAccount();//删除该账号

			return true;
		} catch (NoResponseException | XMPPErrorException
				| NotConnectedException e) {
			return false;
		}
    }

	//删除某个好友
	public boolean deleteFriend(String friendName){
		boolean b=true;
		try {
			getInstance().getRoster().removeEntry(roster.getEntry(friendName));
		} catch (Exception e) {
			b=false;
		}
		return b;
	}
    
    /**
     * 注册用户信息
     * @param username		账号
     * @param password		账号密码
     * @param attributes	账号其他属性，参考AccountManager.getAccountAttributes()的属性介绍
     * @return
     */
    public boolean registerUser(String username, String password, Map<String, String> attributes) {
    	if(!isConnected()) {
    		return false;
    	}
    	try {
			AccountManager.getInstance(connection).createAccount(username, password, attributes);
			return true;
		} catch (NoResponseException | XMPPErrorException
				| NotConnectedException e) {
			Log.e(TAG, "注册失败", e);
			return false;
		}
    }
    
    /**
     * 修改密码
     * @param newpassword	新密码
     * @return
     */
    public boolean changePassword(String newpassword) {
    	if(!isConnected()) {
    		return false;
    	}
		try {
			AccountManager.getInstance(connection).changePassword(newpassword);
			return true;
		} catch (NoResponseException | XMPPErrorException | NotConnectedException e) {
			Log.e(TAG, "密码修改失败", e);
			return false;
		}
    }
    
    /**
     * 断开连接，注销
     * @return
     */
    public boolean disconnect() {
    	if(!isConnected()) {
    		return false;
    	}
    	connection.disconnect();
    	return true;
    }
    
    /**
     * 更新用户状态
     * @param code
     * @return
     */
    public boolean updateUserState(int code) {
    	if(!isConnected()) {
    		throw new NullPointerException("服务器连接失败，请先连接服务器");
    	}
    	try {
	    	Presence presence;  
	        switch (code) {  
	            case 0://设置在线
					presence = new Presence(Presence.Type.available);   //这里如果改成unavailable则会显示用户不在线
					presence.setStatus("在线");
					connection.sendPacket(presence);
					//connection.getRoster();
	                //presence = new Presence(Presence.Type.available);
	                //presence.setMode(Presence.Mode.available);
					//connection.sendStanza(presence);
	                break;  
	            case 1://设置Q我吧
	                presence = new Presence(Presence.Type.available);  
	                presence.setMode(Presence.Mode.chat);  
	                connection.sendStanza(presence);  
	                break;
	            case 5://设置离线
					//Presence presence1 = new Presence(Presence.Type.available);   //这里如果改成unavailable则会显示用户不在线
					//presence1.setStatus("在线");
					//connection.sendPacket(presence1);

	                presence = new Presence(Presence.Type.unavailable);  
	                connection.sendPacket(presence);
	                break;
	            default:  
	                break;  
	            } 
	        return true;
    	} catch (NotConnectedException e) {
    		e.printStackTrace();
    	}  
    	return false;
    }
    
    /**
     * 是否连接成功
     * @return
     */
    private boolean isConnected() {
    	if(connection == null) {
    		return false;
    	}
    	if(!connection.isConnected()) {
    		try {
				connection.connect();
				return true;
			} catch (SmackException | IOException | XMPPException e) {
				return false;
			}
    	}
    	return true;
    }
    
    /**
     * 获取账户昵称
     * @return
     */
    public String getAccountName() {
    	if(isConnected()) {
    		try {
    			return AccountManager.getInstance(connection).getAccountAttribute("nickname");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
    	}
    	throw new NullPointerException("服务器连接失败，请先连接服务器");
    }
    
    /**
     * 获取账户所有属性信息
     * @return
     */
    public Set<String> getAccountAttributes() {
    	if(isConnected()) {
    		try {
    			return AccountManager.getInstance(connection).getAccountAttributes();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
    	}
    	throw new NullPointerException("服务器连接失败，请先连接服务器");
    }
    
    /**
     * 创建聊天窗口
     * @param jid   好友的JID
     * @return
     */
    public Chat createChat(String jid) {
    	if(isConnected()) {
    		ChatManager chatManager = ChatManager.getInstanceFor(connection);
    		return chatManager.createChat(jid);
    	}
    	throw new NullPointerException("服务器连接失败，请先连接服务器");
    }
    
    /**
     * 获取聊天对象管理器
     * @return
     */
    public ChatManager getChatManager() {
    	if(isConnected()) {
    		ChatManager chatManager = ChatManager.getInstanceFor(connection);
    		return chatManager;
    	}
    	throw new NullPointerException("服务器连接失败，请先连接服务器");
    }

	public Roster getRoster() {
		if(roster == null) {
			roster = Roster.getInstanceFor(connection);
		}
		return roster;
	}
    
    /**
     * 获取当前登录用户的所有好友信息
     * @return
     */
    public List<AddFriend> getAllFriends() {
    	if(isConnected()) {
			List<AddFriend> afList = new ArrayList<AddFriend>();
			Roster roster = SmackManager.getInstance().getRoster();
			Collection<RosterEntry> rg = roster.getEntries();
			AddFriend af = null;
			for (RosterEntry re : rg) {
				af = new AddFriend();
				//当状态不为chatjid时
				if(!re.getUser().endsWith("@point-im-server")) {
					af.setUsername(re.getUser());
					af.setNickname(re.getName());
					af.setChatjid(re.getUser() + "@point-im-server");
					Presence presence = roster.getPresence(af.getChatjid());
					System.out.println("Status is :" + re.getUser() + ": " + presence.getStatus() + "|" + presence.getType().name());
					af.setStatus(presence.getStatus()==null?"离线":presence.getStatus());
					afList.add(af);
				}
			}

    		return afList;
    	}
    	throw new NullPointerException("服务器连接失败，请先连接服务器");
    }
    
    /**
     * 获取指定账号的好友信息
     * @param user	账号
     * @return
     */
    public RosterEntry getFriend(String user) throws NullPointerException {
    	if(isConnected()) {
    		return Roster.getInstanceFor(connection).getEntry(user);
    	}
    	throw new NullPointerException("服务器连接失败，请先连接服务器");
    }
    
    /**
     * 添加好友
     * @param user			用户账号
     * @param nickName		用户昵称
     * @param groupName		所属组名
     * @return
     */
    public boolean addFriend(String user, String nickName, String groupName) {
    	if(isConnected()) {
    		try {
				//  将好友添加进好友列表
				Roster.getInstanceFor(connection).createEntry(user, nickName, new String[]{groupName});
				//关注好友的状态
				Presence presence = new Presence(Presence.Type.subscribe);
				presence.setTo(user+ "@point-im-server");
				//presence.setMode(Presence.Mode.available);
				SmackManager.getInstance().getConnection().sendPacket(presence);

				return true;
			} catch (NotLoggedInException | NoResponseException
					| XMPPErrorException | NotConnectedException e) {
				e.printStackTrace();
				return false;
			}
    	}
    	throw new NullPointerException("服务器连接失败，请先连接服务器");
    }
    
    /**
     * 获取聊天对象的Fully的jid值
     * @param nickname		用户昵称
     * @return
     */
    public String getChatJidByName(String nickname) {
    	RosterEntry friend = SmackManager.getInstance().getFriend(nickname);
    	return getChatJidByUser(friend.getUser());
    }
    
    /**
     * 获取聊天对象的Fully的jid值
     * @param rosterUser	用户账号
     * @return
     */
    public String getChatJidByUser(String rosterUser) {
    	if(!isConnected()) {
    		throw new NullPointerException("服务器连接失败，请先连接服务器");
    	}
    	return rosterUser + "@" + connection.getServiceName();
    }
    
    /**
     * 获取文件传输的完全限定Jid
     * The fully qualified jabber ID (i.e. full JID) with resource of the user to send the file to.
     * @param nickname	用户昵称，也就是RosterEntry中的name
     * @return
     */
    public String getFileTransferJid(String nickname) {
    	String chatJid = getChatJidByName(nickname);
    	return getFileTransferJidChatJid(chatJid);
    }
    
    /**
     * 获取文件传输的完全限定Jid
     * The fully qualified jabber ID (i.e. full JID) with resource of the user to send the file to.
     * @param chatJid	与好友聊天的限定JID(如：laohu@192.168.0.108)
     * @return
     */
    public String getFileTransferJidChatJid(String chatJid) {
    	return chatJid + "/" + XMPP_CLIENT;
    }
    
    /**
     * 获取发送文件的发送器
     * @param jid	一个完整的jid(如：laohu@192.168.0.108/Smack，后面的Smack应该客户端类型，不加这个会出错)
     * @return
     */
    public OutgoingFileTransfer getSendFileTransfer(String jid) {
    	if(isConnected()) {
			return FileTransferManager.getInstanceFor(connection).createOutgoingFileTransfer(jid);
    	}
    	throw new NullPointerException("服务器连接失败，请先连接服务器");
    }
    
    /**
     * 添加文件接收的监听
     * @param fileTransferListener
     */
    public void addFileTransferListener(FileTransferListener fileTransferListener) {
    	if(isConnected()) {
			FileTransferManager.getInstanceFor(connection).addFileTransferListener(fileTransferListener);
			return;
    	}
    	throw new NullPointerException("服务器连接失败，请先连接服务器");
    }
    
    /**
     * 创建群聊聊天室
     * @param roomName		聊天室名字
     * @param nickName		创建者在聊天室中的昵称
     * @param password		聊天室密码
     * @return
     */
    public MultiUserChat createChatRoom(String roomName, String nickName, String password) {
    	if(!isConnected()) {
			throw new NullPointerException("服务器连接失败，请先连接服务器");
    	}
    	MultiUserChat muc = null;  
	    try {  
	        // 创建一个MultiUserChat  
	    	muc = MultiUserChatManager.getInstanceFor(connection).getMultiUserChat(roomName + "@conference." + connection.getServiceName());
	        // 创建聊天室  
	        boolean isCreated = muc.createOrJoin(nickName);
	        if(isCreated) {
	        	// 获得聊天室的配置表单  
	        	Form form = muc.getConfigurationForm();  
	        	// 根据原始表单创建一个要提交的新表单。  
	        	Form submitForm = form.createAnswerForm();
	        	// 向要提交的表单添加默认答复  
	        	List<FormField> fields = form.getFields();
	        	for(int i = 0; fields != null && i < fields.size(); i++) {
	        		if(FormField.Type.hidden != fields.get(i).getType() && fields.get(i).getVariable() != null) {  
	        			// 设置默认值作为答复  
	        			submitForm.setDefaultAnswer(fields.get(i).getVariable());  
	        		}  
	        	}
	        	// 设置聊天室的新拥有者  
	        	List<String> owners = new ArrayList<String>();
	        	owners.add(connection.getUser());// 用户JID  
	        	submitForm.setAnswer("muc#roomconfig_roomowners", owners);  
	        	// 设置聊天室是持久聊天室，即将要被保存下来  
	        	submitForm.setAnswer("muc#roomconfig_persistentroom", true);  
	        	// 房间仅对成员开放  
	        	submitForm.setAnswer("muc#roomconfig_membersonly", false);  
	        	// 允许占有者邀请其他人  
	        	submitForm.setAnswer("muc#roomconfig_allowinvites", true);  
	        	if(password != null && password.length() != 0) {  
	        		// 进入是否需要密码  
	        		submitForm.setAnswer("muc#roomconfig_passwordprotectedroom",  true);  
	        		// 设置进入密码  
	        		submitForm.setAnswer("muc#roomconfig_roomsecret", password);  
	        	}  
	        	// 能够发现占有者真实 JID 的角色  
	        	// submitForm.setAnswer("muc#roomconfig_whois", "anyone");  
	        	// 登录房间对话  
	        	submitForm.setAnswer("muc#roomconfig_enablelogging", true);  
	        	// 仅允许注册的昵称登录  
	        	submitForm.setAnswer("x-muc#roomconfig_reservednick", true);  
	        	// 允许使用者修改昵称  
	        	submitForm.setAnswer("x-muc#roomconfig_canchangenick", false);  
	        	// 允许用户注册房间  
	        	submitForm.setAnswer("x-muc#roomconfig_registration", false);  
	        	// 发送已完成的表单（有默认值）到服务器来配置聊天室  
	        	muc.sendConfigurationForm(submitForm);  
	        }
	    } catch (XMPPException | SmackException e) {  
	        e.printStackTrace();  
	        return null;  
	    }  
	    return muc; 
    }
    
    /**
     * 加入一个群聊聊天室
     * @param roomName		聊天室名字
     * @param nickName		用户在聊天室中的昵称
     * @param password		聊天室密码
     * @return
     */
    public MultiUserChat joinChatRoom(String roomName, String nickName, String password) {
    	if(!isConnected()) {
			throw new NullPointerException("服务器连接失败，请先连接服务器");
    	}  
        try {
            // 使用XMPPConnection创建一个MultiUserChat窗口  
            MultiUserChat muc = MultiUserChatManager.getInstanceFor(connection).getMultiUserChat(roomName + "@conference." + connection.getServiceName());  
            // 聊天室服务将会决定要接受的历史记录数量  
            DiscussionHistory history = new DiscussionHistory();  
            history.setMaxChars(0);  
            // history.setSince(new Date());  
            // 用户加入聊天室  
            muc.join(nickName, password);  
            return muc;  
        } catch (XMPPException | SmackException e) {  
            e.printStackTrace();  
            return null;  
        }  
    }

	/**
	 * 搜索好友
	 * @param userName
	 * @return
	 * @throws NotConnectedException
	 * @throws XMPPErrorException
	 * @throws NoResponseException
     */
	public List<AddFriend> searchUser(String userName) throws NotConnectedException, XMPPErrorException, NoResponseException {
		List<AddFriend> results = new ArrayList<AddFriend>();
		try {

		System.out.println("查询开始..............." + connection.getHost()
				+ connection.getServiceName());

		UserSearchManager usm = new UserSearchManager(connection);

		Form searchForm = usm.getSearchForm(("search." + connection.getServiceName()));//"search.point-im-server"
		Form answerForm = searchForm.createAnswerForm();
		answerForm.setAnswer("Username", true);
		answerForm.setAnswer("search", userName);
		ReportedData data = usm.getSearchResults(answerForm, ("search." + connection.getServiceName()));// + connection.getServiceName()

		List<ReportedData.Row> it = data.getRows();
		ReportedData.Row row = null;
			AddFriend friend = null;
		for (int i = 0; i < it.size(); i++) {
			friend = new AddFriend();
			row = it.get(i);
			Log.e("search", "username is : " + row.getValues("Username") + row.getValues("Nickname") + row.getValues("Name"));
			// 若存在，则有返回,UserName一定非空，其他两个若是有设，一定非空
			friend.setUsername(row.getValues("Username").get(0));
			friend.setNickname(row.getValues("Name").get(0));
			results.add(friend);
		}
		} catch (Exception e) {
			 e.printStackTrace();
		}
		return results;
	}

}
