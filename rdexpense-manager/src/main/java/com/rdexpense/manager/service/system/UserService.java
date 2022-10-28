package com.rdexpense.manager.service.system;


import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.itextpdf.text.Document;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.List;

/**
 * @author rdexpense
 * @version 1.0
 * @date 2020/3/31 16:10
 */
public interface UserService {

    /**
     * 查询列表
     * @return
     */
    List<PageData> queryUserList(PageData pd);

    /**
     * 新增员工
     * @param pd
     * @return
     */
    void addUser(PageData pd);

    /**
     * 更新员工
     * @param pd
     * @return
     */
    void updateUser(PageData pd);

    /**
     * 重置密码
     * @param pd
     * @return
     */
    void resetPassword(PageData pd);

    /**
     * 更新密码
     * @param pd
     * @return
     */
    void updatePassword(PageData pd);

    /**
     * 删除用户信息
     * @param pd
     * @return
     */
    void deleteUser(PageData pd);

    /**
     * 查询用户信息
     * @param pd
     * @return
     */
   PageData getUserDetail(PageData pd);


    /**
     * 查询列表
     * @return
     */
    List<PageData> getPost(PageData pd);

    /**
     * 导出excel
     */
    HSSFWorkbook exportExcel(PageData pd);


    /**
     * 导出pdf
     */
    void exportPDF(PageData pd, Document document) throws Exception;

}
