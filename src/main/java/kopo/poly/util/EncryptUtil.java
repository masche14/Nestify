package kopo.poly.util;

import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

public class EncryptUtil {

    public static void main(String[] args) throws Exception {
        System.out.println("----------------------------------------------");
        System.out.println("<해시 암호화알고리즘>");

        String planText = "암호화할 문자열";
        System.out.println("해시 암호화할 문자열 : " + planText);
        String hashEnc = EncryptUtil.encHashSHA256(planText);

        System.out.println("해시 암호화 결과 : " + hashEnc);
        System.out.println("----------------------------------------------");

        System.out.println("<AES-128 암호화알고리즘>");
        System.out.println("AES-128 암호화할 문자열 : " + planText);
        String aesEnc = EncryptUtil.encAES128CBC(planText); // 암호화 문자열

        System.out.println("AES-128 암호화 결과 : " + aesEnc);

        String aesDec = EncryptUtil.decAES128CBC(aesEnc); // 복호화 문자열
        System.out.println("AES-128 복호화 결과 : " + aesDec);
        System.out.println("----------------------------------------------");
    }

    final static String addMessage = "PolyDataAnalysis";
    final static byte[] ivBytes = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    final static String key = "PolyTechnic12345";

    public static String encHashSHA256(String str) {
        String res;
        String planText = addMessage+str;

        try{
            MessageDigest sh = MessageDigest.getInstance("SHA-256");

            sh.update(planText.getBytes());

            byte[] byteData = sh.digest();

            StringBuilder sb = new StringBuilder();

            for (byte byteDatum : byteData) {
                sb.append(Integer.toString((byteDatum & 0xff) + 0x100, 16).substring(1));

            }

            res = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            res = "";
        }

        return res;
    }

    public static String encAES128CBC(String str)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

        byte[] textBytes = str.getBytes(StandardCharsets.UTF_8);
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        SecretKeySpec newKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher;
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec);
        return Base64.encodeBase64String(cipher.doFinal(textBytes));
    }

    public static String decAES128CBC(String str)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

        byte[] textBytes = Base64.decodeBase64(str);
        // byte[] textBytes = str.getBytes("UTF-8");
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        SecretKeySpec newKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec);
        return new String(cipher.doFinal(textBytes), StandardCharsets.UTF_8);
    }


}
