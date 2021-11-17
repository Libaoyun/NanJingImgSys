package com.rdexpense.manager.controller.system;

import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.entity.ResponseEntity;
import com.common.util.CheckParameter;
import com.common.util.ConstantMsgUtil;
import com.common.util.PropertyUtil;
import com.common.util.SerialNumberUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.rdexpense.manager.controller.base.BaseController;
import com.rdexpense.manager.dto.system.operationLog.IdListDto;
import com.rdexpense.manager.dto.system.operationLog.OperationLogDto;
import com.rdexpense.manager.dto.system.operationLog.OperationLogListDTO;
import com.rdexpense.manager.service.system.OperationLogService;
import com.rdexpense.manager.util.LogUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.common.util.ConstantMsgUtil.INFO_EXPORT_SUCCESS;

/**
 * @ClassName: OperationLogController
 * @Description:
 * @Author: rdexpense
 * @Date: 2020/4/9 18:32
 * @Version: 1.0
 */
@RestController
@RequestMapping(value = "/operationLog")
@Api(value = "操作日志", tags = "操作日志")
public class OperationLogController extends BaseController {

    public static final Logger logger = LoggerFactory.getLogger(OperationLogController.class);

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private LogUtil logUtil;

    @ApiOperation(value="获取操作日志列表", notes="获取操作日志列表")
    @PostMapping(value = "/queryOperationLogList")
    public ResponseEntity<PageInfo<OperationLogListDTO>> findOperationLogList(OperationLogDto operationLogDto) {
        PageData pd = this.getParams();
        try {
            PageHelper.startPage(pd.getInt("pageNum"), pd.getInt("pageSize"));
            List<PageData> list = operationLogService.findOperationLogList(pd);
            PageInfo<PageData> pageInfo = new PageInfo<>(list);
            return ResponseEntity.success(PropertyUtil.pushPageList(pageInfo, OperationLogListDTO.class), ConstantMsgUtil.INFO_QUERY_SUCCESS.desc());
        } catch (Exception e) {
            logger.error("获取操作日志列表失败--查询列表,request=[{}]", pd);
            return ResponseEntity.failure(ConstantMsgUtil.ERR_QUERY_FAIL.val(), e.getMessage());
        }
    }


    @ApiOperation(value = "导出Excel")
    @PostMapping(value = "/exportExcel", consumes = "application/json")
    public ResponseEntity exportExcel(IdListDto idListDto){
        PageData pd = this.getParams();
        CheckParameter.checkDefaultParams(pd);
        CheckParameter.stringIsNull(pd.getString("idList"), "主键ID集合");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        ResponseEntity result = null;
        try {
            ResponseEntity<HSSFWorkbook> responseEntity = operationLogService.exportExcel(pd);
            HSSFWorkbook wb = responseEntity.getData();
            HttpServletResponse res = this.getResponse();
            String serialNumber = SerialNumberUtil.generateSerialNo("operationLog");
            String fileName = "操作日志_" + sdf.format(new Date()) + "_"+serialNumber +".xls";
            res.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
            OutputStream out = res.getOutputStream();
            wb.write(out);
            out.close();
            result = ResponseEntity.success(null, INFO_EXPORT_SUCCESS.desc());
            return result;
        } catch (Exception e) {
            result = ResponseEntity.failure(ConstantMsgUtil.ERR_EXPORT_FAIL.val(), e.getMessage());
            throw new MyException(ConstantMsgUtil.ERR_EXPORT_FAIL.desc(), e);
        } finally {
            logUtil.saveLogData(result.getCode(), 6, "操作日志", pd);
        }
    }

}
