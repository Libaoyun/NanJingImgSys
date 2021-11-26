package com.rdexpense.manager.service.flow.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.base.dao.mysql.BaseDao;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.util.CheckParameter;
import com.common.util.ConstantValUtil;
import com.rdexpense.manager.service.flow.FlowService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author luxiangbao
 * @date 2021/11/12 11:48
 * @description 组织管理实现类
 */
@Service("FlowService")
public class FlowServiceImpl implements FlowService {

    @Autowired
    private BaseDao baseDao;


    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /**
     * 新增流程
     *
     * @param pd
     */
    @Override
    @Transactional
    public String  addFlow(PageData pd) {

        setFlowContent(pd);

        baseDao.insert("FlowMapper.insertData", pd);

        return  pd.getString("id");
    }


    /**
     * 编辑流程
     *
     * @param pd
     */
    @Override
    public String updateFlow(PageData pd) {
        setFlowContent(pd);

        baseDao.update("FlowMapper.updateData", pd);

        return  pd.getString("id");
    }


    /**
     * 重新将json格式进行封装
     * @param pd
     */
    private void setFlowContent(PageData pd){
        JSONObject object = new JSONObject();

        JSONArray nodeArr = new JSONArray();

        JSONObject jSONObject = JSONObject.parseObject(pd.getString("flowContent"));
        List<PageData> nodeList = JSONObject.parseArray(jSONObject.getString("nodeList"),PageData.class);

        Iterator<PageData> iterator=nodeList.iterator();
        while (iterator.hasNext()){
            PageData next = iterator.next();
            if(StringUtils.isBlank(next.getString("id"))){
                iterator.remove();
            }
        }


        if(!CollectionUtils.isEmpty(nodeList)){
            for(PageData data : nodeList){

                JSONObject newObj = new JSONObject();
                newObj.put("id",data.getString("id"));
                newObj.put("name",data.getString("name"));
                newObj.put("status",data.getString("status"));
                newObj.put("type",data.getString("type"));
                newObj.put("handleStrategy",data.getString("handleStrategy"));
                newObj.put("handleStatus",data.getString("handleStatus"));
                newObj.put("remark",data.getString("remark"));
                newObj.put("left",data.getString("left"));
                newObj.put("top",data.getString("top"));
                newObj.put("ico",data.getString("ico"));

                JSONArray handleRangeArr = JSONArray.parseArray(data.getString("handleRange"));
                newObj.put("handleRange",handleRangeArr);

                nodeArr.add(newObj);
            }
        }

        object.put("name",jSONObject.getString("name"));
        object.put("lineList",JSONArray.parseArray(jSONObject.getString("lineList")));
        object.put("nodeList",nodeArr);

        pd.put("flowContent",JSONArray.parseObject(object.toString()).toString());
    }

    /**
     * 删除流程
     *
     * @param pd
     */
    @Override
    public void deleteFlow(PageData pd) {
        List<String> idList = JSONArray.parseArray(pd.getString("idList"), String.class);

        if (!CollectionUtils.isEmpty(idList)) {
            baseDao.delete("FlowMapper.deleteData", idList);
        }

    }

    /**
     * 查看流程
     *
     * @param pd
     * @return
     */
    @Override
    public PageData getFlow(PageData pd) {
        PageData data = (PageData) baseDao.findForObject("FlowMapper.getData", pd);

        if(data != null){
            return data;
        }

        PageData result = new PageData();
        result.put("id","");
        result.put("name","");
        result.put("type","");
        result.put("status","0");
        result.put("handleStrategy","");
        result.put("remark","");
        result.put("nodeList","");
        result.put("lineList","");

        PageData result1 = new PageData();
        result1.put("flowContent",result);
        return result1;
    }

    /**
     * 查看流程
     *
     * @param pd
     * @return
     */
    @Override
    public PageData getSerialFlow(PageData pd) {

        PageData data = (PageData) baseDao.findForObject("FlowMapper.getSerialFlow", pd);
        return data;

    }


    /**
     * 查询审批人
     *
     * @param pd
     * @return
     */
    @Override
    public List<PageData> queryFlowUser(PageData pd) {
        List<PageData> list = (List<PageData>) baseDao.findForList("FlowMapper.queryFlowUser", pd);
        return list;
    }


