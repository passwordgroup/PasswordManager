package com.rt;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.axis.encoding.Base64;
import org.apache.commons.lang.StringUtils;

public class complete {
    public static final String KEY_MAC = "HmacSHA512"; 
    public static int length=128;
    private static String KE=null;
    private static String ensup=null;
    private static String EncKeKi=null;
 //SHA256
 public static String encryptSHA(String msg,String key) {
		String salt;
	    if(key==null)salt= getSaltSHA1();
	    else salt = key;
		StringBuilder sb = new StringBuilder();
		try{
			MessageDigest md = MessageDigest.getInstance("SHA-512");
         md.update(salt.getBytes());
         byte[] bytes = md.digest(msg.getBytes());
         for(int i=0; i< bytes.length ;i++){
             sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
         }
		}catch(Exception e){
		}
		return sb.toString();
	}
 private static String getSaltSHA1(){
     SecureRandom sr;
     byte[] salt = new byte[16];
		try {
			sr = SecureRandom.getInstance("SHA1PRNG");
			 sr.nextBytes(salt);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		 return salt.toString();
 }
//AES
	private static byte[] encryptfirst(String content, String password)
			throws Exception {

		KeyGenerator kgen = KeyGenerator.getInstance("AES");
                SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG" ); 
                secureRandom.setSeed(password.getBytes()); 
		kgen.init(length, secureRandom);
		SecretKey secretKey = kgen.generateKey();
		byte[] enCodeFormat = secretKey.getEncoded();
		SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		byte[] byteContent = content.getBytes("utf-8");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] result = cipher.doFinal(byteContent);
		return result; 

	}
	private static byte[] decryptfirst(byte[] content, String password)
			throws Exception {

		KeyGenerator kgen = KeyGenerator.getInstance("AES");
                 SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG" ); 
                  secureRandom.setSeed(password.getBytes()); 
		kgen.init(length, secureRandom);
		SecretKey secretKey = kgen.generateKey();
		byte[] enCodeFormat = secretKey.getEncoded();
		SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] result = cipher.doFinal(content);
		return result; 
	}
	public static byte[] encrypt2(String content, String password) throws NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		
			SecretKeySpec key = new SecretKeySpec(password.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
			byte[] byteContent = content.getBytes("utf-8");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] result = cipher.doFinal(byteContent);
			return result; 
	}

	public static String encryptAES(String content,String password) throws Exception {
		byte[] encryptResult = encryptfirst(content, password);
		return Base64.encode(encryptResult);
	}

	public static String decryptAES(String content, String password) throws Exception {

		byte[] decryptResult = decryptfirst(Base64.decode(content), password);
		return new String(decryptResult,"UTF-8");
	}
   
 //HMAC 
 public static byte[] encryptHMAC(byte[] data, String key) throws Exception {
     SecretKey secretKey = new SecretKeySpec(decryptBASE64(key), KEY_MAC);
     Mac mac = Mac.getInstance(secretKey.getAlgorithm());
     mac.init(secretKey);
     return mac.doFinal(data);
 }
 public static byte[] decryptBASE64(String key) throws Exception {
     return (new BASE64Decoder()).decodeBuffer(key);
 }

 //MD5 
	public static String encryptMD5(String msg) throws NoSuchAlgorithmException{
		return encrypt(msg, null);
	}
	private static String encrypt(String msg, String type) throws NoSuchAlgorithmException{
		MessageDigest md;
		StringBuilder password = new StringBuilder();
			md = MessageDigest.getInstance("MD5");
			if(StringUtils.isNotBlank(type)){
				md.update(type.getBytes());
			}else {
				md.update(msg.getBytes());
			}
			
			byte[] bytes = md.digest();
			for (int i = 0; i < bytes.length; i++) {
				String param = Integer.toString((bytes[i] & 0xff) + 0x100, 16);
				password.append(param.substring(1));
			}
		return password.toString();
	}
 //PRK
 public static byte[] ComputPRK(String MP, String username, int c) throws Exception
 {
	 byte[] PRK ;
	 String key=encryptSHA(MP,"");
	 for(int i=1;i<c;i++)
	 {
		 byte[] u= null;
		 u=encryptHMAC(key.getBytes(),username);
		 String U=new String(u,"UTF-8");
		 if(key==U)key=0+"";
		 else key=1+"";
		 username=U;
	 }
	 PRK=key.getBytes();
	 return PRK;
 }

public static String getNowtimetwo()
{
	Calendar cal=Calendar.getInstance();
    SimpleDateFormat f=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String strDate= f.format(cal.getTime());
	return strDate;
}

 public static void FirstTimelog(String MP, String username) throws Exception
 {     
    String uid=encryptSHA(username+getNowtimetwo(),"");   
    
    KeyGenerator kg=KeyGenerator.getInstance("AES");
    kg.init(256); 
    SecretKey ke=kg.generateKey();       
    KE=ke.toString();
    byte[] PRK=ComputPRK(MP, uid, 512);         
    
    String k1=null;
    String u1=uid+1+"";
    k1=encryptSHA(PRK.toString(), encryptMD5(u1));
    System.out.println(k1);
    String EncK1Ke=encryptAES(ke.toString(),k1);  
 }        
 public static void encrypmessage(String message,String ke) throws Exception
 {
 	    KeyGenerator kg=KeyGenerator.getInstance("AES");
 	    kg.init(256); 
 	    SecretKey ki=kg.generateKey();    //ki生成
 	    
 	    ensup=encryptAES(message, ki.toString());    //生成加密的sup
 	    
 	    EncKeKi=encryptAES(ki.toString(), ke);       //生成ke加密的ki
 }
  
 public static void decrypmessage(String ensup , String EncKeKi, String ke) throws Exception
 {
 	    String deki=decryptAES(EncKeKi, ke);    //得到解密后的ki
 	    String desup=decryptAES(ensup, deki);   //得到解密后的sup
 	    System.out.println(desup);
 }
 
  public static void main(String[] args) throws Exception
  {
	  byte[] MP={45,12,43,21};              //照片读取MP
	  String mp=encryptMD5(MP.toString());       //转化为计算所需要的mp
	  String username="username";      //获取的用户名
	  String message="message";    //获取用户的sup
	 FirstTimelog(mp, username);
	 encrypmessage(message, KE);
	 decrypmessage(ensup, EncKeKi, KE);
  }
}
