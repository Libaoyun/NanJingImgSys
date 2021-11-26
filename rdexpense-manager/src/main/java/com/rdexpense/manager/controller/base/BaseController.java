package com.rdexpense.manager.controller.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.common.base.dao.mysql.BaseDao;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.util.BaseUtil;
import com.common.util.ConstantMsgUtil;
import com.github.pagehelper.PageHelper;
import com.rdexpense.manager.dto.base.UserInfoDTO;
import com.rdexpense.manager.util.UseTokenInfo;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.ognl.Ognl;
import org.apache.ibatis.ognl.OgnlContext;
import org.apache.ibatis.ognl.OgnlException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.sql.Time;
import java.text.ParseException;
import java.util.*;


/**
 * <pre>
 * 对象功能:BaseController基类
 * 开发人员:lixin
 * 创建时间:2018-01-15
 * </pre>
 */
public class BaseController extends BaseUtil {

    @Autowired
    private UseTokenInfo useTokenInfo;

    @Autowired
    private BaseDao dao;

    /**
     * new PageData对象
     *
     * @return
     */
    public PageData getPageData() {
        return new PageData(this.getRequest());
    }

    public PageData getPageDataJson() {
        return new PageData(this.getRequest(), null);
    }

    /**
     * 得到ModelAndView
     *
     * @return
     */
    public ModelAndView getModelAndView() {
        return new ModelAndView();
    }

