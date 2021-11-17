package com.common.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * <pre>
 * 对象功能:文件处理类
 * 开发人员:lixin
 * 创建时间:2018-01-15
 * </pre>
 */
public class FileUtil {

	/**
	 * 根据文件夹删除文件夹及下面的所有文件
	 * 
	 * @param file:文件夹
	 */
	public static void deleteFile(File file) {
		if (file.exists()) {// 判断文件是否存在
			if (file.isFile()) {// 判断是否是文件
				file.delete();// 删除文件
			} else if (file.isDirectory()) {// 否则如果它是一个目录
				File[] files = file.listFiles();// 声明目录下所有的文件 files[];
				for (int i = 0; i < files.length; i++) {// 遍历目录下所有的文件
					deleteFile(files[i]);// 把每个文件用这个方法进行迭代
				}
				file.delete();// 删除文件夹
			}
		}
	}

	/**
	 * 获取文件大小 返回 KB 保留3位小数 没有文件时返回0
	 * 
	 * @param filepath
	 *            文件完整路径，包括文件名
	 * @return
	 */
	public static Double getFilesize(String filepath) {
		File backupath = new File(filepath);
		return Double.valueOf(backupath.length()) / 1000.000;
	}

	/**
	 * 创建目录
	 * 
	 * @param destDirName目标目录名
	 * @return
	 */
	public static Boolean createDir(String destDirName) {
		File dir = new File(destDirName);
		if (!dir.getParentFile().exists()) { // 判断有没有父路径，就是判断文件整个路径是否存在
			return dir.getParentFile().mkdirs(); // 不存在就全部创建
		}
		return false;
	}

	/**
	 * 删除文件
	 * 
	 * @param filePathAndName
	 *            String 文件路径及名称 如c:/fqf.txt
	 *            String
	 * @return boolean
	 */
	public static void delFile(String filePathAndName) {
		try {
			String filePath = filePathAndName;
			filePath = filePath.toString();
			File myDelFile = new File(filePath);
			myDelFile.delete();
		} catch (Exception e) {
			System.out.println("删除文件操作出错");
			e.printStackTrace();
		}
	}

	/**
	 * 读取到字节数组0
	 * 
	 * @param filePath
	 *            //路径
	 * @throws IOException
	 */
	public static byte[] getContent(String filePath) throws IOException {
		File file = new File(filePath);
		long fileSize = file.length();
		if (fileSize > Integer.MAX_VALUE) {
			System.out.println("file too big...");
			return null;
		}
		FileInputStream fi = new FileInputStream(file);
		byte[] buffer = new byte[(int) fileSize];
		int offset = 0;
		int numRead = 0;
		while (offset < buffer.length && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
			offset += numRead;
		}
		fi.close();
		// 确保所有数据均被读取
		if (offset != buffer.length) {
			throw new IOException("Could not completely read file " + file.getName());
		}
		return buffer;
	}

	/**
	 * 读取到字节数组1
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static byte[] toByteArray(String filePath) throws IOException {

		File f = new File(filePath);
		if (!f.exists()) {
			throw new FileNotFoundException(filePath);
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(f));
			int buf_size = 1024;
			byte[] buffer = new byte[buf_size];
			int len = 0;
			while (-1 != (len = in.read(buffer, 0, buf_size))) {
				bos.write(buffer, 0, len);
			}
			return bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			bos.close();
		}
	}

	/**
	 * 读取到字节数组2
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static byte[] toByteArray2(String filePath) throws IOException {
		File f = new File(filePath);
		if (!f.exists()) {
			throw new FileNotFoundException(filePath);
		}
		FileChannel channel = null;
		FileInputStream fs = null;
		try {
			fs = new FileInputStream(f);
			channel = fs.getChannel();
			ByteBuffer byteBuffer = ByteBuffer.allocate((int) channel.size());
			while ((channel.read(byteBuffer)) > 0) {
				// do nothing
				// System.out.println("reading");
			}
			return byteBuffer.array();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				channel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Mapped File way MappedByteBuffer 可以在处理大文件时，提升性能
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static byte[] toByteArray3(String filePath) throws IOException {

		FileChannel fc = null;
		RandomAccessFile rf = null;
		try {
			rf = new RandomAccessFile(filePath, "r");
			fc = rf.getChannel();
			MappedByteBuffer byteBuffer = fc.map(MapMode.READ_ONLY, 0, fc.size()).load();
			// System.out.println(byteBuffer.isLoaded());
			byte[] result = new byte[(int) fc.size()];
			if (byteBuffer.remaining() > 0) {
				// System.out.println("remain");
				byteBuffer.get(result, 0, byteBuffer.remaining());
			}
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				rf.close();
				fc.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * <p>
	 * 功能描述：查询路径下所有文件（不包括文件夹）
	 * </p>
	 * 
	 * @param args
	 *
	 */
	public static ArrayList<File> queryDirectoryFile(String path) {
		File file = new File(path);
		File[] tempList = file.listFiles();
		ArrayList<File> list = new ArrayList<File>();
		for (int i = 0; i < tempList.length; i++) {
			if (tempList[i].isFile()) {
				list.add(tempList[i]);
			}
		}
		return list;
	}

