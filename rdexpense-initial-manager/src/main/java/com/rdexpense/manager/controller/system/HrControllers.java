package com.rdexpense.manager.controller.system;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.dao.redis.RedisDao;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.util.ConstantMsgUtil;
import com.common.util.ConstantValUtil;
import com.common.util.PositionUtil;
import com.rdexpense.manager.controller.base.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.net.SocketTimeoutException;
import java.util.*;


@RestController
public class HrControllers extends BaseController {

    private final static Logger logger = LoggerFactory.getLogger(HrControllers.class);

    @Value("${hr.api.url}")
    private String hrPrefix;

    @Value("${hrTree.org.position}")
    private String orgPosition;

    @Value("${hrTree.position.path}")
    private String hrPositionPath;

    @Value("${hrTree.users}")
    private String hrUsers;

    @Value("${organ.api.url}")
    private String orgPrefix;

    @Value("${orgTree.url}")
    private String orgUrl;

    @Value("${hrTree.user.url}")
    private String hrUser;

    @Autowired
    @Resource(name = "redisDao")
    private RedisDao redisDao;

    @Value("${hrTree.root}")
    private String hrTreeRoot;

    @Value("${hrTree.child}")
    private String hrTreeChild;

    @Value("${hrTree.search}")
    private String searchUser;

    @Resource(name = "httpRestTemplate")
    private RestTemplate httpRestTemplate;

    @Value("${hrTree.position.path}")
    private String hrPath;

    /**
     * 获取HR树中的岗位树，可模糊查询
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value ="/stationtree", method = RequestMethod.GET, produces = "application/json")
    public com.common.entity.RespEntity getStationTree() {
        com.common.entity.RespEntity res = new com.common.entity.RespEntity();
        PageData pd = this.getPageData();

        HttpHeaders headers = new HttpHeaders();
        //header中增加token
        String remoteToken = (String) redisDao.vGet(ConstantValUtil.REMOTE_TOKEN);
        headers.add("Authorization", "Bearer " + remoteToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        //获取信息
        if(remoteToken == null){
            res.setMsg("null token");
            res.setStatus("1");
            return res;
        }

        //RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<PageData> response = null;
        try {
            response = httpRestTemplate.exchange(hrPrefix + orgPosition, HttpMethod.GET,entity, PageData.class);
        } catch (ResourceAccessException e){
            if(e.getCause() instanceof SocketTimeoutException){
                logger.warn("====[/business/stationtree]调用HR系统连接或响应超时[{}]====",e.getMessage());
                throw new MyException(String.valueOf(HttpServletResponse.SC_REQUEST_TIMEOUT),"调用HR系统连接或响应超时",e);
            }else {
                logger.warn("====[/business/stationtree]调用HR系统发生I/O异常[{}]====",e.getMessage());
                throw new MyException("调用HR系统失败",e);
            }
        }

        if(response.getStatusCode() == HttpStatus.OK){
            PageData responseData = response.getBody();
            logger.debug("====[/business/stationtree]调用HR系统获取HR树[{}]====",responseData.toString());
            //拿到点击的是什么
            String code=pd.getString("code");
            if (StringUtils.isNotBlank(code)){

            }
            //判断是否模糊查询
            String name = pd.getString("name");
            if(StringUtils.isNotBlank(name)) {
                //注意：为了防止重复，将数据先塞到set中！
                HashSet<PageData> set = new HashSet<PageData>();
                //查询根节点
                if(responseData.getString("name").contains(name)) {
                    PageData data = new PageData();
                    data.put("type", responseData.getInt("type"));
                    data.put("id", responseData.getInt("id"));
                    data.put("name", responseData.getString("name"));
                    data.put("code", responseData.getString("code"));
                    data.put("order", responseData.getInt("order"));
                    data.put("pid", 0);
                    set.add(data);
                }
                //查询子节点
                List<PageData> childStr = (List<PageData>)responseData.get("children");
                //根节点肯定有children，不做判断
                String jsonStr = JSON.toJSONString(childStr);
                List<PageData> childList = JSONObject.parseArray(jsonStr, PageData.class);
                for(PageData data:childList) {
                    getSet(set, data, name, responseData.getInt("id"));
                }

                //将所有匹配的节点，向上递归，查找父节点，然后将父节点也增加到set中
                //为了方便向上递归，将responseData转变为带父节点的list
                List<PageData> allList = new ArrayList<PageData>();
                convertList(responseData, allList, 0);
                //存入父节点
                HashSet<PageData> tmpSet = new HashSet<PageData>();
                tmpSet.addAll(set);
                for(PageData data:tmpSet) {
                    getParentSet(data.getInt("pid"), set, allList);
                }
                //Set转为List并排序
                List<PageData> retList = new ArrayList<PageData>(set);
                Collections.sort(retList, new Comparator<PageData>() {
                    public int compare(PageData arg0, PageData arg1) {
                        return arg0.getInt("id").compareTo(arg1.getInt("id"));
                    }
                });
                res.setResponseList(retList);
            }else {
                res.setResponseObject(responseData);
            }
        }else {
            logger.info("====[/business/stationtree]调用HR系统获取树失败[{}],url[{}],token[{}]====",response.getStatusCode(),hrPrefix + orgPosition,remoteToken);
            res.setMsg("获取失败");
            res.setStatus("1");
        }
        return res;
    }

    /**
     * 递归查询某关键字的子节点，并赋值到set中
     */
    @SuppressWarnings("unchecked")
    private void getSet(HashSet<PageData> set, PageData pd, String name, int pid){
        int id = pd.getInt("id");
        //若该节点的名称包含关键字段
        if(pd.getString("name").contains(name)) {
            PageData data = new PageData();
            data.putAll(pd);
            data.put("pid", pid);
            data.remove("children");
            set.add(data);
        }
        //递归查询子节点
        List<PageData> childStr = (List<PageData>)pd.get("children");
        if(childStr!=null) {
            String jsonStr = JSON.toJSONString(childStr);
            List<PageData> childList = JSONObject.parseArray(jsonStr, PageData.class);
            for(PageData data:childList) {
                getSet(set, data, name, id);
            }
        }
    }

