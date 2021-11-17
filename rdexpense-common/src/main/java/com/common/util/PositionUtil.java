package com.common.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.common.entity.PageData;
import com.common.util.JsonUtil;

/**
 * 获取人员的基本信息、岗位、部门、单位集合
 * @author rdexpense
 *
 */
public class PositionUtil {

	/**
	 * 根据岗位查询部门和单位
	 * @param positionMap 筛选出的部门和单位信息
	 * @param orgDataStr 从HR获取的岗位完整路径（岗位、部门、单位信息）
	 */
	public static void getDepartOrgInfo(Map<String, Object> positionMap, String orgDataStr) {
		//遍历岗位的完整路径，找出部门和单位
		List<PageData> orgList = JsonUtil.jsonToPageDataList(orgDataStr);
		Collections.sort(orgList, new Comparator<PageData>() {
			public int compare(PageData arg0, PageData arg1) {
				return arg0.getString("code").compareTo(arg1.getString("code"));
			}
		});
		String orgTemp="";
		String departTemp="";
		String orgIdTemp="";
		String departIdTemp="";
		for(PageData data:orgList) {
			int type = data.getInt("type");
			if(type==1) {
				orgIdTemp += data.getString("id")+"/";
				orgTemp += data.getString("name")+"/";
			}else if(type==2) {
				departIdTemp += data.getString("id")+"/";
				departTemp += data.getString("name")+"/";
			}
		}
		String orgId = orgIdTemp.substring(0, orgIdTemp.length()-1);
		String departId = departIdTemp.substring(0, departIdTemp.length()-1);
		String org = orgTemp.substring(0, orgTemp.length()-1);
		String depart = departTemp.substring(0, departTemp.length()-1);
		//将部门信息放到岗位中去
		positionMap.put("orgId", orgId);
		positionMap.put("departId", departId);
		positionMap.put("org", org);
		positionMap.put("depart", depart);
	}


	/**
	 * 获取人员的岗位和部门集合
	 * @param positionStr
	 * @return
	 */
	public static String[] getPosition(String positionStr) {
		String[] ret= {"","",""};
		String positionIds="";
		String positionNames="";
		String positionName="";
		if(StringUtils.isNotBlank(positionStr)) {
			List<PageData> positionList = JSONObject.parseArray(positionStr, PageData.class);
			for(PageData data: positionList) {
				positionIds+=data.getString("orgId")+"/"+data.getString("departId")+"/"+data.getString("id")+";";
				positionNames+=data.getString("org")+"/"+data.getString("depart")+"/"+data.getString("name")+";";
				positionName+=data.getString("org")+"/"+data.getString("depart")+";";
			}
			ret[0] = positionIds.substring(0, positionIds.length()-1);
			ret[1] = positionNames.substring(0, positionNames.length()-1);
			ret[2] = positionNames.substring(0, positionName.length()-1);
		}
		return ret;
	}

	/**
	 * 单独获取岗位的信息
	 * @param positionStr
	 * @return
	 */
	public static String[] getSinglePosition(String positionStr) {
		String[] ret= {"",""};
		String positionIds="";
		String positionNames="";
		if(StringUtils.isNotBlank(positionStr)) {
			List<PageData> positionList = JSONObject.parseArray(positionStr, PageData.class);
			for(PageData data: positionList) {
				positionIds+=data.getString("id")+";";
				positionNames+=data.getString("name")+";";
			}
			ret[0] = positionIds.substring(0, positionIds.length()-1);
			ret[1] = positionNames.substring(0, positionNames.length()-1);
		}
		return ret;
	}

	/**
	 * 单独获取部门的信息
	 * @param positionStr
	 * @return
	 */
	public static String[] getSingleDepart(String positionStr) {
		String[] ret= {"",""};
		String positionIds="";
		String positionNames="";
		if(StringUtils.isNotBlank(positionStr)) {
			List<PageData> positionList = JSONObject.parseArray(positionStr, PageData.class);
			for(PageData data: positionList) {
				positionIds+=data.getString("departId")+";";
				positionNames+=data.getString("depart")+";";
			}
			ret[0] = positionIds.substring(0, positionIds.length()-1);
			ret[1] = positionNames.substring(0, positionNames.length()-1);
		}
		return ret;
	}

	/**
	 * 单独获取单位的信息
	 * @param positionStr
	 * @return
	 */
	public static String[] getSingleOrg(String positionStr) {
		String[] ret= {"",""};
		String positionIds="";
		String positionNames="";
		if(StringUtils.isNotBlank(positionStr)) {
			List<PageData> positionList = JSONObject.parseArray(positionStr, PageData.class);
			for(PageData data: positionList) {
				positionIds+=data.getString("orgId")+";";
				positionNames+=data.getString("org")+";";
			}
			ret[0] = positionIds.substring(0, positionIds.length()-1);
			ret[1] = positionNames.substring(0, positionNames.length()-1);
		}
		return ret;
	}

	/**
	 * 从HR系统拿到的岗位全路径，拼接
	 * @param positionMap
	 * @param orgDataStr
	 */
	public static String[] getDepartOrgInfo(String orgDataStr) {
		//遍历岗位的完整路径，找出部门和单位
		List<PageData> orgList = JsonUtil.jsonToPageDataList(orgDataStr);
		Collections.sort(orgList, new Comparator<PageData>() {
			public int compare(PageData arg0, PageData arg1) {
				return arg0.getString("code").compareTo(arg1.getString("code"));
			}
		});
		String[] ret= {"",""};
		String orgTemp="";
		String departTemp="";
		String orgIdTemp="";
		String departIdTemp="";
		String id="";
		String name="";
		for(PageData data:orgList) {
			int type = data.getInt("type");
			if(type==1) {//单位
				orgIdTemp += data.getString("id")+"/";
				orgTemp += data.getString("name")+"/";
			}else if(type==2) {//部门
				departIdTemp += data.getString("id")+"/";
				departTemp += data.getString("name")+"/";
			}else if(type==3) {//岗位
				id = data.getString("id");
				name = data.getString("name");
			}
		}
		String orgId = orgIdTemp.substring(0, orgIdTemp.length()-1);
		String departId = departIdTemp.substring(0, departIdTemp.length()-1);
		String org = orgTemp.substring(0, orgTemp.length()-1);
		String depart = departTemp.substring(0, departTemp.length()-1);

		String positionId = orgId+"/"+departId+"/"+id+";";
		String positionName = org+"/"+depart+"/"+name+";";
		ret[0] = positionId;
		ret[1] = positionName;
		return ret;
	}

	/**
	 * 判断岗位是否在范围之内
	 */
	public static boolean isInPosition(String positionIds, String inPositon) {
		String[] positionArr = null;
		String[] inPositonArr = null;
		if(StringUtils.isNotBlank(positionIds) && StringUtils.isNotBlank(inPositon)) {
			positionArr = positionIds.split(";");
			inPositonArr = inPositon.split(",");
			for(String position:positionArr) {
				for(String iPositon:inPositonArr) {
					if(position.contains(iPositon)) {
						return true;
					}
				}
			}
		}
		return false;
	}

}
