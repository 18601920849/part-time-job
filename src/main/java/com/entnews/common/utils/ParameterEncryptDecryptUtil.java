package com.entnews.common.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.Assert;

/**
 * 参数加密解密
 *
 * @author lisongtao
 * @date 2023/3/17 9:54
 */
public class ParameterEncryptDecryptUtil {


    /**
     * RSA非对称加密
     */
    private final String publicKey;
    /**
     * RSA非对称加密
     */
    private final String privateKey;
    /**
     * 移位加密（凯撒加密）加密偏移量
     */
    private final int encodeOffset;
    /**
     * 移位加密（凯撒加密）解密偏移量
     */
    private final int decodeOffset;


    /**
     * 构建参数加解密工具类
     */
    public ParameterEncryptDecryptUtil(String publicKey, String privateKey, int encodeOffset, int decodeOffset) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.encodeOffset = encodeOffset;
        this.decodeOffset = decodeOffset;
    }

    /**
     * 加密字符串解密过程
     * 1、md5摘要串移位解密
     * 2、加密字符串md5加密，判断是否与传递来的md5摘要加密是否相同
     * 3、RSA私钥解密
     *
     * @author lisongtao
     * @date 2023/3/17 10:08
     * @param encryptingString  传递字符串
     * @param isEncrypting  是否为加密字符串，前期联调传递参数可以为未加密状态
     * @return
     */
    public JSONObject decryptionParameter(String encryptingString, boolean isEncrypting) throws Exception {
        if (encryptingString == null || "".equals(encryptingString)) {
            throw new Exception("传入加密参数不能为空！");
        }
        if (!isEncrypting) {
            return JSON.parseObject(encryptingString);
        }
        ParameterEntity parameterEntity = JSON.parseObject(encryptingString, ParameterEntity.class);
        String encryptionParameter = parameterEntity.getEncryptionParameter();
        String verificationParameter = parameterEntity.getVerificationParameter();
        // 1、进行移位解密
        String decode = CaesarUtils.decode(verificationParameter, decodeOffset);
        // 2、再次对加密参数取MD5摘要
        String verificationParameter2 = MD5Utils.MD5(encryptionParameter);
        // 3、判断MD5摘要是否相同
        if (!decode.equals(verificationParameter2)) {
            throw new Exception("请求体存在被篡改风险，解密失败！");
        }
        // 4、RSA私钥解密
        RSAUtils rsaUtils = new RSAUtils(publicKey, privateKey);
        String plaintext = rsaUtils.privateDecrypt(encryptionParameter);
        return JSONObject.parseObject(plaintext);
    }


    /**
     * 返回参数加密过程
     * 1、RSA公钥加密
     * 2、加密字符串md5加密
     * 3、md5加密串移位加密
     *
     * @author lisongtao
     * @date 2023/3/17 10:38
     * @param jsonObject    返回json对象
     * @param isEncrypting  是否为需要加密，前期联调可以先不加密
     * @return
     */
    public String encryptionParameter(JSONObject jsonObject, boolean isEncrypting) {
        Assert.notEmpty(jsonObject, "加密参数不能为空！");
        if (!isEncrypting) {
            return jsonObject.toJSONString();
        }
        RSAUtils rsaUtils = new RSAUtils(publicKey, privateKey);
        // 1、RSA公钥加密
        String encryptionParameter = rsaUtils.publicEncrypt(jsonObject.toJSONString());
        // 2、取MD5摘要值
        String digestString = MD5Utils.MD5(encryptionParameter);
        // 3、md5加密串移位加密
        String verificationParameter = CaesarUtils.encode(digestString, encodeOffset);
        ParameterEntity parameterEntity = new ParameterEntity();
        parameterEntity.setEncryptionParameter(encryptionParameter);
        parameterEntity.setVerificationParameter(verificationParameter);
        return JSON.toJSONString(parameterEntity);
    }


    /**
     * 加密传输，请求体内容
     */
    static class ParameterEntity {
        /**
         * 加密参数
         */
        private String encryptionParameter;
        /**
         * MD5和移位 加密参数
         */
        private String verificationParameter;

        public String getEncryptionParameter() {
            return encryptionParameter;
        }

        public void setEncryptionParameter(String encryptionParameter) {
            this.encryptionParameter = encryptionParameter;
        }

        public String getVerificationParameter() {
            return verificationParameter;
        }

        public void setVerificationParameter(String verificationParameter) {
            this.verificationParameter = verificationParameter;
        }
    }


    public static void main(String[] args) throws Exception {
        // 发起方加密操作，以及需要参数
        // RSA非对称加密 公钥1
        String publicKey1 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA6Mwa4yHvuhtKBfv7CbNQ\n" +
                "EImOuKEG+G6y43iaTo6fPvBA+8dVKhBynkfSDZv1YlQhB3K9duSbEKLhobj1pIpE\n" +
                "KXLx88SSUyNRnMMIxqWjz+UfxJrXaMrDSCmUIXO6iehcESPZri0R20U7AXHOeEcZ\n" +
                "nFqJZal0uucuzAZB9Gx7Iiq3L8Rp9+cPElxPa3OgxCS0Jd369bkOvCXgpKkeyzzn\n" +
                "rkWYdLzUt5ZVKFPHhk6gouLDrwnjDziutfjSmjpO30b7D0z7ZkXYTJM+qM3g7tO0\n" +
                "It91oI1G3wsxHhdfoK2+evfKF63KX8YAz3l/DUzR7GPO2BkHCPoKjNIi1/CR9KFt\n" +
                "wQIDAQAB";
        // RSA非对称加密 私钥2
        String privateKey2 = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQC8IAb5hEWMR2Eh\n" +
                "+EvvJ7BpJehtKD5yPyObW9O4FKTjgP0Fe3jXK7Vl0wYnpZR7D86UQ+ej/el/FlYN\n" +
                "Y3IQHXIm9l6+jpXbeFVMO3yp/9MreXIsjkAUxKFMIxVYZT6TaFZWZRLTAqpMOti7\n" +
                "455O4DCISypDayt+/zoW+HIUiFKdu3is4tsE4aPP5W8fNuS99DHPZegR/UTnanoM\n" +
                "BXgfBi0+nX48Kd8wQAo92i24GZCkhifsMoX39C2gpmhQOKg7ce8eu+gZ8Dq1wMUg\n" +
                "CAACoS3zBYNALur/aNxsbf6k98RjBSbutlE+R9ZoSF3UFk0qsgXafQW0vH9lsCNP\n" +
                "dtmsO86/AgMBAAECggEAS36ezesi7dSHSlaWQncu+ENaLtQDm11zMJl9MVY3hYTy\n" +
                "BQ0zxUWpGh/BTI/KMkh5ok5N2yW+Wl3gUqch0w2+DZy5EMevo896i4kj0iTz3XJz\n" +
                "OyYq2Sq/gApkOoEGx6kLXMha8YsURFspNt11XjnhwBBJ+Zw8bE6IRUeWoF7EqL8u\n" +
                "IU2yNRcHSesx0u0ylFJB/vZlswTqW7Z7xdtRVvhKseS79OQHWKx6hiopsLt7vdAt\n" +
                "PmyKwBnlL4yB48N/lDvWPi/12tDeMMJ7VajNVAt3Qvd2DfGBZHUkAl5du1QbjRVK\n" +
                "IRhtG2CDB93WAP9HJ3H+hkwrnQOtsPAdex5jQeIsIQKBgQDwjTNs2CviRUMKvJYZ\n" +
                "d78gtL9WUAGSix7A19sZn9Ma5quuaUeyANaNr9FXeWhomG/s1cfsSgZr/wg8vbW3\n" +
                "lCiJMqLWB2Pgh8vJ6ocKBR3bFd/XwwoqNTNIdTMV+Nvtk75GvifdFabNG+D0RrZi\n" +
                "TkX187wtEzz0MiU4gRR4HJB+mwKBgQDINOggARchMk4Fjph8dExxxMtbWewl6iEO\n" +
                "KEyP7Cnn2AIvgtUhf20Jl4cu/UTpWUHs60I2gTmDFkpn/1KCx82tJ33pq2HJPwsE\n" +
                "jVcxb7PiAWiNBxiIqjf8yNXA+w0/zodmOI1hjK1a+y0llRw/DTxi1LMzQsZ/IBio\n" +
                "RerJmAbArQKBgG+wT+lAsZd8qzUn/NVUWSS5Z4GO4jwFZkEJuEw8fgsmgR4Y+dX3\n" +
                "N6dPs/ZWg5jU9lrTo+DdOgAYyN1TBDV5kByrqxmPEtukkYVt5EdNuuAHVwhaIbUZ\n" +
                "QHdrG04fXxX/HEDCd8XaELl5MUiaMIKSbz9UbNHM9ec4BTooDLX8JUbzAoGAMiO1\n" +
                "4pYDUegETDX7EGgb4P41I6qwjopFPwNusTinAg5B9d4hPGNqTlWpoGYDhSE6dngw\n" +
                "u++/FIpXlhYGdwRNCUlzqjqmobztKHWxWYstFLPfmDontcpTfTZa+hqlpc42cK5H\n" +
                "EiAmJjvzgY8HZO8OqX21Z2gYyCukoTfpZhVx030CgYAMj6MGmW2F/ujeovqNTEyi\n" +
                "5BPsjCl748mLzyW/5HOYcqsTBpbo9skjTzWJRe+6WA4bR9xZmz90kYm7Jr1PLT2J\n" +
                "eAwytFX79erT0ZAXh8dYLKxCmYy7x/JHGeEkJP/w6Rgn0DITHqhiFHOdkJXQlA88\n" +
                "r3zydC51waEHwuiLL/uttw==";
        // 移位加密（凯撒加密）加密偏移量
        int encodeOffset = 3;
        // 移位加密（凯撒加密）解密偏移量
        int decodeOffset = 3;
        // 建议以上4个参数参数化
        ParameterEncryptDecryptUtil encryptDecryptUtil = new ParameterEncryptDecryptUtil(publicKey1, privateKey2, encodeOffset, decodeOffset);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", "1630755029416382464");
        jsonObject.put("nickName", "测试2");
        jsonObject.put("phonenumber", "13012345678");
        jsonObject.put("email", "13012345678@163.com");
        System.out.println(jsonObject.toJSONString());
        String encrypt = encryptDecryptUtil.encryptionParameter(jsonObject, true);
        System.out.println(encrypt);
        // 接口接收方
        // RSA非对称加密 私钥1
        String privateKey1 = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDozBrjIe+6G0oF\n" +
                "+/sJs1AQiY64oQb4brLjeJpOjp8+8ED7x1UqEHKeR9INm/ViVCEHcr125JsQouGh\n" +
                "uPWkikQpcvHzxJJTI1GcwwjGpaPP5R/EmtdoysNIKZQhc7qJ6FwRI9muLRHbRTsB\n" +
                "cc54RxmcWollqXS65y7MBkH0bHsiKrcvxGn35w8SXE9rc6DEJLQl3fr1uQ68JeCk\n" +
                "qR7LPOeuRZh0vNS3llUoU8eGTqCi4sOvCeMPOK61+NKaOk7fRvsPTPtmRdhMkz6o\n" +
                "zeDu07Qi33WgjUbfCzEeF1+grb5698oXrcpfxgDPeX8NTNHsY87YGQcI+gqM0iLX\n" +
                "8JH0oW3BAgMBAAECggEAdVPZATxrW/rA9f8naJ4s7gjGG5tcrfzsv+RasNkEvW3M\n" +
                "8phl/ctIWSmNXjqKE8Ke2ugjQTa6SEovOZjEhOcCr3k1JEygCrK0QTOyyptU0kSC\n" +
                "HE6F3lTC0y3RcIKpMzQeoqzU2wnwA/kCYhn/m3MKH1kqAkjDgidd2IoW15+M94xl\n" +
                "qkL/an8fTTDjs7etuucKqKalCVObirG+SuS6C0l2TjixzOzHWri9bb/g+OiZq82K\n" +
                "2RomzZBZeE/sK7IBTksOrGG52P7w+El8hVfY6mHkA0o6212gCcHgLApch79kLRdX\n" +
                "L4wQA1oc6drp4dHWLkfkZXjCz0b58n7aBX9aN/pY+QKBgQD3XkYoMCcQnFCMox0a\n" +
                "+cFlh4IZvRScEahlnDg73whPPmKjmdS1QeX7diWVD1L1blsWYdUAG0E206QcJ08j\n" +
                "F2zRqrRHeRVW5VltTXn5tnA+kJDljASK4ZGhKKPSB+XloXigemqQ8VGuqce+p5Om\n" +
                "ffAgykADQ7ytYLODvIU5jfYs4wKBgQDw66taHvBzrX3jZGBevSjXK3hvZa73ZjKy\n" +
                "ImHAB5mCsQedxenvMXvFBYT/WgfJPj1gFffz3epMxUFw7frthq7UKC+Or/g8pYFt\n" +
                "Kr6cSU82OZuiCJg1gfqEJcuIX0G/fHKWzAENwQcNRrp7DjDmW5oeBe7maUIRGORh\n" +
                "SULEB7SACwKBgQCNUBbmGHQIojPF09zpryoTVTE2gPTIH7JCsdbSJRx7IJl6+oMw\n" +
                "NQAsAheAKuvqd2ujwqnK0McVihwjVTV5R4WL1Wf9uKc/J/BJZ8w5okZHB5EvASkL\n" +
                "Kcdm+G91qbtkzjS6AJTmTmWqUSVhvhu0LhfCs6eT9dtQdIC4zjaRZY7vnwKBgQCb\n" +
                "4pFz9ZL19mr/SEKG6nRTjWAXDD9C+xCQbGi9XO16P9vCvX2ZdHY2TFT6+KETGL+T\n" +
                "vYM7evqQhA3M6V80c7IJuprTA49mhlyRa1f8Pf8QAgRuuorqDFkxSldAvQoIwZhP\n" +
                "dF62LJDIiVw+JTeExf6ZsJMc6TyDXYzgWgvmVWn4cwKBgD5T5TIBHxzM40jLKm/A\n" +
                "rGkB4mKb3gerZzJCDLhunCVS8G40a2y0OEw+Ro/hRUaypFyooHWPdkR06aBhmwHk\n" +
                "HcDhakPWYxnkZAAgTREjur6hDn1d4KMrtFPzBXqyoDhjhS/CauMaV/xkfvnKoJNZ\n" +
                "O352xpG8TzV7Dso5NXg9v4wT";
        // RSA非对称加密 公钥2
        String publicKey2 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvCAG+YRFjEdhIfhL7yew\n" +
                "aSXobSg+cj8jm1vTuBSk44D9BXt41yu1ZdMGJ6WUew/OlEPno/3pfxZWDWNyEB1y\n" +
                "JvZevo6V23hVTDt8qf/TK3lyLI5AFMShTCMVWGU+k2hWVmUS0wKqTDrYu+OeTuAw\n" +
                "iEsqQ2srfv86FvhyFIhSnbt4rOLbBOGjz+VvHzbkvfQxz2XoEf1E52p6DAV4HwYt\n" +
                "Pp1+PCnfMEAKPdotuBmQpIYn7DKF9/QtoKZoUDioO3HvHrvoGfA6tcDFIAgAAqEt\n" +
                "8wWDQC7q/2jcbG3+pPfEYwUm7rZRPkfWaEhd1BZNKrIF2n0FtLx/ZbAjT3bZrDvO\n" +
                "vwIDAQAB";
        ParameterEncryptDecryptUtil encryptDecryptUtil2 = new ParameterEncryptDecryptUtil(publicKey2, privateKey1, encodeOffset, decodeOffset);
        JSONObject jsonObject1 = encryptDecryptUtil2.decryptionParameter(encrypt, true);
        System.out.println(jsonObject1);

    }


}
