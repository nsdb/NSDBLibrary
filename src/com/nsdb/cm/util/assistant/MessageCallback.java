package com.nsdb.cm.util.assistant;

/**
 * 객체간의 간단한 통신을 도와주는 인터페이스입니다.
 * @author NSDB
 *
 */
public interface MessageCallback {
	public void onMessage(MessageCallback callback, int code, Object data);
}
