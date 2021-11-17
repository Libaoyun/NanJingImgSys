package com.rdexpense.manager.service.system.impl;

import com.alibaba.fastjson.JSON;
import com.common.base.dao.mysql.BaseDao;
import com.common.base.dao.redis.RedisDao;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.util.ErrorCodeEnum;
import com.rdexpense.manager.service.system.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Slf4j
@Service
public class LoginServiceImpl implements LoginService {


    @Autowired
    @Resource(name = "redisDao")
    private RedisDao redisDao;

    @Autowired
    @Resource(name = "baseDao")
    private BaseDao baseDao;



    /**
     * 外部用户登录
     * @param pd
     * @return
     */
    @Override
    public PageData externalUserLogin(PageData pd) {
        PageData result = new PageData();

        //此处用户名身份证
        //查管理人员表
        PageData user = (PageData) baseDao.findForObject("UserMapper.findExternalUser", pd);
        if (user == null) {
            throw new MyException(ErrorCodeEnum.USER_LOGIN_EXCEPTION.desc());

        }

        //校验是否首次登陆，首次登陆需要重置密码,前提是已经授权的
        result.put("firstLogin", user.getInt("version") == 0);

        //校验用户密码
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean matches = encoder.matches(pd.getString("password"), user.getString("password"));
        if (!matches) {
            throw new MyException(ErrorCodeEnum.USER_LOGIN_EXCEPTION.desc());
        }

        //获取登陆token
        String userToken = setExternalUserInfos(pd, user);
        result.put("token", userToken);
        return result;

    }


    /**
     * 退出系统
     * @param token
     */
    @Override
    public PageData logout(String token) {
        PageData data = null;
        if(redisDao.existsKey(token)){
            data= (PageData) redisDao.vGet(token);
            if(data == null){
                log.warn("获取redis信息异常：request = [{}]", JSON.toJSONString(token));
                throw new MyException(ErrorCodeEnum.GET_USER_TOKEN_EXCEPTION.desc());
            }

            redisDao.delete(token);
        }else {
            throw new MyException(ErrorCodeEnum.GET_USER_TOKEN_EXCEPTION.desc());
        }

        return data;
    }


    /**
     * 获取用户信息
     * @param token
     * @return
     */
    @Override
    public PageData getUserInfo(String token) {
        PageData pageData = new PageData();
        //EXTERNAL:1233:UUID
        String userCode =   token.split(":")[1];
        pageData.put("userCode",userCode);
        PageData user = (PageData) baseDao.findForObject("UserMapper.findExternalUser", pageData);

        PageData data = new PageData();
        if (user == null) {
            throw new MyException(ErrorCodeEnum.AUTHORIZE_EXCEPTION.desc());

        }else {

            //查询部门职务表
            List<PageData> userInfoList = new ArrayList<>();
            userInfoList.add(user);
            List<PageData> postList = (List<PageData>) baseDao.findForList("UserMapper.queryAllPostData", userInfoList);

            if (!CollectionUtils.isEmpty(postList)) {
                data = postList.get(0);
            }

        }
        user.put("token",token);

        //2小时过期
        Long exp = 7200L;
        // 登录成功将token,设置redis
        BeanUtils.copyProperties(user,pageData);
        //用户类型 1 内部员工（一体化） ,0-外部员工
        String tokenKey = user.getString("token");

        pageData.put("token",tokenKey);
        pageData.put("exptime",exp);
        pageData.put("userName",user.getString("userName"));
        pageData.put("firstLogin",user.getInt("version") == 0);

        pageData.put("departmentId",data.getString("departmentCode"));
        pageData.put("department",data.getString("departmentName"));
        pageData.put("postId",data.getString("postCode"));
        pageData.put("post",data.getString("postName"));


        List<PageData> authList = (List<PageData>) baseDao.findForList("UserMapper.queryAuthData",pageData);
        if (!CollectionUtils.isEmpty(authList)){
            pageData.put("companyId",authList.get(0).getString("companyId"));
            pageData.put("organizationList",authList);
        }

        if(redisDao.existsKey(tokenKey)){
            PageData data1 = (PageData) redisDao.vGet(tokenKey);
            if(data == null){
                return null;
            }

            pageData.put("userCode",data1.getString("userCode"));

        }

        redisDao.vSet(tokenKey, pageData, exp);

        return pageData;
    }


    /**
     * 设置返回信息
     * @param pd
     * @param user
     * @return
     */

    private String setExternalUserInfos(PageData pd, PageData user) {

        PageData pageData = new PageData();
        //2小时过期
        Long exp = 7200L;
        // 登录成功将token,设置redis
        //  String tokenKey ="EXTERNAL:"+userName+":"+ UUID.randomUUID().toString();
        String tokenKey ="EXTERNAL:"+pd.getString("userCode")+":"+ UUID.randomUUID().toString();
        BeanUtils.copyProperties(user,pageData);

        pageData.put("token",tokenKey);
        pageData.put("exptime",exp);
        pageData.put("userCode",user.getString("userCode"));
        pageData.put("userName",user.getString("userName"));
        pageData.put("firstLogin",user.getInt("version") == 0);


        redisDao.vSet(tokenKey, pageData, exp);

        return tokenKey;
    }




}
