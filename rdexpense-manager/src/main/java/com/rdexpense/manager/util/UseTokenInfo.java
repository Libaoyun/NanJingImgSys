package com.rdexpense.manager.util;

import com.alibaba.fastjson.JSON;
import com.common.base.dao.redis.RedisDao;
import com.common.entity.PageData;
import com.rdexpense.manager.dto.base.UserInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 *
 * 功能描述: 用户Token信息获取
 *
 * @Author: rdexpense
 * @Param:
 * @Date: 2020/4/24 10:55
 * @return:
 */
@Service
@Slf4j
@Component
public class UseTokenInfo {

    @Autowired(required=true)
    private RedisDao redisDao;


    public UserInfoDTO getUserInfoByToken(String redisKey){
        if(redisDao.existsKey(redisKey)){
            PageData pageData = (PageData)redisDao.vGet(redisKey);
            if(pageData == null){
                log.warn("获取redis信息异常：request = [{}]", JSON.toJSONString(redisKey));
                return null;
            }

            //每次调接口，都更改token的失效时间
            redisDao.vSetUpdate(redisKey, 7200L);

            UserInfoDTO userTokenInfoVo = new UserInfoDTO();
            userTokenInfoVo.setUserName(pageData.getString("userName"));
            userTokenInfoVo.setUserCode(pageData.getString("userCode"));
            userTokenInfoVo.setExptime(Long.parseLong(pageData.getString("exptime")));
            userTokenInfoVo.setFirstLogin(pageData.getBoolean("firstLogin"));
            userTokenInfoVo.setCompanyId(pageData.getString("companyId"));
            userTokenInfoVo.setDepartment(pageData.getString("department"));
            userTokenInfoVo.setDepartmentId(pageData.getString("departmentId"));
            userTokenInfoVo.setPost(pageData.getString("post"));
            userTokenInfoVo.setPostId(pageData.getString("postId"));
            userTokenInfoVo.setToken(pageData.getString("token"));


            return userTokenInfoVo;
        }
        log.warn("token失效：request = [{}]",redisKey);
        return null;
    }


    public boolean checkSelf(String userId,String token) {
        UserInfoDTO info = getUserInfoByToken(token);
        return String.valueOf(info.getUserCode()).equals(userId);
    }
}
