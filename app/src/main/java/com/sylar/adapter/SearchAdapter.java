/**
 * 
 */
package com.sylar.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.circlegit.R;
import com.sylar.ucmlmobile.MessageConstants;
import com.sylar.ucmlmobile.XmppConnection;
import com.sylar.unit.CircularImage;

import org.jivesoftware.smackx.packet.VCard;


/**
 * @author MZH
 *
 */
public class SearchAdapter extends ArrayAdapter<String> {
	Context context;
	
	public SearchAdapter(Context context) {
		super(context, 0);
		this.context = context;
	}
	
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.row_search, null);
		}

		final String item = getItem(position);
		TextView nameView = (TextView) convertView.findViewById(R.id.nameView);
		VCard vcard= XmppConnection.getInstance().getUservcard(item);
		nameView.setText(vcard.getField("Name"));
		CircularImage contacthead = (CircularImage) convertView.findViewById(R.id.contacthead);
		Bitmap bmp= MessageConstants.findAllHeadById(item);
		if(bmp!=null){
			contacthead.setImageBitmap(bmp);
		}
		return convertView;
	}
}
