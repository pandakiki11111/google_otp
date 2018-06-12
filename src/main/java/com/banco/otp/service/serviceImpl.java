package com.banco.otp.service;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
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

	static final String API_KEY = "A33EB88877C5546A5B66C4E8B584CE716";
	/**
	 * @param auth
	 * @param user
	 * @param host
	 * @param signature
	 * 
	 */
	public Map<String, Object> getCode(Map<String, Object> param) {
		// TODO Auto-generated method stub
		Map<String, Object> result = new HashMap<>();
		
		if(!param.containsKey("auth")){
			result.put("result", "failed");
			result.put("message", "required auth parameter");
			return result;
		}
		
		if(!param.containsKey("user")){
			result.put("result", "failed");
			result.put("message", "required user parameter");
			return result;
		}
		
		if(!param.containsKey("host")){
			result.put("result", "failed");
			result.put("message", "required host parameter");
			return result;
		}
		
		boolean signature = false;
		
		String auth = param.get("auth").toString();
		String user = param.get("user").toString();
		String host = param.get("host").toString();
		String sign = param.get("signature").toString();
		
		if("key".equals(auth)){
			signature = check_apiKey(sign);
		}
		else if("sign".equals(auth)){
			
			if(!param.containsKey("date")){
				result.put("result", "failed");
				result.put("message", "required date parameter");
				return result;
			}
			
			String date = param.get("date").toString();
			String message = user+host+date;
			
			signature = check_signature(sign, message);
		}
		else{
			result.put("result", "failed");
			result.put("message", "invalid auth parameter");
			return result;
		}
		
		if(!signature){
			result.put("result", "failed");
			result.put("message", "failed to confirm auth");
			return result;
		}
		
		//바코드 주소 만들기
		byte[] buffer = new byte[5 + 5 * 5];
		new Random().nextBytes(buffer);
		byte[] secretKey = Arrays.copyOf(buffer, 5);
		Base32 codec = new Base32();
        byte[] bEncodedKey = codec.encode(secretKey);
        String secret = new String(bEncodedKey);
        
        String url = getQRBarcodeURL(user, host, secret);
        
        System.out.println("URL : " + url);
        System.out.println("encodedKey : " + secret);
        
        result.put("key", secret);
        result.put("url", url);
        
		return result;
	}
	
	public boolean check_apiKey(String sign){
		boolean result = false;
		
		if(API_KEY.equals(sign)){
			result = true;
		}
		return result;
	}
	
	public boolean check_signature(String sign, String message){
		
		boolean result = false;
		
		//test
		String sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
		System.out.println(sdf);
		
	    Mac sha512_HMAC = null;
	    String hash = null;
	    
		System.out.println(message);
		
	    try{
	        byte [] byteKey = API_KEY.getBytes("UTF-8");
	        final String HMAC_SHA512 = "HmacSHA512";
	        sha512_HMAC = Mac.getInstance(HMAC_SHA512);
	        SecretKeySpec keySpec = new SecretKeySpec(byteKey, HMAC_SHA512);
	        sha512_HMAC.init(keySpec);
	        byte [] mac_data = sha512_HMAC.doFinal(message.getBytes("UTF-8"));
	        hash = bytesToHex(mac_data);
	        System.out.println("hash : " + hash);
	    } catch(Exception e){
	    	e.printStackTrace();
	    }
		
	    if(hash.equals(sign)){
	    	result = true;
	    }
		
		return result;
	}
	
	public static String bytesToHex(byte[] bytes) {
	    final char[] hexArray = "0123456789ABCDEF".toCharArray();
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
    public static String getSHA512(String input){
		String toReturn = null;
		try {
		    MessageDigest digest = MessageDigest.getInstance("SHA-512");
		    digest.reset();
		    digest.update(input.getBytes("utf8"));
		    toReturn = String.format("%040x", new BigInteger(1, digest.digest()));
		} catch (Exception e) {
		    e.printStackTrace();
		}
		return toReturn;
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
		
		if(!param.containsKey("auth")){
			result.put("result", "failed");
			result.put("message", "required auth parameter");
			return result;
		}
		
		if(!param.containsKey("code")){
			result.put("result", "failed");
			result.put("message", "required code parameter");
			return result;
		}
		
		if(!param.containsKey("key")){
			result.put("result", "failed");
			result.put("message", "required key parameter");
			return result;
		}
		
		if(!param.containsKey("signature")){
			result.put("result", "failed");
			result.put("message", "required date parameter");
			return result;
		}
		
		boolean signature = false;
		
		String auth = param.get("auth").toString();
		String code = param.get("code").toString();
		String key = param.get("key").toString();
		String sign = param.get("signature").toString();
		
		if("key".equals(auth)){
			signature = check_apiKey(sign);
		}
		else if("sign".equals(auth)){
			
			if(!param.containsKey("date")){
				result.put("result", "failed");
				result.put("message", "required date parameter");
				return result;
			}
			
			String date = param.get("date").toString();
			String message = code+key+date;
			
			signature = check_signature(sign, message);
		}
		else{
			result.put("result", "failed");
			result.put("message", "invalid auth parameter");
			return result;
		}
		
		if(!signature){
			result.put("result", "failed");
			result.put("message", "failed to confirm auth");
			return result;
		}
		
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
