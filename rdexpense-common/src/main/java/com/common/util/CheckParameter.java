package com.common.util;

import com.alibaba.fastjson.JSONObject;
import com.common.base.exception.MyException;
import com.common.entity.PageData;
import com.common.entity.RespEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 校验接口参数通用类
 *
 * @author rdexpense
 */
public class CheckParameter {
    protected static Logger logger = Logger.getLogger(CheckParameter.class);

    // private static ResponseEntity responseEntity;

	// 手机号码11位
	public static final Pattern mobileRegex = Pattern.compile("^1\\d{10}$");
    /**
     * 金额类型校验  14位  1-12 位整数，两位小数
     */
    private static final Pattern AMOUNT_REGEX14 = Pattern.compile("^\\d{1,12}\\.\\d{2}$");

    /**
     * 金额类型校验  4位  1-2 位整数，两位小数
     */
    private static final Pattern AMOUNT_REGEX4 = Pattern.compile("^\\d{1,2}\\.\\d{2}$");

    /**
     * 整数，可为0
     */
    private static final Pattern INTEGET4 = Pattern.compile("^\\d{1,4}$");

    /**
     *   10位  1-8 位整数，两位小数
     */
    private static final Pattern INTEGET10 = Pattern.compile("^\\d{1,8}\\.\\d{2}$");

    /**
     *  6位整数
     */
    private static final Pattern INTEGET6 = Pattern.compile("^[1-9]{1,6}$");

    /**
     * 身份证号位数
     */
    private static final Pattern ID_NUMBER = Pattern.compile("^\\d{15}|\\d{18}$");

