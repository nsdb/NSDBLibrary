package com.nsdb.cm.util.assistant;

import android.content.Context;
import android.view.View;

/**
 * 레이아웃의 특정 뷰에 연결하여 그 뷰의 컨트롤러 역할을 하는 객체입니다.
 * 컴포넌트화 하기에는 범용성이 없으나 효율적인 관리를 위한 코드 분화가 필요할 경우 (XML에 include 형식으로 추가되는 뷰 등)에 유용하게 사용할 수 있습니다.<br>
 * MessageCallback을 implements 하며, 이 객체를 관리하는 액티비티 등도 콜백을 가짐으로써 상호간의 통신이 가능합니다. 같은 Assistant가 새로운 Assistant를 생성하여 트리구조를 형성하는 것도 가능합니다.<br>
 * 이 객체를 상속하는 객체의 이름에는 앞에 Ast를 붙여주는 것을 추천합니다.<br>
 * MessageCallback의 onMessage에 사용하는 code 값은 따로 객체에 상수를 추가하여 관리해주십시요.
 * @see MessageCallback
 * @author NSDB
 *
 */
public abstract class AbsAssistant implements MessageCallback {

	private Context parent;
	private View contentView;
	private MessageCallback callback;
	
	public AbsAssistant(Context parent, View contentView) {
		this.parent=parent;
		this.contentView=contentView;
	}
	
	public void setCallback(MessageCallback callback) {
		this.callback=callback;
	}
	
	
	protected final Context getContext() { return parent; }
	protected final View getContentView() { return contentView; }
	protected final View findViewById(int id) { return contentView.findViewById(id); }
	protected final MessageCallback getCallback() { return callback; }
}
