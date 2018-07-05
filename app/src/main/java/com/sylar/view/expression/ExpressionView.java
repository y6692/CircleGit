package com.sylar.view.expression;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.administrator.circlegit.R;
import com.sylar.adapter.EmojiAdapter;
import com.sylar.model.Emoji;

import java.util.ArrayList;
import java.util.List;


/**
 * 自定义表情控件，包括静态表情和动态表情 使用方法：在布局文件引用，然后在类文件 expressionView = (ExpressionView)
 * findViewById(R.id.expression_view); expressionView.setEditText(msgEditText);
 * 需要弹出表情控件时 把expressionView.setVisibility(View.VISIBLE);即可
 * <p>
 * 不需要动态表情的话，expressionView.setNoGif();
 * 此控件与Expressions，ExpressionUtil两个类配套使用，不然解析不了表情类
 *
 * @author mzh
 */
public class ExpressionView extends LinearLayout implements OnItemClickListener {
    private ViewPager vpEmoji;
    private LinearLayout llIndex;
    // 表情
    private EditText msgEditText;
    private int[] expressionImages;
    private String[] expressionImageNames;
    private Context mContext;
    private int pageSize = 24;
    /**
     * 保存于内存中的表情集合
     */
    private List<Emoji> mMsgEmojiData = new ArrayList<Emoji>();
    /**
     * 表情分页的结果集合
     */
    public List<List<Emoji>> mPageEmojiDatas = new ArrayList<List<Emoji>>();

    /**
     * 表情页界面集合
     */
    private ArrayList<View> pageViews;

    /**
     * 表情数据填充器
     */
    private List<EmojiAdapter> faceAdapters;


    /**
     * 当前表情页
     */
    private int current = 0;
    /**
     * 游标点集合
     */
    private ArrayList<ImageView> pointViews;

    @SuppressLint("NewApi")
    public ExpressionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public ExpressionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ExpressionView(Context context) {
        super(context);
        init(context);
    }

    public void setEditText(EditText msgEditText) {
        this.msgEditText = msgEditText;
    }

