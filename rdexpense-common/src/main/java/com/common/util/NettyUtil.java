package com.common.util;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Auther: rdexpense
 * @Date: 2021/2/4 14:02
 * @Description: 对16进制,字节,asc字符数据的一些处理方法
 */
public class NettyUtil {

  //crc 算法用到的表
  private static int crc16_ccitt_table[] = { 0X0000, 0X1189, 0X2312, 0X329B, 0X4624, 0X57AD, 0X6536, 0X74BF,
    0X8C48, 0X9DC1, 0XAF5A, 0XBED3, 0XCA6C, 0XDBE5, 0XE97E, 0XF8F7,
    0X1081, 0X0108, 0X3393, 0X221A, 0X56A5, 0X472C, 0X75B7, 0X643E,
    0X9CC9, 0X8D40, 0XBFDB, 0XAE52, 0XDAED, 0XCB64, 0XF9FF, 0XE876,
    0X2102, 0X308B, 0X0210, 0X1399, 0X6726, 0X76AF, 0X4434, 0X55BD,
    0XAD4A, 0XBCC3, 0X8E58, 0X9FD1, 0XEB6E, 0XFAE7, 0XC87C, 0XD9F5,
    0X3183, 0X200A, 0X1291, 0X0318, 0X77A7, 0X662E, 0X54B5, 0X453C,
    0XBDCB, 0XAC42, 0X9ED9, 0X8F50, 0XFBEF, 0XEA66, 0XD8FD, 0XC974,
    0X4204, 0X538D, 0X6116, 0X709F, 0X0420, 0X15A9, 0X2732, 0X36BB,
    0XCE4C, 0XDFC5, 0XED5E, 0XFCD7, 0X8868, 0X99E1, 0XAB7A, 0XBAF3,
    0X5285, 0X430C, 0X7197, 0X601E, 0X14A1, 0X0528, 0X37B3, 0X263A,
    0XDECD, 0XCF44, 0XFDDF, 0XEC56, 0X98E9, 0X8960, 0XBBFB, 0XAA72,
    0X6306, 0X728F, 0X4014, 0X519D, 0X2522, 0X34AB, 0X0630, 0X17B9,
    0XEF4E, 0XFEC7, 0XCC5C, 0XDDD5, 0XA96A, 0XB8E3, 0X8A78, 0X9BF1,
    0X7387, 0X620E, 0X5095, 0X411C, 0X35A3, 0X242A, 0X16B1, 0X0738,
    0XFFCF, 0XEE46, 0XDCDD, 0XCD54, 0XB9EB, 0XA862, 0X9AF9, 0X8B70,
    0X8408, 0X9581, 0XA71A, 0XB693, 0XC22C, 0XD3A5, 0XE13E, 0XF0B7,
    0X0840, 0X19C9, 0X2B52, 0X3ADB, 0X4E64, 0X5FED, 0X6D76, 0X7CFF,
    0X9489, 0X8500, 0XB79B, 0XA612, 0XD2AD, 0XC324, 0XF1BF, 0XE036,
    0X18C1, 0X0948, 0X3BD3, 0X2A5A, 0X5EE5, 0X4F6C, 0X7DF7, 0X6C7E,
    0XA50A, 0XB483, 0X8618, 0X9791, 0XE32E, 0XF2A7, 0XC03C, 0XD1B5,
    0X2942, 0X38CB, 0X0A50, 0X1BD9, 0X6F66, 0X7EEF, 0X4C74, 0X5DFD,
    0XB58B, 0XA402, 0X9699, 0X8710, 0XF3AF, 0XE226, 0XD0BD, 0XC134,
    0X39C3, 0X284A, 0X1AD1, 0X0B58, 0X7FE7, 0X6E6E, 0X5CF5, 0X4D7C,
    0XC60C, 0XD785, 0XE51E, 0XF497, 0X8028, 0X91A1, 0XA33A, 0XB2B3,
    0X4A44, 0X5BCD, 0X6956, 0X78DF, 0X0C60, 0X1DE9, 0X2F72, 0X3EFB,
    0XD68D, 0XC704, 0XF59F, 0XE416, 0X90A9, 0X8120, 0XB3BB, 0XA232,
    0X5AC5, 0X4B4C, 0X79D7, 0X685E, 0X1CE1, 0X0D68, 0X3FF3, 0X2E7A,
    0XE70E, 0XF687, 0XC41C, 0XD595, 0XA12A, 0XB0A3, 0X8238, 0X93B1,
    0X6B46, 0X7ACF, 0X4854, 0X59DD, 0X2D62, 0X3CEB, 0X0E70, 0X1FF9,
    0XF78F, 0XE606, 0XD49D, 0XC514, 0XB1AB, 0XA022, 0X92B9, 0X8330,
    0X7BC7, 0X6A4E, 0X58D5, 0X495C, 0X3DE3, 0X2C6A, 0X1EF1, 0X0F78
  };

  /**
   *  将字节数组转16进制字符
   * @param bytes
   * @return
   */
  public static String BinaryToHexString(byte[] bytes) {
    String hexStr = "0123456789ABCDEF";
    String result = "";
    String hex = "";
    for (byte b : bytes) {
      hex = String.valueOf(hexStr.charAt((b & 0xF0) >> 4));
      hex += String.valueOf(hexStr.charAt(b & 0x0F));
      result += hex + " ";
    }
    return result;
  }


