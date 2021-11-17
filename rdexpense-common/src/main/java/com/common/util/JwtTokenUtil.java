package com.common.util;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

/**
 * <pre>
 * 对象功能:token生成/验证帮助类
 * 开发人员:rdexpense
 * 创建时间:2018-08-15
 * </pre>
 */
public class JwtTokenUtil {

	protected static Logger logger = Logger.getLogger(JwtTokenUtil.class);

	/** 公用秘钥 **/
	private static final String SECRET = "QLrpZF4Q89AlYiSo2veJ6g==";

	/**
	 * 根据userId和userName获取Token值
	 * 
	 * @param taskId
	 *            用户Id
	 * @param userName
	 *            用户名称
	 * @param expTime
	 *            超时时间
	 * @return String token值
	 * @exception Exception
	 */
	public static String createToken(String duserId, String userName, int expTime) throws Exception {
		// 设置签发时间(档期时间)
		Date iatDate = new Date();
		Calendar nowTime = Calendar.getInstance();
		nowTime.add(Calendar.MILLISECOND, expTime);
		// 设置过去时间
		Date expiresDate = nowTime.getTime();
		Map<String, Object> headerMap = new HashMap<String, Object>();
		headerMap.put("alg", "HS256");
		headerMap.put("typ", "JWT");
		String token = JWT.create().withHeader(headerMap)// header
				.withClaim("expTime", expTime)// payload
				.withClaim("duserId", duserId).withClaim("userName", userName)//.withExpiresAt(expiresDate)
				.withIssuedAt(iatDate).sign(Algorithm.HMAC256(SECRET));// 加密
		return token;
	}

	/**
	 * 根据token值验证是否有效
	 * 
	 * @param token
	 *            值
	 * @return Map token包含的用户信息
	 */
	public static Map<String, Claim> verifyToken(String token) {
		Map<String, Claim> claimMap = null;
		try {
			JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
			DecodedJWT jwt = verifier.verify(token);
			claimMap = jwt.getClaims();
		} catch (TokenExpiredException e) {
			logger.error("登录凭证已过期,请重新登录!");
		} catch (JWTDecodeException e) {
			logger.error("登录凭证请求非法,请重新登录!");
		} catch (Exception e) {
			logger.error("登录凭证错误,请重新登录!");
		}
		return claimMap;
	}

	public static void main(String[] args) {
		try {
			// Map<String, Claim> claimMap = verifyToken(createToken("123456"));
			Map<String, Claim> claimMap = verifyToken(
					"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHBUaW1lIjozNjAwMDAwLCJleHAiOjE1MzQzMTc5NDUsInVzZXJJZCI6IjEyMzQ1NiIsImlhdCI6MTUzNDMxNDM0NX0.ZKmoCTP2_H7_E65yMIMm5HVuXOIgEnQuyhfHpQHvCEs");
			System.out.println("-------" + claimMap.get("expTime").asInt());
			System.out.println("-------" + claimMap.get("userId").asString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
