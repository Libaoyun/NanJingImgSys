package com.common.entity;

import com.common.util.ConstantMsgUtil;

import java.io.Serializable;


public class ResponseEntity1<T> implements Serializable {
	private int code;
	private String msg;
	private T data;
	private String[] time;

	public static <T> ResponseEntity1<T> success(T data,String[] time) {
		return ResponseEntity1.success(data,time,"");
	}

	//成功时候的调用
	public static <T> ResponseEntity1<T> success(T data, String[] time , String msg) {
		return new ResponseEntity1<T>(data,time,msg);
	}

	public static <T> ResponseEntity1<T> success(String msg) {
		return ResponseEntity1.success(null,null,msg);
	}

	private ResponseEntity1(T data, String[] time,String msg) {
		this.code = ConstantMsgUtil.SUCC_STATUS.val();
		this.msg = msg;
		this.data = data ;
		this.time = time ;
	}

	public static <T> ResponseEntity1<T> failure(int code, String msg) {
		return new ResponseEntity1<T>(code,msg);
	}

	private ResponseEntity1(int code, String msg) {
		this.code = code;
		this.msg = msg;
		this.data = null;
	}

	private ResponseEntity1() {
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

	public String[] getTime() {
		return time;
	}

	public void setTime(String[] time) {
		this.time = time;
	}
}
