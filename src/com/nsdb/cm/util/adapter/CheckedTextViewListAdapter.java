package com.nsdb.cm.util.adapter;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

/**
 * CheckedTextView를 이용한 리스트뷰를 쉽게 생성하기 위한 아답터입니다.<br>
 * 일반적으로 ListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE)을 겸하여 사용합니다. 잊고 넘어가지 않았는지 확인하십시요.
 * @author NSDB
 *
 */
public class CheckedTextViewListAdapter extends ArrayAdapter<String> {

	private int style;
	
	public CheckedTextViewListAdapter(Context context,int style) {
		super(context,0);
		this.style=style;
	}
	
	@Override
	public View getView(int position,View v,ViewGroup parent) {			
		if(position<0 || position>=getCount()) return null;
		if(v==null) {
			v=new CheckedTextView(new ContextThemeWrapper(getContext(),style));
		}
		CheckedTextView ctv=(CheckedTextView)v;
		ctv.setText(getItem(position));
		return v;
	}
	
}
