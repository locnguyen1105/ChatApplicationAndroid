package com.example.chatapplication.Stringee;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Stringee
 */
public class GenAccessToken {

	public static String genAccessToken(String keySid, String keySecret, int expireInSecond,String userid) {
		try {
			Algorithm algorithmHS = Algorithm.HMAC256(keySecret);

			Map<String, Object> headerClaims = new HashMap<String, Object>();
			headerClaims.put("typ", "JWT");
			headerClaims.put("alg", "HS256");
			headerClaims.put("cty", "stringee-api;v=1");

			long exp = (long) (System.currentTimeMillis()) + expireInSecond * 1000;

			String token = JWT.create().withHeader(headerClaims)
					.withClaim("jti", keySid + "-" + System.currentTimeMillis())
					.withClaim("iss", keySid)
					.withExpiresAt(new Date(exp))
					.withClaim("userId",userid)
					.sign(algorithmHS);

			return token;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

}