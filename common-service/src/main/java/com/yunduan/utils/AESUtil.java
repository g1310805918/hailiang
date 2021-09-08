package com.yunduan.utils;

import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.util.BitSet;

/**
 * AES 加密解密
 */
public class AESUtil {

    private static final transient Logger log = LoggerFactory.getLogger(AESUtil.class);

    /**
     * AES方法
     */
    private static final String AES_Algorithm = "AES";
    /**
     * 秘钥
     */
    private static final String keyStr = "2523454dsfdsftyu";

    private static AES aes = null;

    private static BitSet dontNeedEncoding;


    static {
        dontNeedEncoding = new BitSet(256);
        int i;
        for (i = 'a'; i <= 'z'; i++) {
            dontNeedEncoding.set(i);
        }
        for (i = 'A'; i <= 'Z'; i++) {
            dontNeedEncoding.set(i);
        }
        for (i = '0'; i <= '9'; i++) {
            dontNeedEncoding.set(i);
        }
        dontNeedEncoding.set('+');
        /**
         * 这里会有误差,比如输入一个字符串 123+456,它到底是原文就是123+456还是123 456做了urlEncode后的内容呢？<br>
         * 其实问题是一样的，比如遇到123%2B456,它到底是原文即使如此，还是123+456 urlEncode后的呢？ <br>
         * 在这里，我认为只要符合urlEncode规范的，就当作已经urlEncode过了<br>
         * 毕竟这个方法的初衷就是判断string是否urlEncode过<br>
         */

        dontNeedEncoding.set('-');
        dontNeedEncoding.set('_');
        dontNeedEncoding.set('.');
        dontNeedEncoding.set('*');
    }

    /**
     * 解密数据转对象
     *
     * @param decryptStr
     * @param tClass
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static <T> T decryptToObj(String decryptStr, Class<T> tClass) {
        try {

//            System.out.println("decryptStr = " + decryptStr);
//            return JSONObject.parseObject(decryptStr, tClass);
            return JSONObject.parseObject(decrypt(decryptStr), tClass);
        } catch (Exception e) {
            log.error("解密错误  " + decryptStr, e);
            throw new RuntimeException("解密错误");
        }
    }

    /**
     * 解密数据转JSON
     *
     * @param decryptStr
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static JSONObject decryptToJSON(String decryptStr) {
        try {
//            return JSONObject.parseObject(decryptStr);
            return JSONObject.parseObject(decrypt(decryptStr));
        } catch (Exception e) {
            log.error("解密错误" + decryptStr, e);
            throw new RuntimeException("解密错误");
        }
    }


    /**
     * 解密数据
     *
     * @param decryptStr
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String decrypt(String decryptStr) {
        try {
            //替换加密串中 空格 为 加号
            decryptStr = decryptStr.replaceAll(" ","+");
            return getAes().decryptStr(decryptStr, Charset.forName("UTF-8")).replaceAll(" ", "");
        } catch (Exception e) {
            log.error("解密错误  " + decryptStr, e);
            throw new RuntimeException("解密错误");
        }
    }

    /**
     * 加密数据
     *
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String encrypt(final String encryptData) {
        try {
            return getAes().encryptBase64(encryptData, "UTF-8");
        } catch (Exception e) {
            log.error("加密错误  " + encryptData, e);
            throw new RuntimeException("加密错误");
        }
    }

    /**
     * 判断str是否urlEncoder.encode过<br>
     * 经常遇到这样的情况，拿到一个URL,但是搞不清楚到底要不要encode.<Br>
     * 不做encode吧，担心出错，做encode吧，又怕重复了<Br>
     *
     * @param str
     * @return
     */
    public static boolean hasUrlEncoded(String str) {

        /**
         * 支持JAVA的URLEncoder.encode出来的string做判断。 即: 将' '转成'+' <br>
         * 0-9a-zA-Z保留 <br>
         * '-'，'_'，'.'，'*'保留 <br>
         * 其他字符转成%XX的格式，X是16进制的大写字符，范围是[0-9A-F]
         */
        boolean needEncode = false;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (dontNeedEncoding.get((int) c)) {
                continue;
            }
            if (c == '%' && (i + 2) < str.length()) {
                // 判断是否符合urlEncode规范
                char c1 = str.charAt(++i);
                char c2 = str.charAt(++i);
                if (isDigit16Char(c1) && isDigit16Char(c2)) {
                    continue;
                }
            }
            // 其他字符，肯定需要urlEncode
            needEncode = true;
            break;
        }

        return !needEncode;
    }


    /**
     * 判断c是否是16进制的字符
     *
     * @param c
     * @return
     */
    private static boolean isDigit16Char(char c) {
        return (c >= '0' && c <= '9') || (c >= 'A' && c <= 'F');
    }


    private static AES getAes() throws UnsupportedEncodingException {
        if (aes == null) {
            aes = new AES(Mode.CBC, Padding.PKCS5Padding, new SecretKeySpec(keyStr.getBytes("UTF-8"), AES_Algorithm), new IvParameterSpec(keyStr.getBytes("UTF-8")));
        }
        return aes;
    }


    public static void main(String[] args) {

    }

}