    /**
     * 查看待办
     * @param pd
     * @return
     */
    @Override
    public List<PageData> queryWaitDone(PageData pd) {
        List<PageData> list = (List<PageData>) baseDao.findForList("FlowMapper.queryWaitDone",pd);
        return list;
    }


    /**
     * 查看待办
     * @param pd
     * @return
     */
    @Override
    public List<PageData> queryIsDone(PageData pd) {
        List<PageData> list = (List<PageData>) baseDao.findForList("FlowMapper.queryIsDone",pd);
        return list;
    }

    /**
     * 启动审批工作流
     *
     * 启动成功后返回审批实例ID，参数processInstId到对象中
     * @param pd
     * @return
     */
    @Override
    @Transactional
    public PageData startFlow(PageData pd) {
        //校验参数
        CheckParameter.stringLengthAndEmpty(pd.getString("menuCode"), "菜单编码", 128);
        CheckParameter.stringLengthAndEmpty(pd.getString("serialNumber"), "单据号", 128);

        //1、查询该功能菜单下配置的工作流
        PageData flowData = (PageData) baseDao.findForObject("FlowMapper.queryByMenuCode", pd);
        if (flowData != null) {

            //2、查询下一个审批人
            String flowContent = flowData.getString("flowContent");
            if (flowContent != null) {
                JSONObject jSONObject = JSONObject.parseObject(flowContent);

                List<PageData> nodeList = JSONObject.parseArray(jSONObject.getString("nodeList"),PageData.class);
                List<PageData> lineList = JSONObject.parseArray(jSONObject.getString("lineList"),PageData.class);

                if (CollectionUtils.isEmpty(nodeList) || CollectionUtils.isEmpty(lineList)) {
                    throw new MyException("审批配置异常，请重新设置");
                }

                //3、找到开始节点
                PageData startData = null;
                for (PageData nodeData : nodeList) {
                    //type是start表示是开始节点，找到节点后，找下一个节点
                    String type = nodeData.getString("type");
                    if (type.equals("start")) {
                        startData = nodeData;
                        nodeData.put("handleStatus",1);//把状态改成已审批
                        break;
                    }

                }

                //4、获取下一个节点
                PageData nextApproveNode = null;
                if (startData != null) {
                    nextApproveNode = getNextApproveNode(lineList,nodeList,startData.getString("id"));
                }else {
                    throw new MyException("审批流配置错误，没有开始节点");
                }


                if(nextApproveNode != null){
                    if(nextApproveNode.getString("type").equals("end")){
                        throw new MyException("审批流配置错误，没有审批中间节点");
                    }

                }else {
                    throw new MyException("未配置下一个审批节点");

                }

                //把下一个节点的状态，改成待审批
                for (PageData nodeData : nodeList) {
                    if (nodeData.getString("id").equals(nextApproveNode.getString("id"))) {
                        nodeData.put("handleStatus", 2);
                        break;
                    }

                }

                //查询下一个节点的审批人员
                List<PageData> approvalUserList = getApprovalUserList(nextApproveNode);


                PageData activityData = new PageData();

                activityData.put("serialNumber", pd.getString("serialNumber"));
                activityData.put("status", 1);

                jSONObject.put("nodeList",JSONArray.parseArray(JSON.toJSONString(nodeList)));
                activityData.put("flowContent",jSONObject.toString());
                activityData.put("flowId",flowData.getString("flowId"));
                activityData.put("createUserId",pd.getString("createUserId"));
                activityData.put("createUser",pd.getString("createUser"));

                //3、插入流程活动表，并获取主键ID
                int index = baseDao.insert("FlowMapper.insertApprovalActivity", activityData);
                String processInstId = null;
                if (index > 0) {
                    processInstId = activityData.getString("id");//实例ID
                    pd.put("processInstId",processInstId);//审批实例ID
                }

                //封装下一个审批人
                PageData nextUser = transforNextUser(approvalUserList);

                //4、插入发起人记录到审批履历表
                if(processInstId != null){
                    pd.put("approveStartTime",format.format(new Date()));
                    insertApproveSchedule(pd,startData,nextApproveNode,nextUser,
                            ConstantValUtil.APPROVAL_RESULT_NAME[0],ConstantValUtil.APPROVAL_RESULT[0],ConstantValUtil.APPROVAL_RESULT_NAME[0]);

                }


                //5、插入待办信息
                if(processInstId != null){
                    insertApproveDone(pd,startData,nextApproveNode,nextUser,approvalUserList);
                }

            }


        } else {
            throw new MyException("未配置审批流");
        }

        return pd;
    }


