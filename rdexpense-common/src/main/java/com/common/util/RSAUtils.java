package com.common.util;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import javax.crypto.Cipher;

/**
 * <pre>
 * 对象功能:解密工具类
 * 创建时间:2018-01-31
 * </pre>
 */
public class RSAUtils {

	// 模
	private static String modulus = "124947910988087432123264938636514806436767405930838073535431439090507671964247329353589406429709372921384351925349601737984006402734256375470373273337915224108470268921549366632321238550329494790229600376533991068561076666834694234639774017118446627243975943554709115310378743065504729964827569712301840100823";

	// 公钥指数
	private static String public_exponent = "65537";

	// 私钥指数
	private static String private_exponent = "4844479329550180280226684301621742270104307161912500493668176552618825922168431021979502902755565811575715065418211666939551097385411987885777781826321658416109788174508724912698384784473579820950437598125715686502404850405095314919958956505509982092020999183366277388987665612806523937520550906274446720153";

//	public static void main(String[] args) throws Exception {
//		String param = "taskId=10000000000001&taskType=2";
//		String jiami = setEncrypt(param);
//		System.out.println("加密后的参数：" + jiami);
//		String jiemi = getDecode(jiami);
//		System.out.println("解密后的参数：" + jiemi);
//	}

	/**
	 * 解密
	 **/
	public static String getDecode(String mi) throws Exception {
		// 使用模和指数生成公钥和私钥
		RSAPublicKey pubKey = RSAUtils.getPublicKey(modulus, public_exponent);
		RSAPrivateKey priKey = RSAUtils.getPrivateKey(modulus, private_exponent);
		// 私钥签名
		byte[] sign = sign(priKey, mi);
		// 签名验证
		boolean signflg = signValidate(pubKey, mi, sign);
		// 解密后的明文
		if (signflg) {
			String ming = RSAUtils.decryptByPrivateKey(mi, priKey);
			return ming;
		} else {
			throw new NoSuchAlgorithmException("签名验证失败,请确认参数是否有误!");
		}
	}

	/**
	 * 加密
	 **/
	public static String setEncrypt(String param) throws Exception {
		// 使用模和指数生成公钥和私钥
		RSAPublicKey pubKey = RSAUtils.getPublicKey(modulus, public_exponent);
		// 加密后的密文
		String mi = RSAUtils.encryptByPublicKey(param, pubKey);
		return mi;
	}

	/**
	 * 生成公钥和私钥
	 * 
	 * @throws NoSuchAlgorithmException
	 *
	 */
	public static HashMap<String, Object> getKeys() throws NoSuchAlgorithmException {
		HashMap<String, Object> map = new HashMap<String, Object>();
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
		keyPairGen.initialize(1024);
		KeyPair keyPair = keyPairGen.generateKeyPair();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		map.put("public", publicKey);
		map.put("private", privateKey);
		return map;
	}

