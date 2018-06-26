package com.bingo.common.utility;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * 各种加密，解密算法
 * Created by xinxin on 2016/6/21.
 */
public class SecurityClass {


    //region RSA非对称加密解密
    private static final String g_RSA_Public_Key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCNegiKpXlT6NbEtKZx+Bx5UCtL8F1/ADZIA1cIiG0TqW0hTBHDONFopQPJWw7sXDldL/WB3znS1orHorgojqXMH/FPN28R69kLGKdR4xRQeRDCO9QoaHzUflQnNA5SG1/ovSErRrvBmf31zIPeTg5ijYJ9brO+TSWwGoJiYtRvvQIDAQAB";
    private static final String g_RSA_Private_Key = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAI16CIqleVPo1sS0pnH4HHlQK0vwXX8ANkgDVwiIbROpbSFMEcM40WilA8lbDuxcOV0v9YHfOdLWiseiuCiOpcwf8U83bxHr2QsYp1HjFFB5EMI71ChofNR+VCc0DlIbX+i9IStGu8GZ/fXMg95ODmKNgn1us75NJbAagmJi1G+9AgMBAAECgYA/7/DAR/xHEalOCJ5YT1r+8F5A2YZHiR7++JNyxcUV57HsJGa6pYjuE/VcIIzkAOHj3XLEmM+XG2Joyn8TxTPkFPY5SEJYyncs6BONVDbcjqTxMROFqQalRaE/ojw3NqrxYARJGSO6yYhP9gS3quyFzYLVZ1Phs0HB/U2kefesuQJBAPRoDe7UFYKaiQ/PC4BOCJzHoUoF85XTFi66AV9y0I9x/IaNRExGF+eW+lNQtfH1NifLXOgLFJdEAUW8MUChbtMCQQCUMA2XikK1nhMhBmsjul0Kg80sMa93yKZMFqLjN1EMrhUNtveurzp/U94VQlIlH+0gnVPHqc/wYn5H2wHSsi0vAkEAiP7WaHuKvxVeJHVargWPgEnJ15M0cVPLyE9Mu7LAwtcSxFzk3pgfiBmxoQfJpKFdRLsSDaNAXHWq/Oq69M5ILwJAUAIB1KfZjKVkPphwkEG2qk6vIAVTb6Dt6HbwSy06nVYAF/+Jis8hDk4BwgikwVaTeOB4s3yDwI3tEG62dUOuEQJBAPA+xIUFSLWkdaJ+mdscuDCb+QiK1EY4KY4jShT6gh1tuTFgT0LcziqKbYfDsSNc+3jbKjzqOwPhXVNuQOFR8ZA=";
    /**
     * RSA最大解密密文大小
     */
    private static int g_MAX_DECRYPT_BLOCK = 128;
    /**
     * RSA最大加密明文大小
     */
    private static int g_MAX_ENCRYPT_BLOCK = 117;
    /**
     * 生成公钥和私钥
     *
     * @throws NoSuchAlgorithmException
     */
    /**
     * 生成密钥对(公钥和私钥)
     *
     * @return
     * @throws Exception
     */
    public static Map<String, Object> createRSAKeys() {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(1024);
            KeyPair keyPair = keyPairGen.generateKeyPair();
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            Map<String, Object> keyMap = new HashMap<String, Object>(2);
            keyMap.put("public", publicKey);
            keyMap.put("private", privateKey);
            return keyMap;
        } catch (Exception ex) {
            return new HashMap<String, Object>();
        }
    }

    /**
     * 获取私钥
     *
     * @param keyMap 密钥对
     * @return
     * @throws Exception
     */
    public static String getRSAPrivateKey(Map<String, Object> keyMap)
            throws Exception {
        Key key = (Key) keyMap.get("private");
        return new String(Base64.encode(key.getEncoded()));
    }

    /**
     * 获取公钥
     *
     * @param keyMap 密钥对
     * @return
     * @throws Exception
     */
    public static String getRSAPublicKey(Map<String, Object> keyMap)
            throws Exception {
        Key key = (Key) keyMap.get("public");
        return new String(Base64.encode(key.getEncoded()));
    }

    /**
     * 私钥解密
     *
     * @param encryptedData 已加密数据
     * @param key           私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] decryptRSA(byte[] encryptedData, String key) {
        try {
            byte[] keyBytes = Base64.decode(key);
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, privateK);
            int inputLen = encryptedData.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段解密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > g_MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(encryptedData, offSet, g_MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * g_MAX_DECRYPT_BLOCK;
            }
            byte[] decryptedData = out.toByteArray();
            out.close();
            return decryptedData;
        } catch (Exception ex) {
            return null;
        }
    }

    public static byte[] decryptRSA(byte[] encryptedData) {
        return decryptRSA(encryptedData, g_RSA_Private_Key);
    }

    public static byte[] decryptRSA(String encryptedData) throws Base64DecodingException {
        return decryptRSA(decryptRSA(Base64.decode(encryptedData), g_RSA_Private_Key));
    }

    public static String decryptRSAToString(String encryptedData) throws Base64DecodingException {
        return new String(decryptRSA(Base64.decode(encryptedData), g_RSA_Private_Key));
    }

    public static byte[] decryptRSA(String encryptedData, String key) throws Base64DecodingException {
        return decryptRSA(decryptRSA(Base64.decode(encryptedData), key));
    }

    public static String decryptRSAToString(String encryptedData, String key) throws Base64DecodingException {
        return new String(decryptRSA(decryptRSA(Base64.decode(encryptedData), key)));
    }

    /**
     * 公钥加密
     *
     * @param data 源数据
     * @param key  公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] encryptRSA(byte[] data, String key) {
        try {
            byte[] keyBytes = Base64.decode(key);
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            Key publicK = keyFactory.generatePublic(x509KeySpec);
            // 对数据加密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, publicK);
            int inputLen = data.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段加密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > g_MAX_ENCRYPT_BLOCK) {
                    cache = cipher.doFinal(data, offSet, g_MAX_ENCRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * g_MAX_ENCRYPT_BLOCK;
            }
            byte[] encryptedData = out.toByteArray();
            out.close();
            return encryptedData;
        } catch (Exception ex) {
            return null;
        }
    }

    public static byte[] encryptRSA(byte[] data) {
        return encryptRSA(data, g_RSA_Public_Key);
    }

    public static byte[] encryptRSA(String data) {
        return encryptRSA(data.getBytes(), g_RSA_Public_Key);
    }

    public static String encryptRSAToString(String data) {
        return Base64.encode(encryptRSA(data.getBytes(), g_RSA_Public_Key));
    }

    public static byte[] encryptRSA(String data, String key) {
        return encryptRSA(data.getBytes(), key);
    }

    public static String encryptRSAToString(String data, String key) {
        return Base64.encode(encryptRSA(data.getBytes(), key));
    }
    //endregion

    //region 一般加密解密
    public static String encryptMD5(String str) {
        return encryptByMessageDigest(str, "MD5");
    }

    public static String encryptSHA1(String str) {
        return encryptByMessageDigest(str, "SHA1");
    }

    public static String encryptSHA256(String str) {
        return encryptByMessageDigest(str, "SHA-256");
    }

    public static String encryptSHA384(String str) {
        return encryptByMessageDigest(str, "SHA-384");
    }

    public static String encryptSHA512(String str) {
        return encryptByMessageDigest(str, "SHA-512");
    }

    public static String encryptBase64(String str) {
        if (str == null) return null;
        return (new BASE64Encoder()).encode(str.getBytes());
    }

    public static String decryptBase64(String str) {
        if (str == null) return null;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            byte[] b = decoder.decodeBuffer(str);
            return new String(b);
        } catch (Exception e) {
            return "";
        }
    }

    public static String decryptBase64(byte[] b) {
        try {
            return new String(b);
        } catch (Exception e) {
            return "";
        }
    }
    //endregion

    //region 对称加密AES

    /**
     * AES加密算法，不受密钥长度限制
     *
     * @param content
     * @param key
     * @return
     */
    public static String encryptAES(String content, String key) {
        try {
            SecretKeySpec secretKey = (SecretKeySpec) initKeyForAES(key);
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);// 初始化
            byte[] result = cipher.doFinal(byteContent);
            return asHex(result); // 加密
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * AES解密算法，不受密钥长度限制
     *
     * @param content
     * @param key
     * @return
     */
    public static String decryptAES(String content, String key) {
        try {
            SecretKeySpec secretKey = (SecretKeySpec) initKeyForAES(key);
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, secretKey);// 初始化
            byte[] result = cipher.doFinal(asBytes(content));
            return new String(result); // 加密
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //endregion

    //region 对称加密DES

    /**
     * DES加密算法，不受密钥长度限制
     *
     * @param content
     * @param key
     * @return
     */
    public static String encryptDES(String content, String key) {
        try {
            SecureRandom sr = new SecureRandom();
            Key securekey = initKeyForDES(key);
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
            byte[] bt = cipher.doFinal(content.getBytes());
            String strs = new BASE64Encoder().encode(bt);
            return strs;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * DES解密算法
     *
     * @param content
     * @param key
     * @return
     */
    public static String decryptDES(String content, String key) {
        try {
            SecureRandom sr = new SecureRandom();
            Cipher cipher = Cipher.getInstance("DES");
            Key securekey = initKeyForDES(key);
            cipher.init(Cipher.DECRYPT_MODE, securekey,sr);
            byte[] res = new BASE64Decoder().decodeBuffer(content);
            res = cipher.doFinal(res);
            return new String(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //endregion

    //region 对称加密DESede

    /**
     * DESede加密算法
     *
     * @param content
     * @param key
     * @return
     */
    public static String encryptDESede(String content, String key) {
        try {
            SecureRandom sr = new SecureRandom();
            Key securekey = initKeyForDESede(key);
            Cipher cipher = Cipher.getInstance("DESede");
            cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
            byte[] bt = cipher.doFinal(content.getBytes());
            String strs = new BASE64Encoder().encode(bt);
            return strs;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * DESede解密算法
     *
     * @param content
     * @param key
     * @return
     */
    public static String decryptDESede(String content, String key) {
        try {
            SecureRandom sr = new SecureRandom();
            Cipher cipher = Cipher.getInstance("DESede");
            Key securekey = initKeyForDESede(key);
            cipher.init(Cipher.DECRYPT_MODE, securekey,sr);
            byte[] res = new BASE64Decoder().decodeBuffer(content);
            res = cipher.doFinal(res);
            return new String(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //endregion

    //region 子程序
    private static Key initKeyForAES(String key) throws NoSuchAlgorithmException {
        if (null == key || key.length() == 0) {
            throw new NullPointerException("key not is null");
        }
        SecretKeySpec key2 = null;
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(key.getBytes());
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128, random);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            key2 = new SecretKeySpec(enCodeFormat, "AES");
        } catch (NoSuchAlgorithmException ex) {
            throw new NoSuchAlgorithmException();
        }
        return key2;
    }

    private static Key initKeyForDES(String key) throws NoSuchAlgorithmException {
        SecretKey securekey = null;
        if (key == null) {
            key = "";
        }
        KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
        keyGenerator.init(new SecureRandom(key.getBytes()));
        securekey = keyGenerator.generateKey();
        return securekey;
    }

    private static Key initKeyForDESede(String key) throws NoSuchAlgorithmException {
        SecretKey securekey = null;
        if (key == null) {
            key = "";
        }
        KeyGenerator keyGenerator = KeyGenerator.getInstance("DESede");
        keyGenerator.init(new SecureRandom(key.getBytes()));
        securekey = keyGenerator.generateKey();
        return securekey;
    }

    /**
     * 将2进制数值转换为16进制字符串
     *
     * @param buf
     * @return
     */
    private static String asHex(byte buf[]) {
        StringBuffer strbuf = new StringBuffer(buf.length * 2);
        int i;
        for (i = 0; i < buf.length; i++) {
            if (((int) buf[i] & 0xff) < 0x10)
                strbuf.append("0");
            strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
        }
        return strbuf.toString();
    }

    /**
     * 将16进制转换
     *
     * @param hexStr
     * @return
     */
    private static byte[] asBytes(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }


    private static String encryptByMessageDigest(String str, String type) {
        MessageDigest dig;
        try {
            dig = MessageDigest.getInstance(type);
            dig.update(str.getBytes());
            byte[] digest = dig.digest();

            String hex = asHex(digest);
            return hex;
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }
    //endregion


    public static void main(String args[]){
        System.out.println(encryptMD5("Gmcc12#$"));
    }

}