    /**
     * 审批同意 返回0表示审批流程结束，返回1表示还需要审批
     * @param pd
     * @return
     */
    @Override
    @Transactional
    public int approveFlow(PageData pd) {
        int result = 1;
        //校验参数
        CheckParameter.stringLengthAndEmpty(pd.getString("menuCode"), "菜单编码", 128);
        CheckParameter.stringLengthAndEmpty(pd.getString("waitId"), "待办表主键ID", 128);

        //1、根据待办ID查询，代码数据
        PageData waitData = (PageData) baseDao.findForObject("FlowMapper.queryApproveWaitData", pd);
        if (waitData == null) {

            throw new MyException("待办信息不存在，或者已审批过");

        }
        pd.put("processInstId",waitData.getString("processInstId"));
        //2、查询流程活动表获取当前单据正在使用的流程
        PageData flowData = (PageData) baseDao.findForObject("FlowMapper.queryByProcessInstId", waitData);
        if (flowData == null) {
            throw new MyException("当前单据使用的流程记录不存在");
        }

        String flowContent = flowData.getString("flowContent");
        if (flowContent == null) {
            throw new MyException("当前单据使用的流程内容不存在");
        }

        JSONObject jSONObject = JSONObject.parseObject(flowContent);

        List<PageData> nodeList = JSONObject.parseArray(jSONObject.getString("nodeList"), PageData.class);
        List<PageData> lineList = JSONObject.parseArray(jSONObject.getString("lineList"), PageData.class);


        String currentApproveNodeId = waitData.getString("nextApproveNodeId");
        //3、获取下一个节点
        PageData nextApproveNode = getNextApproveNode(lineList, nodeList, currentApproveNodeId);


        PageData activityData = new PageData();

        List<PageData> approvalUserList = new ArrayList<>();
        //当下一个节点是结束节点时，表示流程结束了
        if (nextApproveNode.getString("type").equals("end")) {
            result = 0;
            //修改当前节点以及结束节点的状态为，已审批
            for (PageData nodeData : nodeList) {
                String id = nodeData.getString("id");
                if (id.equals(currentApproveNodeId) || id.equals(nextApproveNode.getString("id"))) {
                    nodeData.put("handleStatus",1);

                }

            }

            activityData.put("status", 3);
            activityData.put("endTime", format.format(new Date()));

        }else {
            //当还有下一个审批节点时，继续查找下一个审批人
            approvalUserList = getApprovalUserList(nextApproveNode);
            //修改当前节点的状态为已审批
            for (PageData nodeData : nodeList) {
                if (nodeData.getString("id").equals(currentApproveNodeId)) {
                    nodeData.put("handleStatus",1);
                }

            }

            activityData.put("status", 2);
            activityData.put("endTime", null);
        }

        //封装下一个审批人
        PageData nextUser = transforNextUser(approvalUserList);



        //5\插入审批履历表
        PageData currentApproveNode = new PageData();
        currentApproveNode.put("id",currentApproveNodeId);
        currentApproveNode.put("name",waitData.getString("nextApproveNodeName"));

        //查询上个同一个单据流程履历表的最近一次的结束时间
        PageData scheduleData = (PageData) baseDao.findForObject("FlowMapper.queryScheduleData",waitData);
        if(scheduleData == null){
            pd.put("approveStartTime",format.format(new Date()));
        }else {
            pd.put("approveStartTime",scheduleData.getString("approveEndTime"));
        }

        insertApproveSchedule(pd,currentApproveNode,nextApproveNode,nextUser,
                ConstantValUtil.APPROVAL_RESULT_NAME[1],ConstantValUtil.APPROVAL_RESULT[1],ConstantValUtil.APPROVAL_RESULT_NAME[1]);

        //6、先修改待办信息，先判断该节点是什么处理策略 1：一人同意即可(默认)，2：所以人必须同意
        String handleStrategy = waitData.getString("handleStrategy");
        waitData.put("createUserId",pd.getString("createUserId"));
        Boolean bool = false;//是否该节点已经审批完成 false表示没有审完 true表示已审完
        if(handleStrategy.equals(1)){
            //修改待办表的数据为 已办
            baseDao.update("FlowMapper.updateApproveStatus",waitData);

            //修改待办人员表中自己的状态
            baseDao.update("FlowMapper.updateApproveUserStatus",waitData);

            bool = true;

            for (PageData nodeData : nodeList) {
                //把下一个节点改成待审批
                if (nodeData.getString("id").equals(nextApproveNode.getString("id"))) {
                    nodeData.put("handleStatus",2);
                }

            }

        }else {
            //查询该待办的人员是否都审批完成
            List<PageData> approvedList = (List<PageData>) baseDao.findForList("FlowMapper.queryNotApproveUser",waitData);
            if(CollectionUtils.isEmpty(approvedList)){//表示该节点只剩自己最后一个审批,自己审完则流程结束
                //修改待办表的数据为 已办
                baseDao.update("FlowMapper.updateApproveStatus",waitData);

                //修改待办人员表中自己的状态
                baseDao.update("FlowMapper.updateApproveUserStatus",waitData);

                bool = true;

                for (PageData nodeData : nodeList) {
                    //把下一个节点改成待审批
                    if (nodeData.getString("id").equals(nextApproveNode.getString("id"))) {
                        nodeData.put("handleStatus",2);
                    }

                }

            }else {//表示该节点还有其他人需要审批，则只需要该自己的状态即可
                //修改待办人员表中自己的状态
                baseDao.update("FlowMapper.updateApproveUserStatus",waitData);
            }

        }


        //4、修改活动表中的流程状态
        jSONObject.put("nodeList",JSONArray.parseArray(JSON.toJSONString(nodeList)));
        activityData.put("flowContent",jSONObject.toString());
        activityData.put("id",flowData.getString("id"));

        baseDao.update("FlowMapper.updateActivityData",activityData);


        //7、插入新的待办信息
        if(result == 1 && bool == true){
            insertApproveDone(pd,currentApproveNode,nextApproveNode,nextUser,approvalUserList);
        }


        return result;
    }


