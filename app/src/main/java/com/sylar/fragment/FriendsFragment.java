package com.sylar.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.circlegit.R;
import com.sylar.activity.AddDongtaiActivity;
import com.sylar.app.BaseFragment;
import com.sylar.constant.CircleConstants;
import com.sylar.model.ChatItem;
import com.sylar.ucmlmobile.ContextUtil;
import com.sylar.ucmlmobile.FriendActivity;
import com.sylar.ucmlmobile.InviteFriendActivity;
import com.sylar.ucmlmobile.MessageConstants;
import com.sylar.ucmlmobile.NewChatActivity;
import com.sylar.ucmlmobile.RoomInfoActivity;
import com.sylar.ucmlmobile.RoomMemActivity;
import com.sylar.ucmlmobile.SearchFriendActivity;
import com.sylar.ucmlmobile.XmppConnection;
import com.sylar.unit.ImageUtil;
import com.sylar.unit.Tool;
import com.sylar.unit.XmppLoadThread;
import com.sylar.view.SearchView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 好友
 * created by Djy
 * 2017/6/14 8:38
 */
public class FriendsFragment extends BaseFragment {

    @BindView(R.id.lh_tv_title)
    TextView lhTvTitle;
    @BindView(R.id.ll_back)
    LinearLayout llBack;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.ll_right)
    LinearLayout llRight;
    @BindView(R.id.search_view)
    SearchView searchView;
    @BindView(R.id.tab)
    TabLayout tab;
    @BindView(R.id.viewpager)
    ViewPager vp;
    @BindView(R.id.ll_bj)
    RelativeLayout llbj;
    @BindView(R.id.ll_add)
    RelativeLayout lladd;
    @BindView(R.id.ll_jhy)
    LinearLayout lljhy;
    @BindView(R.id.ll_fdt)
    LinearLayout llfdt;


    public static final String TAG = FriendsFragment.class.getSimpleName();
    Unbinder unbinder;
    private String mTitle;
    public static FriendFragment friendFragment;
    private QunFragment qunFragment;
    private MyPagerAdapter myPagerAdapter;
    public static int k=0;
    public static FriendsFragment sf;

    public static FriendsFragment getInstance(String title) {
        FriendsFragment sf = new FriendsFragment();
        sf.mTitle = title;
        return sf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fr_friends, null);
        unbinder = ButterKnife.bind(this, v);
        ContextUtil.ctx = getActivity();

        k++;
        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isFFDestroy",true);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * 初始化
     */
    private void init() {
        lhTvTitle.setText("好友");
        llBack.setVisibility(View.GONE);
        llRight.setVisibility(View.VISIBLE);
        ivRight.setBackgroundResource(R.mipmap.add);
        myPagerAdapter = new MyPagerAdapter(getFragmentManager());
        vp.setAdapter(myPagerAdapter);
        myPagerAdapter.notifyDataSetChanged();
        vp.setOffscreenPageLimit(2);
        tab.setupWithViewPager(vp, true);
        searchView.setBgColor(0xfff2f2f2);
        searchView.setTvCancelVisible(View.GONE);
        searchView.setMaxWordNum(15);
        searchView.setOnThinkingClickListener(new SearchView.OnThinkingClickListener() {
            @Override
            public void onThinkingClick(String text) {
                switch (tab.getSelectedTabPosition()){
                    case 0:
                        friendFragment.filter(text);
                        break;
                    case 1:
                        break;
                }
            }
        });
    }


    /**
     * 网络操作 只能在onHiddenChanged（）判断当前show自己页面时，才去网络操作
     */
    private void net() {
        ContextUtil.curtab = 3;
        friendFragment.filter(null);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        // show自己页面时，才去网络操作
        if (!hidden) {
            if(MessageConstants.ffriends==0){
                MessageConstants.ffriends=1;
                MessageConstants.friendschange=1;
            }

            net();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    class MyPagerAdapter extends FragmentPagerAdapter {
        private String[] titles = new String[]{"好友", "群"};
        private List<Fragment> fragmentList;


        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
            friendFragment = new FriendFragment();
            qunFragment = new QunFragment();
            fragmentList = new ArrayList<>();
            fragmentList.add(friendFragment);
            fragmentList.add(qunFragment);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }


    @OnClick({R.id.iv_right, R.id.ll_bj, R.id.ll_jhy, R.id.ll_fdt})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_right:
                llbj.setVisibility(View.VISIBLE);
                lladd.setVisibility(View.VISIBLE);
                break;

            case R.id.ll_jhy:
                llbj.setVisibility(View.GONE);
                lladd.setVisibility(View.GONE);
                Intent i = new Intent(getActivity(), SearchFriendActivity.class);
                startActivity(i);
                break;

            case R.id.ll_fdt:
                llbj.setVisibility(View.GONE);
                lladd.setVisibility(View.GONE);
                startActivityForResult(new Intent(getActivity(), AddDongtaiActivity.class), 88);
                break;

            case R.id.ll_bj:
                llbj.setVisibility(View.GONE);
                lladd.setVisibility(View.GONE);
                break;

        }
    }



    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0) {
                Tool.initToast(getActivity(), "成员"+"踢出成功");

                FriendActivity.f=0;
                XmppLoadThread.mdialog.dismiss();
            }
        }
    };


}