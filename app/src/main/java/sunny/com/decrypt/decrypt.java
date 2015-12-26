package sunny.com.decrypt;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.axis.encoding.Base64;


public class decrypt {

    public static int length=128;
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
    public static String decrypmessage(String ensup , String EncKeKi, String ke) throws Exception
    {
        String deki=decryptAES(EncKeKi, ke);    //得到解密后的ki
        String desup=decryptAES(ensup, deki);   //得到解密后的sup
        return desup;
    }

//    public static void main(String[] args) throws Exception
//    {
//        String message="ensup";
//        String ke="from the phone";
//        String EncKeKi="from the service";
//        decrypmessage(message, EncKeKi, ke);
//    }
}