    /**
     * 将pd转变为带父节点的list
     * @param pd
     * @param tmpList
     * @param pid
     */
    @SuppressWarnings("unchecked")
    private void convertList(PageData pd, List<PageData> tmpList, int pid) {
        int id = pd.getInt("id");
        PageData data = new PageData();
        data.putAll(pd);
        data.put("pid", pid);
        data.remove("children");
        tmpList.add(data);
        //递归查询子节点
        List<PageData> childStr = (List<PageData>)pd.get("children");
        if(childStr!=null) {
            String jsonStr = JSON.toJSONString(childStr);
            List<PageData> childList = JSONObject.parseArray(jsonStr, PageData.class);
            for(PageData data1:childList) {
                convertList(data1, tmpList, id);
            }
        }
    }

    /**
     * 查找出所有的父节点
     * @param parentId
     * @param set
     * @param
     */
    private void getParentSet(int parentId, HashSet<PageData> set, List<PageData> list) {
        for(PageData data:list) {
            if(data.getInt("id")==parentId) {
                set.add(data);
                //向上递归
                getParentSet(data.getInt("pid"), set, list);
                break;
            }
        }
    }


    @GetMapping("/stationtree/root")
    public com.common.entity.RespEntity getStationTreeRoot(){
        com.common.entity.RespEntity res = new com.common.entity.RespEntity();
        //获取根节点
        String rootPath = hrPrefix+hrTreeRoot;
        ResponseEntity rootResponseEntity = hrTreeFun(rootPath);
        LinkedHashMap<String,Object> pageData = (LinkedHashMap<String,Object>) rootResponseEntity.getBody();

        //获取根节点下的第一层子节点
        String childPath = String.format(hrPrefix+hrTreeChild,pageData.get("id"));
        ResponseEntity childResponseEntity = hrTreeFun(childPath);
        List childList = (List) childResponseEntity.getBody();
        pageData.put("children",childList);
        res.setResponseObject(pageData);
        return res;
    }

