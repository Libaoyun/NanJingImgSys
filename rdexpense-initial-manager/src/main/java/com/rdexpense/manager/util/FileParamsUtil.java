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
        CheckParameter.stringLengthAndEmpty(createUserId, "上传人id",128);

        String  createUser = dto.getCreateUser();
        CheckParameter.stringLengthAndEmpty(createUser, "上传人姓名",128);

        String  creatorOrgId = dto.getCreatorOrgId();
        CheckParameter.stringLengthAndEmpty(creatorOrgId, "右上角项目ID",128);

        String  creatorOrgName = dto.getCreatorOrgName();
        CheckParameter.stringLengthAndEmpty(creatorOrgName, "右上角项目名称",128);

        String  menuCode = dto.getMenuCode();
        CheckParameter.stringLengthAndEmpty(menuCode, "菜单编码",128);

        MultipartFile file = dto.getFile();
        CheckParameter.isNull(file, "上传文件");

        pageData.put("creatorOrgId",creatorOrgId);
        pageData.put("creatorOrgName",creatorOrgName);
        pageData.put("createUserId",createUserId);
        pageData.put("createUser",createUser);
        pageData.put("menuCode",menuCode);

        return pageData;

    }

}
