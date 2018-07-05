package model;

/**
 * 
 */


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author MZH
 *
 */
public class Room implements Serializable {
	public String name;
	public String description;
	public String roomid;
	public List<String> friendList = new ArrayList<String>();

	private String jid;
	private int roomID;
	public Room() {
		super();
	}

	
	public Room(String name){
		this.name = name;
	}
	
	@Override
	 public boolean equals(Object obj) {
	  boolean isEqual = false;
	  if (obj instanceof Room) {
		  Room t = (Room) obj;
		  isEqual = this.name.equals(t.name);
		  return isEqual;
	  }
	  return super.equals(obj);
	 }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getJid() {
		return jid;
	}

	public void setJid(String jid) {
		this.jid = jid;
	}

	public int getRoomID() {
		return roomID;
	}

	public void setRoomID(int roomID) {
		this.roomID = roomID;
	}
}
