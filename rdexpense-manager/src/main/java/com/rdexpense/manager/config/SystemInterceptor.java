package com.rdexpense.manager.config;

import com.alibaba.fastjson.JSONObject;
import com.common.base.dao.redis.RedisDao;
import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.common.util.ConstantMsgUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.common.util.PositionUtil;
import com.rdexpense.manager.dto.base.UserInfoDTO;
import com.rdexpense.manager.dto.system.user.UserDepartmentDto;
import com.rdexpense.manager.util.RailStatic;
import com.rdexpense.manager.util.UseTokenInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


/**
 * 系统拦截实现类
 */
public class SystemInterceptor extends HandlerInterceptorAdapter {

    public static final String IP_UNKNOWN = "unknown";

    public static final String COMMA = ",";

    private static final List<String> POSSIBLE_IP_HEADER = Lists.newArrayList("x-forwarded-for", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR");

    @Autowired
    private UseTokenInfo useTokenInfo;


    /**
     * 前置处理 return true校验通过 false校验失败
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头中取出用户token和系统token
        //取出用户
        String userToken = request.getHeader("token");
        if (StringUtils.isBlank(userToken)) {
            render(response, ConstantMsgUtil.ERR_TOKEN_OVER_FAIL.desc());
            return false;
        }
        UserInfoDTO userInfoVo = useTokenInfo.getUserInfoByToken(userToken);
        if (userInfoVo == null) {
            render(response, ConstantMsgUtil.ERR_TOKEN_OVER_FAIL.desc());
            return false;
        }


        RailStatic.setUserId(userInfoVo.getUserCode());
        RailStatic.setUserName(userInfoVo.getUserName());
        RailStatic.setIp(getIpAddress(request));


        RailStatic.setCompanyId(userInfoVo.getCompanyId());
        RailStatic.setDepartment(userInfoVo.getDepartment());
        RailStatic.setDepartmentId(userInfoVo.getDepartmentId());
        RailStatic.setPost(userInfoVo.getPost());
        RailStatic.setPostId(userInfoVo.getPostId());

        return true;
    }

    private static String getIpAddress(HttpServletRequest request) {
        String ip = null;
        for (String ipHeader : POSSIBLE_IP_HEADER) {
            ip = request.getHeader(ipHeader);
            if (!Strings.isNullOrEmpty(ip) && !IP_UNKNOWN.equalsIgnoreCase(ip)) {
                break;
            } else {
                ip = null;
            }
        }
        if (ip == null) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.indexOf(COMMA) != -1) {
            ip = ip.substring(0, ip.indexOf(COMMA));
        }
        return ip;
    }


    /**
     * 接口处理完毕
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
        RailStatic.clearAll();
    }

    private void render(HttpServletResponse response, String desc) throws Exception {
        ResponseEntity<Object> entity = ResponseEntity.failure(ConstantMsgUtil.ERR_API_FAIL.val(), desc);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().print(JSONObject.toJSON(entity));
    }

    private void render(HttpServletResponse response,int val, String desc) throws Exception {
        ResponseEntity<Object> entity = ResponseEntity.failure(val, desc);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().print(JSONObject.toJSON(entity));
    }
}