    /**
     * 回退到上一个节点
     * @param pd
     */
    @Override
    public void backPreviousNode(PageData pd) {
        //校验参数
        CheckParameter.stringLengthAndEmpty(pd.getString("menuCode"), "菜单编码", 128);
        CheckParameter.stringLengthAndEmpty(pd.getString("waitId"), "待办表主键ID", 128);

        //1、根据待办ID查询，代码数据
        PageData waitData = (PageData) baseDao.findForObject("FlowMapper.queryApproveWaitData", pd);
        if (waitData == null) {

            throw new MyException("待办信息不存在，或者已审批过");

        }
        pd.put("processInstId",waitData.getString("processInstId"));
        //2、查询流程活动表获取当前单据正在使用的流程
        PageData flowData = (PageData) baseDao.findForObject("FlowMapper.queryByProcessInstId", waitData);
        if (flowData == null) {
            throw new MyException("当前单据使用的流程记录不存在");
        }

        String flowContent = flowData.getString("flowContent");
        if (flowContent == null) {
            throw new MyException("当前单据使用的流程内容不存在");
        }

        JSONObject jSONObject = JSONObject.parseObject(flowContent);

        List<PageData> nodeList = JSONObject.parseArray(jSONObject.getString("nodeList"), PageData.class);
        List<PageData> lineList = JSONObject.parseArray(jSONObject.getString("lineList"), PageData.class);


        //修改

        String currentApproveNodeId = waitData.getString("nextApproveNodeId");


        //5、找上一个节点
        PageData previousApproveNode = getPreviousApproveNode(lineList, nodeList, currentApproveNodeId);

        PageData activityData = new PageData();
        //判断上一个节点是否是开始节点 ，如果是开始节点则相当于打回，该流程结束，发起人需编辑后重新提交
        //如果不是开始节点，则从上一个节点重新走审批流程
        String type = previousApproveNode.getString("type");
        if(type.equals("start")){
            //表示打回，流程结束
            //5、插入审批履历表
            PageData currentApproveNode = new PageData();
            currentApproveNode.put("id",currentApproveNodeId);
            currentApproveNode.put("name",waitData.getString("nextApproveNodeName"));

            //查询上个同一个单据流程履历表的最近一次的结束时间
            PageData scheduleData = (PageData) baseDao.findForObject("FlowMapper.queryScheduleData",waitData);
            if(scheduleData == null){
                pd.put("approveStartTime",format.format(new Date()));
            }else {
                pd.put("approveStartTime",scheduleData.getString("approveEndTime"));
            }

            insertApproveSchedule(pd,currentApproveNode,new PageData(),new PageData(),
                    ConstantValUtil.APPROVAL_RESULT_NAME[3],ConstantValUtil.APPROVAL_RESULT[3],ConstantValUtil.APPROVAL_RESULT_NAME[3]);


            //6.修改待办信息
            waitData.put("createUserId",pd.getString("createUserId"));
            //修改待办表的数据为 已办
            baseDao.update("FlowMapper.updateApproveStatus",waitData);

            //修改待办人员表中自己的状态
            baseDao.update("FlowMapper.updateApproveUserStatus",waitData);

            //7、插入新的待办信息
            //查询这条单据的编制人
            List<PageData> approvalUserList = new ArrayList<>();
            PageData data = new PageData();
            data.put("userCode",flowData.getString("createUserId"));
            approvalUserList.add(data);

            currentApproveNode.put("backFlag",1);
            insertApproveDone(pd,currentApproveNode,new PageData(),new PageData(),approvalUserList);

            //8、删除审批流程实例表
            baseDao.delete("FlowMapper.deleteActivityData",waitData);

        }else {
            //找到当前节点
            PageData currentApproveNode = getCurrentApproveNode( nodeList, currentApproveNodeId);
            //找到当前节点的审批人
            List<PageData> approvalUserList = getApprovalUserList(currentApproveNode);
            //封装当前节点的审批人
            PageData currentUser = transforNextUser(approvalUserList);

            for (PageData nodeData : nodeList) {
                //把上一个节点改成待审批
                if (nodeData.getString("id").equals(currentApproveNode.getString("id"))) {
                    nodeData.put("handleStatus",2);
                }

            }

            activityData.put("status", 2);
            activityData.put("endTime", null);

            //4、修改活动表中的流程状态
            jSONObject.put("nodeList",JSONArray.parseArray(JSON.toJSONString(nodeList)));
            activityData.put("flowContent",jSONObject.toString());
            activityData.put("id",flowData.getString("id"));

            baseDao.update("FlowMapper.updateActivityData",activityData);

            //5\插入审批履历表
//            PageData currentApproveNode = new PageData();
//            currentApproveNode.put("id",currentApproveNodeId);
//            currentApproveNode.put("name",waitData.getString("nextApproveNodeName"));

            //查询上个同一个单据流程履历表的最近一次的结束时间
            PageData scheduleData = (PageData) baseDao.findForObject("FlowMapper.queryScheduleData",waitData);
            if(scheduleData == null){
                pd.put("approveStartTime",format.format(new Date()));
            }else {
                pd.put("approveStartTime",scheduleData.getString("approveEndTime"));
            }

            insertApproveSchedule(pd,previousApproveNode,currentApproveNode,currentUser,
                    ConstantValUtil.APPROVAL_RESULT_NAME[2],ConstantValUtil.APPROVAL_RESULT[2],ConstantValUtil.APPROVAL_RESULT_NAME[2]);

            //6、先修改待办信息
            waitData.put("createUserId",pd.getString("createUserId"));

            //修改待办表的数据为 已办
            baseDao.update("FlowMapper.updateApproveStatus", waitData);

            //修改待办人员表中自己的状态
            baseDao.update("FlowMapper.updateApproveUserStatus", waitData);

            //7、插入新的待办信息
            insertApproveDone(pd,previousApproveNode,currentApproveNode,currentUser,approvalUserList);

        }




    }