    @GetMapping("/stationtree/child")
    public com.common.entity.RespEntity getStationTreeChild(){
        com.common.entity.RespEntity res = new com.common.entity.RespEntity();
        //获取对应节点的子节点
        PageData pd = this.getPageData();
        String parentId = pd.getString("id");
        if (parentId.contains(":")) {
            //截取id,例如hr11:142697
            String[] codes = parentId.split(":");
            parentId = codes[1];
        }
        String childPath = String.format(hrPrefix+hrTreeChild,parentId);
        ResponseEntity childResponseEntity = hrTreeFun(childPath);
        List childList = (List) childResponseEntity.getBody();
        res.setResponseObject(childList);
        return res;
    }

    @GetMapping("/stationtree/designatedId")
    public com.common.entity.RespEntity getdesignatedId(){
        com.common.entity.RespEntity res = new com.common.entity.RespEntity();
        PageData pd = this.getPageData();
        String parentId = pd.getString("id");
        Object o = redisDao.hmGet(ConstantValUtil.HR_CODE_TREE, parentId);
        //这里有可能是查部门，也可能是单位，是两个不同的接口，为了方便，使用hr系统获取组织机构PATH的接口统一获取
//        String url = hrPrefix + hrPath + "/" + parentId;
//        ResponseEntity responseEntity = hrTreeFun(url);
//        List<Map<String, Object>> pathList = (List<Map<String, Object>>) responseEntity.getBody();
        res.setResponseObject(o);
        return res;
    }

    /**
     * 模糊搜索人员
     * @author : liuf
     * @date : 2020年07月27日 14:17

     * @return : com.common.entity.RespEntity
     **/
    @GetMapping("/users/info")
    public com.common.entity.RespEntity searchUser(){
        com.common.entity.RespEntity res = new com.common.entity.RespEntity();
        //获取搜索条件
        PageData pd = this.getPageData();
        String userName = pd.getString("userName");
        String searchUrl = String.format(hrPrefix+searchUser,userName);
        ResponseEntity entity = hrTreeFun(searchUrl);
        List<Map<String,Object>> dataList = new ArrayList<>();
        List<Map<String,Object>> mapList = (List<Map<String, Object>>) entity.getBody();
        int size=mapList.size();
        if(mapList.size()>100){
            size=100;
        }
        List<Map<String,Object>> realList = mapList.subList(0,size);
        for(Map map : realList){
            List<Map<String,Object>> pageDataList = (List<Map<String, Object>>) map.get("positions");
            Map<String,Object> userMap = (Map<String, Object>) map.get("user");
            if(null!=pageDataList && !pageDataList.isEmpty()){
            //  String positionId = pageDataList.get(0).get("id").toString();
                //redis获取对应岗位信息
              String[] userPosition = getPathByUser(Integer.valueOf(userMap.get("id").toString()));
                //String path = (String) redisDao.hmGet(ConstantValUtil.HR_TREE,positionId);
                userMap.put("positionPath",userPosition[1]);
            }
            dataList.add(userMap);
        }
        res.setResponseList(dataList);
        return res;
    }

