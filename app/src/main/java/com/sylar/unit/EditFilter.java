package com.sylar.unit;

import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Wikison on 2016/8/17.
 */
public final class EditFilter {
    private EditFilter() {
    }


    /**
     * 限制输入金额, 精确到小数点后两位
     */
    public static void CashFilter(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        editText.setText(s);
                        editText.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    editText.setText(s);
                    editText.setSelection(2);
                }
                if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        editText.setText(s.subSequence(0, 1));
                        editText.setSelection(1);
                        return;
                    }
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }
    /**
     * 限制输入金额, 精确到小数点后两位, 最大金额
     */
    public static void CashFilter(final EditText editText, final double MAX_VALUE) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        editText.setText(s);
                        editText.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    editText.setText(s);
                    editText.setSelection(2);
                }
                if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        editText.setText(s.subSequence(0, 1));
                        editText.setSelection(1);
                        return;
                    }
                }

                if (s.toString().length() >= 1 && !s.toString().endsWith(".") && Double.valueOf(s.toString()) > 0) {
                    if (Double.valueOf(s.toString()) > MAX_VALUE) {
                        editText.setError("最大值不能超过" + Convert.getMoneyString(MAX_VALUE));
                        s = s.toString().subSequence(0, s.toString().length() - 1);
                        editText.setText(s);
                        editText.setSelection(s.length());
                        return;
                    }

                }


            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    /**
     * 限制输入整数 最大值
     */
    public static void IntegerFilter(final EditText editText, final int MAX_VALUE) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.toString().length() >= 1) {
                    if (s.toString().substring(0).equals("0")) {
                        editText.setText("");
                        return;
                    } else if (Integer.valueOf(s.toString()) > MAX_VALUE) {
                        editText.setError("最大值不能超过" + Convert.getMoneyString(MAX_VALUE));
                        s = s.toString().subSequence(0, s.toString().length() - 1);
                        editText.setText(s);
                        editText.setSelection(s.length());
                        return;
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    /**
     * 限制内容长度, 显示可输入剩余字数
     */
    public static void WordFilter(final EditText editText, final int MAX_CN, final TextView textView) {
        textView.setText(MAX_CN + "");
        TextWatcher textWatcher = new TextWatcher() {
            private CharSequence temp;
            private int selectionStart;
            private int selectionEnd;

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                temp = s;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int number = MAX_CN - s.length();
                textView.setText("" + number);
                selectionStart = editText.getSelectionStart();
                selectionEnd = editText.getSelectionEnd();
                if (temp.length() > MAX_CN) {
                    s.delete(selectionStart - 1, selectionEnd);
                    int tempSelection = selectionStart;
                    editText.setText(s);
                    //设置光标在最后
                    editText.setSelection(tempSelection);
                }
            }
        };
        editText.addTextChangedListener(textWatcher);

        //设置不能输入emoji
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (isHasEmoji(source)) {
                    return "";
                }
                return source;
            }
        };
        editText.setFilters(filters);


    }
    /**
     * 限制内容长度, 显示可输入剩余字数
     */
    public static void WordFilterNoemoji(final EditText editText, final int MAX_CN) {
        TextWatcher textWatcher = new TextWatcher() {
            private CharSequence temp;
            private int selectionStart;
            private int selectionEnd;

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                temp = s;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int number = MAX_CN - s.length();
                selectionStart = editText.getSelectionStart();
                selectionEnd = editText.getSelectionEnd();
                if (temp.length() > MAX_CN) {
                    s.delete(selectionStart - 1, selectionEnd);
                    int tempSelection = selectionStart;
                    editText.setText(s);
                    //设置光标在最后
                    editText.setSelection(tempSelection);
                }
            }
        };
        editText.addTextChangedListener(textWatcher);

        //设置不能输入emoji
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (isHasEmoji(source)) {
                    return "";
                }
                return source;
            }
        };
        editText.setFilters(filters);


    }

    /**
     * 限制内容长度
     */
    public static void WordFilter(final EditText editText, final int MAX_CN) {
        TextWatcher textWatcher = new TextWatcher() {
            private CharSequence temp;
            private int selectionStart;
            private int selectionEnd;

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                temp = s;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                selectionStart = editText.getSelectionStart();
                selectionEnd = editText.getSelectionEnd();
                if (temp.length() > MAX_CN) {
                    s.delete(selectionStart - 1, selectionEnd);
                    int tempSelection = selectionStart;
                    editText.setText(s);
                    //设置光标在最后
                    editText.setSelection(tempSelection);
                }
            }
        };

        editText.addTextChangedListener(textWatcher);

        //设置只能输入中英文数字
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (!isChineseA1(source)) {
                    return "";
                }
                return source;
            }
        };
        editText.setFilters(filters);
    }

    private  static boolean isChineseA1(CharSequence source){
            Pattern p = Pattern.compile("^[a-zA-Z0-9\u4E00-\u9FA5]+$");
            Matcher matcher = p.matcher(source);
            if (matcher.find()) {
                return true;
            }
            return false;

    }

    //过滤emoji
    private static boolean isHasEmoji(CharSequence source) {
        Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

        Matcher emojiMatcher = emoji.matcher(source);
        if (emojiMatcher.find()) {
            return true;
        }
        return false;
    }

    /**
     * 检测是否有emoji表情
     *
     * @param source
     * @return
     */
    private static boolean containsEmoji(String source) {
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isEmojiCharacter(codePoint)) { //如果不能匹配,则该字符是Emoji表情
                return true;
            }
        }
        return false;
    }

    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) ||
                (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000)
                && (codePoint <= 0x10FFFF));
    }

}