    /**
     * 退回到发起人
     * @param pd
     */
    @Override
    public void backOriginalNode(PageData pd) {
        //校验参数
        CheckParameter.stringLengthAndEmpty(pd.getString("menuCode"), "菜单编码", 128);
        CheckParameter.stringLengthAndEmpty(pd.getString("waitId"), "待办表主键ID", 128);

        //1、根据待办ID查询，代码数据
        PageData waitData = (PageData) baseDao.findForObject("FlowMapper.queryApproveWaitData", pd);
        if (waitData == null) {

            throw new MyException("待办信息不存在，或者已审批过");

        }
        pd.put("processInstId",waitData.getString("processInstId"));
        //2、查询流程活动表获取当前单据正在使用的流程
        PageData flowData = (PageData) baseDao.findForObject("FlowMapper.queryByProcessInstId", waitData);
        if (flowData == null) {
            throw new MyException("当前单据使用的流程记录不存在");
        }

//        String flowContent = flowData.getString("flowContent");
//        if (flowContent == null) {
//            throw new MyException("当前单据使用的流程内容不存在");
//        }

//        JSONObject jSONObject = JSONObject.parseObject(flowContent);

//        List<PageData> nodeList = JSONObject.parseArray(jSONObject.getString("nodeList"), PageData.class);

        //修改

        String currentApproveNodeId = waitData.getString("nextApproveNodeId");
        //3、修改当前节点的状态为已审批
//        for (PageData nodeData : nodeList) {
//            if (nodeData.getString("id").equals(currentApproveNodeId)) {
//                nodeData.put("handleStatus",1);
//                break;
//            }
//
//        }

        //4、修改活动表中的流程状态
//        PageData activityData = new PageData();
//        jSONObject.put("nodeList",JSONArray.parseArray(JSON.toJSONString(nodeList)));
//        activityData.put("flowContent",jSONObject.toString());
//        activityData.put("id",flowData.getString("id"));
//        activityData.put("status", 3);
//        activityData.put("endTime", format.format(new Date()));
//
//        baseDao.update("FlowMapper.updateActivityData",activityData);

        //5、插入审批履历表
        PageData currentApproveNode = new PageData();
        currentApproveNode.put("id",currentApproveNodeId);
        currentApproveNode.put("name",waitData.getString("nextApproveNodeName"));

        //查询上个同一个单据流程履历表的最近一次的结束时间
        PageData scheduleData = (PageData) baseDao.findForObject("FlowMapper.queryScheduleData",waitData);
        if(scheduleData == null){
            pd.put("approveStartTime",format.format(new Date()));
        }else {
            pd.put("approveStartTime",scheduleData.getString("approveEndTime"));
        }

        insertApproveSchedule(pd,currentApproveNode,new PageData(),new PageData(),
                ConstantValUtil.APPROVAL_RESULT_NAME[3],ConstantValUtil.APPROVAL_RESULT[3],ConstantValUtil.APPROVAL_RESULT_NAME[3]);


        //6.修改待办信息
        waitData.put("createUserId",pd.getString("createUserId"));
        //修改待办表的数据为 已办
        baseDao.update("FlowMapper.updateApproveStatus",waitData);

        //修改待办人员表中自己的状态
        baseDao.update("FlowMapper.updateApproveUserStatus",waitData);

        //7、插入新的待办信息
        //查询这条单据的编制人
        List<PageData> approvalUserList = new ArrayList<>();
        PageData data = new PageData();
        data.put("userCode",flowData.getString("createUserId"));
        approvalUserList.add(data);

        currentApproveNode.put("backFlag",1);
        insertApproveDone(pd,currentApproveNode,new PageData(),new PageData(),approvalUserList);

        //8、删除审批流程实例表
        baseDao.delete("FlowMapper.deleteActivityData",waitData);


    }



