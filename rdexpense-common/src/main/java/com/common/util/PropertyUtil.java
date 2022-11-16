package com.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 *对象功能：字符转化成驼峰格式的方法
 *开发人员：rdexpense
 *创建时间：2018年11月21日
 */
public class PropertyUtil {

    private static Pattern linePattern = Pattern.compile("_(\\w)");

    /**
     * 下划线转驼峰
     */
    public static String lineToCamelCase(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }
        str = str.toLowerCase();
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 把map的key转换成驼峰命名
     *
     * @param map map数据
     * @return Mp<String, Objecat>
     */
    public static Map<String, Object> replaceKeyCamelCase(Map<String, Object> map) {
        Map<String, Object> respMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof List) {
                List value1 = (List) value;
                if (value1.size() == 0) {
                    respMap.put(lineToCamelCase(entry.getKey()), new ArrayList<>());
                }
                //递归数组
                respMap.put(lineToCamelCase(entry.getKey()), replaceChildKey(value1));
            } else if (value instanceof Map) {
                Map<String, Object> innerMap = replaceKeyCamelCase((Map<String, Object>) value);
                respMap.put(lineToCamelCase(entry.getKey()), innerMap);
            } else {
                if (entry.getValue() == null) {
                    respMap.put(lineToCamelCase(entry.getKey()), "");
                } else {
                    respMap.put(lineToCamelCase(entry.getKey()), entry.getValue());
                }
            }
        }
        return respMap;
    }

    public static List<Map<String, Object>> replaceChildKey(List<Map<String, Object>> data) {
        List<Map<String, Object>> resp = new ArrayList<>();
        //遍历list
        for (Map<String, Object> datum : data) {
            Map<String, Object> objectMap = new HashMap<>();
            //遍历map
            for (Map.Entry<String, Object> entry : datum.entrySet()) {
                Object value = entry.getValue();
                if (value instanceof List) {
                    List value1 = (List) value;
                    if (value1.size() == 0) {
                        objectMap.put(lineToCamelCase(entry.getKey()), new ArrayList<>());
                    } else {
                        List childKey = replaceChildKey(value1);
                        objectMap.put(lineToCamelCase(entry.getKey()), childKey);
                    }
                } else {
                    if (entry.getValue() == null) {
                        objectMap.put(lineToCamelCase(entry.getKey()), "");
                    } else {
                        objectMap.put(lineToCamelCase(entry.getKey()), entry.getValue());
                    }
                }
            }
            resp.add(objectMap);
        }
        return resp;
    }

    public static <T> T covertModel(Object element, Class<T> destClazz) {
        if (element instanceof Map) {
            Map<String, Object> map = PropertyUtil.replaceKeyCamelCase((Map<String, Object>) element);
            return JSON.parseObject(JSON.toJSONString(map), destClazz);
        }
        return (T) element;
    }

    public static <E> List<E> covertListModel(Object element, Class<E> destClazz) {
        List list = (List) element;
        if (CollectionUtils.isEmpty(list)) {
            return (List<E>) new JSONArray();
        }
        ArrayList<E> arrayList = new ArrayList<>();
        JSONArray platformList = JSON.parseArray(JSON.toJSONString(list));
        for (Object o : platformList) {
            E object = JSONObject.parseObject(JSON.toJSONString(o), destClazz);
            arrayList.add(object);
        }
        return arrayList;
    }

    public static <T> ResponseEntity<T> pushData(Object element, Class<T> destClazz, String msg) {
        if (element instanceof Map) {
            return ResponseEntity.success(covertModel(element, destClazz), msg);
        }
        return null;
    }

    public static <T> ResponseEntity<T> pushData2(Object element, Class<T> destClazz, String msg) {
        if (element != null) {
            if (element instanceof Map) {
                return ResponseEntity.success(covertModel(element, destClazz), msg);
            }
        }
        return ResponseEntity.success(null, msg);
    }

    public static <T> PageInfo<T> pushPageList(PageInfo<PageData> element, Class<T> destClazz) {
        List<PageData> pageData = element.getList();
        List<T> dest = covertListModel(pageData, destClazz);
        PageInfo<T> datas = new PageInfo<>();
        BeanUtils.copyProperties(element, datas);
        datas.setList(dest);
        return datas;
    }
}
