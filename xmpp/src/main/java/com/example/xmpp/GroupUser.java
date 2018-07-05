package com.example.xmpp;

public class GroupUser {
	
	public String id;
	public String name;
	public boolean isGroup;

	
	public GroupUser(String id, String name, boolean isGroup) {
		this.id = id;
		this.name = name;
		this.isGroup = isGroup;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


}