    /**
     * 得到request对象
     *
     * @return
     */
    public HttpServletRequest getRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        return request;
    }

    /**
     * 得到response对象
     *
     * @return
     */
    public HttpServletResponse getResponse() {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getResponse();

        return response;
    }

    /**
     * 设置MyBatis分页参数
     *
     * @param request
     */
    public void setStartPage(HttpServletRequest request) throws Exception {
        // 分页获取列表信息,pageNum:当前页码;pageSize:每页个数
        String pageNum = request.getParameter("pageNum");
        String pageSize = request.getParameter("pageSize");
        if (StringUtils.isNotEmpty(pageNum) && StringUtils.isNotEmpty(pageSize)) {
            // 如果加上该PageHelper.startPage(MyBatis自带分页操作),那么下一个执行的sql语句会执行分页操作(第二条sql不受影响)
            PageHelper.startPage(Integer.parseInt(pageNum), Integer.parseInt(pageSize));
            if (Integer.parseInt(pageSize) > 100) {
                throw new Exception("pageSize 超过了一百");
            }
        } else {
            throw new Exception("pageNum或pageSize为空,无法获取分页数据~");
        }
    }

    /**
     * 设置MyBatis分页参数
     */
    public void setStartPage() throws Exception {
        setStartPage(this.getRequest());
    }

    /**
     * 常量定义
     */
    private static final String ROOT = "root";

    /* 根对象 */
    private Object root;

    /**
     * 集合元素名称、类型映射表
     */
    private Map<String, Class<?>> elementClazzMap = new HashMap<String, Class<?>>();

    public void doDataBind(Object targetObj) {
        this.doDataBind(this.getRequest(), targetObj);
    }

    /**
     * <p>
     * 功能描述:表单数据绑定
     * </p>
     *
     * @param request
     * @param targetObj 目标对象
     */
    @SuppressWarnings("rawtypes")
    public void doDataBind(HttpServletRequest request, Object targetObj) {
        String ROOT = "root";
        OgnlContext context = new OgnlContext();
        Object root = targetObj;
        this.root = targetObj;
        context.put(ROOT, root);

        Enumeration paramNameEnum = request.getParameterNames();
        List<String> paramNames = new ArrayList<String>();
        while (paramNameEnum.hasMoreElements()) {
            String paramKey = (String) paramNameEnum.nextElement();
            paramNames.add(0, paramKey);
        }
        Map map = request.getParameterMap();
        for (String paramKey : paramNames) {
            Object paramValue = map.get(paramKey);
            if (paramValue != null) {
                // 绑定绑定
                this.doBindField(paramKey, paramValue, context);
            }
        }

    }

    /**
     * <p>
     * 功能描述:表单数据绑定
     * </p>
     *
     * @param request
     * @param targetObj 目标对象
     *                  目标对象中集合元素类型，有多个集合元素
     */
    @SuppressWarnings({"rawtypes", "unused"})
    private void doDataBind(HttpServletRequest request, Object targetObj, String[] elementNames, Class[] elementClazzs) {
        String ROOT = "root";
        OgnlContext context = new OgnlContext();
        Object root = targetObj;
        this.root = targetObj;
        context.put(ROOT, root);

        // 集合元素类型映射表
        if (elementNames != null && elementClazzs != null && elementNames.length == elementClazzs.length) {
            for (int i = 0; i < elementNames.length; i++) {
                elementClazzMap.put(elementNames[i], elementClazzs[i]);
            }
        }
        Enumeration paramNameEnum = request.getParameterNames();
        List<String> paramNames = new ArrayList<String>();
        while (paramNameEnum.hasMoreElements()) {
            String paramKey = (String) paramNameEnum.nextElement();
            paramNames.add(0, paramKey);
        }
        Map map = request.getParameterMap();
        for (String paramKey : paramNames) {
            Object paramValue = map.get(paramKey);
            if (paramValue != null) {
                // 绑定绑定
                this.doBindField(paramKey, paramValue, context);
            }
        }

    }

    /**
     * <p>
     * 功能描述:字段数据绑定
     * </p>
     *
     * @param key     键,如personVo.personId、bizEntityItemVo.dictVo.dictId
     * @param value   值
     * @param context 上下文对象
     */
    @SuppressWarnings({"unchecked", "rawtypes", "unused"})
    private void doBindField(String key, Object value, OgnlContext context) {
        // 绑定思路:
        // 1、从外往内按路径依次遍历
        // 1.1、对于非叶子节点，也即上级属性，如上级List/Object，则需判断是否为空，对于为空的则根据其类型进行实例化
        // 1.2、对于叶子节点，也即真正要绑定数据的属性字段，基于OGNL机制进行绑定
        // 以personVo.personId为例，下同
        key = ROOT.concat(".").concat(key);
        String[] ks = key.split("\\.");// root、personVo、personId
        // 是否叶子节点元素
        boolean isLeafElement = false;
        // 从外往内依次处理personVo、personVo.personId属性
        try {
            String listElClassName = null;// 集合元素的类名
            for (int i = 1; i < ks.length; i++) {
                if (i == (ks.length - 1)) {
                    isLeafElement = true;
                }
                String fieldName = ks[i];
                String fieldPath = this.getFieldPath(i, ks);
                String fieldParentPath = this.getFieldPath(i - 1, ks);
                Object fieldParentObj = null;
                // 非叶子节点，若为空则根据其类型进行实例化
                if (!isLeafElement) {
                    Object fieldObj = null;
                    try {
                        fieldObj = Ognl.getValue(fieldPath, context);
                    } catch (Exception e) {
                    } finally {
                        if (fieldObj == null) {
                            if (!this.isListElement(fieldName)) {// 非集合元素
                                fieldParentObj = Ognl.getValue(fieldParentPath, context);
                                Field field = null;
                                try {
                                    field = fieldParentObj.getClass().getDeclaredField(fieldName);
                                } catch (NoSuchFieldException e) {
                                    field = fieldParentObj.getClass().getSuperclass().getDeclaredField(fieldName);
                                } finally {
                                    if (field != null) {
                                        fieldObj = Class.forName(field.getType().getName()).newInstance();
                                        Ognl.setValue(fieldPath, context, fieldObj);
                                    }
                                }

                            } else {// 集合元素
                                int beginIndex = fieldName.lastIndexOf("[");
                                int endIndex = fieldName.lastIndexOf("]");
                                Integer elementIndex = Integer.parseInt(fieldName.substring(beginIndex + 1, endIndex));
                                String listFieldName = fieldName.substring(0, beginIndex);
                                listElClassName = this.elementClazzMap.get(listFieldName).getName();
                                String listFieldPath = fieldParentPath.concat(".").concat(listFieldName);
                                Object listObj = Ognl.getValue(listFieldPath, context);
                                if (listObj != null && listObj instanceof List) {
                                    List list = (List) listObj;
                                    for (int j = 0; j < elementIndex + 1; j++) {
                                        try {
                                            list.get(j);
                                        } catch (IndexOutOfBoundsException ioe) {
                                            list.add(j, elementClazzMap.get(listFieldName).newInstance());
                                        }
                                    }
                                }
                            }

                        } else {
                            listElClassName = fieldObj.getClass().getName();
                        }
                    }

                } else {// 叶子节点，基于OGNL进行绑定
                    String[] values = (String[]) value;
                    if (values != null && values.length > 0) {
                        String clazzName = null;
                        Class clazz = null;
                        Field field = null;
                        if (StringUtils.isNotEmpty(listElClassName)) {
                            clazzName = listElClassName;
                        } else if (fieldParentObj == null) {
                            clazzName = this.root.getClass().getName();
                        } else {
                            clazzName = fieldParentObj.getClass().getName();
                        }
                        clazz = Class.forName(clazzName);
                        try {
                            field = clazz.getDeclaredField(fieldName);
                        } catch (Exception ex) {
                        }
                        if (field != null) {
                            if ("java.util.Date".equals(field.getType().getName())) {
                                convertValueToDate(context, fieldPath, values[0]);
                            } else if ("java.sql.Time".equals(field.getType().getName())) {
                                convertValueToTime(context, fieldPath, values[0]);
                            } else if ("boolean".equals(field.getType().getName())) {
                                Ognl.setValue(fieldPath, context, Boolean.valueOf(values[0]));
                            } else {
                                Ognl.setValue(fieldPath, context, values[0]);
                            }
                        } else {
                            Ognl.setValue(fieldPath, context, values[0]);
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    /**
     * <p>
     * 功能描述:转换值到time
     * </p>
     *
     * @param context
     * @param fieldPath
     * @param value
     * @throws OgnlException
     */
    private void convertValueToTime(OgnlContext context, String fieldPath, String value) throws OgnlException {
        Time time = Time.valueOf(value);
        Ognl.setValue(fieldPath, context, time);
    }

    /**
     * <p>
     * 功能描述:转换值到Date
     * </p>
     *
     * @param context
     * @param fieldPath
     * @param value
     * @throws OgnlException
     * @throws ParseException
     */
    @SuppressWarnings("deprecation")
    private void convertValueToDate(OgnlContext context, String fieldPath, String value)
            throws OgnlException, ParseException {
        /*
         * SimpleDateFormat sdf =null; if (value.length()>10) { sdf=new
         * SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); }else{ sdf=new
         * SimpleDateFormat("yyyy-MM-dd"); } value = value.replace("T", " ");
         * Ognl.setValue(fieldPath, context, sdf.parse(value));
         */
        Ognl.setValue(fieldPath, context, new Date(value));
    }

    /**
     * <p>
     * 功能描述:获取字段路径
     * </p>
     *
     * @param k  索引
     * @param ks 字段数组
     * @return
     */
    private String getFieldPath(int k, String[] ks) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < k + 1; i++) {
            buf.append(ks[i]).append(".");
        }
        buf.delete(buf.length() - 1, buf.length());
        return buf.toString();
    }

    /**
     * <p>
     * 功能描述:判断是否为list元素
     * </p>
     *
     * @param fieldName
     * @return
     */
    private boolean isListElement(String fieldName) {
        return !(fieldName.lastIndexOf("[") == -1 && fieldName.lastIndexOf("]") == -1);
    }

    public String getUserToken(String key) {
        return getRequest().getHeader(key);
    }

    private PageData putUserInfo(HttpServletRequest request) {
        String userToken = request.getHeader("token");
        //将用户信息放进pageData
        String method = request.getMethod();
        PageData pageData = null;
        if (method.equalsIgnoreCase("GET")) {
            pageData = this.getPageData();
        } else if (method.equalsIgnoreCase("POST") || method.equalsIgnoreCase("DELETE")) {
            pageData = this.getPageJson(this.getRequest(), null);
        }
        UserInfoDTO userInfoVo = useTokenInfo.getUserInfoByToken(userToken);

        pageData.put("createUserId", userInfoVo.getUserCode());
        pageData.put("createUser", userInfoVo.getUserName());
        pageData.put("companyId", userInfoVo.getCompanyId());
        pageData.put("token", userToken);
        return pageData;
    }

    /**
     * 本方法支持接收的格式
     * 1.字符串，不支持[v1,v2,v3...]格式；
     * 2.对象及对象中包含数组，如：{"key1":"","key2":"[{"key3":"",}]"}；
     * 3.数组及数组中包含数组，如：[{"key":"value","key":"value"},{"key":"value","key":"value"}]
     * 4.其它格式暂不提供支持
     */
    private PageData getPageJson(HttpServletRequest request, Object o) {
        Map properties = getMap(request);
        PageData returnMap = new PageData();
        Iterator entries = properties.entrySet().iterator();
        Map.Entry entry;
        String name = "";
        String value = "";

        while (entries.hasNext()) {
            entry = (Map.Entry) entries.next();
            name = (String) entry.getKey();
            Object valueObj = entry.getValue();
            if (valueObj == null||valueObj.equals("")) {
                value = null;
            } else if (!valueObj.equals("") && valueObj.toString().indexOf("{") == 0){
                //遍历对象中数据,包含对象中包含的数组对象的格式
                //如:{"key1":"","key2":"[{"key3":"",}]"}
                value = objectEmptyToNull(name,valueObj);

            } else if (!valueObj.equals("") && valueObj.toString().indexOf("[") ==0 ) {
                //对象是[{"key":"value","key":"value"},{"key":"value","key":"value"}]格式的进行处理
                //把value值为空字段串的("")的转换为null值
                if (valueObj.toString().length() > 2&&valueObj.toString().contains("{")) {
                    value = arrayObjectEmptyToNull(name,valueObj);
                }else {
                    value = valueObj.toString();
                }

            } else if (!valueObj.equals("") && valueObj != null) {
                value = valueObj.toString();
            }
            returnMap.put(name, value);
        }
        return returnMap;
    }

    /**
     * 把对象中的空字符串替换为null
     * @param valueObj
     * @return
     */
    private String objectEmptyToNull(String name,Object valueObj) {
        JSONObject jsonObject = null;
        try {
            jsonObject = JSONObject.parseObject(valueObj.toString());
        } catch (Exception e) {
            throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_WRONGFUL.desc(), name));
        }

        JSONObject newJsonObject = new JSONObject();
        for (Map.Entry<String, Object> entrys : jsonObject.entrySet()) {
            String mapValue = String.valueOf(entrys.getValue());
            if (!"".equals(mapValue)) {
                //判断value值是否为数组对象
                //格式为[{"key":"value","key":"value"},{"key":"value","key":"value"}]
                if (mapValue.indexOf("[") == 0
                        &&mapValue.length() > 2&&mapValue.contains("{")) {
                    JSONArray newJsonArray = new JSONArray();
                    //遍历数组对象，获取数组对象的数据
                    mapValue = getPageJsonChild(name,mapValue,newJsonArray);
                }
            } else if (mapValue.isEmpty()) {
                mapValue = null;
            }
            newJsonObject.put(entrys.getKey(),mapValue);
        }
        return JSONObject.toJSONString(newJsonObject, SerializerFeature.WriteMapNullValue);
    }

    /**
     * 把数组中的空字符串替换为null
     * @param name
     * @param valueObj
     * @return
     */
    private String arrayObjectEmptyToNull(String name,Object valueObj) {
        List<Object> valuesList = null;
        try {
            valuesList = JSONObject.parseArray(valueObj.toString(), Object.class);
        } catch (Exception e) {
            throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_WRONGFUL.desc(), name));
        }

        JSONArray newJsonArray = new JSONArray();

        for (int i = 0; i < valuesList.size(); i++) {
            JSONObject newJsonObject = new JSONObject();
            JSONObject jsonObject = (JSONObject) valuesList.get(i);
            for (Map.Entry<String, Object> entrys : jsonObject.entrySet()) {
                String mapValue = String.valueOf(entrys.getValue());
                if (!"".equals(mapValue)) {
                    //判断value值是否为数组对象
                    //格式为[{"key":"value","key":"value"},{"key":"value","key":"value"}]
                    if (mapValue.indexOf("[") == 0 &&mapValue.length() > 2&&mapValue.contains("{")) {
                        JSONArray childJsonArray = new JSONArray();
                        mapValue = getPageJsonChild(name,mapValue,childJsonArray);
                    }
                } else if (mapValue.isEmpty()) {
                    mapValue = null;
                }
                newJsonObject.put(entrys.getKey(),mapValue);
            }
            newJsonArray.add(newJsonObject);
        }
        return JSONArray.toJSONString(newJsonArray, SerializerFeature.WriteMapNullValue);
    }

    /**
     * 递归遍历value中的对象或数组的所有信息
     * @param keyName  key值
     * @param value    value值
     * @param newListObjects   返回对象数组
     * @return
     */
    private String getPageJsonChild(String keyName,String value,JSONArray newJsonArray) {
        List<Object> valuesList = null;
        try {
            valuesList = JSONObject.parseArray(value.toString(), Object.class);
        } catch (Exception e) {
            throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_WRONGFUL.desc(), keyName));
        }

        for (int i = 0; i < valuesList.size(); i++) {
            JSONObject newJsonObject = new JSONObject();
            JSONObject jsonObject = (JSONObject) valuesList.get(i);
            for (Map.Entry<String, Object> entrys : jsonObject.entrySet()) {
                String mapValue = String.valueOf(entrys.getValue());
                if (!"".equals(mapValue)) {
                    //判断是否为数组对象
                    if (mapValue.indexOf("[") == 0
                            &&mapValue.length() > 2&&mapValue.contains("{")) {
                        getPageJsonChild(entrys.getKey().toString(),mapValue,newJsonArray);
                    }
                } else if (mapValue.isEmpty()) {
                    mapValue = null;
                }
                newJsonObject.put(entrys.getKey(),mapValue);
            }
            newJsonArray.add(newJsonObject);
        }
        return JSONArray.toJSONString(newJsonArray, SerializerFeature.WriteMapNullValue);
    }