    /**
     * 插入待办信息
     * @param pd
     * @param currentApproveNode
     * @param nextApproveNode
     * @param nextUser
     * @param approvalUserList
     */
    private void insertApproveDone(PageData pd,PageData currentApproveNode,PageData nextApproveNode,PageData nextUser,List<PageData> approvalUserList){
        String title = (String) baseDao.findForObject("FlowMapper.queryMenuData",pd);

        PageData doneData = new PageData();

        doneData.put("processInstId",pd.getString("processInstId"));//审批实例ID
        doneData.put("approveNodeId",currentApproveNode.getString("id"));
        doneData.put("approveNodeName",currentApproveNode.getString("name"));
        doneData.put("createUserId",pd.getString("createUserId"));
        doneData.put("createUser",pd.getString("createUser"));

        doneData.put("nextApproveUserId",nextUser.getString("userCode"));
        doneData.put("nextApproveUserName",nextUser.getString("userName"));
        doneData.put("nextApproveNodeId",nextApproveNode.getString("id"));
        doneData.put("nextApproveNodeName",nextApproveNode.getString("name"));

        doneData.put("serialNumber", pd.getString("serialNumber"));
        doneData.put("titleName",title);
        doneData.put("approveTime",null);
        doneData.put("status",0);
        doneData.put("handleStrategy",nextApproveNode.getString("handleStrategy"));
        doneData.put("backFlag",currentApproveNode.getString("backFlag"));
        int result = baseDao.insert("FlowMapper.insertApprovalNotDone", doneData);

        if(result > 0){
            for(PageData data : approvalUserList){
                data.put("approveId",doneData.getString("id"));
            }

            baseDao.batchInsert("FlowMapper.insertApprovalNotDoneUser", approvalUserList);
        }



    }


