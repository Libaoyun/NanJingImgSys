package com.rdexpense.manager.service.system;

import com.common.entity.PageData;

import java.util.List;

/**
 * @Description:
 * @Author: rdexpense
 * @Date: 2021/1/19 09:40
 * @Version: 1.0
 */
public interface DictionaryService {

    List<PageData> findDictionaryTreeInfo();

    List<PageData> findDictionariesList(PageData pd);

    void createDictionaryInfo(PageData pd);

    void updateDictionaryInfo(PageData pd);

    void deleteDictionaryInfo(PageData pd);

    List<PageData> findPullDownDictionariesList(PageData pd);

    List<PageData> queryDictionaryType();

    void saveDictionType(PageData pd);

    void modifyDictionType(PageData pd);

    void removeDictionType(PageData pd);
}
