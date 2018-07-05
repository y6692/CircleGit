package com.sylar.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.example.administrator.circlegit.R;
import com.sylar.model.User;
import com.sylar.ucmlmobile.MessageConstants;
import com.sylar.ucmlmobile.XmppConnection;
import com.sylar.unit.StringUtil;

import org.jivesoftware.smackx.packet.VCard;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 加入圈子
 * created by Djy
 * 2017/7/7 0007 下午 2:42
 */
public class SearchFriendsAdapter extends BaseListAdapter<User> implements SectionIndexer {

    private SparseIntArray positionOfSection;
    private SparseIntArray sectionOfPosition;
    private OnChatClickListner onChatClickListner;

    public SearchFriendsAdapter(Context context, List<User> list) {
        super(context, list);
    }

    public void setData(List<User> list) {
        clearAll();
        addALL(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_friend, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(R.string.app_name, holder);
        } else {
            holder = (ViewHolder) convertView.getTag(R.string.app_name);
        }

        final User friend = getData().get(position);
        String userName = friend.getUsername();
        VCard vcard= XmppConnection.getInstance().getUservcard(userName);
        String nickName = vcard.getField("Name");
        String header = friend.getHeader();
        if (position == 0 || header != null
                && !header.equals(getItem(position - 1).getHeader())) {
            if ("".equals(header)) {
                holder.header.setVisibility(View.GONE);
                holder.headline.setVisibility(View.VISIBLE);
            } else {
                holder.headline.setVisibility(View.VISIBLE);
                holder.header.setVisibility(View.VISIBLE);
                holder.header.setText(header);
            }
        } else {
            holder.header.setVisibility(View.GONE);
        }
        if (!StringUtil.isBlank(nickName)){
            holder.messageContactName.setText(nickName);
        }
        holder.messageContactUsername.setText(userName);
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.header)
        TextView header;
        @BindView(R.id.headline)
        ImageView headline;
        @BindView(R.id.contacthead)
        ImageView contacthead;
        @BindView(R.id.message_contact_name)
        TextView messageContactName;
        @BindView(R.id.message_contact_username)
        TextView messageContactUsername;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public void setOnChatClickListner(OnChatClickListner onChatClickListner) {
        this.onChatClickListner = onChatClickListner;
    }

    public interface OnChatClickListner {
        void onChatClick(User friend);
    }

    @Override
    public int getPositionForSection(int section) {
        return positionOfSection.get(section);
    }

    @Override
    public int getSectionForPosition(int position) {
        return sectionOfPosition.get(position);
    }

    @Override
    public Object[] getSections() {
        positionOfSection = new SparseIntArray();
        sectionOfPosition = new SparseIntArray();
        int count = getCount();
        List<String> list = new ArrayList<String>();
        list.add(mContext.getString(R.string.search_header));
        positionOfSection.put(0, 0);
        sectionOfPosition.put(0, 0);
        for (int i = 1; i < count; i++) {
            String letter = getItem(i).getHeader();
            int section = list.size() - 1;
            if (list.get(section) != null && !list.get(section).equals(letter)) {
                list.add(letter);
                section++;
                positionOfSection.put(section, i);
            }
            sectionOfPosition.put(i, section);
        }
        return list.toArray(new String[list.size()]);
    }
}