	/**
	 * 根据地址获取文本数据
	 */
	public static String getReadFileTxt(String path) {
		String laststr = "";
		File file = new File(path);// 打开文件
		BufferedReader reader = null;
		try {
			FileInputStream in = new FileInputStream(file);
			reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));// 读取文件
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				laststr = laststr + tempString;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException el) {
				}
			}
		}
		return laststr;
	}

	/**
	 * 创建存放临时文件的文件夹
	 *
	 * @return
	 */
	public static File createFile() {
		//根据文件的项目路径，获得文件的系统路径。
		String projectPath = System.getProperty("user.dir");
		//存放临时文件
		File file = new File(projectPath + "\\temporaryfiles");
		//如果文件夹不存在
		if (!file.exists()) {
			//创建文件夹
			file.mkdir();
		}
		return file;
	}



	/**
	 * 文件上传
	 *
	 * @param file
	 * @param model
	 * @return
	 */
	public static String upload(MultipartFile file, String model, String path) {
		Date now = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMM");// 设置日期格式
		String timeFormate = df.format(now);// now为获取当前系统时间

		// 判断是否有对应的上传文件夹，没有则创建文件夹：根路径/模块名称/月份
		judgeFile(path,model, timeFormate);
		//生成服务器上的文件名，使用uuid保证唯一
		String serveFileName = UUID.randomUUID().toString();
		// 上传文件
		String filePath = path + model + "/" + timeFormate+"/"+serveFileName;
		//文件相对路径
		String relativePath = model + "/" + timeFormate+"/"+serveFileName;
		File dest = new File(filePath);
		try {
			file.transferTo(dest);
		} catch (IOException e) {
		}
		return relativePath;
	}

	/**
	 * 判断路径下是否有相应的模块名文件夹
	 *
	 * @param model
	 * @param timeFormate
	 */
	private static void judgeFile(String path,String model, String timeFormate) {
		List<String> names = new ArrayList<String>();
		File file = new File(path);
		if(!file.exists()) {
			//创建根文件夹
			file.mkdirs();
		}
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			// 对比是否有相应模块名的文件夹
			for (int i = 0; i < files.length; i++) {
				String name = files[i].getName();
				names.add(name);
			}
			boolean is = names.contains(model);
			if (is == false) {
				// 创建相应模块的文件夹
				createFile(path,model);
				// 获取当前的时间，格式201907
				// 创建日期的文件夹
				String FileName = model + "/" + timeFormate;
				createFile(path,FileName);
			} else {
				List<String> nameDates = new ArrayList<String>();
				String FileName = path + model;
				File fileModel = new File(FileName);
				File[] fileChilds = fileModel.listFiles();
				for (int i = 0; i < fileChilds.length; i++) {
					String name = fileChilds[i].getName();
					nameDates.add(name);
				}
				boolean ischild = nameDates.contains(model);
				if (ischild == false) {
					// 创建相应模块的文件夹
					String FileChildName = model + "/" + timeFormate;
					createFile(path,FileChildName);
				}
			}
		}
	}



	/**
	 * 创建文件夹
	 *
	 * @param fileName
	 */
	private static void createFile(String path,String fileName) {
		String filePath = path + fileName;
		File file = new File(filePath);
		if (!file.exists()) {// 如果文件夹不存在
			file.mkdir();// 创建文件夹
		}
	}



}