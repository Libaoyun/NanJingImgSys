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

    void addHandBook(PageData pageData) ;

    void updateHandBook(PageData pageData) ;

    List<PageData> searchRecord(PageData pageData) ;

    void deleteHandBook(PageData pageData) ;

    PageData handBookDetail(PageData pageData);


    List<PageData> searchNode(PageData pageData) ;


}
