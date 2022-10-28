package com.common.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.entity.PageData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 对象功能:JSON帮助类
 * 开发人员:rdexpense
 * 创建时间:2018-01-19
 * </pre>
 */

public class JsonUtil {
	private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);
	public static PageData jsonToPageData(String str) {
		PageData pd = (PageData) JSONObject.parseObject(str, PageData.class);
		return pd;
	}

	public static PageData jsonToPageData(Object obj) {
		PageData pd = (PageData) JSONObject.parseObject(obj.toString(), PageData.class);
		return pd;
	}

	public static List<PageData> jsonToPageDataList(String str) {
		List<PageData> pdLst = (List<PageData>) JSONArray.parseArray(str, PageData.class);
		return pdLst;
	}

	public static List<PageData> jsonToPageDataList(Object obj) {
		List<PageData> pdLst = (List<PageData>) JSONArray.parseArray(obj.toString(), PageData.class);
		return pdLst;
	}

	public static List<?> jsonToList(Object obj) {
		List<?> pdLst = (List<?>) JSONArray.parseArray(obj.toString(), Object.class);
		return pdLst;
	}

	public static String pageDataToString(Object obj) {
		return JSONArray.toJSONString(obj);
	}

	public static Object pageDataToJson(Object obj) {
		return JSONArray.toJSON(obj);
	}

	public static PageInfo<?> jsonToPageInfo(Object obj) {
		PageInfo<?> pageInfo = (PageInfo<?>) JSONObject.parseObject(obj.toString(), PageInfo.class);
		return pageInfo;
	}

	/**
	 * json的int数组转List<PageData>
	 * @param str
	 * @return
	 */
	public static List<PageData> jsonArrToList(String str){
		List<PageData> ret = new ArrayList<PageData>();
		List<Integer> intList = JSONObject.parseArray(str, Integer.class);
		for(Integer id:intList) {
			PageData pd = new PageData();
			pd.put("id", id);
			ret.add(pd);
		}
		return ret;
	}

	/**
	 * json的String数组转List<PageData>
	 * @param str
	 * @return
	 */
	public static List<PageData> jsonArrToStringList(String str){
		List<PageData> ret = new ArrayList<PageData>();
		List<String> intList = JSONObject.parseArray(str, String.class);
		for(String id:intList) {
			PageData pd = new PageData();
			pd.put("id", id);
			ret.add(pd);
		}
		return ret;
	}

	// public static void main(String[] args) {
	// String str =
	// FileUtil.getReadFileTxt("C:/Users/lxwindow/Desktop/模板数据JSON.json");
	// PageData pd = jsonToPageData(str);
	// List<PageData> pdLst = jsonToPageDataList(pd.get("tagObjLst"));
	// for (PageData pagedata : pdLst) {
	// for (Object key1 : pagedata.keySet()) {
	// System.out.println(key1 + " " + pagedata.get(key1));
	// }
	// }
	// }


	public static final ObjectMapper mapper = new ObjectMapper();

	public static void copyProperties(Object source, Object target){
		BeanUtils.copyProperties(source,target);
	}


	public static String toString(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj.getClass() == String.class) {
			return (String) obj;
		}
		try {
			return mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			logger.error("json序列化出错：" + obj, e);
			return null;
		}
	}

	public static <T> T toBean(Object obj, Class<T> tClass) {
		try {
			String json = toString(obj);
			return mapper.readValue(json, tClass);
		} catch (IOException e) {
			logger.error("json解析出错：" + toString(obj), e);
			return null;
		}
	}

	public static <E> List<E> toList(String json, Class<E> eClass) {
		try {
			return mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, eClass));
		} catch (IOException e) {
			logger.error("json解析出错：" + json, e);
			return null;
		}
	}

	public static <K, V> HashMap<K, V> toMap(String json, Class<K> kClass, Class<V> vClass) {
		try {
			return mapper.readValue(json, mapper.getTypeFactory().constructMapType(HashMap.class, kClass, vClass));
		} catch (IOException e) {
			logger.error("json解析出错：" + json, e);
			return null;
		}
	}

	public static <K, V> HashMap<K, V> objectToMap(Object obj, Class<K> kClass, Class<V> vClass) {
		try {
			String json = toString(obj);
			return mapper.readValue(json, mapper.getTypeFactory().constructMapType(HashMap.class, kClass, vClass));
		} catch (IOException e) {
			logger.error("json解析出错：" + toString(obj), e);
			return null;
		}
	}

	public static <T> T nativeRead(String json, TypeReference<T> type) {
		try {
			return mapper.readValue(json, type);
		} catch (IOException e) {
			logger.error("json解析出错：" + json, e);
			return null;
		}
	}
}
