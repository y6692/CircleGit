package com.sylar.model;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.sylar.model.apimodel.APIM_getMucManager;
import com.sylar.unit.ImageUtil;
import com.sylar.unit.JsonUtil;

import org.jivesoftware.smackx.packet.VCard;

/**
 * @author MZH
 *
 */
public class User {
	private String nickname;
	private String truename;
	private String headimg;
	private String intro;
	private String mobile;
	private String sex;
	private String adr;
	public VCard vCard;
	private String header;
	private String nicknamepinyin; // 拼音
	private Bitmap bitmap;
	private double lat = 0.0;
	private double lon = 0.0;

	// djy add
	private String username; // 用户名
	private String name;     // 昵称
	private String email;    // 邮箱
	private String plainPassword; // 密码(md5加密)
	private String Accesstoken;
	private String telephone; // 手机
	private String varCard; // 名片信息string
	private VarCard varCardObj;  // 名片对象
	
	public User() {
		super();
	}
	
	public User(VCard vCard){
		if (vCard!=null) {
//			nickname = vCard.getField("nickName");
//			email = vCard.getField("email");
//			intro = vCard.getField("intro");
			sex = vCard.getField("sex");
//			mobile = vCard.getField("mobile");
			adr = vCard.getField("address");
//			String latAndlon = vCard.getField("latAndlon");-
//			if (latAndlon!=null && !latAndlon.equals("")) {
//				String[] latAndLons = latAndlon.split(",");
//				lat = Double.valueOf(latAndLons[0]);
//				lon = Double.valueOf(latAndLons[1]);
//			}
			this.vCard = vCard;
			bitmap = ImageUtil.getBitmapFromBase64String(vCard.getField("headimg"));
		}
	}


	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getNicknamepinyin() {
		return nicknamepinyin;
	}

	public void setNicknamepinyin(String nicknamepinyin) {
		this.nicknamepinyin = nicknamepinyin;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTruename() {
		return truename;
	}

	public void setTruename(String truename) {
		this.truename = truename;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getHeadimg() {
		return headimg;
	}

	public void setHeadimg(String headimg) {
		this.headimg = headimg;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getAdr() {
		return adr;
	}

	public void setAdr(String adr) {
		this.adr = adr;
	}

	public VCard getvCard() {
		return vCard;
	}

	public void setvCard(VCard vCard) {
		this.vCard = vCard;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public String getPlainPassword() {
		return plainPassword;
	}

	public void setPlainPassword(String plainPassword) {
		this.plainPassword = plainPassword;
	}

	public String getAccesstoken() {
		return Accesstoken;
	}

	public void setAccesstoken(String accesstoken) {
		Accesstoken = accesstoken;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getVarCard() {
		return varCard;
	}

	public void setVarCard(String varCard) {
		this.varCard = varCard;
		varCardObj = JsonUtil.jsonToObject(varCard, VarCard.class);
	}

	public VarCard getVarCardObj() {
		if(varCardObj == null)
			varCardObj = JsonUtil.jsonToObject(varCard, VarCard.class);

		return varCardObj;
	}

	public void setVarCardObj(VarCard varCardObj) {
		this.varCardObj = varCardObj;
	}
}