    private ResponseEntity hrTreeFun(String path){
        HttpHeaders headers = new HttpHeaders();
        String remoteToken = (String) redisDao.vGet(ConstantValUtil.REMOTE_TOKEN);
        System.out.println("========"+remoteToken);
        if(remoteToken == null){
            throw new MyException("请求一体化token为null");
        }
        headers.add("Authorization", "Bearer " + remoteToken);
        try {
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<Object> response = httpRestTemplate.exchange(path, HttpMethod.GET,entity, Object.class);
            if(response.getStatusCode() != HttpStatus.OK){
                logger.info("====[hrTreeFun]调用HR系统获取机构树失败[{}]====",response.toString());
                throw new MyException("调用HR系统获取机构树失败");
            }
            return response;
        }catch (ResourceAccessException e){
            if(e.getCause() instanceof SocketTimeoutException){
                logger.warn("====[hrTreeFun]调用HR系统连接或响应超时[{}]====",e.getMessage());
                throw new MyException("调用HR系统连接或响应超时",e);
            }else {
                logger.warn("====[hrTreeFun]调用HR系统发生I/O异常[{}]====",e.getMessage());
                throw new MyException("调用HR系统失败",e);
            }
        } catch (Exception e) {
            logger.warn("====[hrTreeFun]获取HR树中的岗位树失败[{}], url[{}],token[{}]====",e.getMessage(),path,remoteToken);
            throw new MyException(ConstantMsgUtil.ERR_QUERY_FAIL.desc(),e);
        }
    }


    /**
     * 根据岗位获取人员信息
     */
    @RequestMapping(value ="/users", method = RequestMethod.GET, produces = "application/json")
    public com.common.entity.RespEntity getUserByStation() {
        com.common.entity.RespEntity res = new com.common.entity.RespEntity();
        PageData pd = this.getPageData();
        //岗位id
        int id = pd.getInt("id");
        String name = pd.getString("name");
        String path = "";
        String remoteToken = "";
        try {

            //类型：1-单位 2-部门 3-岗位，当type为3时返回前端用户数据，否则返回空数据
            int type = pd.getInt("type");
            if(type==3) {
                HttpHeaders headers = new HttpHeaders();
                //header中增加token
                remoteToken = (String) redisDao.vGet(ConstantValUtil.REMOTE_TOKEN);
                headers.add("Authorization", "Bearer " + remoteToken);
                HttpEntity<String> entity = new HttpEntity<>(headers);

                //获取用户信息
                path =  hrUsers+"/"+id+"/users";
                if(remoteToken == null){
                    res.setMsg("null token");
                    res.setStatus("1");
                    return res;
                }

                //RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<JSONArray> response = httpRestTemplate.exchange(hrPrefix + path, HttpMethod.GET,entity, JSONArray.class);

                if(response.getStatusCode() == HttpStatus.OK){
                    JSONArray responseArr = response.getBody();
                    logger.debug("====[/business/users]调用HR系统获取用户信息[{}]====",responseArr);
                    List<PageData> dataList = JSONObject.parseArray(responseArr.toJSONString(), PageData.class);
                    List<PageData> retList = new ArrayList<PageData>();//模糊查询的结果
                    if(StringUtils.isNotBlank(name)) {
                        for(PageData data:dataList) {
                            if(data.getString("name").contains(name)) {
                                retList.add(data);
                            }
                        }
                    }else {
                        retList.addAll(dataList);
                    }

                    res.setResponseList(retList);
                }else {
                    logger.info("====[/business/users]调用HR系统获取岗位人员信息失败[{}],url[{}],token[{}]====",response.getStatusCode(),hrPrefix + path,remoteToken);
                    res.setMsg("获取失败");
                    res.setStatus("1");
                }
            }else {
                List<PageData> list2 = new ArrayList<PageData>();
                res.setResponseList(list2);
            }
        } catch (ResourceAccessException e){
            if(e.getCause() instanceof SocketTimeoutException){
                logger.warn("====[/business/users]调用HR系统连接或响应超时[{}]====",e.getMessage());
                throw new MyException(String.valueOf(HttpServletResponse.SC_REQUEST_TIMEOUT),"调用HR系统连接或响应超时",e);
            }else {
                logger.warn("====[/business/users]调用HR系统异常[{}]====",e.getMessage());
                throw new MyException("调用HR系统失败",e);
            }
        }catch (Exception e) {
            logger.error("====[/business/users]调用HR系统获取岗位人员信息异常[{}],url[{}],token[{}]====",e.getMessage(),hrPrefix + path,remoteToken);
            throw new MyException(ConstantMsgUtil.ERR_QUERY_FAIL.desc(),e);
        }
        return res;
    }


