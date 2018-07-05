package com.sylar.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.circlegit.R;


public class LoadingDialog extends Dialog {
    /**
     * 缓冲界面
     *
     * @param context
     */
//    public LoadingDialog(Context context) {
//        super(context, R.style.dialog_activity);
//    }
    TextView loadingtext;

    public LoadingDialog(Context context){
        super(context, R.style.dialog_activity);
        View dialogView=getLayoutInflater().inflate(R.layout.dialog_loading,null);
        ImageView ivLoading= (ImageView) dialogView.findViewById(R.id.loading);
        loadingtext= (TextView) dialogView.findViewById(R.id.loadingtext);
        ivLoading.setImageResource(R.drawable.loading_bg);
        setContentView(dialogView);
        AnimationDrawable animationDrawable= (AnimationDrawable) ivLoading.getDrawable();
        animationDrawable.start();
        setCanceledOnTouchOutside(true);
        setCancelable(true);
    }

    public void setMessageText(String messageText){
        loadingtext.setText(messageText);
    }


//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.dialog_loading);
//        laoding = (ImageView) this.findViewById(R.id.loading);
//        laoding.setImageResource(R.drawable.loading_bg);
//        animationDrawable = (AnimationDrawable) laoding.getDrawable();
//        animationDrawable.start();
//    }

}