	/**
	 * 检查字符串是否为空值
	 *
	 * @param paraValue 参数值
	 * @param paraName  参数名称
	 * @return
	 */
	public static boolean isEmpty(RespEntity respEntity, String paraValue, String paraName) {
		if (StringUtils.isEmpty(paraValue) || "null".equals(paraValue) || "undefined".equals(paraValue)) {
			respEntity.setMsg(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_NO_EMPTY.desc(), paraName));
			respEntity.setStatus(ConstantMsgUtil.FAIL_STATUS.val().toString());
			return true;
		}
		return false;
	}


	/**
	 * 检查数值是否为空值
	 *
	 * @param paraValue 参数值
	 * @param paraName  参数名称
	 * @return
	 */
	public static boolean isEmptyInt(RespEntity respEntity, Integer paraValue, String paraName) {
		if (paraValue == null ) {
			respEntity.setMsg(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_NO_EMPTY.desc(), paraName));
			respEntity.setStatus(ConstantMsgUtil.FAIL_STATUS.val().toString());
			return true;
		}
		return false;
	}


	/**
	 * 检查字符串长度
	 *
	 * @param paraValue  参数值
	 * @param paraName   参数名称
	 * @param paraLength 参数长度
	 * @return
	 */
	public static boolean stringLength(RespEntity respEntity, String paraValue, String paraName,
                                       Integer paraLength) {
		if (!StringUtils.isEmpty(paraValue) && paraValue.length() > paraLength) {
			respEntity.setMsg(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_SUPER_LONG.desc(), paraName));
			respEntity.setStatus(ConstantMsgUtil.FAIL_STATUS.val().toString());
			return true;
		}
		return false;
	}


	/**
	 * 检查整数类型参数长度及是否为纯数字类型
	 *
	 * @param paraValue 参数值
	 * @param paraName  参数名称
	 * @param maxInt    最大整数位
	 * @return
	 */
	public static void intNumberLength(String paraValue, String paraName,
									   Integer maxInt) {
		if (!StringUtils.isEmpty(paraValue)) {
			// 判断是否包含负号
			if (paraValue.contains("-")) {
				paraValue = paraValue.substring(1, paraValue.length());
				maxInt = maxInt - 1;
			}
			// 判断是否都为数字
			if (isNumeric(paraValue)) {
				if (paraValue.length() > maxInt) {
					paraName = paraName + ";" + maxInt;
					throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_INT_LONG.desc(), paraName));
				}
			} else {
				throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_WRONGFUL.desc(), paraName));
			}
		}
	}


    /**
     * 检查浮点类型参数长度，包括整数位和小数位长度，及是否为纯数字类型
     *
     * @param paraValue  参数值
     * @param paraName   参数名称
     * @param maxInt     最大整数位
     * @param maxDecimal 最大小数位
     * @return
     */
    public static boolean floatNumberLength(RespEntity respEntity, String paraValue, String paraName,
                                            Integer maxInt, Integer maxDecimal) {
        if (!StringUtils.isEmpty(paraValue)) {
            int sumNuber = 0;
            // 检查是否包含多个小数点
            for (int i = 0; i < paraValue.length(); i++) {
                if (".".equals(paraValue.charAt(i))) {
                    sumNuber++;
                }
            }

            // 检查是否包含多个小数点
            if (sumNuber > 1) {
                String pointParaName = paraName + ";" + sumNuber;
                respEntity
                        .setMsg(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_DECIMAL_POINT.desc(), pointParaName));
                respEntity.setStatus(ConstantMsgUtil.FAIL_STATUS.val().toString());
            } else {
                if (paraValue.contains(".")) {
                    // 检查是否大于最大整数位和最大小数位
                    int intNumber = paraValue.indexOf(".");
                    String intParaValue = paraValue.substring(0, intNumber);
                    // 判断是否包含负号
                    if (intParaValue.contains("-")) {
                        intParaValue = intParaValue.substring(1, intParaValue.length());
                        maxInt = maxInt - 1;
                    }

                    // 判断是否为数字
                    if (isNumeric(intParaValue)) {
                        if (intParaValue.length() > maxInt) {
                            String intParaName = paraName + ";" + maxInt;
                            respEntity.setMsg(
                                    ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_INT_LONG.desc(), intParaName));
                            respEntity.setStatus(ConstantMsgUtil.FAIL_STATUS.val().toString());
                            return true;
                        }
                    } else {
                        respEntity
                                .setMsg(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_WRONGFUL.desc(), paraName));
                        respEntity.setStatus(ConstantMsgUtil.FAIL_STATUS.val().toString());
                        return true;
                    }

                    String floatParaValue = paraValue.substring(intNumber + 1, maxDecimal);
                    // 判断是否为数字
                    if (isNumeric(intParaValue)) {
                        // 小数位超长
                        if (floatParaValue.length() > maxDecimal) {
                            String floatParaName = paraName + ";" + maxDecimal;
                            respEntity.setMsg(
                                    ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_FLOAT_LONG.desc(), floatParaName));
                            respEntity.setStatus(ConstantMsgUtil.FAIL_STATUS.val().toString());
                            return true;
                        }
                    } else {
                        respEntity
                                .setMsg(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_WRONGFUL.desc(), paraName));
                        respEntity.setStatus(ConstantMsgUtil.FAIL_STATUS.val().toString());
                        return true;
                    }
                } else {
                    // 不包含小数位时，检查整数位是否大于最大整数位
                    if (paraValue.length() > maxInt) {
                        String intParaName = paraName + ";" + maxInt;
                        respEntity.setMsg(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_INT_LONG.desc(), intParaName));
                        respEntity.setStatus(ConstantMsgUtil.FAIL_STATUS.val().toString());
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * 检查手机号码的合法性
     *
     * @param paraValue 参数值
     * @param paraName  参数名称
     * @return
     */
    public static boolean checkMobileNumberLength(String paraValue, String paraName) {
        if (!StringUtils.isEmpty(paraValue)) {
            Matcher matcher = mobileRegex.matcher(paraValue);
            boolean flag = matcher.matches();
            if (!flag) {
                throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_WRONGFUL.desc(), paraName));
               /* responseEntity.setMsg(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_WRONGFUL.desc(), paraName));
                responseEntity.setStatus(ConstantMsgUtil.FAIL_STATUS.val());*/
            }
            return true;
        }
        return false;
    }

    /**
     * 检查邮箱的合法性
     *
     * @param paraValue 参数值
     * @param paraName  参数名称
     * @return
     */
    public static boolean checkEmail(RespEntity respEntity, String paraValue, String paraName) {
        String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern regex = Pattern.compile(check);
        if (!StringUtils.isEmpty(paraValue)) {
            Matcher matcher = regex.matcher(paraValue);
            boolean flag = matcher.matches();
            if (!flag) {
                respEntity.setMsg(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_WRONGFUL.desc(), paraName));
                respEntity.setStatus(ConstantMsgUtil.FAIL_STATUS.val().toString());
                return true;
            }
        }
        return false;
    }

    /**
     * 判断一个字符串是否为数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        for (int i = str.length(); --i >= 0; ) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }


    /**
     * 校验Decimal类型
     *
     * @param
     * @param
     * @param
     * @param
     */

	public static void checkSaveRetirement(PageData pd) {
		// 主表字段校验
		String creator_org_code = pd.getString("creator_org_code");
		stringLengthAndEmpty(creator_org_code, "creator_org_code", 128);
		String creator_org_name = pd.getString("creator_org_name");
		stringLengthAndEmpty(creator_org_name, "creator_org_name", 128);
		String creator_user_name = pd.getString("creator_user_name");
		stringLengthAndEmpty(creator_user_name, "creator_user_name", 32);
		String remark = pd.getString("remark");
		stringLengthAndEmpty(remark, "remark", 1024);
		String total_original_value = pd.getString("total_original_value");
		checkDecimal(total_original_value, "total_original_value", 18, 2);
		String total_net_value = pd.getString("total_net_value");
		checkDecimal(total_net_value, "total_net_value", 18, 2);

		// 子表字段校验
		String details = pd.getString("details");
		@SuppressWarnings("rawtypes")
		List<Map> listDetail = JSONObject.parseArray(details, Map.class);
		if (listDetail.size() > 0) {
			for (int i = 0; i < listDetail.size(); i++) {
				@SuppressWarnings("unchecked")
				Map<String, Object> map = listDetail.get(i);

				String equipment_name = (String) map.get("equipment_name");
				stringLengthAndEmpty(equipment_name, "equipment_name", 128);
				String equipment_management_code = (String) map.get("equipment_management_code");
				stringLengthAndEmpty(equipment_management_code, "equipment_management_code", 128);
				String spec = (String) map.get("spec");
				stringLengthAndEmpty(spec, "spec", 128);
				String plate_number = (String) map.get("plate_number");
				stringLengthAndEmpty(plate_number, "plate_number", 128);
				String manufacturer = (String) map.get("manufacturer");
				stringLengthAndEmpty(manufacturer, "manufacturer", 128);

				String ower_org = (String) map.get("ower_org");
				stringLengthAndEmpty(ower_org, "ower_org", 128);
				String place = (String) map.get("place");
				stringLengthAndEmpty(place, "place", 128);
				String retirement_type = (String) map.get("retirement_type");
				stringLengthAndEmpty(retirement_type, "retirement_type", 64);
				String reason = (String) map.get("reason");
				stringLengthAndEmpty(reason, "reason", 1024);

				String original_value = (String) map.get("original_value");
				checkDecimal(original_value, "original_value", 18, 2);
				String net_value = (String) map.get("net_value");
				checkDecimal(net_value, "net_value", 18, 2);
			}
		}

		// 附件字段校验
		String files = pd.getString("files");
		@SuppressWarnings("rawtypes")
		List<Map> listFile = JSONObject.parseArray(files, Map.class);
		if (listFile.size() > 0) {
			for (int i = 0; i < listFile.size(); i++) {
				@SuppressWarnings("unchecked")
				Map<String, Object> map = listFile.get(i);

				String module = (String) map.get("module");
				stringLengthAndEmpty(module, "module", 64);
				String file_path = (String) map.get("file_path");
				stringLengthAndEmpty(file_path, "file_path", 1024);
				String file_ext = (String) map.get("file_ext");
				stringLengthAndEmpty(file_ext, "file_ext", 64);
				String remark1 = (String) map.get("remark");
				stringLengthAndEmpty(remark1, "remark", 64);
				String upload_user_name = (String) map.get("upload_user_name");
				stringLengthAndEmpty(upload_user_name, "upload_user_name", 32);
			}
		}

	}

    /**
     * 校验Decimal类型
     *
     * @param paraValue
     * @param paraName
     * @param intLength
     * @param smallLength
     */
    public static void checkDecimal(String paraValue, String paraName, Integer intLength, Integer smallLength) {
        if (StringUtils.isEmpty(paraValue) || "null".equals(paraValue) || "undefined".equals(paraValue)) {
            throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_NO_EMPTY.desc(), paraName));
        }

        String[] paraValueArr = paraValue.split("\\.");
        int int_length = paraValueArr[0].length();
        int small_length = 0;
        if (paraValueArr.length > 1) {
            small_length = paraValueArr[1].length();
        }

        String intName = paraName + ";" + intLength;
        if (!StringUtils.isEmpty(paraValue) && int_length > intLength) {
            throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_INT_LONG.desc(), intName));
        }

        String decimalName = paraName + ";" + small_length;
        if (!StringUtils.isEmpty(paraValue) && small_length > smallLength) {
            throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_DECIMAL_POINT.desc(), decimalName));
        }

    }


    /**
     * 校验Decimal类型
     *
     * @param paraValue
     * * @param paraValue
     * @param paraName
     * @param intLength
     * @param smallLength
     */
    public static void checkDecimal(String paraValue,Integer roeNumber, String paraName, Integer intLength, Integer smallLength) {
        String[] paraValueArr = paraValue.split("\\.");
        int int_length = paraValueArr[0].length();
        int small_length = 0;
        if (paraValueArr.length > 1) {
            small_length = paraValueArr[1].length();
        }

        String intName = roeNumber + ";" +paraName + ";" + intLength;
        if (!StringUtils.isEmpty(paraValue) && int_length > intLength) {
            String error = ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_CELL_DECIMAL.desc(), intName);
            throw new MyException(error);
        }

        String decimalName = roeNumber + ";" +paraName + ";" + smallLength;
        if (!StringUtils.isEmpty(paraValue) && small_length > smallLength) {
            String error = ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_CELL_DECIMAL.desc(), decimalName);
            throw new MyException(error);
        }

    }

    /**
     * 校验字符串是否为空
     *
     * @param paraValue
     * @param paraName
     * @param
     */
    public static void stringIsNull(String paraValue, String paraName) {
        if (StringUtils.isEmpty(paraValue) || "null".equals(paraValue) || "undefined".equals(paraValue)) {
            throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_NO_EMPTY.desc(), paraName));
        }
    }


    /**
     * 校验字符串类型以及长度
     *
     * @param paraValue  参数值
     * @param paraName 参数名
     * @param paraLength 参数长度
     */
    public static void stringLengthAndEmpty(String paraValue, String paraName, Integer paraLength) {
        if (StringUtils.isEmpty(paraValue) || "null".equals(paraValue) || "undefined".equals(paraValue)) {
            throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_NO_EMPTY.desc(), paraName));
        }

        String name = paraName + ";" + paraLength;
        boolean isExist = checkStr(paraValue);
        if(isExist && paraValue.length() > 2*paraLength){ //如果是汉字,那么汉字长度只能小于数据库存储长度的一半
            throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_SUPER_LONG.desc(), name));
        }
        if (!StringUtils.isEmpty(paraValue) && paraValue.length() > paraLength) {
            throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_SUPER_LONG.desc(), name));
        }
    }



    public static void stringLengthAndEmpty1(String paraValue, String paraName, Integer paraLength) {
        if (StringUtils.isEmpty(paraValue) || "null".equals(paraValue) || "undefined".equals(paraValue)) {
            throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_NO_EMPTY.desc(), paraName));
        }

        String name = paraName + ";" + paraLength;
        boolean isExist = checkStr(paraValue);
        if(isExist && 2*(paraValue.length()) < paraLength){ //如果是汉字,那么汉字长度只能小于数据库存储长度的一半
            throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_MIN_LONG.desc(), name));
        }
        if (!StringUtils.isEmpty(paraValue) && paraValue.length() < paraLength) {
            throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_MIN_LONG.desc(), name));
        }
    }



    public static void checkDefaultParams(PageData pd){
        checkPositiveInt(pd.getString("menuCode"), "功能菜单编码");
        CheckParameter.stringLengthAndEmpty(pd.getString("creatorOrgId"), "右上角项目ID",128);
//        CheckParameter.stringLengthAndEmpty(pd.getString("creatorOrgName"), "右上角项目名称",256);
    }


    public static void checkBusinessIdList(PageData pd){
        checkDefaultParams(pd);
        //校验取出参数
        String idList = pd.getString("businessIdList");
        if (StringUtils.isBlank(idList)) {
            throw new MyException("业务主键ID不能为空");
        }

    }



    /**
     * 校验是否为null 或者为空
     *
     * @param paraValue  参数值
     * @param paraName 参数名
     */
    public static void isNull(Object paraValue, String paraName) {
        if ("null".equals(paraValue) || "undefined".equals(paraValue) || paraValue.equals("")) {
            throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_NO_EMPTY.desc(), paraName));
        }
    }

    /**
     * 校验电话号码
     *
     * @param paraValue  参数值
     * @param paraName 参数名
     */
    public static void checkPhone(String paraValue, String paraName) {
        if (StringUtils.isEmpty(paraValue) || "null".equals(paraValue) || "undefined".equals(paraValue)) {
            throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_NO_EMPTY.desc(), paraName));
        }
        //手机号码检验
        String regExp = "^((13[0-9])|(14[5,7,9])|(15[0-3,5-9])|(166)|(17[3,5,6,7,8])|(18[0-9])|(19[8,9]))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        if (!p.matcher(paraValue).matches()) {
            throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.ERR_PHONE.desc(), paraName));
        }

    }


    /**
     * 校验单元格字符串类型以及长度
     * @param paraValue  参数值
     * @param rowNumber  表格行数
     * @param paraName 参数名
     * @param paraLength 参数长度
     */
    public static void stringLength(String paraValue, Integer rowNumber,String paraName, Integer paraLength) {
        String name = rowNumber +";" + paraName + ";" + paraLength;
        boolean isExist = checkStr(paraValue);
        if(isExist && 2*(paraValue.length()) > paraLength){ //如果是汉字,那么汉字长度只能小于数据库存储长度的一半
            throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_CELL_LONG.desc(), name));
        }
        if (!StringUtils.isEmpty(paraValue) && paraValue.length() > paraLength) {
            throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_CELL_LONG.desc(), name));
        }
    }





    /**
     * 校验单元格字符串类型以及长度
     * @param paraValue  参数值
     * @param rowNumber  表格行数
     * @param paraName 参数名
     * @param paraLength 参数长度
     */
    public static void stringMinLength(String paraValue, Integer rowNumber,String paraName, Integer paraLength) {
        String name = rowNumber +";" + paraName + ";" + paraLength;
        boolean isExist = checkStr(paraValue);
        if(isExist && 2*(paraValue.length()) < paraLength){ //如果是汉字,那么汉字长度只能小于数据库存储长度的一半
            throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_CELL_MIN_LONG.desc(), name));
        }
        if (!StringUtils.isEmpty(paraValue) && paraValue.length() < paraLength) {
            throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_CELL_MIN_LONG.desc(), name));
        }
    }



    /**
     * 判断整个字符串都由汉字组成
     * @param str 字符串
     * @return
     * 汉字基本集中在[19968,40869]之间,共有20901个汉字
     * unicode编码范围：
     * 汉字：[0x4e00,0x9fa5]（或十进制[19968,40869]）
     */
    private static boolean checkStr(String str)
    {
        int n = 0;
        for(int i = 0; i < str.length(); i++) {
            n = (int)str.charAt(i);
            if(!(19968 <= n && n <40869)) {
                return false;
            }
        }
        return true;
    }


    /**
     * 校验字符串长度
     *
     * @param paraValue
     * @param paraName
     * @param paraLength
     */
    public static void stringLength(String paraValue, String paraName, Integer paraLength) {
        String name = paraName + ";" + paraLength;
        if (!StringUtils.isEmpty(paraValue) && paraValue.length() > paraLength) {
            throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_SUPER_LONG.desc(), name));
        }
    }


    /**
     * 适用于controller 返回值不为ResponseEntity的参数校验
     *
     * @param paraValue
     * @param paraName
     */
    public static void stringEmpty(String paraValue, String paraName) {
        if (StringUtils.isEmpty(paraValue) || "null".equals(paraValue) || "undefined".equals(paraValue)) {
            throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_NO_EMPTY.desc(), paraName));
        }
    }



    /**
     * 判断日期格式是否正确
     * @param
     */
    public static void checkDate(Map pd, String paraValue, String paraName, String para) {
        if(!isLegalDate(pd, paraValue, para)) {
            throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_DATE_FORMAT.desc(), paraName));
        }
    }

    private static boolean isLegalDate(Map pd,String sDate, String para) {
        int[] legalLen = {10,26,24};//三种合法的日期格式
        if ((sDate == null) || (sDate.length() != legalLen[0]) && (sDate.length() != legalLen[1])&& (sDate.length() != legalLen[2])) {
            return false;
        }
        if(sDate.length()==26) {//当日期为26位时，去除两端双引号
            sDate = sDate.substring(1, sDate.length()-1);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date date = sdf.parse(sDate);
            pd.put(para, sDate);
            return sDate.equals(sdf.format(date));
        } catch (Exception e) {
            try {
                Date date2 = sdf2.parse(sDate);
                return sDate.equals(sdf2.format(date2));
            } catch (Exception e2) {
                return false;
            }
        }
    }

    public static void checkPositiveInt(String paraValue, String paraName) {
        if (StringUtils.isEmpty(paraValue) || "null".equals(paraValue) || "undefined".equals(paraValue)) {
            throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_NO_EMPTY.desc(), paraName));
        }
        if(!(paraValue.matches("[0-9]+")) || (Integer.parseInt(paraValue)<0)) {
            throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_INT_FORMAT.desc(), paraName));
        }
    }

    /**
     * 检验税率
     * @param paraValue
     * @param paraName
     */
    public static void checkTaxRate(String paraValue, String paraName) {
        if (StringUtils.isEmpty(paraValue) || "null".equals(paraValue) || "undefined".equals(paraValue)) {
            throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_NO_EMPTY.desc(), paraName));
        }
        if(!(paraValue.matches("[0-9]+")) || (Integer.parseInt(paraValue)<0)) {
            throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_INT_FORMAT.desc(), paraName));
        }
        Integer tax = Integer.valueOf(paraValue);
        if (tax < 0 || tax > 99) {
            throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.ERR_TAX_RATE.desc(), paraName));
        }
    }

    /**
     * 校验小数必须为位数
     *
     * @param paraValue
     * @param paraName
     * @param intLength
     * @param smallLength
     */
    public static void checkDecimalLength(String paraValue, String paraName, Integer intLength, Integer smallLength) {
        if (StringUtils.isEmpty(paraValue) || "null".equals(paraValue) || "undefined".equals(paraValue)) {
            throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_NO_EMPTY.desc(), paraName));
        }

        String[] paraValueArr = paraValue.split("\\.");
        int int_length = paraValueArr[0].length();
        if (paraValueArr.length == 1) {
            String intName = paraName + ";" + smallLength;
            throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_WRONGFUL.desc(), intName));
        }

        String intName = paraName + ";" + intLength;
        if (!StringUtils.isEmpty(paraValue) && int_length > intLength) {
            throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_INT_LONG.desc(), intName));
        }
        int small_length = paraValueArr[1].length();
        String decimalName = paraName + ";" + small_length;
        if (!StringUtils.isEmpty(paraValue) && small_length != smallLength) {
            throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_FLOAT_LONG2.desc(), decimalName));
        }

    }
    /**
     * 校验金额  14位，2位整数
     * @param paraValue
     * @return
     */
    public static boolean amountRegex14(String paraValue){
        if (!StringUtils.isEmpty(paraValue)){
            Matcher matcher = AMOUNT_REGEX14.matcher(paraValue);
            boolean flag = matcher.matches();
            if (!flag) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 校验金额  14位，2位整数
     * @param paraValue
     * @return
     */
    public static boolean amountRegex4(String paraValue){
        if (!StringUtils.isEmpty(paraValue)){
            Matcher matcher = AMOUNT_REGEX4.matcher(paraValue);
            boolean flag = matcher.matches();
            if (!flag) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     *   4位
     * @param paraValue
     * @return
     */
    public static boolean Integer4(String paraValue){
        if (!StringUtils.isEmpty(paraValue)){
            Matcher matcher = INTEGET4.matcher(paraValue);
            boolean flag = matcher.matches();
            if (!flag) {
                return false;
            }
            return true;
        }
        return false;
    }


    /**
     *   10位
     * @param paraValue
     * @return
     */
    public static boolean Integer10(String paraValue){
        if (!StringUtils.isEmpty(paraValue)){
            Matcher matcher = INTEGET10.matcher(paraValue);
            boolean flag = matcher.matches();
            if (!flag) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     *   10位
     * @param paraValue
     * @return
     */
    public static boolean Integer6(String paraValue){
        if (!StringUtils.isEmpty(paraValue)){
            Matcher matcher = INTEGET6.matcher(paraValue);
            boolean flag = matcher.matches();
            if (!flag) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 身份证号
     * @param idNumber
     * @return
     */
    public static boolean idNumber(String idNumber,String paraName){
        if(!StringUtils.isEmpty(idNumber)){
            Matcher matcher = ID_NUMBER.matcher(idNumber);
            boolean flag = matcher.matches();
            if (!flag) {
                throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_WRONGFUL.desc(), paraName));
            }
            return true;
        }
        return false;
    }

    /**
     * 校验字符串长度(不允许为空)
     * @param paraValue
     * @param paraLength
     * @return boolean true：未超过 false:超过
     */
    public static boolean stringLengthIsTrue(String paraValue, Integer paraLength) {
        if (StringUtils.isEmpty(paraValue) || "null".equals(paraValue) || "undefined".equals(paraValue)){
            return false;
        }
        if (paraValue.length() > paraLength) {
            return false;
        }
        return true;
    }

    /**
     * 检查手机号码的合法性
     * @param paraValue 参数值
     * @return boolean
     */
    public static boolean checkMobileNumberLength(String paraValue) {
        if (!StringUtils.isEmpty(paraValue)) {
            Matcher matcher = mobileRegex.matcher(paraValue);
            boolean flag = matcher.matches();
            if (!flag) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 校验字符串长度(允许为空)
     * @param paraValue
     * @param paraLength
     * @return boolean true：未超过 false:超过
     */
    public static boolean stringLength(String paraValue, Integer paraLength) {
        if (!StringUtils.isEmpty(paraValue) && paraValue.length() < paraLength) {
            return true;
        }else if(StringUtils.isEmpty(paraValue)){
            return true;
        }
        return false;
    }

    /**
     * 检查整数类型参数长度及是否为纯数字类型
     *
     * @param paraValue 参数值
     * @param maxInt    最大整数位
     * @return
     */
    public static Boolean intNumberLengthIs(String paraValue, Integer maxInt) {
        if (!StringUtils.isEmpty(paraValue)) {
            // 判断是否包含负号
            if (paraValue.contains("-")) {
                paraValue = paraValue.substring(1, paraValue.length());
                maxInt = maxInt - 1;
            }
            // 判断是否都为数字
            if (isNumeric(paraValue)) {
                if (paraValue.length() > maxInt) {
                   return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * 校验小数必须为位数
     *
     * @param paraValue
     * @param intLength
     * @param smallLength
     */
    public static boolean checkDecimalLengthIs(String paraValue, Integer intLength, Integer smallLength) {
        if (StringUtils.isEmpty(paraValue) || "null".equals(paraValue) || "undefined".equals(paraValue)) {
            return false;
        }

        String[] paraValueArr = paraValue.split("\\.");
        int int_length = paraValueArr[0].length();
        if (paraValueArr.length == 1) {
            return false;
        }

        if (!StringUtils.isEmpty(paraValue) && int_length > intLength) {
            return false;
        }
        int small_length = paraValueArr[1].length();
        if (!StringUtils.isEmpty(paraValue) && small_length != smallLength) {
            return false;
        }
        return true;

    }

    public static void checkDecimalLength1(String paraValue, String paraName, Integer intLength, Integer smallLength) {
        if (StringUtils.isEmpty(paraValue) || "null".equals(paraValue) || "undefined".equals(paraValue)) {
            throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_NO_EMPTY.desc(), paraName));
        }

        String[] paraValueArr = paraValue.split("\\.");
        int int_length = paraValueArr[0].length();
        if (paraValueArr.length == 1) {
            String intName = paraName + ";" + smallLength;
            throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_FLOAT_LONG1.desc(), intName));
        }

        String intName = paraName + ";" + intLength;
        if (!StringUtils.isEmpty(paraValue) && int_length > intLength) {
            throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_INT_LONG.desc(), intName));
        }
        int small_length = paraValueArr[1].length();
        String decimalName = paraName + ";" + small_length;
        if (!StringUtils.isEmpty(paraValue) && small_length != smallLength) {
            throw new MyException(ConstantMsgUtil.getProperty(ConstantMsgUtil.WAN_FLOAT_LONG2.desc(), decimalName));
        }

    }

    public static void checkSearchParams(PageData pd){
        CheckParameter.stringLengthAndEmpty(pd.getString("isProject"), "项目标识",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("creatorOrgId"), "右上角项目ID",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("pageNum"), "当前页码",128);
        CheckParameter.stringLengthAndEmpty(pd.getString("pageSize"), "每页数量",128);

    }
}