  /**
   *  将数据转成时间可以格式  1变成01
   * @param number
   * @return
   */
  public static  String returnZero(int number){
    return  String.format("%02d",number);

  }


  /**
   *  16进制 高8位在前低8位在后 然后转成10进制
   * @param str
   * @return
   */
  private static String decodeHexString(String str) {

    str =HighLowHex(spaceHex(str));

    String value =new BigInteger(str, 16).toString();

    return value;

  }
  private static   String spaceHex(String str){

    char[] array= str.toCharArray();

    if(str.length()<=2) return str;

    StringBuffer buffer =new StringBuffer();

    for(int i=0;i<array.length;i++){

      int start =i+1;

      if(start%2==0){

        buffer.append(array[i]).append(" ");

      }else{

        buffer.append(array[i]);

      }

    }

    return buffer.toString();

  }

  private static  String HighLowHex(String str){

    if(str.trim().length()<=2) return str;

    List<String> list = Arrays.asList( str.split(" "));

    Collections.reverse(list);

    StringBuffer stringBuffer = new StringBuffer();

    for(String string:list){

      stringBuffer.append(string);

    }

    return stringBuffer.toString();

  }

  /**
   * 16进制转换成为string类型字符串
   * @param s
   * @return
   */
  public static String hexStringToString(String s) {
    if (s == null || s.equals("")) {
      return null;
    }
    s = s.replace(" ", "");
    byte[] baKeyword = new byte[s.length() / 2];
    for (int i = 0; i < baKeyword.length; i++) {
      try {
        baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    try {
      s = new String(baKeyword, "UTF-8");
      new String();
    } catch (Exception e1) {
      e1.printStackTrace();
    }
    return s;
  }

  /**
   *  传入s数返回具体的时间点
   * @param seconds
   * @return
   */

  private  static String getTime(String seconds){
    Date date = new Date();
    //Long time = date.getTime();
    Long time=Long.valueOf(seconds+"000");
    System.out.println(time);
    Date d = new Date(time);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return sdf.format(d);
  }

  /**
   *  10 进制转16进制
   * @param n
   * @return
   */
  private static String intToHex(int n) {
    StringBuffer s = new StringBuffer();
    String a;
    char []b = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    while(n != 0){
      s = s.append(b[n%16]);
      n = n/16;
    }
    a = s.reverse().toString();
    return a;
  }



  static String rev(String ox){
    String s = (new String(ox));
    byte b[] = s.getBytes();
    byte result[] = new byte[b.length];
    for (int i= b.length-1, j=0; i>=0;i=i-2,j=j+2){
      result[j]= b[i-1];
      result[j+1]= b[i];
    }
    return  new String(result);
  }
  /**
   * 字符串转化成为16进制字符串
   * @param s
   * @return
   */
  public static String strTo16(String s) {
    String str = "";
    for (int i = 0; i < s.length(); i++) {
      int ch = (int) s.charAt(i);
      String s4 = Integer.toHexString(ch);
      str = str + s4;
    }
    return str;
  }

  /**
   * 采用查表法，根据数据生成CRC校验码
   *
   * @param message  校验值，为需要校验的字节
   * @return
   */
  public static String CRC_16_X25(byte[] message) {
    int crc_reg = 0xffff;//CRC校验时初值
    for (int i = 0; i < message.length; i++) {
      crc_reg = (crc_reg >> 8) ^ crc16_ccitt_table[(crc_reg ^ message[i]) & 0xff];
    }
    return Integer.toHexString(~crc_reg & 0xffff).toUpperCase();
  }
  /**
   * 将16进制字符串转换为byte[]
   *
   * @param str
   * @return
   */
  public static byte[] toBytes(String str) {
    if(str == null || str.trim().equals("")) {
      return new byte[0];
    }

    byte[] bytes = new byte[str.length() / 2];
    for(int i = 0; i < str.length() / 2; i++) {
      String subStr = str.substring(i * 2, i * 2 + 2);
      bytes[i] = (byte) Integer.parseInt(subStr, 16);
    }

    return bytes;
  }

  /**
   * @param
   * @return  将HEX字符串每两个一组截开，截成Hex数
   */
  public static List strSplit(String str){
    List<String> strList = new ArrayList<String>();
//		 String[] strArray = new String[50];
    boolean Flag = false;
    if((str.length())%2 == 0){
      Flag = true;
    }
    if(Flag){
      for(int i=0;i<((str.length())/2);i++){
        String substring = str.substring(0+i*2, 2+i*2);
//				 strArray[i] = substring;
        strList.add(substring);
      }
    }
    return strList;
  }


  /**
   * 16进制转化为字母
   * @param hex  要转化的16进制数，用逗号隔开     如：53,68,61,64,6f,77
   * @return
   */
  public static String hexTolLetter(String hex) {
    StringBuilder sb = new StringBuilder();
    String[] split = hex.split(",");
    for (String str : split) {
      int i = Integer.parseInt(str, 16);
      sb.append((char)i);
    }
    return sb.toString();
  }

  /**
   * 将字节数组转换成十六进制的字符串并且去空格
   *
   * @return
   */
  public static String BinaryToHexStringRemoveKo(byte[] bytes) {
    String hexStr = "0123456789ABCDEF";
    String result = "";
    String hex = "";
    for (byte b : bytes) {
      hex = String.valueOf(hexStr.charAt((b & 0xF0) >> 4));
      hex += String.valueOf(hexStr.charAt(b & 0x0F));
      result += hex;
    }
    return result;
  }
}
