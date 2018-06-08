package com.banco.otp.service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base32;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

@Service
@ComponentScan("com.banco.otp")
public class serviceImpl {

	/**
	 * 
	 * @param user
	 * @param host
	 */
	public Map<String, Object> getCode(Map<String, Object> param) {
		// TODO Auto-generated method stub
		
		Map<String, Object> result = new HashMap<>();
		
		if(!param.containsKey("user")){
			result.put("result", "failed");
			return result;
		}
		
		if(!param.containsKey("host")){
			result.put("result", "failed");
			return result;
		}
		
		String user = param.get("user").toString();
		String host = param.get("host").toString();
		
		byte[] buffer = new byte[5 + 5 * 5];
		new Random().nextBytes(buffer);
		byte[] secretKey = Arrays.copyOf(buffer, 5);
		Base32 codec = new Base32();
        byte[] bEncodedKey = codec.encode(secretKey);
        String secret = new String(bEncodedKey);
        String url = getQRBarcodeURL(user, host, secret); // 생성된 바코드 주소!
        System.out.println("URL : " + url);
        
        System.out.println("encodedKey : " + secret);
        
        result.put("key", secret);
        result.put("url", url);
        
		return result;
	}
	
    public static String getQRBarcodeURL(String user, String host, String secret) {
        String format = "https://chart.googleapis.com/chart?chs=200x200&cht=qr&chl=otpauth://totp/"
        		+ user
        		+ "%40"
        		+ host
        		+ "%3Fsecret%3D"
        		+ secret
        		+ "%26issuer%3Dbanco%2520auth&choe=UTF-8";
        
        return format;
    }
    
    /**
     * 
     * @param code
     * @param key
     * @return
     */
	public Map<String, Object> getResult(Map<String, Object> param) {
		// TODO Auto-generated method stub
		Map<String, Object> result = new HashMap<>();
		String code = param.get("code").toString();
		
		long user_code = Integer.parseInt(code);
        String encodedKey = param.get("key").toString();
        long l = new Date().getTime();
        long ll =  l / 30000;
		
        boolean check_code = false;
        try {
            // 키, 코드, 시간으로 일회용 비밀번호가 맞는지 일치 여부 확인.
            check_code = check_code(encodedKey, user_code, ll);
        } catch (Exception e) {
            e.printStackTrace();
        }
         
        // 일치한다면 true.
        System.out.println("check_code : " + check_code);
        
        result.put("result", check_code);
		return result;
	}

	private static boolean check_code(String secret, long code, long t) throws NoSuchAlgorithmException, InvalidKeyException {
        Base32 codec = new Base32();
        byte[] decodedKey = codec.decode(secret);
 
        // Window is used to check codes generated in the near past.
        // You can use this value to tune how far you're willing to go.
        int window = 3;
        for (int i = -window; i <= window; ++i) {
            long hash = verify_code(decodedKey, t + i);
 
            if (hash == code) {
                return true;
            }
        }
 
        // The validation code is invalid.
        return false;
    }
     
    private static int verify_code(byte[] key, long t)
            throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] data = new byte[8];
        long value = t;
        for (int i = 8; i-- > 0; value >>>= 8) {
            data[i] = (byte) value;
        }
 
        SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signKey);
        byte[] hash = mac.doFinal(data);
 
        int offset = hash[20 - 1] & 0xF;
 
        // We're using a long because Java hasn't got unsigned int.
        long truncatedHash = 0;
        for (int i = 0; i < 4; ++i) {
            truncatedHash <<= 8;
            // We are dealing with signed bytes:
            // we just keep the first byte.
            truncatedHash |= (hash[offset + i] & 0xFF);
        }
 
        truncatedHash &= 0x7FFFFFFF;
        truncatedHash %= 1000000;
 
        return (int) truncatedHash;
    }
}
