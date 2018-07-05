/*
 * common data convert 
 * 
 * Created by sealy on 2013-07-01. 
 * Copyright 2013 Sealy, Inc. All rights reserved.
 */

package com.sylar.unit;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;
import android.util.Pair;


import java.text.DecimalFormat;
import java.util.Date;

/**
 * common data convert
 *
 * @author Sealy
 */

public abstract class Convert {
    private static final String strTag = "Convert";

    /**
     * get Color form String
     *
     * @param sColor a color string format(#XXXXXX).
     */
    public static int stringToColor(String sColor) {
        try {
            int iColor;
            if (sColor.startsWith("#"))
                iColor = Integer.valueOf(sColor.substring(1), 16);
            else
                iColor = Integer.valueOf(sColor, 16);
            return Color.rgb(Color.red(iColor), Color.green(iColor), Color.blue(iColor));
        } catch (Exception e) {
            Log.e(strTag, "Convert.stringToColor:" + e.toString());
        }
        return 0;
    }

    /**
     * get Color form String
     *
     * @param sColor a RGB color string Format(red,green,blue)
     */
    public static int stringRGBToColor(String sColor) {
        try {
            int iRed = 0, iGreen = 0, iBlue = 0;
            String sTemp[] = sColor.split(",");
            if (sTemp.length >= 1) iRed = Integer.valueOf(sTemp[0]);
            if (sTemp.length >= 2) iRed = Integer.valueOf(sTemp[1]);
            if (sTemp.length >= 3) iRed = Integer.valueOf(sTemp[2]);
            return Color.rgb(iRed, iGreen, iBlue);
        } catch (Exception e) {
            Log.e(strTag, "Convert.stringRGBToColor:" + e.toString());
        }
        return 0;
    }

    /**
     * get string form color (#XXXXXX)
     *
     * @param sColor a color value.
     */
    public static String colorToString(int sColor) {
        try {
            String sRed = Integer.toHexString(Color.red(sColor));
            sRed = sRed.length() < 2 ? ('0' + sRed) : sRed;    //fill two characters
            String sBule = Integer.toHexString(Color.blue(sColor));
            sBule = sBule.length() < 2 ? ('0' + sBule) : sBule;    //fill two characters
            String sGreen = Integer.toHexString(Color.green(sColor));
            sGreen = sGreen.length() < 2 ? ('0' + sGreen) : sGreen;    //fill two characters
            return '#' + sRed + sGreen + sBule;
        } catch (Exception e) {
            Log.e(strTag, "Convert.colorToString:" + e.toString());
        }
        return null;
    }

    /**
     * get string form color(red,green,blue)
     *
     * @param sColor a color value.
     */
    public static String colorToRGBString(int sColor) {
        try {
            String sRed = Integer.toString(Color.red(sColor));
            String sBule = Integer.toString(Color.blue(sColor));
            String sGreen = Integer.toString(Color.green(sColor));
            return sRed + "," + sGreen + "," + sBule;
        } catch (Exception e) {
            Log.e(strTag, "Convert.colorToRGBString:" + e.toString());
        }
        return null;
    }

    /**
     * format DateTime
     *
     * @param dDate   Waiting for the formatted date. today=new Date()
     * @param sFormat the format string.format:yyyy-MM-dd HH:mm:ss
     * @return String Get a string for the formatted date.
     */
    @SuppressLint("SimpleDateFormat")
    public static String dateToString(Date dDate, String sFormat) {
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(sFormat);
            return sdf.format(dDate);
        } catch (Exception e) {
            Log.e(strTag, "Convert.dateToString:" + e.toString());
        }
        return null;
    }

    /**
     * Get date From String
     *
     * @param dDate   a DateTime string
     * @param sFormat the format string.format:yyyy-MM-dd HH:mm:ss
     * @return Format Get a Date from string.
     */
    @SuppressLint("SimpleDateFormat")
    public static Date stringToDate(String dDate, String sFormat) {
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(sFormat);
            return (Date) sdf.parse(dDate);
        } catch (Exception e) {
            Log.e(strTag, "Convert.stringToDate:" + e.toString());
        }
        return null;
    }

    /**
     * Get Hex String From byte
     *
     * @param buf a b group
     * @return HexString.
     */
    public static String byteToHexString(byte[] buf) {
        try {
            char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
            StringBuilder sb = new StringBuilder(buf.length * 2);
            for (int i = 0; i < buf.length; i++) {
                sb.append(HEX_DIGITS[(buf[i] & 0xf0) >> 4]);
                sb.append(HEX_DIGITS[buf[i] & 0x0f]);
            }
            return sb.toString();
        } catch (Exception e) {
            Log.e(strTag, "Convert.byteToHexString:" + e.toString());
        }
        return null;
    }

    /**
     * Get Hex char From byte
     *
     * @param binary binary
     * @return char hex
     */
    public static char binaryToHex(int binary) {
        char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        return HEX_DIGITS[binary];
    }


    /**
     * Get Hex String From String
     *
     * @param txt a String
     * @return HexString.
     */
    public static String stringToHexString(String txt) {
        return byteToHexString(txt.getBytes());
    }

    /**
     * Get String From Hex String
     *
     * @param hexString a Hex String
     * @return String.
     */
    public static String stringfromHexString(String hexString) {
        return new String(stringToByte(hexString));
    }

    /**
     * Get byte[] From Hex String
     *
     * @param hexString a Hex String
     * @return byte[].
     */
    public static byte[] stringToByte(String hexString) {
        try {
            int len = hexString.length() / 2;
            byte[] result = new byte[len];
            for (int i = 0; i < len; i++)
                result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
            return result;
        } catch (Exception e) {
            Log.e(strTag, "Convert.stringToByte:" + e.toString());
        }
        return null;
    }

    public static String pairsToJson(Pair<String, String>... pairs) {
        String result = "";
        result = "{";
        for (int i = 0; i < pairs.length; i++) {
            result += "\"" + pairs[i].first + "\"";
            result += ":";
            result += "\"" + pairs[i].second + "\"";
            result += ",";
        }
        result = result.length() == 1 ? result + "}" : result.substring(0, result.length() - 1) + "}";
        return result;
    }

    //加密json
    public static String securityJson(String json) {
        try {
            json = Security.encrypt(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }


    //金额保留2位小数
    public static String getMoneyString(double a) {
        double d1 = (double) (Math.round(a*10000)/10000.0000000000);
        DecimalFormat df = new DecimalFormat("0.00");
        String result = df.format(d1);
        return result;
    }
}
