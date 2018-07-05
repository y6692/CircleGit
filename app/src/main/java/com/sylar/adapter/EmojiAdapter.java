package com.sylar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;


import com.example.administrator.circlegit.R;
import com.sylar.model.Emoji;

import java.util.List;


public class EmojiAdapter extends BaseAdapter {

    private List<Emoji> data;

    private LayoutInflater inflater;

    private int size = 0;
    private Context context;

    public EmojiAdapter(Context context, List<Emoji> list) {
        this.inflater = LayoutInflater.from(context);
        this.data = list;
        this.size = list.size();
        this.context = context;
    }

    @Override
    public int getCount() {
        return this.size;
    }

    @Override
    public Emoji getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Emoji emoji = data.get(position);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.face_item, null);
            viewHolder.iv_face = (ImageView) convertView.findViewById(R.id.face_iv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.iv_face.setImageResource(emoji.getResId());

        return convertView;
    }

    class ViewHolder {

        public ImageView iv_face;
    }
}