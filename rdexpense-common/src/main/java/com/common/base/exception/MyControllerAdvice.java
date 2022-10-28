package com.common.base.exception;



import com.common.entity.ResponseEntity;
import com.common.util.ConstantMsgUtil;
import com.google.common.base.Splitter;
import org.apache.log4j.Logger;
import org.redisson.client.RedisConnectionException;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * <pre>
 * 对象功能:异常处理类
 * 开发人员:lixin
 * 创建时间:2018-07-24
 * </pre>
 */
@RestControllerAdvice
public class MyControllerAdvice {

    public static final String SYSTEM_ERROR = "系统异常！请联系管理员！";

//    public static final String CH_REG = "[^\u4e00-\u9fa5]";

	protected Logger logger = Logger.getLogger(this.getClass());


    /**
     * 自定义异常
     */
    @ExceptionHandler({MyException.class,Exception.class, MyIOException.class, MyFileNotFoundException.class, BindException.class})
    public ResponseEntity<String> handleBaseException(HttpServletRequest request, Exception e) {
        //打印日志
        printLog(request, e);
//        String errMsg = e.getMessage().replaceAll(CH_REG, "");
        if (e.getCause()==null){
            return ResponseEntity.failure(ConstantMsgUtil.FAIL_STATUS.val(), String.format("%s", e.getMessage()));
        }else if (e.getCause().getMessage()!=null){
            return ResponseEntity.failure(ConstantMsgUtil.FAIL_STATUS.val(), String.format("%s--%s", e.getMessage(),e.getCause().getMessage()));

        }
//        return null;
//        if (e.getCause()==null){
//            return ResponseEntity.failure(ConstantMsgUtil.FAIL_STATUS.val(), errMsg);
//        }else if (e.getCause().getMessage()!=null){
//            return ResponseEntity.failure(ConstantMsgUtil.FAIL_STATUS.val(), String.format("%s--%s", errMsg,e.getCause().getMessage().replaceAll(CH_REG,"")));
//        }
        return null;
    }



    private void printLog(HttpServletRequest request, Exception e) {
        String message = String.format("接口[%s]出现异常，异常摘要：%s",
                request.getRequestURI(),
                e.getMessage());
        logger.error(message, e);
    }

//	/**
//	 * 全局异常捕捉处理
//	 *
//	 * @param ex
//	 * @return
//	 */
//	@ResponseBody
//	@ExceptionHandler(value = Exception.class)
//	public ResponseEntity<String> errorHandler(Exception ex) {
//		RespEntity respEntity = new RespEntity();
////		responseEntity.setStatus(ConstantMsgUtil.getFailStatus());
//		if (StringUtils.isNotEmpty(ex.getMessage())) {
//			respEntity.setMsg(ex.getMessage());
//		} else {
////			responseEntity.setMsg(ConstantMsgUtil.getFailMsg());
//		}
//		logger.error(ex.getMessage(), ex);
//		return respEntity;
//	}

//	/**
//	 * 拦截捕捉自定义异常 MyException.class
//	 *
//	 * @param ex
//	 * @return
//	 */
//	@ResponseBody
//	@ExceptionHandler(value = {MyException.class,Exception.class,MyIOException.class,MyFileNotFoundException.class, BindException.class})
//	public ResponseEntity<String> myErrorHandler(Exception ex) {
//		logger.error(ex.getMessage(), ex.getCause());
//		//TODO 插入日志
//		return ResponseEntity.failure(ConstantMsgUtil.FAIL_STATUS.val(),ex.getMessage());
//	}
//
//	/**
//	 * 拦截捕捉自定义异常 MyIOException
//	 * @param e
//	 * @return
//	 */
//	@ResponseBody
//	@ExceptionHandler(value = MyIOException.class)
//	public RespEntity myIOErrorHandler(MyIOException e){
//		RespEntity respEntity = new RespEntity();
////		responseEntity.setStatus(e.getErrorCode());
//		respEntity.setMsg(e.getMessage());
//		logger.error(e.getMessage(),e);
//		return respEntity;
//	}
//
//	/**
//	 * 拦截捕捉自定义异常 MyFileNotFoundException
//	 * @param e
//	 * @return
//	 */
//	@ResponseBody
//	@ExceptionHandler(value = MyFileNotFoundException.class)
//	public RespEntity myFileErrorHandler(MyFileNotFoundException e){
//		RespEntity respEntity = new RespEntity();
////		responseEntity.setStatus(e.getErrorCode());
//		respEntity.setMsg(e.getMessage());
//		logger.error(e.getMessage(),e);
//		return respEntity;
//	}
}
