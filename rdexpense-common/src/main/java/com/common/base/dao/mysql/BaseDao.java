package com.common.base.dao.mysql;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <pre>
 * 对象功能:BaseDao接口类
 * 开发人员:lixin
 * 创建时间:2018-01-15
 * </pre>
 */
public interface BaseDao {

	/**
	 * 保存对象
	 *
	 * @param str
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public int insert(String str, Object obj);

	/**
	 * 批量保存对象
	 *
	 * @param str
	 * @param objs
	 * @return
	 * @throws Exception
	 */
	public int batchInsert(String str, List<?> objs);

	/**
	 * 修改对象
	 *
	 * @param str
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public int update(String str, Object obj);

	/**
	 * 批量更新
	 * @param str
	 * @param objs
	 */
	public int batchUpdate2(String str, List<?> objs);

	/**
	 * 批量修改对象
	 *
	 * @param str
	 * @param objs
	 * @return
	 * @throws Exception
	 */
	public void batchUpdate(String str, List<?> objs);

	/**
	 * 批量修改对象(通过sql的case/when拼接)
	 *
	 * @param str
	 * @param objs
	 * @return
	 * @throws Exception
	 */
	public int batchUpdateCaseWhen(String str, List<?> objs);

	/**
	 * 删除对象
	 *
	 * @param str
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public int delete(String str, Object obj);


	/**
	 * 批量删除对象
	 *
	 * @param str
	 * @param objs
	 * @return
	 * @throws Exception
	 */
	public int batchDelete(String str, List<?> objs);

	/**
	 * 查找对象
	 *
	 * @param str
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public Object findForObject(String str, Object obj);

	/**
	 * 查找对象
	 *
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public Object findForObject(String str);

	/**
	 * 查找对象
	 *
	 * @param str
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public Object findForList(String str, Object obj);

	/**
	 * 查找对象封装成Map
	 *
	 * @param sql
	 * @param obj
	 * @param key
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public Object findForMap(String sql, Object obj, String key, String value);

	/**
	 * 查找对象
	 * @param str
	 * @return
	 */
	public Object findForList(String str);


  /**
   * 批量保存对象
   *
   * @param str
   * @param objs
   * @return
   * @throws Exception
   */
  public <T> int batchInsertBigData(String str, List<T> objs);

}
