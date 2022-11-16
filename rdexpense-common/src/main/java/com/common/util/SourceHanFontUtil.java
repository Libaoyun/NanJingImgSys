package com.common.util;

import org.apache.log4j.Logger;

import com.common.base.exception.MyException;
import com.itextpdf.text.pdf.BaseFont;

/**
 * 思源字体工具类
 * @author rdexpense
 *
 */
public class SourceHanFontUtil {

	private static Logger log = Logger.getLogger(SourceHanFontUtil.class);

	/**
	 * 获取思源黑体字体
	 * @return
	 */
	public static BaseFont getSourceHanSansFont() {

		//思源黑体
		String fontSans = System.getProperty("user.dir")+"/config/SourceHanSansSC-Regular.otf";
		log.info("==============黑体路径========="+fontSans);
		BaseFont bfSansChinese = null;

		try {
			bfSansChinese = BaseFont.createFont(fontSans, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
		} catch (Exception e) {
			throw new MyException(ConstantMsgUtil.getProperty("思源黑体字体文件！", ConstantMsgUtil.WAN_NO_EXISTING.desc()));
		}

		return bfSansChinese;
	}

	/**
	 * 获取思源宋体字体
	 * @return
	 */
	public static BaseFont getSourceHanSerifFont() {

		//思源黑体
		String fontSerif = System.getProperty("user.dir")+"/config/SourceHanSerifSC-Regular.otf";
		log.info("==============宋体路径========="+fontSerif);
		BaseFont bfSerifChinese = null;

		try {
			bfSerifChinese = BaseFont.createFont(fontSerif, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
		} catch (Exception e) {
			throw new MyException(ConstantMsgUtil.getProperty("思源宋体字体文件！", ConstantMsgUtil.WAN_NO_EXISTING.desc()));
		}

		return bfSerifChinese;
	}

	/**
	 * 获取思源宋体字体文件路径
	 * @return
	 */
	public static String getSourceHanSerifFontPath() {
		//思源宋体
		String fontSerif = System.getProperty("user.dir")+"/config/SourceHanSerifSC-Regular.otf";
		log.info("==============宋体路径========="+fontSerif);
		return fontSerif;
	}

	/**
	 * 获取思源黑体字体文件路径
	 * @return
	 */
	public static String getSourceHanSansFontPath() {

		//思源黑体
		String fontSans = System.getProperty("user.dir")+"/config/SourceHanSansSC-Regular.otf";
		log.info("==============黑体路径========="+fontSans);
		return fontSans;
	}
}
