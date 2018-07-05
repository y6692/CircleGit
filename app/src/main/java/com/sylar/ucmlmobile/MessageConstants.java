package com.sylar.ucmlmobile;

import android.graphics.Bitmap;

import com.sylar.model.ChatItem;
import com.sylar.model.User;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageConstants {
	public static int friendschange=0;
	public static int ffriends=0;
	public static HashMap<String, List<ChatItem>> msgMap = new HashMap<String, List<ChatItem>>();
	public static HashMap<String, Map> movieMap = new HashMap<String, Map>();
	public static List<Msg> listMsg = new ArrayList<Msg>();
	public static HashMap<String, List<ChatItem>> recentlyMap = new HashMap<String, List<ChatItem>>();
	public static List<String> recently = new ArrayList<String>();
	public static HashMap<String, List<ChatItem>> rosterMap = new HashMap<String, List<ChatItem>>();
	public static List<String> roster = new ArrayList<String>();
	public static List<GroupUser> group_user = new ArrayList<GroupUser>();
	public static List<GroupUser> group_user_all = new ArrayList<GroupUser>();
	public static List<User> friendslist = new ArrayList<User>();
	public static List<User> friendslist_all = new ArrayList<User>();
	public static List<User> searchlist = new ArrayList<User>();
	public static List<String> addfriendslist = new ArrayList<String>();

	public static boolean isFriend(String username) {
		for(int i = 0; i < friendslist.size(); i++) {
			if(friendslist.get(i).getUsername().equals(username)) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasMe(String username) {
		for(int i = 0; i < friendslist_all.size(); i++) {
			if(friendslist_all.get(i).getUsername().equals(username)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isMyroom(String roomname) {
		for(int i = 0; i < XmppConnection.myRooms.size(); i++) {
			if(XmppConnection.myRooms.get(i).name.equals(roomname)) {
				return true;
			}
		}
		return false;
	}

	public static Bitmap findAllHeadById(String username) {
		Bitmap bmp=null;
		for(int i = 0; i < friendslist_all.size(); i++) {
			if(friendslist_all.get(i).getUsername().equals(username)) {
				bmp = friendslist_all.get(i).getBitmap();
				break;
			}
		}
		return bmp;
	}

	public static Bitmap findFriendHeadById(String username) {
		Bitmap bmp=null;
		for(int i = 0; i < friendslist.size(); i++) {
			if(friendslist.get(i).getUsername().equals(username)) {
				bmp = friendslist.get(i).getBitmap();
				break;
			}
		}
		return bmp;
	}
	
	public static String findNameById(String username) {
		String name = "";
		for(int i = 0; i < friendslist.size(); i++) {
			if(friendslist.get(i).getUsername().equals(username)) {
				name = friendslist.get(i).getName();
				break;
			}
		}

		if(name.equals("")) {
			name = username;
		}
		return name;
	}

	public static void delNameById(String username) {
		for(int i = 0; i < friendslist.size(); i++) {
			if(friendslist.get(i).getUsername().equals(username)) {
				friendslist.remove(i);
				break;
			}
		}
	}

	public static String getNameById(String id) {
		List<User> list = XmppConnection.getInstance().searchMember(id);

		if(list.size()!=0){
			String name = list.get(0).getName();

			return name;
		}else{

			return id;
		}
	}

	public static String getChatname(JSONArray arr, String username) {
		String name = "";
		for(int i = 0; i < arr.length(); i++) {
			try {
				if(arr.getJSONObject(i).getString("oid").equals(username)) {
					name = arr.getJSONObject(i).getString("name");
					break;
				}
			} catch(JSONException e) {
				e.printStackTrace();
			}
		}
		return name;
	}

}
