package com.sylar.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.circlegit.R;
import com.flyco.roundview.RoundTextView;
import com.sylar.model.Room;
import com.sylar.unit.StringUtil;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 加入圈子
 * created by Djy
 * 2017/7/7 0007 下午 2:42
 */
public class SearchRoomAdapter extends BaseListAdapter<Room> {
    private OnAddClickListner onAddClickListner;

    public SearchRoomAdapter(Context context, List<Room> list) {
        super(context, list);
    }

    public void setData(List<Room> list, boolean isLoadMore) {
        if (!isLoadMore) {
            clearAll();
        }
        addALL(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_search_room, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(R.string.app_name, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.string.app_name);
        }

        final Room item = getData().get(position);
        if (!StringUtil.isBlank(item.name)){
            holder.tvName.setText(item.name);
        }
        holder.tvDescription.setText(""+item.description);
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onAddClickListner != null)
                    onAddClickListner.onAddClick(item);
            }
        });
        return convertView;
    }


    static class ViewHolder {
        @BindView(R.id.iv_img)
        ImageView ivImg;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_description)
        TextView tvDescription;
        @BindView(R.id.add)
        RoundTextView add;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public void setOnAddClickListner(OnAddClickListner onAddClickListner) {
        this.onAddClickListner = onAddClickListner;
    }

    public interface OnAddClickListner {
        void onAddClick(Room room);
    }
}