//    private PageData getPageJson(HttpServletRequest request, Object o) {
//        Map properties = getMap(request);
//        PageData returnMap = new PageData();
//        Iterator entries = properties.entrySet().iterator();
//        Map.Entry entry;
//        String name = "";
//        String value = "";
//        while (entries.hasNext()) {
//            entry = (Map.Entry) entries.next();
//            name = (String) entry.getKey();
//            Object valueObj = entry.getValue();
//            if (null == valueObj) {
//                value = "";
//            } else if (valueObj instanceof String[]) {
//                String[] values = (String[]) valueObj;
//                for (int i = 0; i < values.length; i++) {
//                    value = values[i] + ",";
//                }
//                value = value.substring(0, value.length() - 1);
//            } else {
//                value = valueObj.toString();
//            }
//            returnMap.put(name, value);
//        }
//        return returnMap;
//    }

    //接收前台json数据
    private Map getMap(HttpServletRequest request) {
        Map mapJson = null;
        try {
            InputStreamReader is = new InputStreamReader(request.getInputStream());
            BufferedReader reader = new BufferedReader(is);
            String str = "";
            String wholeStr = "";
            //一行一行的读取body体里面的内容；
            while ((str = reader.readLine()) != null) {
                wholeStr += str;
            }
            mapJson = JSON.parseObject(wholeStr);
            //关闭流
            is.close();
        } catch (Exception e) {
            logger.warn("PageData接收参数异常。。。。。。。。{}");
        }
        return mapJson;
    }

    private Map<String, Object> Obj2Map(Object obj) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            map.put(field.getName(), field.get(obj));
        }
        return map;
    }

    /**
     * 设置MyBatis分页参数
     */
    protected PageData startPageHelper() {
        PageData params = this.getParams();
        // 分页获取列表信息,pageNum:当前页码;pageSize:每页个数
        PageHelper.startPage(Integer.parseInt(MapUtils.getString(params, "pageNum", "1")),
                Integer.parseInt(MapUtils.getString(params, "pageSize", "10")));
        return params;
    }

    protected PageData getParams() {
        return putUserInfo(this.getRequest());
    }

    protected PageData getLoginParams(){
        return putUserInfos(this.getRequest());
    }

    private PageData putUserInfos(HttpServletRequest request) {
        //将用户信息放进pageData
        String method = request.getMethod();
        PageData pageData = null;
        if (method.equalsIgnoreCase("GET")) {
            pageData = this.getPageData();
        } else if (method.equalsIgnoreCase("POST") || method.equalsIgnoreCase("DELETE")) {
            pageData = this.getPageJson(this.getRequest(), null);
        }

        return pageData;
    }


}
