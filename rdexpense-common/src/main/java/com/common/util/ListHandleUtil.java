package com.common.util;

import com.common.entity.PageData;

import java.util.ArrayList;
import java.util.List;

/** 对集合处理的公共类
 * @author rdexpense
 * @date 2020/09/09 22:10
 */
public class ListHandleUtil {

    //该共用方法将一个集合，按照设置的集合元素的长度，分割成多个集合
    //批量插入，每次允许插入的结合的长度

    public static List<List<PageData>> splitMobile(List<PageData> allList, Integer totalSize,Integer cutSize) {

        List<List<PageData>> result = new ArrayList<>();

        int pre = totalSize / cutSize;
        int last = totalSize % cutSize;

        //前面pre个集合，每个大小都是numTotal个元素
        for (int i = 0; i < pre; i++) {
            List<PageData> itemList = new ArrayList<>();
            for (int j = 0; j < cutSize; j++) {
                itemList.add(allList.get(i * cutSize + j));
            }
            result.add(itemList);
        }

        //last的进行处理
        if (last > 0) {
            List<PageData> itemList = new ArrayList<>();
            for (int i = 0; i < last; i++) {
                itemList.add(allList.get(pre * cutSize + i));
            }
            result.add(itemList);
        }

        return result;

    }

    public static List<List<PageData>> groupList(List<PageData> list, int toIndex) {
        List<List<PageData>> listGroup = new ArrayList<List<PageData>>();
        int listSize = list.size();
        //子集合的长度
        for (int i = 0; i < list.size(); i += toIndex) {
            if (i + toIndex > listSize) {
                toIndex = listSize - i;
            }
            List<PageData> newList = list.subList(i, i + toIndex);
            listGroup.add(newList);
        }
        return listGroup;
    }
}
