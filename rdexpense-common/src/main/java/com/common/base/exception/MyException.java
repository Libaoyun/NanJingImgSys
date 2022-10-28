package com.common.base.exception;

import com.common.entity.RespEntity;

/**
 * <pre>
 * 对象功能:异常类
 * 开发人员:lixin
 * 创建时间:2018-07-24
 * </pre>
 */
public class MyException extends RuntimeException {

	private static final long serialVersionUID = 3858543830715293667L;

	/**
	 * 错误编码
	 */
//	private int errorCode = ConstantMsgUtil.getFailStatus();

	/**
	 * 错误提示
	 */
	private String errorMsg;

	/**
	 * 错误提示
	 */
	private RespEntity respEntity;

//	public int getErrorCode() {
//		return errorCode;
//	}
//
//	public void setErrorCode(int errorCode) {
//		this.errorCode = errorCode;
//	}
//
	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public RespEntity getRespEntity() {
		return respEntity;
	}

	public void setRespEntity(RespEntity respEntity) {
		this.respEntity = respEntity;
	}

	/**
	 * 构造一个基本异常.
	 *
	 * @param errorMsg:错误信息描述
	 */
//	public MyException() {
//		super();
//		this.setErrorMsg(errorMsg);
//	}
//
	/**
	 * 构造一个基本异常.
	 *
	 * @param errorMsg:错误信息描述
	 */
	public MyException(String errorMsg) {
		super(errorMsg);
		this.setErrorMsg(errorMsg);
	}

	/**
	 * 构造一个基本异常.
	 *
	 * @param errorCode:错误编码
	 * @param errorMsg:错误信息描述
	 */
//	public MyException(int errorCode, String errorMsg) {
//		super(errorMsg);
//		this.setErrorCode(errorCode);
//		this.setErrorMsg(errorMsg);
//	}

	/**
	 * 构造一个基本异常.
	 *
	 * @param errorMsg:错误信息描述
	 * @param cause:异常
	 */
	public MyException(String errorMsg, Throwable cause) {
		super(errorMsg, cause);
//		this.setErrorMsg(errorMsg);
	}

	/**
	 * 构造一个基本异常.
	 *
	 * @param errorCode:错误编码
	 * @param errorMsg:错误信息描述
	 * @param cause:异常
	 */
	public MyException(int errorCode, String errorMsg, Throwable cause) {
		super(errorMsg, cause);
//		this.setErrorCode(errorCode);
//		this.setErrorMsg(errorMsg);
	}

	/**
	 * 构造一个基本异常.
	 *
	 * @param respEntity:返回数据对象
	 */
	public MyException(RespEntity respEntity) {
		this.setRespEntity(respEntity);
	}

	/**
	 * 构造一个基本异常.
	 *
	 * @param respEntity:返回数据对象
	 * @param cause:异常
	 */
	public MyException(RespEntity respEntity, Throwable cause) {
		super(cause);
		this.setRespEntity(respEntity);
	}


}