	/**
	 * 使用模和指数生成RSA公钥
	 * 注意：【此代码用了默认补位方式，为RSA/None/PKCS1Padding，不同JDK默认的补位方式可能不同，如Android默认是RSA
	 * /None/NoPadding】
	 *
	 * @param modulus
	 *            模
	 * @param exponent
	 *            指数
	 * @return
	 */
	public static RSAPublicKey getPublicKey(String modulus, String exponent) {
		try {
			BigInteger b1 = new BigInteger(modulus);
			BigInteger b2 = new BigInteger(exponent);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			RSAPublicKeySpec keySpec = new RSAPublicKeySpec(b1, b2);
			return (RSAPublicKey) keyFactory.generatePublic(keySpec);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 使用模和指数生成RSA私钥
	 * 注意：【此代码用了默认补位方式，为RSA/None/PKCS1Padding，不同JDK默认的补位方式可能不同，如Android默认是RSA
	 * /None/NoPadding】
	 *
	 * @param modulus
	 *            模
	 * @param exponent
	 *            指数
	 * @return
	 */
	public static RSAPrivateKey getPrivateKey(String modulus, String exponent) {
		try {
			BigInteger b1 = new BigInteger(modulus);
			BigInteger b2 = new BigInteger(exponent);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(b1, b2);
			return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 公钥加密
	 *
	 * @param data
	 * @param publicKey
	 * @return
	 * @throws Exception
	 */
	public static String encryptByPublicKey(String data, RSAPublicKey publicKey) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		// 模长
		int key_len = publicKey.getModulus().bitLength() / 8;
		// 加密数据长度 <= 模长-11
		String[] datas = splitString(data, key_len - 11);
		String mi = "";
		// 如果明文长度大于模长-11则要分组加密
		for (String s : datas) {
			mi += bcd2Str(cipher.doFinal(s.getBytes()));
		}
		return mi;
	}

	/**
	 * 私钥解密
	 *
	 * @param data
	 * @param privateKey
	 * @return
	 * @throws Exception
	 */
	public static String decryptByPrivateKey(String data, RSAPrivateKey privateKey) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		// 模长
		int key_len = privateKey.getModulus().bitLength() / 8;
		byte[] bytes = data.getBytes();
		byte[] bcd = ASCII_To_BCD(bytes, bytes.length);
		// 如果密文长度大于模长则要分组解密
		String ming = "";
		byte[][] arrays = splitArray(bcd, key_len);
		for (byte[] arr : arrays) {
			ming += new String(cipher.doFinal(arr));
		}
		return ming;
	}

	/**
	 * ASCII码转BCD码
	 *
	 */
	public static byte[] ASCII_To_BCD(byte[] ascii, int asc_len) {
		byte[] bcd = new byte[asc_len / 2];
		int j = 0;
		for (int i = 0; i < (asc_len + 1) / 2; i++) {
			bcd[i] = asc_to_bcd(ascii[j++]);
			bcd[i] = (byte) (((j >= asc_len) ? 0x00 : asc_to_bcd(ascii[j++])) + (bcd[i] << 4));
		}
		return bcd;
	}

	public static byte asc_to_bcd(byte asc) {
		byte bcd;

		if ((asc >= '0') && (asc <= '9')){
			bcd = (byte) (asc - '0');
		}
		else if ((asc >= 'A') && (asc <= 'F')){
			bcd = (byte) (asc - 'A' + 10);
		}
		else if ((asc >= 'a') && (asc <= 'f')){
			bcd = (byte) (asc - 'a' + 10);
		}
		else{
			bcd = (byte) (asc - 48);
		}
		return bcd;
	}

	/**
	 * BCD转字符串
	 */
	public static String bcd2Str(byte[] bytes) {
		char temp[] = new char[bytes.length * 2], val;

		for (int i = 0; i < bytes.length; i++) {
			val = (char) (((bytes[i] & 0xf0) >> 4) & 0x0f);
			temp[i * 2] = (char) (val > 9 ? val + 'A' - 10 : val + '0');

			val = (char) (bytes[i] & 0x0f);
			temp[i * 2 + 1] = (char) (val > 9 ? val + 'A' - 10 : val + '0');
		}
		return new String(temp);
	}

	/**
	 * 拆分字符串
	 */
	public static String[] splitString(String string, int len) {
		int x = string.length() / len;
		int y = string.length() % len;
		int z = 0;
		if (y != 0) {
			z = 1;
		}
		String[] strings = new String[x + z];
		String str = "";
		for (int i = 0; i < x + z; i++) {
			if (i == x + z - 1 && y != 0) {
				str = string.substring(i * len, i * len + y);
			} else {
				str = string.substring(i * len, i * len + len);
			}
			strings[i] = str;
		}
		return strings;
	}

	/**
	 * 拆分数组
	 */
	public static byte[][] splitArray(byte[] data, int len) {
		int x = data.length / len;
		int y = data.length % len;
		int z = 0;
		if (y != 0) {
			z = 1;
		}
		byte[][] arrays = new byte[x + z][];
		byte[] arr;
		for (int i = 0; i < x + z; i++) {
			arr = new byte[len];
			if (i == x + z - 1 && y != 0) {
				System.arraycopy(data, i * len, arr, 0, y);
			} else {
				System.arraycopy(data, i * len, arr, 0, len);
			}
			arrays[i] = arr;
		}
		return arrays;
	}

	/**
	 * 私钥签名
	 * 
	 * @throws Exception
	 */
	public static byte[] sign(RSAPrivateKey rsaPrivateKey, String src) throws Exception {
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(rsaPrivateKey.getEncoded()); // 私钥转换成pkcs8格式
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec); // 用key工厂对象生成私钥
		Signature signature = Signature.getInstance("MD5withRSA"); // md5
																	// RSA签名对象
		signature.initSign(privateKey); // 初始化签名
		signature.update(src.getBytes());
		byte[] result = signature.sign(); // 对消息进行签名
		return result;
	}

	/**
	 * 公钥签名验证
	 * 
	 * @throws Exception
	 */
	public static boolean signValidate(RSAPublicKey rsaPublicKey, String src, byte[] result) throws Exception {
		X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(rsaPublicKey.getEncoded());
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
		Signature signature = Signature.getInstance("MD5withRSA"); // md5
																	// RSA签名对象
		signature.initVerify(publicKey);
		signature.update(src.getBytes());
		boolean verify = signature.verify(result);
		return verify;
	}

}