    /**
     * 插入审批履历表
     * @param pd
     * @param currentApproveNode
     * @param nextApproveNode
     * @param nextUser
     * @param approveComment
     * @param approveResultId
     * @param approveResultName
     */
    private void insertApproveSchedule(PageData pd,PageData currentApproveNode,PageData nextApproveNode,PageData nextUser,
                                       String approveComment,String approveResultId,String approveResultName){
        PageData nextApprove = new PageData();
        nextApprove.put("processInstId",pd.getString("processInstId"));//审批实例ID
        nextApprove.put("approveComment",approveComment);
        nextApprove.put("approveResultId", approveResultId);
        nextApprove.put("approveResultName",approveResultName);
        nextApprove.put("approveNodeId",currentApproveNode.getString("id"));
        nextApprove.put("approveNodeName",currentApproveNode.getString("name"));

        nextApprove.put("createUserId",pd.getString("createUserId"));
        nextApprove.put("createUser",pd.getString("createUser"));
        nextApprove.put("departmentCode",pd.getString("departmentCode"));
        nextApprove.put("departmentName",pd.getString("departmentName"));


        nextApprove.put("nextApproveUserId",nextUser.getString("userCode"));
        nextApprove.put("nextApproveUserName",nextUser.getString("userName"));
        nextApprove.put("nextApproveNodeId",nextApproveNode.getString("id"));
        nextApprove.put("nextApproveNodeName",nextApproveNode.getString("name"));
        nextApprove.put("fileId",pd.getString("fileId"));
        nextApprove.put("approveStartTime",pd.getString("approveStartTime"));

        baseDao.insert("FlowMapper.insertApprovalSchedule", nextApprove);
    }


    /**
     * 查询下一个审批节点
     * @param lineList
     * @param nodeList
     * @param currentNodeId
     * @return
     */
    private PageData getNextApproveNode(List<PageData> lineList,List<PageData> nodeList,String currentNodeId){

        PageData nextApproveNode = null;
        String nextNode = null;
        for (PageData lineData : lineList) {
            if (lineData.getString("from").equals(currentNodeId)) {
                nextNode = lineData.getString("to");
                break;
            }
        }


        if (StringUtils.isNotBlank(nextNode)) {
            for (PageData nodeData : nodeList) {
                String type = nodeData.getString("type");
                String status = nodeData.getString("status");
                if (nodeData.getString("id").equals(nextNode)) {
                    if (type.equals("end")) {
                        throw new MyException("流程配置异常，没有审批中间节点，或者未启用");
                    }
                    //当该节点是禁用的，则继续往下寻找
                    if(status.equals("0")){
                        getNextApproveNode(lineList,nodeList,nextNode);
                    }else{
                        nextApproveNode = nodeData;
                        break;
                    }

                }


            }

        } else {
            throw new MyException("下一个审批节点不存在，或者未启用");
        }

        if(nextApproveNode == null){
            throw new MyException("下一个审批节点不存在，或者未启用");
        }

        return nextApproveNode;
    }

