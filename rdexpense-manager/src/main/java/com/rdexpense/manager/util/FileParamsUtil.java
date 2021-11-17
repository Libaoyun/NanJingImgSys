package com.rdexpense.manager.util;

import com.common.entity.PageData;
import com.common.util.CheckParameter;
import com.rdexpense.manager.dto.base.UploadTemplateFileDto;
import org.springframework.web.multipart.MultipartFile;

/**
 * @auther rdexpense
 * @date 2020/8/20 16:41
 * @describe 导入excel，校验传参工具类
 */
public class FileParamsUtil {


    public static PageData checkParams(UploadTemplateFileDto dto) {

        PageData pageData = new PageData();

        String  createUserId = dto.getCreateUserId();
        CheckParameter.stringLengthAndEmpty(createUserId, "上传人id",64);

        String  createUser = dto.getCreateUser();
        CheckParameter.stringLengthAndEmpty(createUser, "上传人姓名",64);

        String  menuCode = dto.getMenuCode();
        CheckParameter.stringLengthAndEmpty(menuCode, "菜单编码",64);

        MultipartFile file = dto.getFile();
        CheckParameter.isNull(file, "上传文件");


        String  orgCode = dto.getOrgCode();
        CheckParameter.stringLengthAndEmpty(orgCode, "组织编码",64);

        String  orgName = dto.getOrgName();
        CheckParameter.stringLengthAndEmpty(orgName, "组织名称",256);


        pageData.put("createUserId",createUserId);
        pageData.put("createUser",createUser);
        pageData.put("menuCode",menuCode);
        pageData.put("orgCode",orgCode);
        pageData.put("orgName",orgName);

        return pageData;

    }

}
