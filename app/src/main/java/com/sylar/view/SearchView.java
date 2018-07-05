package com.sylar.view;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.circlegit.R;


/**
 * Created by wikison on 2016/6/8.
 */
public class SearchView extends LinearLayout implements View.OnClickListener {

    /**
     * 输入框
     */
    private EditText etInput;

    /**
     * 删除键
     */
    private ImageView ivDelete;

    /**
     * 返回按钮
     */
    private ImageView ivBack;
    private TextView tvCancel;
    private LinearLayout llBack;
    /**
     * 背景
     */
    private LinearLayout llRoot;

    /**
     * 上下文对象
     */
    private Context mContext;

    /**
     * 搜索回调接口
     */
    private SearchViewListener mListener;

    private String strHint;

    private String strSearch;

    private int maxWordNum;
    private boolean unThinking;

    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.view_search, this);
        initViews();
    }

    public String getStrHint() {
        return etInput.getHint().toString();
    }

    public void setStrHint(String strHint) {
        etInput.setHint(strHint);
    }

    public int getMaxWordNum() {
        return maxWordNum;
    }

    public void setMaxWordNum(int maxWordNum) {
        this.maxWordNum = maxWordNum;
    }

    public String getStrSearch() {
        return etInput.getText().toString();
    }

    public void setStrSearch(String strSearch) {
        this.etInput.setText(strSearch);
    }

    //View.VISIBLE GONE
    public void setBackVisible(int visible){
        llBack.setVisibility(visible);
    }

    public void setTvCancelVisible(int visible){
        tvCancel.setVisibility(visible);
    }

    public void setTvCancelColor(int colorId){
        tvCancel.setTextColor(colorId);
    }
    public void setBgColor(int colorId){
        llRoot.setBackgroundColor(colorId);
    }

    public void setTvCancelSize(int textSize){
        tvCancel.setTextSize(textSize);
    }

    public void setUnThinking(boolean unThinking) {
        this.unThinking = unThinking;
    }

    public boolean isUnThinking() {
        return unThinking;
    }

    /**
     * 设置搜索回调接口
     *
     * @param listener 监听者
     */
    public void setSearchViewListener(SearchViewListener listener) {
        mListener = listener;
    }

    private void initViews() {
        etInput = (EditText) findViewById(R.id.search_et_input);
        ivDelete = (ImageView) findViewById(R.id.search_iv_delete);
        ivBack = (ImageView) findViewById(R.id.search_btn_back);
        llBack = (LinearLayout) findViewById(R.id.ll_back);
        llRoot = (LinearLayout) findViewById(R.id.ll_root);
        tvCancel = (TextView) findViewById(R.id.tv_cancel);
        ivDelete.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        llBack.setOnClickListener(this);
        tvCancel.setOnClickListener(this);

        etInput.setMaxLines(1);
        tvCancel.setTextColor(getResources().getColor(R.color.white));
        tvCancel.setTextSize(16);

        etInput.addTextChangedListener(new EditChangedListener());
        etInput.setOnClickListener(this);
        etInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    notifyStartSearching(etInput.getText().toString());
                }
                return true;
            }
        });
    }

    /**
     * 通知监听者 进行搜索操作
     *
     * @param text
     */
    private void notifyStartSearching(String text) {
        if (mListener != null) {
            mListener.onSearch(etInput.getText().toString());
        }
        //隐藏软键盘
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_et_input:
                break;
            case R.id.search_iv_delete:
                etInput.setText("");
                ivDelete.setVisibility(GONE);
                if (mListener != null) {
                    mListener.onClear();
                }
                break;
            case R.id.ll_back:
            case R.id.search_btn_back:
            case R.id.tv_cancel:
                if (onBackClickListner != null)
                    onBackClickListner.onBackClick();

                ((Activity) mContext).onBackPressed();
                break;
        }
    }

    public interface OnBackClickListener {
        void onBackClick();
    }

    private OnBackClickListener onBackClickListner;

    public void setOnBackClickListner(OnBackClickListener onBackClickListner) {
        this.onBackClickListner = onBackClickListner;
    }

    public interface OnThinkingClickListener {
        void onThinkingClick(String text);
    }

    private OnThinkingClickListener onThinkingClickListener;

    public void setOnThinkingClickListener(OnThinkingClickListener onThinkingClickListener) {
        this.onThinkingClickListener = onThinkingClickListener;
    }

    /**
     * search view回调方法
     */
    public interface SearchViewListener {
        /**
         * 开始搜索
         *
         * @param text 传入输入框的文本
         */
        void onSearch(String text);
        void onClear();
    }

    //设置控件的输入字符最大数
    public void setFilter() {
        InputFilter[] filters = {new InputFilter.LengthFilter(maxWordNum)};
        etInput.setFilters(filters);
    }

    private class EditChangedListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (!"".equals(charSequence.toString())) {
                ivDelete.setVisibility(VISIBLE);
            } else {
                ivDelete.setVisibility(GONE);
            }
            if (onThinkingClickListener != null && !isUnThinking()) {
                onThinkingClickListener.onThinkingClick(etInput.getText().toString());
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

}