    /**
     * 查询上一个审批节点
     * @param lineList
     * @param nodeList
     * @param currentNodeId
     * @return
     */
    private PageData getPreviousApproveNode(List<PageData> lineList,List<PageData> nodeList,String currentNodeId){

        PageData previousApproveNode = null;
        String previousNode = null;
        for (PageData lineData : lineList) {
            if (lineData.getString("to").equals(currentNodeId)) {
                previousNode = lineData.getString("from");
                break;
            }
        }

        if (StringUtils.isNotBlank(previousNode)) {
            for (PageData nodeData : nodeList) {
                String status = nodeData.getString("status");
                if (nodeData.getString("id").equals(previousNode)) {
                    //当该节点是禁用的，则继续往下寻找
                    if(status.equals("0")){
                        getPreviousApproveNode(lineList,nodeList,previousNode);
                    }else{
                        previousApproveNode = nodeData;
                        break;
                    }


                }

            }

        } else {
            throw new MyException("上一个审批节点不存在，或者未启用");
        }


        if(previousApproveNode == null){
            throw new MyException("上一个审批节点不存在，或者未启用");
        }

        return previousApproveNode;
    }


    /**
     * 查询当前审批节点
     * @param nodeList
     * @param currentNodeId
     * @return
     */
    private PageData getCurrentApproveNode(List<PageData> nodeList,String currentNodeId){

        PageData currentApproveNode = null;

        if (StringUtils.isNotBlank(currentNodeId)) {
            for (PageData nodeData : nodeList) {
                if (nodeData.getString("id").equals(currentNodeId)) {
                    currentApproveNode = nodeData;
                    break;
                }

            }

        }

        if(currentApproveNode == null){
            throw new MyException("当前审批节点不存在");
        }

        return currentApproveNode;
    }


    /**
     * 封装处理下一个审批人ID和名称
     * @param list
     * @return
     */
    private PageData transforNextUser(List<PageData> list){
        PageData result = new PageData();

        String userCode = "";
        String userName = "";
        if(!CollectionUtils.isEmpty(list)){
            for(PageData data : list) {

                if (StringUtils.isNotBlank(data.getString("userCode"))) {
                    userCode = userCode + data.getString("userCode") + ",";
                }

                if (StringUtils.isNotBlank(data.getString("userName"))) {
                    userName = userName + data.getString("userName") + ",";
                }

            }

        }

        if(StringUtils.isNotBlank(userName)){
            result.put("userName",userName.substring(0,userName.lastIndexOf(",")-1));
        }

        if(StringUtils.isNotBlank(userCode)){
            result.put("userCode",userCode.substring(0,userCode.lastIndexOf(",")-1));
        }

        return result;

    }



    //根据审批节点，查询审批人
    private List<PageData> getApprovalUserList(PageData pd) {
        List<PageData> list = new ArrayList<>();
        List<PageData> handleRangeList = JSONObject.parseArray(pd.getString("handleRange"),PageData.class);
        if (!CollectionUtils.isEmpty(handleRangeList)) {
            TreeMap<String, List<PageData>> map = handleRangeList.stream().collect(Collectors.groupingBy(o -> o.getString("handleType"), TreeMap::new, Collectors.toList()));

            if (map != null && map.size() > 0) {
                for (Map.Entry<String, List<PageData>> entry : map.entrySet()) {
                    String key = entry.getKey();
                    List<PageData> value = entry.getValue();

                    if (!CollectionUtils.isEmpty(value)) {
                        //查询节点下设置的审批人是否都存在
                        List<PageData> userList = (List<PageData>) baseDao.findForList("FlowMapper.queryUser", value);
                        if (!CollectionUtils.isEmpty(userList)) {
                            list.addAll(userList);
                        }

                    } else if (key.equals("post")) {
                        //查询职务下面的人
                        List<PageData> userList = (List<PageData>) baseDao.findForList("FlowMapper.queryUserByPost", value);
                        if (!CollectionUtils.isEmpty(userList)) {
                            list.addAll(userList);
                        }
                    }
                }

            }
            if (CollectionUtils.isEmpty(list)) {
                throw new MyException("下个审批节点设置的审批人不存在");
            }


        } else {

            throw new MyException("下个审批节点未设置审批人");
        }

        return list;

    }


}
