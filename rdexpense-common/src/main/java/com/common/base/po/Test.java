package com.common.base.po;

import com.common.entity.BaseModel;

/**
 * <pre>
 * 对象功能:测试样例实体类
 * 开发人员:lixin
 * 创建时间:2018-01-11
 * </pre>
 */
public class Test extends BaseModel {
	private String tid;
	private String tName;
	private String type;
	private String tDesc;

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String gettName() {
		return tName;
	}

	public void settName(String tName) {
		this.tName = tName;
	}

	public String gettDesc() {
		return tDesc;
	}

	public void settDesc(String tDesc) {
		this.tDesc = tDesc;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
