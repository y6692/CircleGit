package com.example.xmpp;


import android.content.Context;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tool {
	public static String tofirstLowerCase(String str) {

		if (str != null && str.length() > 0) {
			return str.substring(0, 1).toLowerCase() + str.substring(1, str.length());
		} else if (str != null && str.length() == 0) {
			return str.toLowerCase();
		} else {
			return str;
		}

	}

	// Toast
	public static void initToast(Context c, String title) {
		try {
			Toast.makeText(c, title, Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static void initToast_Short(Context c, String title) {
		try {
			Toast.makeText(c, title, Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	
	/**
	 * @param email
	 */
	public static boolean emailFormat(String email) {
		boolean tag = true;
		final String pattern1 = "^([a-zA-Z0-9]){1}([a-zA-Z0-9_\\.-])+@(([a-zA-Z0-9-])+\\.)+([a-zA-Z0-9]{2,4})+$";
		final Pattern pattern = Pattern.compile(pattern1);
		final Matcher mat = pattern.matcher(email);
		if (!mat.find()) {
			tag = false;
		}
		return tag;
	}
}