    private void init(Context context) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.expression_view, this);
        // 引入表情
        expressionImages = Expressions.expressionImgs;
        expressionImageNames = Expressions.expressionImgNames;
        Log.e("djy", "ddd");

        llIndex = (LinearLayout) view.findViewById(R.id.ll_index);
        vpEmoji = (ViewPager) view.findViewById(R.id.vp_emoji);
        parseData();
        initView();
    }

    /**
     * 分页
     */
    private void parseData() {
        try {
            int len = expressionImages.length;
            for (int i = 0; i < len; i++) {
                Emoji emoji = new Emoji();
                emoji.setResId(expressionImages[i]);
                emoji.setName(expressionImageNames[i]);
                mMsgEmojiData.add(emoji);
            }
            int pageCount = (int) Math.ceil(mMsgEmojiData.size() / pageSize + 0.1);
            for (int i = 0; i < pageCount; i++) {
                mPageEmojiDatas.add(getData(i));
            }
        } catch (Exception e) {
            Log.e("djy", e.toString(), e);
        }
    }

    /**
     * 获取分页数据
     *
     * @param page
     * @return
     */
    private List<Emoji> getData(int page) {
        int startIndex = page * pageSize;
        int endIndex = startIndex + pageSize;
        if (endIndex > mMsgEmojiData.size()) {
            endIndex = mMsgEmojiData.size();
        }
        List<Emoji> list = new ArrayList<Emoji>();
        list.addAll(mMsgEmojiData.subList(startIndex, endIndex));
        return list;
    }

    private void initView() {
        Init_viewPager();
        Init_Point();
        Init_Data();
    }

    private void Init_viewPager() {
        pageViews = new ArrayList<View>();
        // 左侧添加空页
        View nullView1 = new View(mContext);
        // 设置透明背景
        nullView1.setBackgroundColor(Color.TRANSPARENT);
        pageViews.add(nullView1);

        // 中间添加表情页
        faceAdapters = new ArrayList<EmojiAdapter>();
        for (int i = 0; i < mPageEmojiDatas.size(); i++) {
            GridView view = (GridView) LayoutInflater.from(mContext).inflate(R.layout.msg_face_gridview, null);
            EmojiAdapter adapter = new EmojiAdapter(mContext, mPageEmojiDatas.get(i));
            view.setSelector(R.color.transparent);
            view.setAdapter(adapter);
            faceAdapters.add(adapter);
            view.setOnItemClickListener(this);
            pageViews.add(view);
        }

        // 右侧添加空页面
        View nullView2 = new View(mContext);
        // 设置透明背景
        nullView2.setBackgroundColor(Color.TRANSPARENT);
        pageViews.add(nullView2);
    }

    /**
     * 初始化游标
     */
    private void Init_Point() {
        pointViews = new ArrayList<ImageView>();
        ImageView imageView;
        for (int i = 0; i < pageViews.size(); i++) {
            imageView = new ImageView(mContext);
            imageView.setBackgroundResource(R.drawable.xml_round_blue_grey_sel);
            LayoutParams layoutParams = new LayoutParams(new ViewGroup.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 10;
            layoutParams.rightMargin = 10;
            layoutParams.width = 12;
            layoutParams.height = 12;
            llIndex.addView(imageView, layoutParams);
            if (i == 0 || i == pageViews.size() - 1) {
                imageView.setVisibility(View.GONE);
            } else {
                imageView.setVisibility(View.VISIBLE);
            }
            pointViews.add(imageView);
        }
    }

    /**
     * 填充数据
     */
    private void Init_Data() {
        vpEmoji.setAdapter(new ViewPagerAdapter(pageViews));
        vpEmoji.setCurrentItem(1);
        // 描绘分页点
        draw_Point(1);
        current = 0;
        vpEmoji.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                current = arg0 - 1;
                // 描绘分页点
                draw_Point(arg0);
                // 如果是第一屏或者是最后一屏禁止滑动，其实这里实现的是如果滑动的是第一屏则跳转至第二屏，如果是最后一屏则跳转到倒数第二屏.
                if (arg0 == pointViews.size() - 1 || arg0 == 0) {
                    if (arg0 == 0) {
                        vpEmoji.setCurrentItem(arg0 + 1);// 第二屏 会再次实现该回调方法实现跳转.
                    } else {
                        vpEmoji.setCurrentItem(arg0 - 1);// 倒数第二屏
//						pointViews.get(arg0 - 1).setBackgroundResource(R.drawable.icon_jw_face_index_prs);
                    }
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Emoji emoji = faceAdapters.get(current).getItem(position);
        Bitmap bitmap = null;
        bitmap = BitmapFactory.decodeResource(getResources(),
                emoji.getResId());

        Matrix matrix = new Matrix();
        matrix.postScale(0.7f, 0.7f); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);

        ImageSpan imageSpan = new ImageSpan(getContext(), resizeBmp);
        SpannableString spannableString = new SpannableString(
                emoji.getName());
        spannableString.setSpan(imageSpan, 0,
                emoji.getName().length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 编辑框设置数据
        msgEditText.append(spannableString);
    }

    class ViewPagerAdapter extends PagerAdapter {

        private List<View> pageViews;

        public ViewPagerAdapter(List<View> pageViews) {
            super();
            this.pageViews = pageViews;
        }

        // 显示数目
        @Override
        public int getCount() {
            return pageViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(pageViews.get(arg1));
        }


        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(pageViews.get(arg1));
            return pageViews.get(arg1);
        }
    }

    /**
     * 绘制游标背景
     */
    public void draw_Point(int index) {
        for (int i = 1; i < pointViews.size(); i++) {
            if (index == i) {
                pointViews.get(i).setEnabled(true);
            } else {
                pointViews.get(i).setEnabled(false);
            }
        }
    }
}
