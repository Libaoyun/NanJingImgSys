package com.imgSys.controller;

import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.common.util.CheckParameter;
import com.common.util.ConstantMsgUtil;
import com.common.util.PropertyUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imgSys.dto.BodyPart;
import com.imgSys.dto.Device;
import com.imgSys.dto.Page.PageParam;
import com.imgSys.dto.serialNum.SerialNumListDto;
import com.imgSys.service.DeviceService;
import com.rdexpense.manager.controller.base.BaseController;

import com.rdexpense.manager.util.LogUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.common.util.ConstantMsgUtil.*;

/**
 * @description: 设备管理
 * @author: Libaoyun
 * @date: 2022-10-19 19:19
 **/

@RestController
@RequestMapping("/device")
@Api(value = "设备管理", tags = "设备管理")
public class DeviceController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(BodyPart.class);

    @Autowired
    private LogUtil logUtil;

    @Autowired
    private DeviceService deviceService;

    @ApiOperation("新增设备信息")
    @PostMapping()
    public ResponseEntity addDevice(Device device){
        PageData pd = this.getParams();

        // FIXME 哪些是必选的，待考究
        CheckParameter.stringLengthAndEmpty(pd.getString("deviceSerialNum"), "设备序列号(Mac地址)",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("deviceName"), "设备名称",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("deviceCode"), "设备资产编码",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("deviceModel"), "设备型号",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("departmentCode"), "所属科室编码",128);

        ResponseEntity result = null;
        try {
            deviceService.addDevice(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_SAVE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_SAVE_FAIL.val(), e.getMessage());
            logger.error("新增设备信息失败,request=[{}]", pd);
            throw new MyException(ERR_SAVE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 1, "设备", pd);
        }
    }

    @ApiOperation(value = "删除一个/多个设备信息")
    @DeleteMapping()
    public ResponseEntity deleteDevice(SerialNumListDto serialNumListDto) {
        PageData pd = this.getParams();
        String idList = pd.getString("idList");
        if (StringUtils.isBlank(idList)) {
            throw new MyException("序列号不能为空!");
        }
        ResponseEntity result = null;
        try {
            deviceService.deleteDevice(pd);
            result = ResponseEntity.success(null, INFO_DELETE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_DELETE_FAIL.val(), e.getMessage());
            logger.error("删除设备失败,request=[{}]", pd);
            throw new MyException(ERR_DELETE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 2, "设备", pd);
        }
    }

    @ApiOperation(value = "编辑设备信息")
    @PostMapping(value = "/update",  consumes = "application/json")
    public ResponseEntity updateDevice(Device device) {
        PageData pd = this.getParams();
        CheckParameter.stringLengthAndEmpty(pd.getString("deviceSerialNum"), "设备序列号",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("deviceName"), "设备名称",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("deviceCode"), "设备资产编码",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("departmentCode"), "所属科室编码",128);

        ResponseEntity result = null;
        try {
            deviceService.updateDevice(pd);
            result = ResponseEntity.success(null, ConstantMsgUtil.INFO_UPDATE_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_UPDATE_FAIL.val(), e.getMessage());
            logger.error("编辑设备失败,request=[{}]", pd);
            throw new MyException(ERR_UPDATE_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 3, "设备", pd);
        }
    }

    /**
     * 这里注意，使用的是参数形式，而不是JSON形式了，如果需要修改参数形式，更改请求方式即可，get是xml，post是json
     * @param device
     * @return
     */
    @GetMapping()
    @ApiOperation(value = "分页(关键词)查询所有设备信息")
    public ResponseEntity<PageInfo<Device>> getAllDevice(Device device, PageParam pageParam) {
        PageData pd = this.getParams();
        try {
            // 这里方便起见先转String，后真正分页时将数据再转回Int即可
            pd.put("pageNum", pd.getString("pageNum") == "" ? "1" : pd.getString("pageNum"));
            pd.put("pageSize", pd.getString("pageSize") == "" ? "10" : pd.getString("pageSize"));
            PageHelper.startPage(pd.getInt("pageNum"), pd.getInt("pageSize"));
            List<PageData> list = deviceService.getAllDevice(pd);
            PageInfo<PageData> pageInfo = new PageInfo<>(list);
            return ResponseEntity.success(PropertyUtil.pushPageList(pageInfo, Device.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (MyException e) {
            logger.error("查询所有设备信息,request=[{}]", pd);
            return ResponseEntity.failure(ERR_QUERY_FAIL.val(), ERR_QUERY_FAIL.desc());
        }
    }

    /**
     * 新增设备时可查看所有部位，修改部位时可以查看设备已配置可选的部位(根据是否有DeviceSerialNum)
     * @param device
     * @return
     */
    @GetMapping("/sketch")
    @ApiOperation(value = "新增设备/修改设备时可见部位示意图")
    public ResponseEntity<List<BodyPart>> getBodyPartByDevice(Device device) {
        PageData pd = this.getParamsFormat("XML");
        try {
            List<PageData> list = deviceService.getBodyPartByDevice(pd);
            return ResponseEntity.success(PropertyUtil.covertListModel(list, BodyPart.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (MyException e) {
            logger.error("查询所有设备信息,request=[{}]", pd);
            return ResponseEntity.failure(ERR_QUERY_FAIL.val(), ERR_QUERY_FAIL.desc());
        }
    }
}
