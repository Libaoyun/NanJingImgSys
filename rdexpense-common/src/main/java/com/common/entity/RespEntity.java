package com.common.entity;

import java.util.List;

import com.github.pagehelper.PageInfo;

/**
 * <pre>
 * 对象功能:响应实体，用于向客户端返回操作结果信息
 * 开发人员:lixin
 * 创建时间:2018-01-15
 * </pre>
 */
public class RespEntity extends PageData {

	private static final long serialVersionUID = -8299562540124103135L;

	/**
	 * 操作状态、0:成功;1:失败;2:Session失效;3:网络异常;4:相同帐号登录
	 */
	protected String status = "0";

	/**
	 * 操作结果提示信息，默认""
	 */
	protected String msg = "操作成功.";

	/**
	 * 默认构造函数
	 */
	public RespEntity() {
		put("status", status);
		put("msg", msg);
	}

	/**
	 * 构造函数
	 * 
	 * @param status
	 * @param msg
	 */
	public RespEntity(String status, String msg) {
		put("status", status);
		put("msg", msg);
	}

	/**
	 * 状态码
	 */
	public String getStatus() {
		return (String) get("status");
	}

	public void setStatus(String status) {
		put("status", status);
	}

	/**
	 * 提示信息
	 */
	public String getMsg() {
		return (String) get("msg");
	}

	public void setMsg(String msg) {
		put("msg", msg);
	}

    /**
     * 标识符
     * @param Tag
     */
    public void setTag(String Tag) {
        put("Tag", Tag);
    }




	/**
	 * 返回对象(注：默认为查询个数或者单个属性时使用)
	 */
	public Object getResponseObject() {
		return get("responseObject");
	}

	public void setResponseObject(Object responseObject) {
		put("responseObject", responseObject);
	}

	/**
	 * 返回list对象(注：查询列表数据无分页时使用)
	 */
	public List<?> getResponseList() {
		return (List<?>) get("responseList");
	}

	public void setResponseList(List<?> responseList) {
		put("responseList", responseList);
	}

	/**
	 * 返回pageData(Map)对象(注：返回多个属性对象,或者对象比较多,层级比较复杂时使用)
	 */
	public PageData getResponsePageData() {
		return (PageData) get("responsePageData");
	}

	public void setResponsePageData(PageData responsePageData) {
		put("responsePageData", responsePageData);
	}

	/**
	 * 返回pageInfo对象(注：查询列表数据分页时使用)
	 */
	public PageInfo<?> getResponsePageInfo() {
		return (PageInfo<?>) get("responsePageInfo");
	}

	public void setResponsePageInfo(PageInfo<?> responsePageInfo) {
		put("responsePageInfo", responsePageInfo);
	}

}
