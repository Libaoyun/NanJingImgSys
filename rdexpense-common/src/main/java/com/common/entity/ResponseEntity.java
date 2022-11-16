package com.common.entity;

import com.common.util.ConstantMsgUtil;
import java.io.Serializable;


public class ResponseEntity<T> implements Serializable {
	private int code;
	private String msg;
	private T data;

	public static <T> ResponseEntity<T> success(T data) {
		return ResponseEntity.success(data,"");
	}

	//成功时候的调用
	public static <T> ResponseEntity<T> success(T data,String msg) {
		return new ResponseEntity<T>(data,msg);
	}

	public static <T> ResponseEntity<T> success(String msg) {
		return ResponseEntity.success(null,msg);
	}

	private ResponseEntity(T data,String msg) {
		this.code = ConstantMsgUtil.SUCC_STATUS.val();
		this.msg = msg;
		this.data = data ;
	}

	public static <T> ResponseEntity<T> failure(int code, String msg) {
		return new ResponseEntity<T>(code,msg);
	}

	private ResponseEntity(int code, String msg) {
		this.code = code;
		this.msg = msg;
		this.data = null;
	}

	private ResponseEntity() {
	}

	public int getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
