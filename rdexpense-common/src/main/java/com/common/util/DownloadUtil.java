package com.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 功能描述: 文件下载的工具类
 * @Author: rdexpense
 * @Date: 2020/4/24 11:25
 */
public class DownloadUtil {

	public static void download(HttpServletResponse resp, String name, String downloadPath, String viewFileName) throws IOException {
		try {
			URLDecoder.decode(downloadPath, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String path = downloadPath + name;

		File file = new File(path);
		if (!file.exists()) {
			throw new IOException("文件不存在");
		}
		resp.reset();
		resp.addHeader("Content-Disposition", "attachment; filename="+URLEncoder.encode(viewFileName, "utf-8"));
		resp.setContentType("application/octet-stream; charset=utf-8");
		byte[] buff = new byte[1024];
		BufferedInputStream bis = null;
		OutputStream os = null;
		try {
			os = resp.getOutputStream();
			bis = new BufferedInputStream(new FileInputStream(file));
			int i = 0;
			while ((i = bis.read(buff)) != -1) {
				os.write(buff, 0, i);
				os.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * 判断文件是否存在
	 *
	 * @param fileName
	 */
	public static boolean fileIsExit(String fileName, String path) {
		String filePath = path + fileName;
		File file = new File(filePath);
		if (!file.exists()) {
			return false;
		}
		return true;
	}


	/**
	 * 压缩文件列表中的文件
	 * @param files
	 * @param outputStream
	 * @throws IOException
	 */
	public static void zipFile(List<Map<String,Object>> list, ZipOutputStream outputStream)
			throws IOException, ServletException {
		try {
			// 压缩列表中的文件
			for (int i = 0; i < list.size(); i++) {
				File file = (File) list.get(i).get("file");
				String fileName = (String) list.get(i).get("fileName");
				try {
					zipFile(fileName, file, outputStream);
				} catch (Exception e) {
					continue;
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}


	/**
	 * 压缩文件列表中的文件
	 * @param files
	 * @param outputStream
	 * @throws IOException
	 */
	/*public static void zipFile2(List<String> fileNameList, List<File> files, ZipOutputStream outputStream)
			throws IOException, ServletException {
		try {
			int size = files.size();
			// 压缩列表中的文件
			for (int i = 0; i < size; i++) {
				File file = (File) files.get(i);
				String fileName = fileNameList.get(i);
				try {
					zipFile(fileName, file, outputStream);
				} catch (Exception e) {
					continue;
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}*/


	/**
	 * 将文件写入到zip文件中
	 * @param inputFile
	 * @param outputstream
	 * @throws Exception
	 */
	public static void zipFile(String fileName, File inputFile, ZipOutputStream outputstream)
			throws IOException, ServletException {
		try {
			if (inputFile.exists()) {
				if (inputFile.isFile()) {
					FileInputStream inStream = new FileInputStream(inputFile);
					BufferedInputStream bInStream = new BufferedInputStream(inStream);
					ZipEntry entry = new ZipEntry(fileName);
					outputstream.putNextEntry(entry);

					final int MAX_BYTE = 10 * 1024 * 1024; // 最大的流为10M
					long streamTotal = 0; // 接受流的容量
					int streamNum = 0; // 流需要分开的数量
					int leaveByte = 0; // 文件剩下的字符数
					byte[] inOutbyte; // byte数组接受文件的数据

					streamTotal = bInStream.available(); // 通过available方法取得流的最大字符数
					streamNum = (int) Math.floor(streamTotal / MAX_BYTE); // 取得流文件需要分开的数量
					leaveByte = (int) streamTotal % MAX_BYTE; // 分开文件之后,剩余的数量

					if (streamNum > 0) {
						for (int j = 0; j < streamNum; ++j) {
							inOutbyte = new byte[MAX_BYTE];
							// 读入流,保存在byte数组
							bInStream.read(inOutbyte, 0, MAX_BYTE);
							outputstream.write(inOutbyte, 0, MAX_BYTE); // 写出流
						}
					}
					// 写出剩下的流数据
					inOutbyte = new byte[leaveByte];
					bInStream.read(inOutbyte, 0, leaveByte);
					outputstream.write(inOutbyte);
					outputstream.closeEntry(); // Closes the current ZIP entry and positions the stream for writing the
					// next entry
					bInStream.close(); // 关闭
					inStream.close();
				}
			} else {
				throw new ServletException("文件不存在！");
			}
		} catch (IOException e) {
			throw e;
		}
	}

	/**
	 * 下载打包的文件
	 * @param file
	 * @param response
	 */
	public static void downloadZip(File file, HttpServletResponse response, HttpServletRequest request) {
		try {
			// 以流的形式下载文件。
			BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file.getPath()));
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);
			fis.close();

			String fileName = file.getName().replace(" ", "");
			// 清空response
			response.reset();
			response.addHeader("Content-Disposition", "attachment; filename="+URLEncoder.encode(fileName, "utf-8"));
			response.setContentType("application/octet-stream; charset=utf-8");
			OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
			toClient.write(buffer);
			toClient.flush();
			toClient.close();
			file.delete(); // 将生成的服务器端文件删除
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 *
	 * @Title proccessFileName下载文件的乱码问题
	 * @author rdexpense
	 * @param @param request
	 * @param @param fileName 要下载文件名
	 * @return String 处理好的文件名
	 */
	/*public static String proccessFileName(HttpServletRequest request, String fileName) {
		String codedFileName = null;
		try {
			String userAgent = request.getHeader("User-Agent");
			if (StringUtils.isNotEmpty(userAgent) && (-1 != userAgent.indexOf("Edge") || -1 != userAgent.indexOf("MSIE")
					|| -1 != userAgent.indexOf("Trident"))) {// IE
				codedFileName = java.net.URLEncoder.encode(fileName, "ISO-8859-1");
			} else if (StringUtils.isNotEmpty(userAgent) && -1 != userAgent.indexOf("Mozilla")) {
				codedFileName = fileName;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return codedFileName;
	}*/

	/**
	 * 将流写入到zip文件中
	 */
	public static void zipFile(String fileName, ByteArrayInputStream input, ZipOutputStream outputstream)
			throws IOException, ServletException {
		try {
			BufferedInputStream bInStream = new BufferedInputStream(input);
			ZipEntry entry = new ZipEntry(fileName);
			outputstream.putNextEntry(entry);

			final int MAX_BYTE = 10 * 1024 * 1024; // 最大的流为10M
			long streamTotal = 0; // 接受流的容量
			int streamNum = 0; // 流需要分开的数量
			int leaveByte = 0; // 文件剩下的字符数
			byte[] inOutbyte; // byte数组接受文件的数据

			streamTotal = bInStream.available(); // 通过available方法取得流的最大字符数
			streamNum = (int) Math.floor(streamTotal / MAX_BYTE); // 取得流文件需要分开的数量
			leaveByte = (int) streamTotal % MAX_BYTE; // 分开文件之后,剩余的数量

			if (streamNum > 0) {
				for (int j = 0; j < streamNum; ++j) {
					inOutbyte = new byte[MAX_BYTE];
					// 读入流,保存在byte数组
					bInStream.read(inOutbyte, 0, MAX_BYTE);
					outputstream.write(inOutbyte, 0, MAX_BYTE); // 写出流
				}
			}
			// 写出剩下的流数据
			inOutbyte = new byte[leaveByte];
			bInStream.read(inOutbyte, 0, leaveByte);
			outputstream.write(inOutbyte);
			outputstream.closeEntry(); // Closes the current ZIP entry and positions the stream for writing the
			// next entry
			bInStream.close(); // 关闭
		} catch (IOException e) {
			throw e;
		}
	}

}