    /**
     * 获取组织树，带模糊查询
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value ="/orgtree", method = RequestMethod.GET, produces = "application/json")
    public com.common.entity.RespEntity getOrgTree() {
        com.common.entity.RespEntity res = new com.common.entity.RespEntity();
        PageData pd = this.getPageData();

        HttpHeaders headers = new HttpHeaders();
        //header中增加token
        String remoteToken = (String) redisDao.vGet(ConstantValUtil.REMOTE_TOKEN);
        headers.add("Authorization", "Bearer " + remoteToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        //获取用户信息
        if(remoteToken == null){
            res.setMsg("null token");
            res.setStatus("1");
            return res;
        }

        ResponseEntity<PageData> response = null;
        try {
            response = httpRestTemplate.exchange(orgPrefix + orgUrl, HttpMethod.GET,entity, PageData.class);
        } catch (ResourceAccessException e){
            if(e.getCause() instanceof SocketTimeoutException){
                logger.warn("====[/business/orgtree]调用组织机构系统连接或响应超时[{}]====",e.getMessage());
                throw new MyException(String.valueOf(HttpServletResponse.SC_REQUEST_TIMEOUT),"调用组织机构系统连接或响应超时",e);
            }else {
                logger.warn("====[/business/orgtree]调用组织机构系统异常[{}]====",e.getMessage());
                throw new MyException("调用组织机构系统失败",e);
            }
        }
        if(response.getStatusCode() == HttpStatus.OK){
            PageData responseData = response.getBody();
            logger.debug("====[/business/orgtree]调用组织机构系统获取数据[{}]====",responseData);
            //判断是否模糊查询
            String name = pd.getString("name");
            if(StringUtils.isNotBlank(name)) {
                //注意：为了防止重复，将数据先塞到set中！
                HashSet<PageData> set = new HashSet<PageData>();
                //查询根节点
                if(responseData.getString("name").contains(name)) {
                    PageData data = new PageData();
                    data.putAll(responseData);
                    data.put("pid", 0);
                    data.remove("children");
                    set.add(data);
                }
                //查询子节点
                List<PageData> childStr = (List<PageData>)responseData.get("children");
                //查询根节点下的所有匹配节点，根节点肯定有children，不做空值判断
                String jsonStr = JSON.toJSONString(childStr);
                List<PageData> childList = JSONObject.parseArray(jsonStr, PageData.class);
                for(PageData data:childList) {
                    getSet(set, data, name, responseData.getInt("id"));
                }

                //将所有匹配的节点，向上递归，查找父节点，然后将父节点也增加到set中
                //为了方便向上递归，将responseData转变为带父节点的list
                List<PageData> allList = new ArrayList<PageData>();
                convertList(responseData, allList, 0);
                //存入父节点
                HashSet<PageData> tmpSet = new HashSet<PageData>();
                tmpSet.addAll(set);
                for(PageData data:tmpSet) {
                    getParentSet(data.getInt("pid"), set, allList);
                }
                //Set转为List并排序
                List<PageData> retList = new ArrayList<PageData>(set);
                Collections.sort(retList, new Comparator<PageData>() {
                    public int compare(PageData arg0, PageData arg1) {
                        return arg0.getString("sortnumber").compareTo(arg1.getString("sortnumber"));
                    }
                });
                res.setResponseList(retList);
            }else {
                res.setResponseObject(responseData);
            }
        }else {
            logger.info("====[/business/orgtree]获取组织树数据失败[{}],url[{}],token[{}]====",response.getStatusCode(),orgPrefix + orgUrl,remoteToken);
            res.setMsg("获取失败");
            res.setStatus("1");
        }
        return res;
    }

    private void getPath(Map<String,Object> userMap, int id, PageData pageData){
        //todo:这里找到符合条件的人后，要终止递归，避免无效查询，使用抛异常的方式
        if(id == pageData.getInt("id")){
            userMap.put("positionPath",pageData.getString("path"));
            throw new MyException("====已找到数据====");
        }
        //递归查询子节点
        List<PageData> childStr = (List<PageData>)pageData.get("children");
        if(childStr!=null){
            String jsonStr = JSON.toJSONString(childStr);
            List<PageData> childList = JSONObject.parseArray(jsonStr, PageData.class);

            for(PageData data : childList){
                getPath(userMap,id,data);
            }
        }
    }

  private String[] getPathByUser(int userId) {
    String responseText2 = null;
    HttpHeaders headers = new HttpHeaders();
    //RestTemplate restTemplate = new RestTemplate();
    //OutRequestUtil.setRestTemplateEncode(restTemplate);
    String remoteToken = (String) redisDao.vGet(ConstantValUtil.REMOTE_TOKEN);
    headers.add("Authorization", "Bearer " + remoteToken);
    HttpEntity<String> entity = new HttpEntity<>(headers);
    if (remoteToken == null) {
      throw new MyException("外部系统的token失效！");
    }
    //获取用户岗位信息
    String path2 = hrUser+"/" + userId + "/positions";
    ResponseEntity<String> response2 = null;
    try {
      response2 = httpRestTemplate.exchange(hrPrefix + path2, HttpMethod.GET, entity, String.class);
    } catch (ResourceAccessException e){
      if(e.getCause() instanceof SocketTimeoutException){

        throw new MyException(String.valueOf(HttpServletResponse.SC_REQUEST_TIMEOUT),"调用HR系统连接或响应超时",e);
      }else {

        throw new MyException("调用HR系统失败",e);
      }
    }
    if (response2.getStatusCode() == HttpStatus.OK) {
      responseText2 = response2.getBody();
    } else {
      throw new MyException("调用HR系统失败！");
    }
    List<Map<String, Object>> listObject = (List<Map<String, Object>>) JSONArray.parse(responseText2);
    //根据岗位获取全路径
    return getPathByPosition(listObject);
  }

  /**
   * 根据岗位获取其全路径
   * @param listObject
   * @return
   */
  private String[] getPathByPosition(List<Map<String, Object>> listObject) {
    String[] ret= {"",""};
    HttpHeaders headers = new HttpHeaders();
    //RestTemplate restTemplate = new RestTemplate();
    //OutRequestUtil.setRestTemplateEncode(restTemplate);

    String remoteToken = (String) redisDao.vGet(ConstantValUtil.REMOTE_TOKEN);
    headers.add("Authorization", "Bearer " + remoteToken);
    HttpEntity<String> entity = new HttpEntity<>(headers);
    if (remoteToken == null) {
      throw new MyException("外部系统的token失效！");
    }

    //根据岗位获取岗位全路径
    String positionIdTmp="";
    String positionNameTmp="";
    for(Map<String, Object> positionMap:listObject) {
      int positionId = (int)positionMap.get("id");
      String path3 = hrPositionPath+"/" + positionId;
      ResponseEntity<String> response3 = null;
      try {
        response3 = httpRestTemplate.exchange(hrPrefix + path3, HttpMethod.GET, entity, String.class);
      } catch (ResourceAccessException e){
        if(e.getCause() instanceof SocketTimeoutException){
          throw new MyException(String.valueOf(HttpServletResponse.SC_REQUEST_TIMEOUT),"调用HR系统连接或响应超时",e);
        }else {
          throw new MyException("调用HR系统失败",e);
        }
      }
      if (response3.getStatusCode() == HttpStatus.OK) {
        String orgDataStr = response3.getBody();
        String[] positionArr = PositionUtil.getDepartOrgInfo(orgDataStr);
        positionIdTmp += positionArr[0];
        positionNameTmp += positionArr[1];
      } else {
        throw new MyException("调用HR系统失败！");
      }
    }
    ret[0] = positionIdTmp.substring(0, positionIdTmp.length()-1);
    ret[1] = positionNameTmp.substring(0, positionNameTmp.length()-1);
    return ret;
  }

}
