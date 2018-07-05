package com.sylar.ucmlmobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.administrator.circlegit.R;


public class MessageAdapter extends BaseAdapter {
	
	private Context ctx;
	private LayoutInflater inflater;
	private String username = null;
	
	public MessageAdapter(Context context, String username) {
		this.ctx = context;
		this.username =username;
	}
	
	@Override
	public int getCount() {
		return MessageConstants.listMsg.size();
	}
	

	@Override
	public Object getItem(int position) {
		return MessageConstants.listMsg.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		this.inflater = (LayoutInflater) this.ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		if(MessageConstants.listMsg.get(position).from.equals("IN")) {
			convertView = inflater.inflate(R.layout.formclient_chat_in, null);
		}
		else {
			convertView = inflater.inflate(R.layout.formclient_chat_out, null);
		}
		
		TextView useridView = (TextView) convertView.findViewById(R.id.formclient_row_userid);
		TextView dateView = (TextView) convertView.findViewById(R.id.formclient_row_date);
		TextView msgView = (TextView) convertView.findViewById(R.id.formclient_row_msg);
		useridView.setText(MessageConstants.findNameById(MessageConstants.listMsg.get(position).userid));
		dateView.setText(MessageConstants.listMsg.get(position).date);
		msgView.setText(MessageConstants.listMsg.get(position).msg);
		return convertView;
	}
}
