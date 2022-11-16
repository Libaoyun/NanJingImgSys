package com.rdexpense.manager.service.system;

import com.common.entity.PageData;
import com.common.entity.ResponseEntity;

import java.util.List;

/**
 * @author luxiangbao
 * @date 2020/6/3 16:50
 * @describe
 */
public interface TemplateService {

    void addRecord(PageData pageData) ;

    void updateRecord(PageData pageData) ;

    List<PageData> searchRecord(PageData pageData) ;

    void deleteRecord(PageData pageData) ;

    PageData recordDetail(PageData pageData);


}
