package model;


/**
 * @author yuanyi
 * 动态
 */
public class Trends {
	private String content;  //动态内容
	private String trendsid; //动态编号
	private String name; //昵称
	private String token;  //令牌
	private String username; //用户名
	private String imagestr; //图片信息
	private String circlenum; //圈号
	private String circlename; //主题
	private int type; //类型 0 是好友动态 1是圈动态


	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTrendsid() {
		return trendsid;
	}

	public void setTrendsid(String trendsid) {
		this.trendsid = trendsid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getImagestr() {
		return imagestr;
	}

	public void setImagestr(String imagestr) {
		this.imagestr = imagestr;
	}

	public String getCirclenum() {
		return circlenum;
	}

	public void setCirclenum(String circlenum) {
		this.circlenum = circlenum;
	}

	public String getCirclename() {
		return circlename;
	}

	public void setCirclename(String circlename) {
		this.circlename = circlename;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
