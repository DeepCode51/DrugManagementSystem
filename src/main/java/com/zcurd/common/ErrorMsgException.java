package com.zcurd.common;

/**
 * 错误消息提示异常。异步请求时，前端会弹出此消息
 * @author 钟世云 2017年1月3日 下午8:35:35
 */
public class ErrorMsgException extends RuntimeException {
	public ErrorMsgException(String string) {
		super(string);
	}

	private static final long serialVersionUID = 1L;

}
